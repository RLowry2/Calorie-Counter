package com.example.caloriecounter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<ExerciseEntry> exercises;

    public ExerciseAdapter(List<ExerciseEntry> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseEntry exercise = exercises.get(position);
        Log.d("AdapterDebug", "Binding exercise: " + exercise.getName());
        holder.exerciseTitle.setText(exercise.getName());
        holder.setsAndReps.setText(exercise.getSets() + " sets of " + exercise.getReps() + " reps");

        // Add checkboxes for each set
        holder.setsContainer.removeAllViews();
        for (int i = 1; i <= exercise.getSets(); i++) {
            CheckBox checkBox = new CheckBox(holder.itemView.getContext());
            checkBox.setText("Set " + i);
            holder.setsContainer.addView(checkBox);
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseTitle;
        TextView setsAndReps;
        ViewGroup setsContainer;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseTitle = itemView.findViewById(R.id.exerciseTitle);
            setsAndReps = itemView.findViewById(R.id.setsAndReps);
            setsContainer = itemView.findViewById(R.id.setsContainer);
        }
    }
}