package com.example.caloriecounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "calorie_counter.db";
    private static final int DATABASE_VERSION = 4; // Increment this when schema changes

    // Common Columns
    private static final String COL_ID = "id";

    // Food Entries Table
    private static final String FOOD_TBL = "food_entries";
    private static final String COL_NAME = "name";
    private static final String COL_CAL = "calories";
    private static final String COL_DATE = "date";

    // Daily Goals Table
    private static final String GOALS_TBL = "daily_goals";
    private static final String COL_GOAL_DATE = "date";
    private static final String COL_GOAL = "goal";

    // Exercise Routines Table
    private static final String ROUTINES_TBL = "exercise_routines";
    private static final String COL_DAY = "day";
    private static final String COL_EXERCISE_NAME = "exercise_name";
    private static final String COL_SETS = "sets";
    private static final String COL_REPS = "reps";

    // Exercise Names Table
    private static final String EXERCISE_NAMES_TBL = "exercise_names_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("FoodDatabaseHelper", "onCreate: Creating database tables.");

        // Create Food Entries Table
        db.execSQL("CREATE TABLE " + FOOD_TBL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT, "
                + COL_CAL + " INTEGER, "
                + COL_DATE + " TEXT)");

        // Create Daily Goals Table
        db.execSQL("CREATE TABLE " + GOALS_TBL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_GOAL_DATE + " TEXT UNIQUE NOT NULL, "
                + COL_GOAL + " INTEGER NOT NULL)");

        // Create Exercise Routines Table
        db.execSQL("CREATE TABLE " + ROUTINES_TBL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DAY + " TEXT NOT NULL, "
                + COL_EXERCISE_NAME + " TEXT NOT NULL, "
                + COL_SETS + " INTEGER NOT NULL, "
                + COL_REPS + " INTEGER NOT NULL)");

        // Create Exercise Names Table
        db.execSQL("CREATE TABLE " + EXERCISE_NAMES_TBL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_EXERCISE_NAME + " TEXT NOT NULL UNIQUE)");

        Log.d("FoodDatabaseHelper", "onCreate: Database tables created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("FoodDatabaseHelper", "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);

        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + FOOD_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + GOALS_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + ROUTINES_TBL);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_NAMES_TBL);

        // Recreate tables
        onCreate(db);
    }

    // ** FOOD ENTRIES METHODS **

    public void insertFood(String name, int cal, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_CAL, cal);
        values.put(COL_DATE, date);
        db.insert(FOOD_TBL, null, values);
        db.close();
    }

    public List<FoodEntry> getFoodEntriesForDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + FOOD_TBL + " WHERE " + COL_DATE + " = ?",
                new String[]{date});

        List<FoodEntry> foodEntries = new ArrayList<>();
        while (cursor.moveToNext()) {
            foodEntries.add(new FoodEntry(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_CAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
            ));
        }
        cursor.close();
        db.close();
        return foodEntries;
    }

    public void deleteFood(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FOOD_TBL, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ** DAILY GOALS METHODS **

    public void setGoalForDate(String date, int goal) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GOAL, goal);

        int updated = db.update(GOALS_TBL, values, COL_GOAL_DATE + " = ?", new String[]{date});
        if (updated == 0) {
            values.put(COL_GOAL_DATE, date);
            db.insert(GOALS_TBL, null, values);
        }
        db.close();
    }

    public int getGoalForDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_GOAL + " FROM " + GOALS_TBL + " WHERE " + COL_GOAL_DATE + " = ?",
                new String[]{date});

        int goal = 0;
        if (cursor.moveToFirst()) {
            goal = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return goal;
    }

    public void propagateGoalToFutureDate(String currentDate, String futureDate) {
        int currentGoal = getGoalForDate(currentDate);
        if (currentGoal > 0 && getGoalForDate(futureDate) == 0) {
            setGoalForDate(futureDate, currentGoal);
        }
    }

    // ** EXERCISE ROUTINES METHODS **

    public boolean insertOrUpdateRoutine(String day, String exerciseName, int sets, int reps) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DAY, day);
        values.put(COL_EXERCISE_NAME, exerciseName);
        values.put(COL_SETS, sets);
        values.put(COL_REPS, reps);

        int updated = db.update(ROUTINES_TBL, values, COL_DAY + " = ? AND " + COL_EXERCISE_NAME + " = ?", new String[]{day, exerciseName});
        if (updated == 0) {
            long insertedId = db.insert(ROUTINES_TBL, null, values);
            db.close();
            return insertedId != -1;
        }

        db.close();
        return true;
    }

    public List<ExerciseEntry> getExercisesForDay(String day) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ROUTINES_TBL,
                new String[]{COL_EXERCISE_NAME, COL_SETS, COL_REPS},
                COL_DAY + " = ?", new String[]{day},
                null, null, null);

        List<ExerciseEntry> routines = new ArrayList<>();
        while (cursor.moveToNext()) {
            routines.add(new ExerciseEntry(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPS))
            ));
        }
        cursor.close();
        db.close();
        return routines;
    }

    public int clearExercisesForDay(String day) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(ROUTINES_TBL, COL_DAY + " = ?", new String[]{day});
        db.close();
        return rowsDeleted;
    }

    // ** EXERCISE NAMES METHODS **

    public boolean insertExerciseName(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXERCISE_NAME, name);

        long result = db.insert(EXERCISE_NAMES_TBL, null, values);
        db.close();
        return result != -1;
    }

    public List<String> getExerciseNames() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(EXERCISE_NAMES_TBL, new String[]{COL_EXERCISE_NAME},
                null, null, null, null, COL_EXERCISE_NAME + " ASC");

        List<String> exerciseNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            exerciseNames.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME)));
        }

        cursor.close();
        db.close();
        return exerciseNames;
    }

    public boolean updateExerciseName(String oldName, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXERCISE_NAME, newName);

        // Update the exercise name where the old name matches
        int rowsAffected = db.update(EXERCISE_NAMES_TBL, values, COL_EXERCISE_NAME + " = ?", new String[]{oldName});
        db.close();

        return rowsAffected > 0;
    }

    public boolean deleteExerciseName(String exerciseName) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete the exercise where the name matches
        int rowsAffected = db.delete(EXERCISE_NAMES_TBL, COL_EXERCISE_NAME + " = ?", new String[]{exerciseName});
        db.close();

        return rowsAffected > 0;
    }
}