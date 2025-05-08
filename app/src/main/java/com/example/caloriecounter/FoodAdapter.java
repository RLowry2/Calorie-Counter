package com.example.caloriecounter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public interface OnDeleteListener {
        void onDelete(FoodEntry foodEntry, int position);
    }

    private List<FoodEntry> foodList;
    private OnDeleteListener deleteListener;

    public FoodAdapter(List<FoodEntry> foodList, OnDeleteListener deleteListener) {
        this.foodList = foodList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodEntry food = foodList.get(position);
        holder.nameView.setText(food.getName());
        holder.calorieView.setText(food.getCalories() + " cal");

        // On long click, open the edit/delete dialog
        holder.itemView.setOnLongClickListener(view -> {
            showEditDialog(view, food, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    private void showEditDialog(View view, FoodEntry food, int position) {
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_edit_food, null);
        EditText editName = dialogView.findViewById(R.id.editFoodName);
        EditText editCalories = dialogView.findViewById(R.id.editFoodCalories);

        editName.setText(food.getName());
        editCalories.setText(String.valueOf(food.getCalories()));

        AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Submit", null) // Overriding below
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setNeutralButton("Delete", (d, which) -> handleDelete(food, position))
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                    handlePositiveButtonClick(dialog, food, position, editName, editCalories));
        });

        dialog.show();
    }

    private void handlePositiveButtonClick(AlertDialog dialog, FoodEntry food, int position, EditText editName, EditText editCalories) {
        String newName = editName.getText().toString().trim();
        String newCaloriesStr = editCalories.getText().toString().trim();

        if (newName.isEmpty() || newCaloriesStr.isEmpty()) {
            Toast.makeText(dialog.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int newCalories = Integer.parseInt(newCaloriesStr);
            food.setName(newName);
            food.setCalories(newCalories);
            notifyItemChanged(position);
            dialog.dismiss();
        } catch (NumberFormatException e) {
            Toast.makeText(dialog.getContext(), "Calories must be a number", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDelete(FoodEntry food, int position) {
        if (deleteListener != null) {
            deleteListener.onDelete(food, position);
        }
    }

    public void removeItem(int position) {
        foodList.remove(position);
        notifyItemRemoved(position);
    }

    public FoodEntry getItem(int position) {
        return foodList.get(position);
    }

    // Method to update the food list in the adapter
    public void updateFoodList(List<FoodEntry> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, calorieView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.textName);
            calorieView = itemView.findViewById(R.id.textCalories);
        }
    }

    public void setFoodList(List<FoodEntry> foodList) {
        this.foodList = foodList;
        notifyDataSetChanged();
    }
}