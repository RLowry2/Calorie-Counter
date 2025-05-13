package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    private static final String TAG = "ExercisesActivity";

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseEntry> exerciseList;
    private DatabaseHelper dbHelper;
    private Button addExerciseButton;
    private Button clearExercisesButton;
    private Button databaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        Log.d(TAG, "onCreate: Initializing ExercisesActivity");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView and list
        exerciseList = new ArrayList<>();
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setAdapter(adapter);

        // Load exercises for today's day (e.g., "Monday")
        String currentDay = "Monday"; // Replace with logic to get the actual day
        loadExercisesForToday(currentDay);

        // Add Exercise Button
        addExerciseButton = findViewById(R.id.addExerciseButton);
        addExerciseButton.setOnClickListener(v -> openAddExerciseDialog(currentDay));

        // Clear Exercises Button
        clearExercisesButton = findViewById(R.id.clearExercisesButton);
        clearExercisesButton.setOnClickListener(v -> clearExercisesForToday(currentDay));

        // Database Button
        databaseButton = findViewById(R.id.databaseButton);
        databaseButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Database button clicked. Navigating to ExerciseNamesActivity");
            Intent intent = new Intent(ExercisesActivity.this, ExerciseNamesActivity.class);
            startActivity(intent);
        });
    }

    private void loadExercisesForToday(String day) {
        Log.d(TAG, "loadExercisesForToday: Loading exercises for " + day);
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getRoutinesForDay(day)); // Fetch exercises for the day
        adapter.notifyDataSetChanged();
    }

    private void openAddExerciseDialog(String currentDay) {
        Log.d(TAG, "openAddExerciseDialog: Opening Add Exercise dialog");
        // Implementation of Add Exercise Dialog
    }

    private void clearExercisesForToday(String day) {
        Log.d(TAG, "clearExercisesForToday: Clearing exercises for " + day);
        int rowsDeleted = dbHelper.clearRoutinesForDay(day);

        if (rowsDeleted > 0) {
            Log.d(TAG, "clearExercisesForToday: Cleared " + rowsDeleted + " exercises");
            Toast.makeText(this, "Cleared all exercises for " + day, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "clearExercisesForToday: No exercises to clear for " + day);
            Toast.makeText(this, "No exercises to clear for " + day, Toast.LENGTH_SHORT).show();
        }

        loadExercisesForToday(day); // Refresh the RecyclerView after clearing
    }
}