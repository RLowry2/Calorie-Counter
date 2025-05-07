package com.example.caloriecounter;

import android.content.Context;
import android.content.SharedPreferences;

public class GoalPreferenceManager {

    private static final String PREFS_NAME = "CalorieCounterPrefs";
    private static final String KEY_GOAL = "calorie_goal";

    public static void setGoal(Context context, int goal) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_GOAL, goal);
        editor.apply();
    }

    public static int getGoal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_GOAL, 0); // Default is 0 if no goal is set
    }
}
