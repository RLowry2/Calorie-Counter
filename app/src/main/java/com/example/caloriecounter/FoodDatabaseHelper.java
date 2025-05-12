package com.example.caloriecounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FoodDatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "calorieCounter.db";
    private static final int VERSION = 4; // Incremented version to include exercise routines

    // Table for food entries
    private static final String FOOD_TBL = "food_entries";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_CAL = "calories";
    private static final String COL_DATE = "date";

    // Table for daily goals
    private static final String GOALS_TBL = "daily_goals";

    // Table for exercise routines
    private static final String ROUTINES_TBL = "exercise_routines";
    private static final String COL_ROUTINE_ID = "id";
    private static final String COL_DAY = "day";
    private static final String COL_EXERCISE_NAME = "exercise_name";
    private static final String COL_SETS = "sets";
    private static final String COL_REPS = "reps";

    public FoodDatabaseHelper(Context ctx) {
        super(ctx, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table for food entries
        db.execSQL("CREATE TABLE " + FOOD_TBL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT, "
                + COL_CAL + " INTEGER, "
                + COL_DATE + " TEXT)");

        // Create table for daily goals
        db.execSQL("CREATE TABLE " + GOALS_TBL + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT UNIQUE NOT NULL, "
                + "goal INTEGER NOT NULL)");

        // Create table for exercise routines
        db.execSQL("CREATE TABLE " + ROUTINES_TBL + " ("
                + COL_ROUTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DAY + " TEXT NOT NULL, "
                + COL_EXERCISE_NAME + " TEXT NOT NULL, "
                + COL_SETS + " INTEGER NOT NULL, "
                + COL_REPS + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + ROUTINES_TBL + " ("
                    + COL_ROUTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_DAY + " TEXT NOT NULL, "
                    + COL_EXERCISE_NAME + " TEXT NOT NULL, "
                    + COL_SETS + " INTEGER NOT NULL, "
                    + COL_REPS + " INTEGER NOT NULL)");
        }
    }

    // Insert a new food entry
    public void insertFood(String name, int cal, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_CAL, cal);
        values.put(COL_DATE, date);
        db.insert(FOOD_TBL, null, values);
        db.close();
    }

    // Retrieve food entries for a specific date
    public List<FoodEntry> getFoodEntriesForDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + FOOD_TBL + " WHERE " + COL_DATE + " = ?",
                new String[]{date});

        List<FoodEntry> foodEntries = new ArrayList<>();
        while (c.moveToNext()) {
            foodEntries.add(new FoodEntry(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getInt(c.getColumnIndexOrThrow(COL_CAL)),
                    c.getString(c.getColumnIndexOrThrow(COL_DATE))
            ));
        }
        c.close();
        db.close();
        return foodEntries;
    }

    // Delete a food entry
    public void deleteFood(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FOOD_TBL, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Insert or update goal for a specific date
    public void setGoalForDate(String date, int goal) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal", goal);

        // Attempt to update an existing goal
        int updated = db.update(GOALS_TBL, values, "date = ?", new String[]{date});
        if (updated == 0) {
            // Insert a new goal if update fails
            values.put("date", date);
            db.insert(GOALS_TBL, null, values);
        }
        db.close();
    }

    // Retrieve goal for a specific date
    public int getGoalForDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT goal FROM " + GOALS_TBL + " WHERE date = ?",
                new String[]{date});

        int goal = 0; // Default value
        if (cursor.moveToFirst()) {
            goal = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return goal;
    }

    // Propagate the current goal to a future date if no goal exists for that date
    public void propagateGoalToFutureDate(String currentDate, String futureDate) {
        int currentGoal = getGoalForDate(currentDate);
        if (currentGoal > 0 && getGoalForDate(futureDate) == 0) {
            setGoalForDate(futureDate, currentGoal);
        }
    }

    // Insert or update a routine
    public void insertOrUpdateRoutine(String day, String exerciseName, int sets, int reps) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DAY, day);
        values.put(COL_EXERCISE_NAME, exerciseName);
        values.put(COL_SETS, sets);
        values.put(COL_REPS, reps);

        // Attempt to update an existing routine
        int updated = db.update(ROUTINES_TBL, values, COL_DAY + " = ? AND " + COL_EXERCISE_NAME + " = ?", new String[]{day, exerciseName});
        if (updated == 0) {
            // Insert a new routine if no existing entry was updated
            db.insert(ROUTINES_TBL, null, values);
        }

        db.close();
    }

    // Retrieve routines for a specific day
    public List<ExerciseEntry> getRoutinesForDay(String day) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ROUTINES_TBL + " WHERE " + COL_DAY + " = ?", new String[]{day});

        List<ExerciseEntry> routines = new ArrayList<>();
        while (cursor.moveToNext()) {
            String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME));
            int sets = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETS));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPS));
            routines.add(new ExerciseEntry(exerciseName, sets, reps)); // Matches the three-argument constructor
        }

        cursor.close();
        db.close();
        return routines;
    }

    // Retrieve all routines
    public List<ExerciseEntry> getAllRoutines() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ROUTINES_TBL, null);

        List<ExerciseEntry> routines = new ArrayList<>();
        while (cursor.moveToNext()) {
            String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME));
            int sets = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETS));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPS));
            routines.add(new ExerciseEntry(exerciseName, sets, reps)); // Matches the three-argument constructor
        }

        cursor.close();
        db.close();
        return routines;
    }
}