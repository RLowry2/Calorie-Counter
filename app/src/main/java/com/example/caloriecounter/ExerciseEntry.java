package com.example.caloriecounter;

public class ExerciseEntry {

    private int id; // Unique identifier for the exercise entry
    private String name;
    private boolean isComplete;
    private int totalSets;
    private int repsPerSet;
    private double weight; // Weight used for the exercise
    private String unit; // Unit for the weight (lbs or kgs)
    private String day; // Field to store the associated day

    // Constructor with all fields
    public ExerciseEntry(String name, int totalSets, int repsPerSet, double weight, String unit) {
        this.name = name;
        this.totalSets = totalSets;
        this.repsPerSet = repsPerSet;
        this.weight = weight;
        this.unit = unit;
        this.isComplete = false; // Default to not completed
    }

    // Constructor with fewer fields, if needed (for compatibility)
    public ExerciseEntry(String name, int totalSets, int repsPerSet, String day) {
        this.name = name;
        this.totalSets = totalSets;
        this.repsPerSet = repsPerSet;
        this.day = day;
        this.weight = 0; // Default weight
        this.unit = "lbs"; // Default unit
    }

    // Getters and Setters
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(int totalSets) {
        this.totalSets = totalSets;
    }

    public void setSets(int totalSets) {
        this.totalSets = totalSets;
    }

    public int getRepsPerSet() {
        return repsPerSet;
    }

    public void setRepsPerSet(int repsPerSet) {
        this.repsPerSet = repsPerSet;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}