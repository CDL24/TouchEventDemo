package com.reseeit.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FavoriteManager extends SQLiteOpenHelper {

    private SQLiteDatabase favoriteDb;
    private MyPrefs mPrefs;

    private static final String TABLE = "favoriteTable";
    private static final String DATABASE = "FavoriteDb";

    public static final String ID = "favoriteID";
    public static final String DATA = "favoriteData";

    public FavoriteManager(Context context) {
        super(context, DATABASE, null, 1);
        mPrefs = new MyPrefs(context);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public void OpenOrCreateDatabase() {
        favoriteDb = this.getWritableDatabase();
        if (mPrefs.isDbStateFreshFavourite()) {
            favoriteDb.execSQL("create table " + TABLE + "(" + ID + " VARCHAR," + DATA + " VARCHAR)");
            mPrefs.setDbStateFavourite(false);
        }
    }

    /**
     * @param id   coupon id
     * @param data
     * @return 1 for success, 0 for already marked as favorite , and -1 for error
     */
    public int add(String id, String data) {
        int status = -1;
        try {
            this.OpenOrCreateDatabase();

            String selectQuery = "SELECT " + ID + " FROM " + TABLE + " WHERE " + ID + "=?";
            Cursor c = favoriteDb.rawQuery(selectQuery, new String[]{id});
            if (c.getCount() > 0)
                status = 0;
            c.close();
            if (status == -1) {
                favoriteDb.execSQL("insert into " + TABLE + " values('" + id + "','" + data + "')");
                status = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            favoriteDb.close();
        }
        return status;
    }

    /**
     * @param id   coupon id
     * @param data
     * @return 1 for success, 0 for error
     */
    public void update(String id, String data) {
        try {
            this.OpenOrCreateDatabase();

            // Set updated values in ContentValues
            ContentValues newValues = new ContentValues();
            newValues.put(ID, id);
            newValues.put(DATA, data);

            // Set where clause value
            String whereClauseValues[] = {id};

            // Executing update operation
            favoriteDb.update(TABLE, newValues, ID + "=?", whereClauseValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            favoriteDb.close();
        }
    }

    public ArrayList<Favorite> getAll() {
        ArrayList<Favorite> favoriteList = new ArrayList<Favorite>();
        Favorite mFavorite;
        try {
            this.OpenOrCreateDatabase();
            Cursor mCursor;
            mCursor = favoriteDb.rawQuery("select * from " + TABLE, null);
            mCursor.moveToFirst();
            for (int counter = 0; counter < mCursor.getCount(); counter++) {
                mFavorite = new Favorite();
                mFavorite = mFavorite.toFavorite(mCursor.getString(1));
                favoriteList.add(mFavorite);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            favoriteDb.close();
        }
        return favoriteList;
    }

    // public void removeHistory(String imagePath) {
    // try {
    // OpenOrCreateDatabase();
    // favoriteDb.execSQL("delete from " + TABLE + " where " + IMAGE_PATH + "=" + imagePath);
    // } catch (Exception e) {
    // } finally {
    // favoriteDb.close();
    // }
    // }

    // Update Student information
    // takes one argument of Student type
    // pass argument with old roll number and updated name,gender,age,marks
    // public void updateStudentInfo(Student newStudent) {
    // try {
    // // Open Database connection
    // OpenOrCreateDatabase();
    //
    // // Set updated values in ContentValues
    // ContentValues newValues = new ContentValues();
    // newValues.put("name", newStudent.getName());
    // newValues.put("gender", newStudent.getGender());
    // newValues.put("age", newStudent.getAge());
    // newValues.put("marks", newStudent.getMarks());
    //
    // // Set where clause value
    // String whereClauseValues[] = { newStudent.getRno() + "" };
    //
    // // Executing update operation
    // historyDb.update("historyTable", newValues, "rno=?", whereClauseValues);
    // } catch (Exception e) {
    // } finally {
    //
    // // Close database connection
    // historyDb.close();
    // }
    // }

}
