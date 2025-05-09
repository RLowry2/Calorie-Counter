package com.example.caloriecounter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SetGoalActivity extends AppCompatActivity {

    private EditText goalEditText;
    private Button saveButton;
    private String selectedDate;
    private FoodDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeUI();
        loadExistingGoal();
        setupSaveButtonListener();
    }

    /**
     * Initializes the UI components and retrieves the selected date.
     */
    private void initializeUI() {
        goalEditText = findViewById(R.id.goalEditText);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new FoodDatabaseHelper(this);

        // Get date from intent, or default to today
        selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate == null) {
            selectedDate = getCurrentDate();
        }

        // Log the selectedDate for debugging
        Log.d("SetGoalActivity", "Selected date: " + selectedDate);
    }

    /**
     * Retrieves the current date in "yyyy-MM-dd" format.
     */
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    /**
     * Loads the existing goal for the selected date into the EditText field.
     */
    private void loadExistingGoal() {
        int currentGoal = dbHelper.getGoalForDate(selectedDate);
        if (currentGoal > 0) {
            goalEditText.setText(String.valueOf(currentGoal));
        } else {
            Log.d("SetGoalActivity", "No goal found for date: " + selectedDate);
            goalEditText.setText(""); // Clear the field if no goal exists
        }
    }

    /**
     * Sets up the Save button's click listener.
     */
    private void setupSaveButtonListener() {
        saveButton.setOnClickListener(v -> saveGoal());
    }

    /**
     * Handles saving the new goal to the database.
     */
    private void saveGoal() {
        String goalText = goalEditText.getText().toString();
        if (isValidInput(goalText)) {
            int goal = Integer.parseInt(goalText);
            dbHelper.setGoalForDate(selectedDate, goal);
            showToast("Goal for " + selectedDate + " set to " + goal + " calories.");
            setResult(RESULT_OK);
            finish();
        } else {
            showToast("Please enter a valid goal.");
        }
    }

    /**
     * Validates the input for the goal field.
     */
    private boolean isValidInput(String input) {
        return !TextUtils.isEmpty(input) && TextUtils.isDigitsOnly(input);
    }

    /**
     * Displays a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}