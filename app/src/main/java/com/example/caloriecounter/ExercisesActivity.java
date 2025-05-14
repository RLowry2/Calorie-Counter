package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    private static final String TAG = "ExercisesActivity";

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseEntry> exerciseList;
    private DatabaseHelper dbHelper;
    private String currentDay = "Monday"; // Placeholder, replace with actual logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        Log.d(TAG, "onCreate: Initializing ExercisesActivity");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView and list
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setAdapter(adapter);

        // Load exercises for today's day
        loadExercisesForToday(currentDay);

        // FAB menu
        FloatingActionButton fabMenu = findViewById(R.id.fabAddExercise);
        fabMenu.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Add Exercise");
        popup.getMenu().add(0, 2, 1, "Clear Exercises");
        popup.getMenu().add(0, 3, 2, "Show Database");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    Log.d(TAG, "Menu clicked: Add Exercise");
                    showAddExerciseDialog(currentDay);
                    return true;
                case 2:
                    Log.d(TAG, "Menu clicked: Clear Exercises");
                    clearExercisesForToday(currentDay);
                    return true;
                case 3:
                    Log.d(TAG, "Menu clicked: Show Database");
                    Intent intent = new Intent(this, ExerciseNamesActivity.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void loadExercisesForToday(String day) {
        Log.d(TAG, "loadExercisesForToday: Loading exercises for " + day);
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getExercisesForDay(day)); // Fetch exercises for the day
        adapter.notifyDataSetChanged();
    }

    private void showAddExerciseDialog(String currentDay) {
        Log.d(TAG, "showAddExerciseDialog: Opening Add Exercise dialog");
        // Implementation of Add Exercise Dialog
    }

    private void clearExercisesForToday(String day) {
        Log.d(TAG, "clearExercisesForToday: Clearing exercises for " + day);
        int rowsDeleted = dbHelper.clearExercisesForDay(day);

        if (rowsDeleted > 0) {
            Log.d(TAG, "clearExercisesForToday: Cleared " + rowsDeleted + " exercises");
        } else {
            Log.d(TAG, "clearExercisesForToday: No exercises to clear for " + day);
        }

        loadExercisesForToday(day); // Refresh the RecyclerView after clearing
    }
}