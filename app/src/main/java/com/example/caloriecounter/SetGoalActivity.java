package com.example.caloriecounter;

import android.os.Bundle;
import android.text.TextUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        // Get date from intent, or default to today
        selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        goalEditText = findViewById(R.id.goalEditText);
        saveButton = findViewById(R.id.saveButton);

        // Load the goal for the selected date from the database
        FoodDatabaseHelper dbHelper = new FoodDatabaseHelper(this);
        int currentGoal = dbHelper.getGoalForDate(selectedDate);
        if (currentGoal > 0) {
            goalEditText.setText(String.valueOf(currentGoal));
        }

        saveButton.setOnClickListener(v -> {
            String goalText = goalEditText.getText().toString();
            if (!TextUtils.isEmpty(goalText)) {
                int goal = Integer.parseInt(goalText);
                dbHelper.setGoalForDate(selectedDate, goal);
                Toast.makeText(this, "Goal for " + selectedDate + " set to " + goal + " calories.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Please enter a valid goal.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
