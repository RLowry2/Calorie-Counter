package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEntryActivity extends AppCompatActivity {

    private EditText foodNameEditText, foodCaloriesEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get date from intent, or default to today
        selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        foodNameEditText = findViewById(R.id.foodNameEditText);
        foodCaloriesEditText = findViewById(R.id.foodCaloriesEditText);
        saveButton = findViewById(R.id.saveButton);

        dbHelper = new DatabaseHelper(this); // Initialize the database helper

        saveButton.setOnClickListener(v -> saveFoodEntry());
    }

    // In AddEntryActivity.java
    private void saveFoodEntry() {
        String foodName = foodNameEditText.getText().toString();
        String foodCaloriesStr = foodCaloriesEditText.getText().toString();

        if (validateInput(foodName, foodCaloriesStr)) {
            int foodCalories = Integer.parseInt(foodCaloriesStr);

            // Insert the food entry into the database
            dbHelper.insertFood(foodName, foodCalories, selectedDate);

            // Return to the CalendarActivity after saving
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedDate", selectedDate);  // Send the selected date back
            setResult(RESULT_OK, resultIntent);
            finish();  // Close the activity
        } else {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.isEmpty()) return false;
        }
        return true;
    }
}