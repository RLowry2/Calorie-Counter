package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
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

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView totalCaloriesText;
    private TextView goalText;
    private RecyclerView foodRecyclerView;
    private FoodAdapter adapter;
    private List<FoodEntry> foodList;
    private String selectedDate;

    // Launchers for add-entry and set-goal flows:
    private final ActivityResultLauncher<Intent> addEntryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    updateUIForSelectedDate(selectedDate);
                    setResult(RESULT_OK);
                }
            });

    private final ActivityResultLauncher<Intent> setGoalLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    updateUIForSelectedDate(selectedDate);
                    setResult(RESULT_OK);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        calendarView      = findViewById(R.id.calendarView);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        goalText          = findViewById(R.id.goalText);
        foodRecyclerView  = findViewById(R.id.foodRecyclerView);

        // RecyclerView + adapter, with delete callback:
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(foodList, (foodEntry, position) -> {
            // Delete from DB + refresh UI + bubble result
            new FoodDatabaseHelper(this).deleteFood(foodEntry.getId());
            updateUIForSelectedDate(selectedDate);
            setResult(RESULT_OK);
        });
        foodRecyclerView.setAdapter(adapter);

        // Default date = today:
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        updateUIForSelectedDate(selectedDate);

        // Date change listener:
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            propagateGoalIfNeeded(selectedDate); // Propagate goal if selected date is in the future
            updateUIForSelectedDate(selectedDate);
        });

        // FAB menu:
        FloatingActionButton fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(v -> showPopupMenu(v));
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Add Entry");
        popup.getMenu().add(0, 2, 1, "Edit Goal");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                Intent i1 = new Intent(this, AddEntryActivity.class);
                i1.putExtra("selectedDate", selectedDate);
                addEntryLauncher.launch(i1);
                return true;
            } else if (item.getItemId() == 2) {
                Intent i2 = new Intent(this, SetGoalActivity.class);
                i2.putExtra("selectedDate", selectedDate);

                // Log the intent data for debugging
                Log.d("CalendarActivity", "Launching SetGoalActivity with date: " + selectedDate);
                setGoalLauncher.launch(i2);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void updateUIForSelectedDate(String date) {
        FoodDatabaseHelper db = new FoodDatabaseHelper(this);

        foodList.clear();
        foodList.addAll(db.getFoodEntriesForDate(date));

        int total = 0;
        for (FoodEntry e : foodList) total += e.getCalories();
        totalCaloriesText.setText("Total Calories: " + total);

        int goal = db.getGoalForDate(date);
        goalText.setText(goal > 0 ? "Goal: " + goal : "Goal: Not Set");

        if (goal > 0) {
            double pct = (double) total / goal;
            totalCaloriesText.setTextColor(getColorForPercentage(pct));
        }

        adapter.updateFoodList(foodList);
    }

    private int getColorForPercentage(double percentage) {
        if (percentage > 1) return getResources().getColor(android.R.color.holo_red_dark);
        if (percentage >= .7) return getResources().getColor(android.R.color.holo_green_dark);
        return getResources().getColor(android.R.color.holo_orange_dark);
    }

    private void propagateGoalIfNeeded(String selectedDate) {
        FoodDatabaseHelper db = new FoodDatabaseHelper(this);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Only propagate if the selected date is in the future
        if (selectedDate.compareTo(today) > 0) {
            db.propagateGoalToFutureDate(today, selectedDate);
        }
    }
}