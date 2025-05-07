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
    private static final int VERSION = 2;

    private static final String TBL = "food_entries";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_CAL = "calories";
    private static final String COL_DATE = "date";
    private static final String COL_GOAL = "goal";

    public FoodDatabaseHelper(Context ctx) {
        super(ctx, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " TEXT,"
                + COL_CAL + " INTEGER,"
                + COL_DATE + " TEXT,"
                + COL_GOAL + " INTEGER DEFAULT 0"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            db.execSQL("ALTER TABLE " + TBL + " ADD COLUMN " + COL_GOAL + " INTEGER DEFAULT 0");
        }
    }

    public void insertFood(String name, int cal, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, name);
        v.put(COL_CAL, cal);
        v.put(COL_DATE, date);
        db.insert(TBL, null, v);
        db.close();
    }

    public List<FoodEntry> getFoodEntriesForDate(String date) {
        List<FoodEntry> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TBL + " WHERE " + COL_DATE + " = ? AND " + COL_NAME + " IS NOT NULL",
                new String[]{ date }
        );
        if (c.moveToFirst()) {
            do {
                out.add(new FoodEntry(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        c.getInt(c.getColumnIndexOrThrow(COL_CAL)),
                        c.getString(c.getColumnIndexOrThrow(COL_DATE))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return out;
    }

    public void deleteFood(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TBL, COL_ID + "=?", new String[]{ String.valueOf(id) });
        db.close();
    }

    /** Insert or update **only today’s** goal */
    public void insertOrUpdateGoal(String date, int goal) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_GOAL, goal);

        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + " FROM " + TBL +
                        " WHERE " + COL_DATE + " = ? AND " + COL_NAME + " IS NULL",
                new String[]{ date }
        );
        if (c.moveToFirst()) {
            // update existing row
            db.update(TBL, v,
                    COL_DATE + "=? AND " + COL_NAME + " IS NULL",
                    new String[]{ date });
        } else {
            // insert new “goal only” row
            v.put(COL_DATE, date);
            v.putNull(COL_NAME);
            db.insert(TBL, null, v);
        }
        c.close();
        db.close();
    }

    public int getGoalForDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_GOAL +
                        " FROM " + TBL +
                        " WHERE " + COL_DATE + " = ? AND " + COL_NAME + " IS NULL",
                new String[]{ date }
        );
        int g = 0;
        if (c.moveToFirst()) g = c.getInt(0);
        c.close();
        return g;
    }

    public void setGoalForDate(String date, int goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal", goal);

        int updated = db.update("daily_goals", values, "date = ?", new String[]{date});
        if (updated == 0) {
            // Insert if not exists
            values.put("date", date);
            db.insert("daily_goals", null, values);
        }
    }

}
