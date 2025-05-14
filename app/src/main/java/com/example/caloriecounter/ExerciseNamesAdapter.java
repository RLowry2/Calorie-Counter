package com.example.caloriecounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseNamesAdapter extends RecyclerView.Adapter<ExerciseNamesAdapter.ViewHolder> {

    private final List<String> exerciseNamesList;
    private final OnExerciseLongPressListener longPressListener;

    public interface OnExerciseLongPressListener {
        void onLongPress(String exerciseName);
    }

    public ExerciseNamesAdapter(List<String> exerciseNamesList, OnExerciseLongPressListener longPressListener) {
        this.exerciseNamesList = exerciseNamesList;
        this.longPressListener = longPressListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String exerciseName = exerciseNamesList.get(position);
        holder.bind(exerciseName);
    }

    @Override
    public int getItemCount() {
        return exerciseNamesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView exerciseNameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
        }

        void bind(String exerciseName) {
            exerciseNameTextView.setText(exerciseName);
            itemView.setOnLongClickListener(v -> {
                longPressListener.onLongPress(exerciseName);
                return true;
            });
        }
    }
}