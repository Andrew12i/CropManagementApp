package com.example.cropmanagementapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cropmanagementapp.model.ActivityLog;
import com.example.cropmanagementapp.model.Crop;
import com.example.cropmanagementapp.model.IncomeLog;
import com.example.cropmanagementapp.model.FinanceEntry;
import com.example.cropmanagementapp.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shamba_tracker.db";
    private static final int DATABASE_VERSION = 5;

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
    public static final String COL_CROP_IMAGE_PATH = "image_path";

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
    public static final String COL_CUSTOM_CROP_IMAGE_PATH = "image_path";

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD_HASH = "password_hash";
    public static final String COL_PASSWORD_SALT = "password_salt";
    public static final String COL_SECURITY_QUESTION = "security_question";
    public static final String COL_SECURITY_ANSWER_HASH = "security_answer_hash";
    public static final String COL_SECURITY_ANSWER_SALT = "security_answer_salt";
    public static final String COL_RECOVERY_CODE_HASH = "recovery_code_hash";
    public static final String COL_RECOVERY_CODE_SALT = "recovery_code_salt";

    public static final String TABLE_INCOME = "income_logs";
    public static final String COL_INCOME_ID = "id";
    public static final String COL_INCOME_CROP_ID = "crop_id";
    public static final String COL_INCOME_DATE = "income_date";
    public static final String COL_INCOME_QUANTITY = "quantity";
    public static final String COL_INCOME_UNIT = "unit";
    public static final String COL_INCOME_RATE = "rate";
    public static final String COL_INCOME_TOTAL = "total_amount";
    public static final String COL_INCOME_BUYER = "buyer_name";
    public static final String COL_INCOME_STATUS = "received_status";
    public static final String COL_INCOME_NOTES = "notes";

    public static final String TABLE_CROP_TYPE_IMAGES = "crop_type_images";
    public static final String COL_IMAGE_CROP_NAME = "crop_name";
    public static final String COL_IMAGE_PATH = "image_path";

    public static final String TABLE_HIDDEN_CROPS = "hidden_crop_types";
    public static final String COL_HIDDEN_CROP_NAME = "crop_name";

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
                COL_CATEGORY + " TEXT, " +
                COL_CROP_IMAGE_PATH + " TEXT" +
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
                COL_CUSTOM_CROP_CATEGORY + " TEXT NOT NULL, " +
                COL_CUSTOM_CROP_IMAGE_PATH + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_PASSWORD_SALT + " TEXT NOT NULL, " +
                COL_SECURITY_QUESTION + " TEXT NOT NULL, " +
                COL_SECURITY_ANSWER_HASH + " TEXT NOT NULL, " +
                COL_SECURITY_ANSWER_SALT + " TEXT NOT NULL, " +
                COL_RECOVERY_CODE_HASH + " TEXT NOT NULL, " +
                COL_RECOVERY_CODE_SALT + " TEXT NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_INCOME + " (" +
                COL_INCOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_INCOME_CROP_ID + " INTEGER NOT NULL, " +
                COL_INCOME_DATE + " TEXT NOT NULL, " +
                COL_INCOME_QUANTITY + " TEXT, " +
                COL_INCOME_UNIT + " TEXT, " +
                COL_INCOME_RATE + " TEXT, " +
                COL_INCOME_TOTAL + " TEXT NOT NULL, " +
                COL_INCOME_BUYER + " TEXT, " +
                COL_INCOME_STATUS + " TEXT NOT NULL, " +
                COL_INCOME_NOTES + " TEXT, " +
                "FOREIGN KEY(" + COL_INCOME_CROP_ID + ") REFERENCES " +
                TABLE_CROPS + "(" + COL_CROP_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_CROP_TYPE_IMAGES + " (" +
                COL_IMAGE_CROP_NAME + " TEXT PRIMARY KEY, " +
                COL_IMAGE_PATH + " TEXT NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_HIDDEN_CROPS + " (" +
                COL_HIDDEN_CROP_NAME + " TEXT PRIMARY KEY" +
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
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_CROPS + " ADD COLUMN " + COL_CROP_IMAGE_PATH + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_CUSTOM_CROPS + " ADD COLUMN " + COL_CUSTOM_CROP_IMAGE_PATH + " TEXT");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COL_EMAIL + " TEXT, " +
                    COL_PASSWORD_HASH + " TEXT NOT NULL, " +
                    COL_PASSWORD_SALT + " TEXT NOT NULL, " +
                    COL_SECURITY_QUESTION + " TEXT NOT NULL, " +
                    COL_SECURITY_ANSWER_HASH + " TEXT NOT NULL, " +
                    COL_SECURITY_ANSWER_SALT + " TEXT NOT NULL, " +
                    COL_RECOVERY_CODE_HASH + " TEXT NOT NULL, " +
                    COL_RECOVERY_CODE_SALT + " TEXT NOT NULL" +
                    ");");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INCOME + " (" +
                    COL_INCOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_INCOME_CROP_ID + " INTEGER NOT NULL, " +
                    COL_INCOME_DATE + " TEXT NOT NULL, " +
                    COL_INCOME_QUANTITY + " TEXT, " +
                    COL_INCOME_UNIT + " TEXT, " +
                    COL_INCOME_RATE + " TEXT, " +
                    COL_INCOME_TOTAL + " TEXT NOT NULL, " +
                    COL_INCOME_BUYER + " TEXT, " +
                    COL_INCOME_STATUS + " TEXT NOT NULL, " +
                    COL_INCOME_NOTES + " TEXT" +
                    ");");
        }
        if (oldVersion < 5) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CROP_TYPE_IMAGES + " (" +
                    COL_IMAGE_CROP_NAME + " TEXT PRIMARY KEY, " +
                    COL_IMAGE_PATH + " TEXT NOT NULL" +
                    ");");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_HIDDEN_CROPS + " (" +
                    COL_HIDDEN_CROP_NAME + " TEXT PRIMARY KEY" +
                    ");");
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ---------------- USER / ACCOUNT CRUD ----------------

    public long addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_EMAIL, isEmpty(user.getEmail()) ? null : user.getEmail());
        values.put(COL_PASSWORD_HASH, user.getPasswordHash());
        values.put(COL_PASSWORD_SALT, user.getPasswordSalt());
        values.put(COL_SECURITY_QUESTION, user.getSecurityQuestion());
        values.put(COL_SECURITY_ANSWER_HASH, user.getSecurityAnswerHash());
        values.put(COL_SECURITY_ANSWER_SALT, user.getSecurityAnswerSalt());
        values.put(COL_RECOVERY_CODE_HASH, user.getRecoveryCodeHash());
        values.put(COL_RECOVERY_CODE_SALT, user.getRecoveryCodeSalt());
        long id = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return id;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                "LOWER(" + COL_USERNAME + ") = LOWER(?)", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean emailExists(String email) {
        if (isEmpty(email)) return false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                "LOWER(" + COL_EMAIL + ") = LOWER(?)", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public User getUserByIdentifier(String identifier) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "LOWER(" + COL_USERNAME + ") = LOWER(?) OR LOWER(" + COL_EMAIL + ") = LOWER(?)",
                new String[]{identifier, identifier}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) user = cursorToUser(cursor);
        cursor.close();
        db.close();
        return user;
    }

    public void updatePassword(long userId, String newHash, String newSalt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD_HASH, newHash);
        values.put(COL_PASSWORD_SALT, newSalt);
        db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));
        user.setEmail(safeString(cursor, COL_EMAIL));
        user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_HASH)));
        user.setPasswordSalt(cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_SALT)));
        user.setSecurityQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COL_SECURITY_QUESTION)));
        user.setSecurityAnswerHash(cursor.getString(cursor.getColumnIndexOrThrow(COL_SECURITY_ANSWER_HASH)));
        user.setSecurityAnswerSalt(cursor.getString(cursor.getColumnIndexOrThrow(COL_SECURITY_ANSWER_SALT)));
        user.setRecoveryCodeHash(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECOVERY_CODE_HASH)));
        user.setRecoveryCodeSalt(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECOVERY_CODE_SALT)));
        return user;
    }

    // ---------------- CROP TYPE IMAGE OVERRIDES ----------------

    public void setCropTypeImage(String cropName, String imagePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IMAGE_CROP_NAME, cropName);
        values.put(COL_IMAGE_PATH, imagePath);
        db.insertWithOnConflict(TABLE_CROP_TYPE_IMAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getCropTypeImage(String cropName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CROP_TYPE_IMAGES, null, COL_IMAGE_CROP_NAME + " = ?",
                new String[]{cropName}, null, null, null);
        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_PATH));
        }
        cursor.close();
        db.close();
        return path;
    }

    public void removeCropTypeImage(String cropName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CROP_TYPE_IMAGES, COL_IMAGE_CROP_NAME + " = ?", new String[]{cropName});
        db.close();
    }

    // ---------------- HIDDEN (DEFAULT) CROP TYPES ----------------

    public void hideCropType(String cropName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HIDDEN_CROP_NAME, cropName);
        db.insertWithOnConflict(TABLE_HIDDEN_CROPS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public Set<String> getHiddenCropTypes() {
        Set<String> hidden = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HIDDEN_CROPS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                hidden.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_HIDDEN_CROP_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hidden;
    }

    // ---------------- CUSTOM CROP TYPE CRUD ----------------

    public long addCustomCropType(String name, String category, String imagePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CUSTOM_CROP_NAME, name);
        values.put(COL_CUSTOM_CROP_CATEGORY, category);
        values.put(COL_CUSTOM_CROP_IMAGE_PATH, imagePath);
        long id = db.insertWithOnConflict(TABLE_CUSTOM_CROPS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return id;
    }

    public void deleteCustomCropType(String cropName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CUSTOM_CROPS, COL_CUSTOM_CROP_NAME + " = ?", new String[]{cropName});
        db.delete(TABLE_CROP_TYPE_IMAGES, COL_IMAGE_CROP_NAME + " = ?", new String[]{cropName});
        db.close();
    }

    public List<String[]> getCustomCropTypes() {
        List<String[]> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOM_CROPS, null, null, null, null, null, COL_CUSTOM_CROP_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOM_CROP_NAME));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CUSTOM_CROP_CATEGORY));
                String imagePath = safeString(cursor, COL_CUSTOM_CROP_IMAGE_PATH);
                items.add(new String[]{name, category, imagePath});
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
        values.put(COL_CROP_IMAGE_PATH, crop.getImagePath());
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
        values.put(COL_CROP_IMAGE_PATH, crop.getImagePath());
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
        db.delete(TABLE_INCOME, COL_INCOME_CROP_ID + " = ?", new String[]{String.valueOf(cropId)});
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
        crop.setImagePath(safeString(cursor, COL_CROP_IMAGE_PATH));

        return crop;
    }

    private String safeString(Cursor cursor, String column) {
        int idx = cursor.getColumnIndexOrThrow(column);
        return cursor.isNull(idx) ? "" : cursor.getString(idx);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
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

    // ---------------- INCOME CRUD ----------------

    public long addIncome(IncomeLog income) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INCOME_CROP_ID, income.getCropId());
        values.put(COL_INCOME_DATE, income.getIncomeDate());
        values.put(COL_INCOME_QUANTITY, income.getQuantity());
        values.put(COL_INCOME_UNIT, income.getUnit());
        values.put(COL_INCOME_RATE, income.getRate());
        values.put(COL_INCOME_TOTAL, income.getTotalAmount());
        values.put(COL_INCOME_BUYER, income.getBuyerName());
        values.put(COL_INCOME_STATUS, income.getReceivedStatus());
        values.put(COL_INCOME_NOTES, income.getNotes());
        long id = db.insert(TABLE_INCOME, null, values);
        db.close();
        return id;
    }

    public List<IncomeLog> getIncomeForCrop(long cropId) {
        List<IncomeLog> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_INCOME, null, COL_INCOME_CROP_ID + " = ?",
                new String[]{String.valueOf(cropId)}, null, null, COL_INCOME_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do { list.add(cursorToIncome(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    private IncomeLog cursorToIncome(Cursor cursor) {
        IncomeLog income = new IncomeLog();
        income.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_INCOME_ID)));
        income.setCropId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_INCOME_CROP_ID)));
        income.setIncomeDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_INCOME_DATE)));
        income.setQuantity(safeString(cursor, COL_INCOME_QUANTITY));
        income.setUnit(safeString(cursor, COL_INCOME_UNIT));
        income.setRate(safeString(cursor, COL_INCOME_RATE));
        income.setTotalAmount(cursor.getString(cursor.getColumnIndexOrThrow(COL_INCOME_TOTAL)));
        income.setBuyerName(safeString(cursor, COL_INCOME_BUYER));
        income.setReceivedStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_INCOME_STATUS)));
        income.setNotes(safeString(cursor, COL_INCOME_NOTES));
        return income;
    }

    public void deleteIncome(long incomeId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_INCOME, COL_INCOME_ID + " = ?", new String[]{String.valueOf(incomeId)});
        db.close();
    }

    public double getTotalIncomeForCrop(long cropId) {
        double total = 0;
        for (IncomeLog income : getIncomeForCrop(cropId)) {
            try { total += Double.parseDouble(income.getTotalAmount()); } catch (NumberFormatException ignored) { }
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

    // ---------------- FARM-WIDE FINANCE SUMMARY ----------------

    public double getTotalIncomeAllCrops() {
        double total = 0;
        for (Crop crop : getAllCrops(null)) total += getTotalIncomeForCrop(crop.getId());
        for (Crop crop : getHarvestedCrops(null)) total += getTotalIncomeForCrop(crop.getId());
        return total;
    }

    public double getTotalExpensesAllCrops() {
        double total = 0;
        for (Crop crop : getAllCrops(null)) total += getTotalExpensesForCrop(crop.getId());
        for (Crop crop : getHarvestedCrops(null)) total += getTotalExpensesForCrop(crop.getId());
        return total;
    }

    /** Per-crop income/expense/net breakdown across ALL crops (active and harvested). */
    public List<FinanceEntry> getFinanceBreakdown() {
        List<FinanceEntry> entries = new ArrayList<>();
        for (Crop crop : getAllCrops(null)) {
            entries.add(new FinanceEntry(crop.getCropName(), crop.getPlotName(),
                    getTotalIncomeForCrop(crop.getId()), getTotalExpensesForCrop(crop.getId())));
        }
        for (Crop crop : getHarvestedCrops(null)) {
            entries.add(new FinanceEntry(crop.getCropName(), crop.getPlotName(),
                    getTotalIncomeForCrop(crop.getId()), getTotalExpensesForCrop(crop.getId())));
        }
        return entries;
    }
}