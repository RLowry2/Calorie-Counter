package com.example.caloriecounter;

import android.util.Log;
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

    private List<ExerciseEntry> exerciseList;

    public ExerciseAdapter(List<ExerciseEntry> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int position) {
        ExerciseEntry exercise = exerciseList.get(position);

        holder.exerciseNameText.setText(exercise.getName());
        holder.setsRepsText.setText("Sets: " + exercise.getSets() + " Reps: " + exercise.getReps());

        // Add checkboxes for each set
        holder.checkboxContainer.removeAllViews();
        for (int i = 1; i <= exercise.getSets(); i++) {
            CheckBox checkBox = new CheckBox(holder.itemView.getContext());
            checkBox.setText("Set " + i);
            holder.checkboxContainer.addView(checkBox);
        }
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public void updateExercises(List<ExerciseEntry> newExercises) {
        this.exerciseList = newExercises;
        notifyDataSetChanged();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameText, setsRepsText;
        LinearLayout checkboxContainer;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseNameText = itemView.findViewById(R.id.exerciseNameText);
            setsRepsText = itemView.findViewById(R.id.setsRepsText);
            checkboxContainer = itemView.findViewById(R.id.checkboxContainer);
        }
    }
}