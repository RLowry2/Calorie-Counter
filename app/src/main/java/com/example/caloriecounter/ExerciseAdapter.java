package com.example.caloriecounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<ExerciseEntry> exerciseList;

    public ExerciseAdapter(List<ExerciseEntry> exerciseList) {
        this.exerciseList = exerciseList;
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

        // Ensure all checkboxes are unchecked when the app is opened
        holder.exerciseCheckbox.setChecked(false);
        holder.setCheckbox1.setChecked(false);
        holder.setCheckbox2.setChecked(false);

        // Listener for child checkboxes (sets)
        holder.setCheckbox1.setOnCheckedChangeListener((buttonView, isChecked) -> updateParentCheckbox(holder));
        holder.setCheckbox2.setOnCheckedChangeListener((buttonView, isChecked) -> updateParentCheckbox(holder));

        // Listener for parent checkbox (exercise)
        holder.exerciseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // If parent checkbox is checked, mark all sets as complete
            holder.setCheckbox1.setChecked(isChecked);
            holder.setCheckbox2.setChecked(isChecked);
        });
    }

    private void updateParentCheckbox(ExerciseViewHolder holder) {
        // If all child (set) checkboxes are checked, check the parent (exercise) checkbox
        boolean allSetsComplete = holder.setCheckbox1.isChecked() && holder.setCheckbox2.isChecked();
        holder.exerciseCheckbox.setChecked(allSetsComplete);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        CheckBox exerciseCheckbox;
        CheckBox setCheckbox1;
        CheckBox setCheckbox2;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseCheckbox = itemView.findViewById(R.id.exerciseCheckbox);
            setCheckbox1 = itemView.findViewById(R.id.setCheckbox1);
            setCheckbox2 = itemView.findViewById(R.id.setCheckbox2);
        }
    }
}