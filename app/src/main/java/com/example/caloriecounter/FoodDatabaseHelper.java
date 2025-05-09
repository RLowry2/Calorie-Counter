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
    private static final int VERSION = 3; // Incremented version to include the daily_goals table

    // Table for food entries
    private static final String TBL = "food_entries";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_CAL = "calories";
    private static final String COL_DATE = "date";

    // Table for daily goals
    private static final String GOALS_TBL = "daily_goals";

    public FoodDatabaseHelper(Context ctx) {
        super(ctx, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table for food entries
        db.execSQL("CREATE TABLE " + TBL + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " TEXT,"
                + COL_CAL + " INTEGER,"
                + COL_DATE + " TEXT"
                + ")");

        // Create table for daily goals
        db.execSQL("CREATE TABLE " + GOALS_TBL + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT UNIQUE NOT NULL, "
                + "goal INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            db.execSQL("ALTER TABLE " + TBL + " ADD COLUMN goal INTEGER DEFAULT 0");
        }
        if (oldV < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + GOALS_TBL + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "date TEXT UNIQUE NOT NULL, "
                    + "goal INTEGER NOT NULL)");
        }
    }

    // Insert a new food entry
    public void insertFood(String name, int cal, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_CAL, cal);
        v.put(COL_DATE, date);
        db.insert(TBL, null, v);
        db.close();
    }

    // Retrieve food entries for a specific date
    public List<FoodEntry> getFoodEntriesForDate(String date) {
        List<FoodEntry> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TBL + " WHERE " + COL_DATE + " = ? AND " + COL_NAME + " IS NOT NULL",
                new String[]{ date }
        );
        while (c.moveToNext()) {
            out.add(mapCursorToFoodEntry(c));
        }
        c.close();
        db.close();
        return out;
    }

    // Map cursor to FoodEntry object
    private FoodEntry mapCursorToFoodEntry(Cursor c) {
        return new FoodEntry(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getInt(c.getColumnIndexOrThrow(COL_CAL)),
                c.getString(c.getColumnIndexOrThrow(COL_DATE))
        );
    }

    // Delete a food entry
    public void deleteFood(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TBL, COL_ID + "=?", new String[]{ String.valueOf(id) });
        db.close();
    }

    // Insert or update goal for a specific date
    public void setGoalForDate(String date, int goal) {
        SQLiteDatabase db = this.getWritableDatabase();
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
                new String[]{ date }
        );

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
}