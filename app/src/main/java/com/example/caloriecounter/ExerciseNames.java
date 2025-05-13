package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseNames extends AppCompatActivity {

    private FoodDatabaseHelper dbHelper;
    private Button saveRoutineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_names);

        // Initialize database helper
        dbHelper = new FoodDatabaseHelper(this);

        // Save Routine Button
        saveRoutineButton = findViewById(R.id.saveRoutineButton);
        saveRoutineButton.setOnClickListener(v -> openCreateRoutineDialog());
    }

    private void openCreateRoutineDialog() {
        // Create an AlertDialog for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Routine");

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