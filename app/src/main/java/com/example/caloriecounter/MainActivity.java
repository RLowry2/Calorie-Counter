package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private FoodAdapter adapter;
    private List<FoodEntry> foodList;
    private TextView todaysDate;

    // Launcher for AddEntryActivity:
    private final ActivityResultLauncher<Intent> addEntryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods();
                }
            });

    // Launcher for CalendarActivity:
    private final ActivityResultLauncher<Intent> calendarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(foodList, this::onDeleteButtonClicked);
        foodRecyclerView.setAdapter(adapter);

        // Today's date
        todaysDate = findViewById(R.id.todaysDate);
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        todaysDate.setText(currentDate);

        // FAB menu
        FloatingActionButton fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(v -> showPopupMenu(v));

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
                    promptSetGoal();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void loadFoods() {
        FoodDatabaseHelper db = new FoodDatabaseHelper(this);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        foodList.clear();
        foodList.addAll(db.getFoodEntriesForDate(today));

        int total = 0;
        for (FoodEntry e : foodList) total += e.getCalories();

        int goal = db.getGoalForDate(today);
        TextView totalTxt = findViewById(R.id.totalCaloriesText);
        totalTxt.setText("Total Calories: " + total + "/" + goal);

        if (goal > 0) {
            double pct = (double) total / goal;
            totalTxt.setTextColor(getColorForPercentage(pct));
        }

        adapter.notifyDataSetChanged();
    }

    private int getColorForPercentage(double percentage) {
        if (percentage > 1) return getResources().getColor(android.R.color.holo_red_dark);
        if (percentage >= .7) return getResources().getColor(android.R.color.holo_green_dark);
        return getResources().getColor(android.R.color.holo_orange_dark);
    }

    private void promptSetGoal() {
        final String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle("Set daily calorie goal");
        EditText in = new EditText(this);
        in.setInputType(InputType.TYPE_CLASS_NUMBER);
        int existing = new FoodDatabaseHelper(this).getGoalForDate(today);
        if (existing > 0) in.setText(String.valueOf(existing));
        b.setView(in)
                .setPositiveButton("Save", (d, w) -> {
                    String s = in.getText().toString().trim();
                    if (s.isEmpty()) {
                        Toast.makeText(this, "Enter a number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int goal = Integer.parseInt(s);
                    new FoodDatabaseHelper(this).insertOrUpdateGoal(today, goal);
                    loadFoods();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onDeleteButtonClicked(FoodEntry entry, int pos) {
        new FoodDatabaseHelper(this).deleteFood(entry.getId());
        loadFoods();
    }
}
