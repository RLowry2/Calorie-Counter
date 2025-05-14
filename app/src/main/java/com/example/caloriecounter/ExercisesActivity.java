package com.example.caloriecounter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
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

public class ExercisesActivity extends AppCompatActivity {

    private static final String TAG = "ExercisesActivity";

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseEntry> exerciseList;
    private DatabaseHelper dbHelper;
    private TextView currentDayText;

    private Calendar calendar;


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

        // Initialize Calendar for day navigation
        calendar = Calendar.getInstance();
        updateDayText();

        // Load exercises for today's day
        loadExercisesForToday();

        // Set up Previous Day Button
        Button previousDayButton = findViewById(R.id.previousDayButton);
        previousDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            updateDayText();
            loadExercisesForToday();
        });

        // Set up Next Day Button
        Button nextDayButton = findViewById(R.id.nextDayButton);
        nextDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            updateDayText();
            loadExercisesForToday();
        });

        // Find the FAB by ID
        FloatingActionButton fabAddExercise = findViewById(R.id.fabAddExercise);

        // Set the click listener
        fabAddExercise.setOnClickListener(v -> {
            Log.d(TAG, "FAB clicked. Opening Add Exercise dialog.");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
            String currentDay = dateFormat.format(calendar.getTime());
            showAddExerciseDialog(currentDay); // Assuming showAddExerciseDialog is implemented
        });
    }

    private void showAddExerciseDialog(String currentDay) {
        Log.d(TAG, "showAddExerciseDialog: Opening Add Exercise dialog");

        // Fetch exercise names from the database
        List<String> exerciseNames = dbHelper.getExerciseNames();

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);

        // Initialize dialog components
        Spinner exerciseNameSpinner = dialogView.findViewById(R.id.exerciseNameSpinner);
        EditText setsInput = dialogView.findViewById(R.id.setsInput);
        EditText repsInput = dialogView.findViewById(R.id.repsInput);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Populate the Spinner with exercise names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseNameSpinner.setAdapter(adapter);

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
            String selectedExercise = exerciseNameSpinner.getSelectedItem().toString();
            String sets = setsInput.getText().toString().trim();
            String reps = repsInput.getText().toString().trim();

            if (!sets.isEmpty() && !reps.isEmpty()) {
                int setsCount = Integer.parseInt(sets);
                int repsCount = Integer.parseInt(reps);

                // Insert the exercise into the database
                boolean isInserted = dbHelper.insertOrUpdateRoutine(currentDay, selectedExercise, setsCount, repsCount);

                if (isInserted) {
                    Log.d(TAG, "showAddExerciseDialog: Exercise added successfully");
                    Toast.makeText(this, "Exercise added!", Toast.LENGTH_SHORT).show();
                    loadExercisesForToday(currentDay); // Refresh the list
                } else {
                    Log.e(TAG, "showAddExerciseDialog: Failed to add exercise");
                    Toast.makeText(this, "Failed to add exercise!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss(); // Close the dialog
            } else {
                Log.w(TAG, "showAddExerciseDialog: Sets or reps are empty");
                Toast.makeText(this, "Sets and reps cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void updateDayText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String currentDay = dateFormat.format(calendar.getTime());
        currentDayText = findViewById(R.id.currentDayText);
        currentDayText.setText(currentDay);
    }

    private void loadExercisesForToday() {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayForDatabase = dbDateFormat.format(calendar.getTime());

        Log.d(TAG, "loadExercisesForToday: Loading exercises for " + dayForDatabase);
        exerciseList = dbHelper.getExercisesForDay(dayForDatabase);

        // Initialize or update the adapter
        if (adapter == null) {
            adapter = new ExerciseAdapter(exerciseList);
            exerciseRecyclerView.setAdapter(adapter);
        } else {
            adapter.updateExercises(exerciseList);
        }
    }

    private void showPopupMenu(View anchor, String day) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Add Exercise");
        popup.getMenu().add(0, 2, 1, "Clear Exercises");
        popup.getMenu().add(0, 3, 2, "Database");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1: // Add Exercise
                    openAddExerciseDialog(day);
                    return true;
                case 2: // Clear Exercises
                    clearExercisesForToday(day);
                    return true;
                case 3: // Database
                    navigateToDatabase();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void navigateToDatabase() {
        Log.d(TAG, "navigateToDatabase: Navigating to ExerciseNamesActivity");
        Intent intent = new Intent(this, ExerciseNamesActivity.class);
        startActivity(intent);
    }

    private void loadExercisesForToday(String day) {
        Log.d(TAG, "loadExercisesForToday: Loading exercises for " + day);
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getExercisesForDay(day)); // Fetch exercises for the day
        adapter.notifyDataSetChanged();
    }

    private void openAddExerciseDialog(String currentDay) {
        Log.d(TAG, "openAddExerciseDialog: Opening Add Exercise dialog");

        // Fetch exercise names from the database
        List<String> exerciseNames = dbHelper.getExerciseNames();

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);

        // Initialize dialog components
        Spinner exerciseNameSpinner = dialogView.findViewById(R.id.exerciseNameSpinner);
        EditText setsInput = dialogView.findViewById(R.id.setsInput);
        EditText repsInput = dialogView.findViewById(R.id.repsInput);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Populate the Spinner with exercise names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseNameSpinner.setAdapter(adapter);

        // Create an AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // Prevent dismissal on outside tap
                .create();

        // Set up Cancel button
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "openAddExerciseDialog: Cancel action");
            dialog.dismiss(); // Close the dialog
        });

        // Set up Confirm button
        confirmButton.setOnClickListener(v -> {
            String selectedExercise = exerciseNameSpinner.getSelectedItem().toString();
            String sets = setsInput.getText().toString().trim();
            String reps = repsInput.getText().toString().trim();

            if (!sets.isEmpty() && !reps.isEmpty()) {
                int setsCount = Integer.parseInt(sets);
                int repsCount = Integer.parseInt(reps);

                // Insert the exercise into the database
                boolean isInserted = dbHelper.insertOrUpdateRoutine(currentDay, selectedExercise, setsCount, repsCount);

                if (isInserted) {
                    Log.d(TAG, "openAddExerciseDialog: Exercise added successfully");
                    Toast.makeText(this, "Exercise added!", Toast.LENGTH_SHORT).show();
                    loadExercisesForToday(currentDay); // Refresh the list
                } else {
                    Log.e(TAG, "openAddExerciseDialog: Failed to add exercise");
                    Toast.makeText(this, "Failed to add exercise!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss(); // Close the dialog
            } else {
                Log.w(TAG, "openAddExerciseDialog: Sets or reps are empty");
                Toast.makeText(this, "Sets and reps cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void clearExercisesForToday(String day) {
        Log.d(TAG, "clearExercisesForToday: Clearing exercises for " + day);
        int rowsDeleted = dbHelper.clearExercisesForDay(day);

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