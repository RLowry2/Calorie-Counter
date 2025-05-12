package com.example.caloriecounter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class CreateRoutineActivity extends AppCompatActivity {

    private RoutinePagerAdapter adapter;
    private FoodDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_routine);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        adapter = new RoutinePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        dbHelper = new FoodDatabaseHelper(this);

        Button saveRoutineButton = findViewById(R.id.saveRoutineButton);
        saveRoutineButton.setOnClickListener(v -> saveRoutines());
    }

    private void saveRoutines() {
        for (int i = 0; i < adapter.getCount(); i++) {
            RoutineDayFragment fragment = (RoutineDayFragment) adapter.getItem(i);
            String day = adapter.getPageTitle(i).toString();
            List<ExerciseEntry> exercises = fragment.getExercises();

            for (ExerciseEntry exercise : exercises) {
                dbHelper.insertOrUpdateRoutine(day, exercise.getName(), exercise.getSets(), exercise.getReps());
            }
        }

        Toast.makeText(this, "Routine saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}