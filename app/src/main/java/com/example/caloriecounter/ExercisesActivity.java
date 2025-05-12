package com.example.caloriecounter;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExercisesActivity extends AppCompatActivity {

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter adapter;
    private List<ExerciseEntry> exerciseList;
    private FoodDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        TextView currentDate = findViewById(R.id.currentDate);
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);

        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setAdapter(adapter);

        String todayDate = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        currentDate.setText(todayDate);

        dbHelper = new FoodDatabaseHelper(this);

        loadExercisesForToday(todayDate);
    }

    private void loadExercisesForToday(String day) {
        exerciseList.clear();
        exerciseList.addAll(dbHelper.getRoutinesForDay(day));

        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "No routines scheduled for today!", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }
}