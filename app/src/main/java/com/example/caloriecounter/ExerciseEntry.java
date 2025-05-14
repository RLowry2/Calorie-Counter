package com.example.caloriecounter;

public class ExerciseEntry {
    private String name;
    private boolean isComplete;
    private int totalSets;
    private int repsPerSet; // New field

    public ExerciseEntry(String name, int totalSets, int repsPerSet) {
        this.name = name;
        this.isComplete = false;
        this.totalSets = totalSets;
        this.repsPerSet = repsPerSet;
    }

    // Getters and setters
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

    public int getRepsPerSet() {
        return repsPerSet;
    }

    public void setRepsPerSet(int repsPerSet) {
        this.repsPerSet = repsPerSet;
    }
}