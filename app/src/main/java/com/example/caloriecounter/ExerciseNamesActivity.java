package com.example.caloriecounter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        adapter = new ExerciseNamesAdapter(exerciseNamesList, this::showEditExerciseDialog);
        exerciseNamesRecyclerView.setAdapter(adapter);

        Button addExerciseButton = findViewById(R.id.addExerciseButton);
        addExerciseButton.setOnClickListener(v -> showAddExerciseDialog());


        // Load exercise names from the database
        loadExerciseNames();
    }

    private void loadExerciseNames() {
        Log.d(TAG, "loadExerciseNames: Fetching exercise names from the database");
        exerciseNamesList.clear();
        exerciseNamesList.addAll(dbHelper.getExerciseNames());

        Log.d(TAG, "loadExerciseNames: Loaded " + exerciseNamesList.size() + " exercises");
        adapter.notifyDataSetChanged();
    }

    private void showEditExerciseDialog(String oldName) {
        Log.d(TAG, "showEditExerciseDialog: Editing exercise - " + oldName);

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_exercise, null);

        // Initialize dialog components
        EditText editExerciseName = dialogView.findViewById(R.id.editExerciseName);
        Button deleteButton = dialogView.findViewById(R.id.deleteButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Pre-fill the EditText with the current exercise name
        editExerciseName.setText(oldName);

        // Create an AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // Prevent dismissal on outside tap
                .create();

        // Set up Delete button
        deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "showEditExerciseDialog: Deleting exercise - " + oldName);
            boolean isDeleted = dbHelper.deleteExerciseName(oldName);

            if (isDeleted) {
                Log.d(TAG, "showEditExerciseDialog: Exercise deleted successfully");
                Toast.makeText(this, "Exercise deleted!", Toast.LENGTH_SHORT).show();
                loadExerciseNames(); // Refresh the list
            } else {
                Log.e(TAG, "showEditExerciseDialog: Failed to delete exercise");
                Toast.makeText(this, "Failed to delete exercise!", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss(); // Close the dialog
        });

        // Set up Cancel button
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "showEditExerciseDialog: Cancel action");
            dialog.dismiss(); // Close the dialog
        });

        // Set up Confirm button
        confirmButton.setOnClickListener(v -> {
            String newName = editExerciseName.getText().toString().trim();

            if (!newName.isEmpty()) {
                boolean isUpdated = dbHelper.updateExerciseName(oldName, newName);

                if (isUpdated) {
                    Log.d(TAG, "showEditExerciseDialog: Exercise updated successfully");
                    Toast.makeText(this, "Exercise updated!", Toast.LENGTH_SHORT).show();
                    loadExerciseNames(); // Refresh the list
                } else {
                    Log.e(TAG, "showEditExerciseDialog: Failed to update exercise");
                    Toast.makeText(this, "Failed to update exercise!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss(); // Close the dialog
            } else {
                Log.w(TAG, "showEditExerciseDialog: New exercise name is empty");
                Toast.makeText(this, "Exercise name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void showAddExerciseDialog() {
        Log.d(TAG, "showAddExerciseDialog: Opening Add Exercise dialog");

        // Inflate a simple layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_database_add_exercise, null);

        // Initialize dialog components
        EditText exerciseNameInput = dialogView.findViewById(R.id.exerciseNameInput);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        // Create an AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // Prevent dismissal on outside tap
                .create();

        // Set up Cancel button
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "showAddExerciseDialog: Cancel action");
            dialog.dismiss(); // Close the dialog
        });

        // Set up Save button
        saveButton.setOnClickListener(v -> {
            String exerciseName = exerciseNameInput.getText().toString().trim();

            if (!exerciseName.isEmpty()) {
                boolean isAdded = dbHelper.insertExerciseName(exerciseName);

                if (isAdded) {
                    Log.d(TAG, "showAddExerciseDialog: Exercise added successfully");
                    Toast.makeText(this, "Exercise added!", Toast.LENGTH_SHORT).show();
                    loadExerciseNames(); // Refresh the list
                } else {
                    Log.e(TAG, "showAddExerciseDialog: Failed to add exercise");
                    Toast.makeText(this, "Failed to add exercise!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss(); // Close the dialog
            } else {
                Toast.makeText(this, "Exercise name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}