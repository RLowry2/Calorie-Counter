package com.example.caloriecounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseNamesAdapter extends RecyclerView.Adapter<ExerciseNamesAdapter.ViewHolder> {

    private List<String> exerciseNamesList;

    public ExerciseNamesAdapter(List<String> exerciseNamesList) {
        this.exerciseNamesList = exerciseNamesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_name, parent, false); // Ensure this matches your layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String exerciseName = exerciseNamesList.get(position);
        holder.exerciseNameTextView.setText(exerciseName);
    }

    @Override
    public int getItemCount() {
        return exerciseNamesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView); // Ensure the ID matches the XML
        }
    }
}