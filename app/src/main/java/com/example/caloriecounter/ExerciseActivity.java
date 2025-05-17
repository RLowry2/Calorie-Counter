package com.example.caloriecounter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExerciseActivity extends AppCompatActivity implements ExerciseAdapter.OnExerciseLongClickListener {

    private static final String TAG = "ExercisesActivity";

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseEntry> exerciseList;
    private DatabaseHelper dbHelper;
    private Calendar calendar; // For day navigation
    private String currentDay; // Current day string

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
        adapter = new ExerciseAdapter(exerciseList, this); // Pass this as the long-click listener
        exerciseRecyclerView.setAdapter(adapter);

        // Initialize Calendar for day navigation
        calendar = Calendar.getInstance();
        updateDayText();

        // Load exercises for today's day
        loadExercisesForToday(currentDay);

        // Set up Previous Day Button
        ImageButton previousDayButton = findViewById(R.id.previousDayButton);
        previousDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, -1); // Move back one day
            updateDayText();
            loadExercisesForToday(currentDay);
        });

        // Set up Next Day Button
        ImageButton nextDayButton = findViewById(R.id.nextDayButton);
        nextDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Move forward one day
            updateDayText();
            loadExercisesForToday(currentDay);
        });

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
                    openExerciseNamesActivity();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void openExerciseNamesActivity() {
        Log.d(TAG, "openExerciseNamesActivity: Starting ExerciseNamesActivity");
        Intent intent = new Intent(this, ExerciseNamesActivity.class);
        startActivity(intent);
    }

    private void updateDayText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        currentDay = dateFormat.format(calendar.getTime()); // Format the current day
        TextView currentDayText = findViewById(R.id.currentDayText);
        currentDayText.setText(currentDay); // Update the TextView with the current day
    }

    private void loadExercisesForToday(String day) {
        Log.d(TAG, "loadExercisesForToday: Loading exercises for " + day);
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getExercisesForDay(day)); // Fetch exercises for the day
        adapter.notifyDataSetChanged();
    }

    private void showAddExerciseDialog(String currentDay) {
        Log.d(TAG, "showAddExerciseDialog: Opening Add Exercise dialog");

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);

        // Initialize dialog components
        AutoCompleteTextView exerciseNameAutocomplete = dialogView.findViewById(R.id.exercise_name_autocomplete);
        EditText setsInput = dialogView.findViewById(R.id.setsInput);
        EditText repsInput = dialogView.findViewById(R.id.repsInput);
        EditText weightInput = dialogView.findViewById(R.id.weightInput); // New weight field
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Fetch exercise names from the database
        List<String> exerciseNames = dbHelper.getExerciseNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, exerciseNames);
        exerciseNameAutocomplete.setAdapter(adapter);

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

        // Set up Confirm button
        confirmButton.setOnClickListener(v -> {
            String selectedExercise = exerciseNameAutocomplete.getText().toString().trim();
            String sets = setsInput.getText().toString().trim();
            String reps = repsInput.getText().toString().trim();
            String weight = weightInput.getText().toString().trim(); // New weight input

            if (!sets.isEmpty() && !reps.isEmpty() && !weight.isEmpty()) {
                // Add the exercise to the database
                boolean isAdded = dbHelper.addExerciseToDay(
                        currentDay,
                        selectedExercise,
                        Integer.parseInt(sets),
                        Integer.parseInt(reps),
                        Double.parseDouble(weight), // Save weight
                        "lbs" // Default unit
                );
                if (isAdded) {
                    Log.d(TAG, "showAddExerciseDialog: Exercise added successfully");
                    Toast.makeText(this, "Exercise added!", Toast.LENGTH_SHORT).show();
                    loadExercisesForToday(currentDay); // Refresh the list
                } else {
                    Log.e(TAG, "showAddExerciseDialog: Failed to add exercise");
                    Toast.makeText(this, "Failed to add exercise!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss(); // Close the dialog
            } else {
                Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
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

    @Override
    public void onExerciseLongClicked(int position) {
        // Confirm deletion with an AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Exercise")
                .setMessage("Are you sure you want to delete this exercise?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the exercise from the database
                    ExerciseEntry exerciseToDelete = exerciseList.get(position);
                    boolean isDeleted = dbHelper.deleteExercise(exerciseToDelete);

                    if (isDeleted) {
                        Log.d(TAG, "onExerciseLongClicked: Exercise deleted successfully");
                        Toast.makeText(this, "Exercise deleted!", Toast.LENGTH_SHORT).show();
                        loadExercisesForToday(currentDay); // Refresh the list
                    } else {
                        Log.e(TAG, "onExerciseLongClicked: Failed to delete exercise");
                        Toast.makeText(this, "Failed to delete exercise!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}