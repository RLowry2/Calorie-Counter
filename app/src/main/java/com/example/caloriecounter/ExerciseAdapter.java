package com.example.caloriecounter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<ExerciseEntry> exerciseList;
    private final OnExerciseLongClickListener longClickListener;
    private final HashMap<Integer, String[]> setWeightsMap; // To store weights for each set
    private final HashMap<Integer, String[]> setUnitsMap;   // To store units for each set

    // Constructor with the long click listener
    public ExerciseAdapter(List<ExerciseEntry> exerciseList, OnExerciseLongClickListener longClickListener) {
        this.exerciseList = exerciseList;
        this.longClickListener = longClickListener;
        this.setWeightsMap = new HashMap<>();
        this.setUnitsMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseEntry exercise = exerciseList.get(position);

        // Set exercise name
        holder.exerciseName.setText(exercise.getName());

        // Dynamically set the total sets and reps
        String setsRepsText = exercise.getTotalSets() + " Sets x " + exercise.getRepsPerSet() + " Reps";
        holder.exerciseSetsReps.setText(setsRepsText);

        // Initialize the weights and units arrays for the exercise if not already done
        if (!setWeightsMap.containsKey(position)) {
            String[] initialWeights = new String[exercise.getTotalSets()];
            String[] initialUnits = new String[exercise.getTotalSets()];
            for (int i = 0; i < initialWeights.length; i++) {
                initialWeights[i] = String.valueOf(exercise.getWeight()); // Default weight
                initialUnits[i] = exercise.getUnit(); // Default unit
            }
            setWeightsMap.put(position, initialWeights);
            setUnitsMap.put(position, initialUnits);
        }

        // Clear any existing views in the sets container (to handle recycling)
        holder.setsContainer.removeAllViews();

        // Dynamically add checkboxes, editable weight inputs, and spinners for each set
        String[] weightsForExercise = setWeightsMap.get(position);
        String[] unitsForExercise = setUnitsMap.get(position);
        for (int i = 0; i < exercise.getTotalSets(); i++) {
            // Create a layout to hold the checkbox, weight input, and spinner
            LinearLayout setLayout = new LinearLayout(holder.setsContainer.getContext());
            setLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Create the checkbox for the set
            CheckBox setCheckbox = new CheckBox(holder.setsContainer.getContext());
            setCheckbox.setText("Set " + (i + 1));
            setCheckbox.setChecked(false); // Default to unchecked

            // Create the editable weight input
            EditText weightInput = new EditText(holder.setsContainer.getContext());
            weightInput.setText(weightsForExercise[i]);
            weightInput.setHint("Enter weight");
            weightInput.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            weightInput.setPadding(16, 0, 16, 0);

            // Listener to update the weight in the map
            int finalI = i;
            weightInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    weightsForExercise[finalI] = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Create the spinner for the unit selector
            Spinner unitSpinner = new Spinner(holder.setsContainer.getContext());
            unitSpinner.setAdapter(new android.widget.ArrayAdapter<>(
                    holder.setsContainer.getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    holder.setsContainer.getResources().getStringArray(R.array.weight_units)
            ));
            unitSpinner.setSelection(unitsForExercise[finalI].equals("kg") ? 1 : 0); // Default to lbs
            unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    unitsForExercise[finalI] = position == 0 ? "lbs" : "kg";
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Add the checkbox, weight input, and spinner to the set layout
            setLayout.addView(setCheckbox);
            setLayout.addView(weightInput);
            setLayout.addView(unitSpinner);

            // Add the set layout to the sets container
            holder.setsContainer.addView(setLayout);
        }

        // Listener for parent checkbox (exercise)
        holder.exerciseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // If parent checkbox is checked, mark all sets as complete
            for (int i = 0; i < holder.setsContainer.getChildCount(); i++) {
                View child = holder.setsContainer.getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout childLayout = (LinearLayout) child;
                    for (int j = 0; j < childLayout.getChildCount(); j++) {
                        View grandChild = childLayout.getChildAt(j);
                        if (grandChild instanceof CheckBox) {
                            ((CheckBox) grandChild).setChecked(isChecked);
                        }
                    }
                }
            }
        });

        // Long press to delete the exercise
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onExerciseLongClicked(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseSetsReps;
        CheckBox exerciseCheckbox;
        LinearLayout setsContainer;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseSetsReps = itemView.findViewById(R.id.exerciseSetsReps);
            exerciseCheckbox = itemView.findViewById(R.id.exerciseCheckbox);
            setsContainer = itemView.findViewById(R.id.setsContainer);
        }
    }

    // Callback interface for long click
    public interface OnExerciseLongClickListener {
        void onExerciseLongClicked(int position);
    }
}