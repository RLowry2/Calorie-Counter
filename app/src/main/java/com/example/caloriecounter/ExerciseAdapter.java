package com.example.caloriecounter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private final HashMap<Integer, String[]> setWeightsMap; // Stores weights for each set
    private final HashMap<Integer, String[]> setUnitsMap;   // Stores units for each set
    private final HashMap<Integer, Boolean[]> setCheckedMap; // Stores checkbox state for each set

    public ExerciseAdapter(List<ExerciseEntry> exerciseList, OnExerciseLongClickListener longClickListener) {
        this.exerciseList = exerciseList;
        this.longClickListener = longClickListener;
        this.setWeightsMap = new HashMap<>();
        this.setUnitsMap = new HashMap<>();
        this.setCheckedMap = new HashMap<>();
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

        // Set total sets and reps
        String setsRepsText = exercise.getTotalSets() + " Sets x " + exercise.getRepsPerSet() + " Reps";
        holder.exerciseSetsReps.setText(setsRepsText);

        // Handle long-click events
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onExerciseLongClicked(position);
                return true;
            }
            return false;
        });

        // Initialize the weights, units, and checkbox states for the exercise
        if (!setWeightsMap.containsKey(position)) {
            int totalSets = exercise.getTotalSets();
            String[] weights = new String[totalSets];
            String[] units = new String[totalSets];
            Boolean[] checkedStates = new Boolean[totalSets];
            for (int i = 0; i < totalSets; i++) {
                weights[i] = String.valueOf(exercise.getWeight()); // Default weight
                units[i] = "lbs"; // Default to lbs
                checkedStates[i] = false; // Default to unchecked
            }
            setWeightsMap.put(position, weights);
            setUnitsMap.put(position, units);
            setCheckedMap.put(position, checkedStates);
        }

        // Clear any existing views in the sets container (to handle recycling)
        holder.setsContainer.removeAllViews();

        // Dynamically add a checkbox, weight input, and unit spinner for each set
        String[] weightsForExercise = setWeightsMap.get(position);
        String[] unitsForExercise = setUnitsMap.get(position);
        Boolean[] checkedStatesForExercise = setCheckedMap.get(position);
        for (int i = 0; i < exercise.getTotalSets(); i++) {
            // Create a layout to hold the checkbox, weight input, and spinner
            LinearLayout setLayout = new LinearLayout(holder.setsContainer.getContext());
            setLayout.setOrientation(LinearLayout.HORIZONTAL);
            setLayout.setPadding(0, 24, 0, 24); // Add vertical padding between rows

            // Create the checkbox
            CheckBox setCheckbox = new CheckBox(holder.setsContainer.getContext());
            setCheckbox.setText("Set " + (i + 1));
            setCheckbox.setChecked(checkedStatesForExercise[i]);

            // Listener to update the checkbox state
            int finalI = i;
            setCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkedStatesForExercise[finalI] = isChecked;
            });

            // Create the weight input
            EditText weightInput = new EditText(holder.setsContainer.getContext());
            weightInput.setText(weightsForExercise[i]);
            weightInput.setHint("Weight");
            weightInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            weightInput.setBackground(holder.setsContainer.getContext().getResources().getDrawable(R.drawable.edit_text_box));
            weightInput.setPadding(16, 8, 16, 8);

            // Add margin between checkbox and weight input
            LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(
                    200,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            weightParams.setMarginStart(32); // Add horizontal margin
            weightInput.setLayoutParams(weightParams);

            // Listener to update the weight in the map
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

            // Create the spinner for unit selection
            Spinner unitSpinner = new Spinner(holder.setsContainer.getContext());
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    holder.setsContainer.getContext(),
                    R.array.weight_units,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            unitSpinner.setAdapter(adapter);

            // Set the initial value for spinner
            int initialPosition = "kg".equals(unitsForExercise[i]) ? 1 : 0;
            unitSpinner.setSelection(initialPosition);

            // Add margin between weight input and spinner
            LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            spinnerParams.setMarginStart(32); // Add horizontal margin
            unitSpinner.setLayoutParams(spinnerParams);

            // Listener to update the unit in the map
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
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseSetsReps;
        LinearLayout setsContainer;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseSetsReps = itemView.findViewById(R.id.exerciseSetsReps);
            setsContainer = itemView.findViewById(R.id.setsContainer);
        }
    }

    // Define the interface for long-click handling
    public interface OnExerciseLongClickListener {
        void onExerciseLongClicked(int position);
    }
}