package com.example.caloriecounter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseNamesActivity extends AppCompatActivity {

    private static final String TAG = "ExerciseNamesActivity";

    private RecyclerView exerciseNamesRecyclerView;
    private ExerciseNamesAdapter adapter;
    private List<String> exerciseNamesList;
    private DatabaseHelper dbHelper;
    private Button addExerciseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing ExerciseNamesActivity");
        setContentView(R.layout.activity_exercise_names);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView and list
        exerciseNamesList = new ArrayList<>();
        exerciseNamesRecyclerView = findViewById(R.id.exerciseNamesRecyclerView);
        exerciseNamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseNamesAdapter(exerciseNamesList);
        exerciseNamesRecyclerView.setAdapter(adapter);

        // Load exercise names from the database
        loadExerciseNames();

        // Add Exercise Button
        addExerciseButton = findViewById(R.id.addExerciseButton);
        addExerciseButton.setOnClickListener(v -> openAddExerciseDialog());
    }

    private void loadExerciseNames() {
        Log.d(TAG, "loadExerciseNames: Fetching exercise names from the database");
        exerciseNamesList.clear();
        exerciseNamesList.addAll(dbHelper.getExerciseNames());

        Log.d(TAG, "loadExerciseNames: Loaded " + exerciseNamesList.size() + " exercises");
        for (String name : exerciseNamesList) {
            Log.d(TAG, "loadExerciseNames: Exercise: " + name);
        }

        adapter.notifyDataSetChanged();
    }

    private void openAddExerciseDialog() {
        Log.d(TAG, "openAddExerciseDialog: Opening Add Exercise dialog");

        // Create an AlertDialog for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Exercise");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setHint("Exercise Name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String exerciseName = input.getText().toString().trim();

            if (!exerciseName.isEmpty()) {
                boolean isSaved = dbHelper.insertExerciseName(exerciseName);

                if (isSaved) {
                    Log.d(TAG, "openAddExerciseDialog: Exercise added successfully");
                    Toast.makeText(this, "Exercise added!", Toast.LENGTH_SHORT).show();
                    loadExerciseNames(); // Refresh the list
                } else {
                    Log.e(TAG, "openAddExerciseDialog: Failed to add exercise");
                    Toast.makeText(this, "Failed to add exercise!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "openAddExerciseDialog: Exercise name is empty");
                Toast.makeText(this, "Exercise name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Log.d(TAG, "openAddExerciseDialog: Dialog canceled");
            dialog.cancel();
        });

        builder.show();
    }
}