package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseEntry> exerciseList;
    private FoodDatabaseHelper dbHelper;
    private Button addExerciseButton;
    private Button clearExercisesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // Initialize database helper
        dbHelper = new FoodDatabaseHelper(this);

        // Initialize RecyclerView and list
        exerciseList = new ArrayList<>();
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setAdapter(adapter);

        // Load exercises for today's day (e.g., "Monday")
        String currentDay = "Monday"; // Replace with logic to get the actual day
        loadExercisesForToday(currentDay);

        // Create Routine Button
        addExerciseButton = findViewById(R.id.addExerciseButton);
        addExerciseButton.setOnClickListener(v -> openAddExerciseDialog());

        // Clear Exercises Button
        clearExercisesButton = findViewById(R.id.clearExercisesButton);
        clearExercisesButton.setOnClickListener(v -> {
            clearExercisesForToday(currentDay);
        });
    }

    // Load exercises from the database for the given day
    private void loadExercisesForToday(String day) {
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getRoutinesForDay(day));

        Log.d("ExercisesActivity", "Loaded exercises for " + day + ": " + exerciseList.size());
        for (ExerciseEntry entry : exerciseList) {
            Log.d("ExercisesActivity", "Exercise: " + entry.getName() + ", Sets: " + entry.getSets() + ", Reps: " + entry.getReps());
        }

        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "No routines scheduled for today!", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    // Clear all exercises for the given day
    private void clearExercisesForToday(String day) {
        int rowsDeleted = dbHelper.clearRoutinesForDay(day);

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Cleared all exercises for " + day, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No exercises to clear for " + day, Toast.LENGTH_SHORT).show();
        }

        loadExercisesForToday(day); // Refresh the RecyclerView after clearing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh the RecyclerView after a routine is created
            String currentDay = "Monday"; // Replace with logic to get the actual day
            loadExercisesForToday(currentDay);
        }
    }

    private void openAddExerciseDialog() {
        // Create an AlertDialog for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Exercise");

        // Set up the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputExercise = new EditText(this);
        inputExercise.setHint("Exercise Name");
        layout.addView(inputExercise);

        final EditText inputSets = new EditText(this);
        inputSets.setHint("Sets");
        inputSets.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputSets);

        final EditText inputReps = new EditText(this);
        inputReps.setHint("Reps");
        inputReps.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputReps);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String exerciseName = inputExercise.getText().toString();
            int sets = Integer.parseInt(inputSets.getText().toString());
            int reps = Integer.parseInt(inputReps.getText().toString());

            // Save to the database
            boolean isSaved = dbHelper.insertOrUpdateRoutine("Monday", exerciseName, sets, reps);

            if (isSaved) {
                Log.d("CreateRoutineActivity", "Routine saved successfully: " + exerciseName);
                Toast.makeText(this, "Routine Saved!", Toast.LENGTH_SHORT).show();

                // Return success result to ExercisesActivity
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish(); // Close CreateRoutineActivity
            } else {
                Log.e("CreateRoutineActivity", "Failed to save routine: " + exerciseName);
                Toast.makeText(this, "Failed to save routine!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}