package com.example.caloriecounter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<ExerciseEntry> exerciseList;

    public ExerciseAdapter(List<ExerciseEntry> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_entry, parent, false); // Use item_exercise_entry layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseEntry exercise = exerciseList.get(position);
        if (holder.exerciseNameTextView == null) {
            Log.e("ExerciseAdapter", "exerciseNameTextView is null");
        }
        holder.exerciseNameTextView.setText(exercise.getName());
        holder.setsTextView.setText("Sets: " + exercise.getSets());
        holder.repsTextView.setText("Reps: " + exercise.getReps());
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        TextView setsTextView;
        TextView repsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
            setsTextView = itemView.findViewById(R.id.setsTextView);
            repsTextView = itemView.findViewById(R.id.repsTextView);
        }
    }
}