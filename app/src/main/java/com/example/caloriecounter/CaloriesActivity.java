package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaloriesActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private FoodAdapter adapter;
    private List<FoodEntry> foodList;
    private TextView todaysDate;
    private TextView totalCaloriesText;
    private TextView goalText;

    // Launcher for AddEntryActivity
    private final ActivityResultLauncher<Intent> addEntryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods();
                }
            });

    // Launcher for CalendarActivity
    private final ActivityResultLauncher<Intent> calendarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods();
                }
            });

    // Launcher for SetGoalActivity
    private final ActivityResultLauncher<Intent> setGoalLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods(); // Refresh the UI when the goal is updated
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);

        // Hide the status bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Initialize views
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(foodList, this::onDeleteButtonClicked, this::onDataChanged);
        foodRecyclerView.setAdapter(adapter);

        todaysDate = findViewById(R.id.todaysDate);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        if (totalCaloriesText == null) {
            Log.e("CaloriesActivity", "View not found: totalCaloriesText");
        }
        goalText = findViewById(R.id.goalText);

        FloatingActionButton fabMenu = findViewById(R.id.fabAdd);
        fabMenu.setOnClickListener(this::showPopupMenu);

        // Set today's date
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        todaysDate.setText(currentDate);

        // Load food data
        loadFoods();
    }

    private void onDataChanged() {
        // Reload data when notified of changes
        loadFoods();
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Add Entry");
        popup.getMenu().add(0, 2, 1, "Calendar");
        popup.getMenu().add(0, 3, 2, "Set Goal");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    addEntryLauncher.launch(new Intent(this, AddEntryActivity.class));
                    return true;
                case 2:
                    calendarLauncher.launch(new Intent(this, CalendarActivity.class));
                    return true;
                case 3:
                    openSetGoalActivity();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void loadFoods() {
        DatabaseHelper db = new DatabaseHelper(this);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Fetch food entries from the database
        foodList.clear();
        foodList.addAll(db.getFoodEntriesForDate(today));
        adapter.notifyDataSetChanged();

        // Calculate total calories
        int totalCalories = 0;
        for (FoodEntry food : foodList) {
            totalCalories += food.getCalories(); // Ensure getCalories() returns the correct value
        }
        totalCaloriesText.setText("Total Calories: " + totalCalories);

        // Fetch and display the goal
        int goal = db.getGoalForDate(today);
        goalText.setText("Goal: " + goal);

        // Update text color based on goal percentage
        if (goal > 0) {
            double percentage = (double) totalCalories / goal;
            totalCaloriesText.setTextColor(getColorForPercentage(percentage));
        }
    }

    private int getColorForPercentage(double percentage) {
        if (percentage > 1) return getResources().getColor(android.R.color.holo_red_dark);
        if (percentage >= 0.7) return getResources().getColor(android.R.color.holo_green_dark);
        return getResources().getColor(android.R.color.holo_orange_dark);
    }

    private void openSetGoalActivity() {
        Intent intent = new Intent(this, SetGoalActivity.class);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        intent.putExtra("selectedDate", today);

        // Log the intent data for debugging
        Log.d("CaloriesActivity", "Launching SetGoalActivity with date: " + today);

        setGoalLauncher.launch(intent);
    }

    private void onDeleteButtonClicked(FoodEntry entry, int position) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteFood(entry.getId());
        loadFoods();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoods(); // Reload data when returning to the activity
    }
}