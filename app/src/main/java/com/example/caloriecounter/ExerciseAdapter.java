package com.example.caloriecounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<ExerciseEntry> exerciseList;
    private final OnExerciseLongClickListener longClickListener;

    // Constructor with the long click listener
    public ExerciseAdapter(List<ExerciseEntry> exerciseList, OnExerciseLongClickListener longClickListener) {
        this.exerciseList = exerciseList;
        this.longClickListener = longClickListener;
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

        // Ensure parent checkbox is unchecked when the app is opened
        holder.exerciseCheckbox.setChecked(false);

        // Clear any existing views in the sets container (to handle recycling)
        holder.setsContainer.removeAllViews();

        // Dynamically add checkboxes for each set
        for (int i = 1; i <= exercise.getTotalSets(); i++) {
            CheckBox setCheckbox = new CheckBox(holder.setsContainer.getContext());
            setCheckbox.setText("Set " + i);
            setCheckbox.setChecked(false); // Default to unchecked

            // Listener for individual checkboxes
            setCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Update parent checkbox state based on child checkboxes
                updateParentCheckbox(holder);
            });

            holder.setsContainer.addView(setCheckbox);
        }

        // Listener for parent checkbox (exercise)
        holder.exerciseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // If parent checkbox is checked, mark all sets as complete
            for (int i = 0; i < holder.setsContainer.getChildCount(); i++) {
                View child = holder.setsContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    ((CheckBox) child).setChecked(isChecked);
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

    private void updateParentCheckbox(ExerciseViewHolder holder) {
        // Check if all child checkboxes are checked
        boolean allSetsComplete = true;

        for (int i = 0; i < holder.setsContainer.getChildCount(); i++) {
            View child = holder.setsContainer.getChildAt(i);
            if (child instanceof CheckBox && !((CheckBox) child).isChecked()) {
                allSetsComplete = false;
                break;
            }
        }

        // Update parent checkbox based on child checkboxes
        holder.exerciseCheckbox.setChecked(allSetsComplete);
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