package com.example.caloriecounter;

public class FoodEntry {
    private int id;
    private String name;
    private int calories;
    private String date;

    // Constructor
    public FoodEntry(int id, String name, int calories, String date) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.date = date;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
