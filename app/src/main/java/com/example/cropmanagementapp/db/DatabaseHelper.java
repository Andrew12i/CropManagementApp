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
 * Central SQLite access point. Handles table creation/upgrades and all
 * CRUD operations for crops, activity logs (with expenses), and
 * farmer-added custom crop types.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shamba_tracker.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_CROPS = "crops";
    public static final String COL_CROP_ID = "id";
    public static final String COL_CROP_NAME = "crop_name";
    public static final String COL_VARIETY = "variety";
    public static final String COL_PLOT_NAME = "plot_name";
    public static final String COL_PLANTING_DATE = "planting_date";
    public static final String COL_HARVEST_DATE = "expected_harvest_date";
    public static final String COL_AREA_PLANTED = "area_planted";
    public static final String COL_IS_HARVESTED = "is_harvested";
    public static final String COL_YIELD_AMOUNT = "yield_amount";
    public static final String COL_HARVESTED_DATE = "harvested_date";
    public static final String COL_CATEGORY = "category";

    public static final String TABLE_ACTIVITIES = "activities";
    public static final String COL_ACTIVITY_ID = "id";
    public static final String COL_ACTIVITY_CROP_ID = "crop_id";
    public static final String COL_ACTIVITY_TYPE = "activity_type";
    public static final String COL_ACTIVITY_DATE = "activity_date";
    public static final String COL_ACTIVITY_NOTES = "notes";
    public static final String COL_ACTIVITY_EXPENSE = "expense_amount";

    public static final String TABLE_CUSTOM_CROPS = "custom_crop_types";
    public static final String COL_CUSTOM_CROP_ID = "id";
    public static final String COL_CUSTOM_CROP_NAME = "crop_name";
    public static final String COL_CUSTOM_CROP_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CROPS + " (" +
                COL_CROP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CROP_NAME + " TEXT NOT NULL, " +
                COL_VARIETY + " TEXT, " +
                COL_PLOT_NAME + " TEXT NOT NULL, " +
                COL_PLANTING_DATE + " TEXT NOT NULL, " +
                COL_HARVEST_DATE + " TEXT NOT NULL, " +
                COL_AREA_PLANTED + " TEXT, " +
                COL_IS_HARVESTED + " INTEGER NOT NULL DEFAULT 0, " +
                COL_YIELD_AMOUNT + " TEXT, " +
                COL_HARVESTED_DATE + " TEXT, " +
                COL_CATEGORY + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_ACTIVITIES + " (" +
                COL_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACTIVITY_CROP_ID + " INTEGER NOT NULL, " +
                COL_ACTIVITY_TYPE + " TEXT NOT NULL, " +
                COL_ACTIVITY_DATE + " TEXT NOT NULL, " +
                COL_ACTIVITY_NOTES + " TEXT, " +
                COL_ACTIVITY_EXPENSE + " TEXT, " +
                "FOREIGN KEY(" + COL_ACTIVITY_CROP_ID + ") REFERENCES " +
                TABLE_CROPS + "(" + COL_CROP_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_CUSTOM_CROPS + " (" +
                COL_CUSTOM_CROP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CUSTOM_CROP_NAME + " TEXT NOT NULL UNIQUE, " +
                COL_CUSTOM_CROP_CATEGORY + " TEXT NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CROPS + " ADD COLUMN " + COL_VARIETY + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_CROPS + " ADD COLUMN " + COL_IS_HARVESTED + " INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_CROPS + " ADD COLUMN " + COL_YIELD_AMOUNT + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_CROPS + " ADD COLUMN " + COL_HARVESTED_DATE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ACTIVITIES + " ADD COLUMN " + COL_ACTIVITY_EXPENSE + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_CROPS + " ADD COLUMN " + COL_CATEGORY + " TEXT");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOM_CROPS + " (" +
                    COL_CUSTOM_CROP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CUSTOM_CROP_NAME + " TEXT NOT NULL UNIQUE, " +
                    COL_CUSTOM_CROP_CATEGORY + " TEXT NOT NULL" +
                    ");");
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ---------------- CUSTOM CROP TYPE CRUD ----------------

    public long addCustomCropType(String name, String category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CUSTOM_CROP_NAME, name);
        values.put(COL_CUSTOM_CROP_CATEGORY, category);
        long id = db.insertWithOnConflict(TABLE_CUSTOM_CROPS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return id;
    }

    public List<String[]> getCustomCropTypes() {
        List<String[]> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOM_CROPS, null, null, null, null, null, COL_CUSTOM_CROP_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOM_CROP_NAME));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOM_CROP_CATEGORY));
                items.add(new String[]{name, category});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    // ---------------- CROP CRUD ----------------

    public long addCrop(Crop crop) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CROP_NAME, crop.getCropName());
        values.put(COL_VARIETY, crop.getVariety());
        values.put(COL_PLOT_NAME, crop.getPlotName());
        values.put(COL_PLANTING_DATE, crop.getPlantingDate());
        values.put(COL_HARVEST_DATE, crop.getExpectedHarvestDate());
        values.put(COL_AREA_PLANTED, crop.getAreaPlanted());
        values.put(COL_IS_HARVESTED, 0);
        values.put(COL_CATEGORY, crop.getCategory());
        long id = db.insert(TABLE_CROPS, null, values);
        db.close();
        return id;
    }

    public int updateCrop(Crop crop) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CROP_NAME, crop.getCropName());
        values.put(COL_VARIETY, crop.getVariety());
        values.put(COL_PLOT_NAME, crop.getPlotName());
        values.put(COL_PLANTING_DATE, crop.getPlantingDate());
        values.put(COL_HARVEST_DATE, crop.getExpectedHarvestDate());
        values.put(COL_AREA_PLANTED, crop.getAreaPlanted());
        values.put(COL_CATEGORY, crop.getCategory());
        int rows = db.update(TABLE_CROPS, values, COL_CROP_ID + " = ?",
                new String[]{String.valueOf(crop.getId())});
        db.close();
        return rows;
    }

    public void markCropHarvested(long cropId, String harvestedDateIso, String yieldAmount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_HARVESTED, 1);
        values.put(COL_HARVESTED_DATE, harvestedDateIso);
        values.put(COL_YIELD_AMOUNT, yieldAmount);
        db.update(TABLE_CROPS, values, COL_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.close();
    }

    public void unmarkCropHarvested(long cropId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_HARVESTED, 0);
        values.putNull(COL_HARVESTED_DATE);
        values.putNull(COL_YIELD_AMOUNT);
        db.update(TABLE_CROPS, values, COL_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.close();
    }

    public void deleteCrop(long cropId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, COL_ACTIVITY_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.delete(TABLE_CROPS, COL_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
        db.close();
    }

    public Crop getCrop(long cropId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROPS, null, COL_CROP_ID + " = ?",
                new String[]{String.valueOf(cropId)}, null, null, null);
        Crop crop = null;
        if (cursor.moveToFirst()) crop = cursorToCrop(cursor);
        cursor.close();
        db.close();
        return crop;
    }

    public List<Crop> getAllCrops(String searchTerm) {
        List<Crop> crops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String like = "%" + searchTerm.trim() + "%";
            cursor = db.query(TABLE_CROPS, null,
                    COL_IS_HARVESTED + " = 0 AND (" + COL_CROP_NAME + " LIKE ? OR " + COL_PLOT_NAME + " LIKE ?)",
                    new String[]{like, like}, null, null, COL_HARVEST_DATE + " ASC");
        } else {
            cursor = db.query(TABLE_CROPS, null, COL_IS_HARVESTED + " = 0", null, null, null,
                    COL_HARVEST_DATE + " ASC");
        }
        if (cursor.moveToFirst()) {
            do { crops.add(cursorToCrop(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return crops;
    }

    public List<Crop> getHarvestedCrops(String searchTerm) {
        List<Crop> crops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String like = "%" + searchTerm.trim() + "%";
            cursor = db.query(TABLE_CROPS, null,
                    COL_IS_HARVESTED + " = 1 AND (" + COL_CROP_NAME + " LIKE ? OR " + COL_PLOT_NAME + " LIKE ?)",
                    new String[]{like, like}, null, null, COL_HARVESTED_DATE + " DESC");
        } else {
            cursor = db.query(TABLE_CROPS, null, COL_IS_HARVESTED + " = 1", null, null, null,
                    COL_HARVESTED_DATE + " DESC");
        }
        if (cursor.moveToFirst()) {
            do { crops.add(cursorToCrop(cursor)); } while (cursor.moveToNext());
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

        crop.setVariety(safeString(cursor, COL_VARIETY));
        crop.setHarvested(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_HARVESTED)) == 1);
        crop.setYieldAmount(safeString(cursor, COL_YIELD_AMOUNT));
        crop.setHarvestedDate(safeString(cursor, COL_HARVESTED_DATE));
        crop.setCategory(safeString(cursor, COL_CATEGORY));

        return crop;
    }

    private String safeString(Cursor cursor, String column) {
        int idx = cursor.getColumnIndexOrThrow(column);
        return cursor.isNull(idx) ? "" : cursor.getString(idx);
    }

    // ---------------- ACTIVITY LOG CRUD ----------------

    public long addActivity(ActivityLog activity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVITY_CROP_ID, activity.getCropId());
        values.put(COL_ACTIVITY_TYPE, activity.getActivityType());
        values.put(COL_ACTIVITY_DATE, activity.getActivityDate());
        values.put(COL_ACTIVITY_NOTES, activity.getNotes());
        values.put(COL_ACTIVITY_EXPENSE, activity.getExpenseAmount());
        long id = db.insert(TABLE_ACTIVITIES, null, values);
        db.close();
        return id;
    }

    public List<ActivityLog> getActivitiesForCrop(long cropId) {
        List<ActivityLog> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVITIES, null, COL_ACTIVITY_CROP_ID + " = ?",
                new String[]{String.valueOf(cropId)}, null, null, COL_ACTIVITY_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do { activities.add(cursorToActivity(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;
    }

    private ActivityLog cursorToActivity(Cursor cursor) {
        ActivityLog log = new ActivityLog();
        log.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIVITY_ID)));
        log.setCropId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIVITY_CROP_ID)));
        log.setActivityType(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVITY_TYPE)));
        log.setActivityDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVITY_DATE)));
        log.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIVITY_NOTES)));
        log.setExpenseAmount(safeString(cursor, COL_ACTIVITY_EXPENSE));
        return log;
    }

    public void deleteActivity(long activityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, COL_ACTIVITY_ID + " = ?", new String[]{String.valueOf(activityId)});
        db.close();
    }

    public double getTotalExpensesForCrop(long cropId) {
        double total = 0;
        for (ActivityLog log : getActivitiesForCrop(cropId)) {
            String expense = log.getExpenseAmount();
            if (expense != null && !expense.trim().isEmpty()) {
                try { total += Double.parseDouble(expense.trim()); } catch (NumberFormatException ignored) { }
            }
        }
        return total;
    }

    // ---------------- SUMMARY QUERIES ----------------

    public int getTotalCropCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CROPS + " WHERE " + COL_IS_HARVESTED + " = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public int getDistinctPlotCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT " + COL_PLOT_NAME + ") FROM " + TABLE_CROPS +
                " WHERE " + COL_IS_HARVESTED + " = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public List<Crop> getUpcomingHarvests(String todayDate, String cutoffDate) {
        List<Crop> crops = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROPS, null,
                COL_IS_HARVESTED + " = 0 AND " + COL_HARVEST_DATE + " >= ? AND " + COL_HARVEST_DATE + " <= ?",
                new String[]{todayDate, cutoffDate}, null, null, COL_HARVEST_DATE + " ASC");
        if (cursor.moveToFirst()) {
            do { crops.add(cursorToCrop(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return crops;
    }
}