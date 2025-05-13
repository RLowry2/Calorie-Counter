package com.example.caloriecounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class RoutineDayFragment extends Fragment {

    private static final String ARG_DAY = "day";

    public static RoutineDayFragment newInstance(String day) {
        RoutineDayFragment fragment = new RoutineDayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    private LinearLayout exerciseContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine_day, container, false);

        // Initialize the exercise container
        exerciseContainer = view.findViewById(R.id.exerciseContainer);
        view.findViewById(R.id.addExerciseButton).setOnClickListener(v -> addExerciseEntry(inflater));

        return view;
    }

    private void addExerciseEntry(LayoutInflater inflater) {
        // Add a new exercise entry dynamically
        View exerciseEntry = inflater.inflate(R.layout.item_exercise_entry, exerciseContainer, false);
        exerciseContainer.addView(exerciseEntry);
    }

    public List<ExerciseEntry> getExercises() {
        List<ExerciseEntry> exercises = new ArrayList<>();
        for (int i = 0; i < exerciseContainer.getChildCount(); i++) {
            View exerciseEntry = exerciseContainer.getChildAt(i);
            EditText nameInput = exerciseEntry.findViewById(R.id.exerciseName);
            EditText setsInput = exerciseEntry.findViewById(R.id.exerciseSets);
            EditText repsInput = exerciseEntry.findViewById(R.id.exerciseReps);

            String name = nameInput.getText().toString().trim();
            String setsStr = setsInput.getText().toString().trim();
            String repsStr = repsInput.getText().toString().trim();

            if (name.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty()) {
                // Skip this entry if any field is empty
                continue;
            }

            try {
                int sets = Integer.parseInt(setsStr);
                int reps = Integer.parseInt(repsStr);
                exercises.add(new ExerciseEntry(name, sets, reps));
            } catch (NumberFormatException e) {
                // Log or handle invalid number input
                e.printStackTrace();
            }
        }
        return exercises;
    }
}