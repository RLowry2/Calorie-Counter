package com.example.caloriecounter;

public class ExerciseEntry {
    private String name;
    private int sets;
    private int reps;

    // Constructor
    public ExerciseEntry(String name, int sets, int reps) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for sets
    public int getSets() {
        return sets;
    }

    // Setter for sets
    public void setSets(int sets) {
        this.sets = sets;
    }

    // Getter for reps
    public int getReps() {
        return reps;
    }

    // Setter for reps
    public void setReps(int reps) {
        this.reps = reps;
    }
}