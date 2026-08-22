package com.example.cropmanagementapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cropmanagementapp.model.ActivityLog;
import com.example.cropmanagementapp.model.Crop;

import java.util.ArrayList;
import java.util.List;

/**
 * Central SQLite access point for the app. Handles table creation and all
 * create / read / update / delete operations for crops and their activity
 * logs. Using plain SQLiteOpenHelper (no Room) keeps the project easy to
 * read and debug for a class assignment while still persisting data
 * between app sessions as required.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shamba_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Crops table
    public static final String TABLE_CROPS = "crops";
    public static final String COL_CROP_ID = "id";
    public static final String COL_CROP_NAME = "crop_name";
    public static final String COL_PLOT_NAME = "plot_name";
    public static final String COL_PLANTING_DATE = "planting_date";
    public static final String COL_HARVEST_DATE = "expected_harvest_date";
    public static final String COL_AREA_PLANTED = "area_planted";

    // Activities table
    public static final String TABLE_ACTIVITIES = "activities";
    public static final String COL_ACTIVITY_ID = "id";
    public static final String COL_ACTIVITY_CROP_ID = "crop_id";
    public static final String COL_ACTIVITY_TYPE = "activity_type";
    public static final String COL_ACTIVITY_DATE = "activity_date";
    public static final String COL_ACTIVITY_NOTES = "notes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCrops = "CREATE TABLE " + TABLE_CROPS + " (" +
                COL_CROP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CROP_NAME + " TEXT NOT NULL, " +
                COL_PLOT_NAME + " TEXT NOT NULL, " +
                COL_PLANTING_DATE + " TEXT NOT NULL, " +
                COL_HARVEST_DATE + " TEXT NOT NULL, " +
                COL_AREA_PLANTED + " TEXT" +
                ");";

        String createActivities = "CREATE TABLE " + TABLE_ACTIVITIES + " (" +
                COL_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACTIVITY_CROP_ID + " INTEGER NOT NULL, " +
                COL_ACTIVITY_TYPE + " TEXT NOT NULL, " +
                COL_ACTIVITY_DATE + " TEXT NOT NULL, " +
                COL_ACTIVITY_NOTES + " TEXT, " +
                "FOREIGN KEY(" + COL_ACTIVITY_CROP_ID + ") REFERENCES " +
                TABLE_CROPS + "(" + COL_CROP_ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(createCrops);
        db.execSQL(createActivities);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CROPS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ---------------------------------------------------------------
    // CROP CRUD
    // ---------------------------------------------------------------

    public long addCrop(Crop crop) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CROP_NAME, crop.getCropName());
        values.put(COL_PLOT_NAME, crop.getPlotName());
        values.put(COL_PLANTING_DATE, crop.getPlantingDate());
        values.put(COL_HARVEST_DATE, crop.getExpectedHarvestDate());
        values.put(COL_AREA_PLANTED, crop.getAreaPlanted());
        long id = db.insert(TABLE_CROPS, null, values);
        db.close();
        return id;
    }

    public int updateCrop(Crop crop) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CROP_NAME, crop.getCropName());
        values.put(COL_PLOT_NAME, crop.getPlotName());
        values.put(COL_PLANTING_DATE, crop.getPlantingDate());
        values.put(COL_HARVEST_DATE, crop.getExpectedHarvestDate());
        values.put(COL_AREA_PLANTED, crop.getAreaPlanted());
        int rows = db.update(TABLE_CROPS, values, COL_CROP_ID + " = ?",
                new String[]{String.valueOf(crop.getId())});
        db.close();
        return rows;
    }

    public void deleteCrop(long cropId) {
        SQLiteDatabase db = getWritableDatabase();
        // Remove related activity logs first (in case foreign keys / cascade
        // are not honoured on the device's SQLite build).
        db.delete(TABLE_ACTIVITIES, COL_ACTIVITY_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.delete(TABLE_CROPS, COL_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.close();
    }

    public Crop getCrop(long cropId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROPS, null, COL_CROP_ID + " = ?",
                new String[]{String.valueOf(cropId)}, null, null, null);

        Crop crop = null;
        if (cursor.moveToFirst()) {
            crop = cursorToCrop(cursor);
        }
        cursor.close();
        db.close();
        return crop;
    }

    /**
     * Returns all crops, optionally filtered by a search term matching the
     * crop name or plot name (case-insensitive, partial match). Pass null
     * or an empty string to return every crop.
     */
    public List<Crop> getAllCrops(String searchTerm) {
        List<Crop> crops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String like = "%" + searchTerm.trim() + "%";
            cursor = db.query(TABLE_CROPS, null,
                    COL_CROP_NAME + " LIKE ? OR " + COL_PLOT_NAME + " LIKE ?",
                    new String[]{like, like}, null, null,
                    COL_HARVEST_DATE + " ASC");
        } else {
            cursor = db.query(TABLE_CROPS, null, null, null, null, null,
                    COL_HARVEST_DATE + " ASC");
        }

        if (cursor.moveToFirst()) {
            do {
                crops.add(cursorToCrop(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return crops;
    }

    private Crop cursorToCrop(Cursor cursor) {
        Crop crop = new Crop();
        crop.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CROP_ID)));
        crop.setCropName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CROP_NAME)));
        crop.setPlotName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PLOT_NAME)));
        crop.setPlantingDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_PLANTING_DATE)));
        crop.setExpectedHarvestDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_HARVEST_DATE)));
        crop.setAreaPlanted(cursor.getString(cursor.getColumnIndexOrThrow(COL_AREA_PLANTED)));
        return crop;
    }

    // ---------------------------------------------------------------
    // ACTIVITY LOG CRUD
    // ---------------------------------------------------------------

    public long addActivity(ActivityLog activity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVITY_CROP_ID, activity.getCropId());
        values.put(COL_ACTIVITY_TYPE, activity.getActivityType());
        values.put(COL_ACTIVITY_DATE, activity.getActivityDate());
        values.put(COL_ACTIVITY_NOTES, activity.getNotes());
        long id = db.insert(TABLE_ACTIVITIES, null, values);
        db.close();
        return id;
    }

    public List<ActivityLog> getActivitiesForCrop(long cropId) {
        List<ActivityLog> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVITIES, null, COL_ACTIVITY_CROP_ID + " = ?",
                new String[]{String.valueOf(cropId)}, null, null,
                COL_ACTIVITY_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                ActivityLog log = new ActivityLog();
                log.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIVITY_ID)));
                log.setCropId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIVITY_CROP_ID)));
                log.setActivityType(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVITY_TYPE)));
                log.setActivityDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVITY_DATE)));
                log.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVITY_NOTES)));
                activities.add(log);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;
    }

    public void deleteActivity(long activityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, COL_ACTIVITY_ID + " = ?", new String[]{String.valueOf(activityId)});
        db.close();
    }

    // ---------------------------------------------------------------
    // SUMMARY QUERIES (for the dashboard)
    // ---------------------------------------------------------------

    public int getTotalCropCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CROPS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getDistinctPlotCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT " + COL_PLOT_NAME + ") FROM " + TABLE_CROPS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Crops whose expected harvest date falls within the next N days
     * (inclusive of today), ordered by soonest first. Used to power
     * the "Upcoming harvests" section of the dashboard.
     */
    public List<Crop> getUpcomingHarvests(String todayDate, String cutoffDate) {
        List<Crop> crops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROPS, null,
                COL_HARVEST_DATE + " >= ? AND " + COL_HARVEST_DATE + " <= ?",
                new String[]{todayDate, cutoffDate}, null, null,
                COL_HARVEST_DATE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                crops.add(cursorToCrop(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return crops;
    }
}