package com.reseeit.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.reseeit.models.ImageDateItemLocal;
import com.reseeit.models.ImageItem;

import java.util.ArrayList;

public class GalleryManager extends SQLiteOpenHelper {

    private SQLiteDatabase galleryDb;
    private MyPrefs mPrefs;

    private static final String TABLE = "galleryTable";
    private static final String DATABASE = "GalleryDb2";
    private static final int DATABASE_VERSION = 1;

    public static final String IMAGE_PATH = "imagePath";
    private static final String IMAGE_STATUS = "imageStatus";
    private static final String IMAGE_TIME = "imageTime";
    public static final String IMAGE_ID = "receipt_id";

    public static final String STATUS_PENDING = "STATUS_PENDING";
    public static final String STATUS_NOT_APPROVED = "STATUS_NOT_APPROVED";
    public static final String STATUS_APPROVED = "Approved";

    public GalleryManager(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
        mPrefs = new MyPrefs(context);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public void OpenOrCreateStudenDatabase() {
        galleryDb = this.getWritableDatabase();
        if (mPrefs.isDbStateFresh()) {
            galleryDb.execSQL("create table " + TABLE + "(" + IMAGE_PATH + " VARCHAR," + IMAGE_STATUS + " VARCHAR," + IMAGE_TIME + " VARCHAR," + IMAGE_ID + " int)");
            mPrefs.setDbState(false);
        }
    }

    public void add(ImageItem image) {
        try {
            this.OpenOrCreateStudenDatabase();
            galleryDb.execSQL("insert into " + TABLE + " values('" + image.imagePath + "','" + image.imageStatus + "','" + image.imageTime + "'," + image.receipt_id + ")");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            galleryDb.close();
        }
    }

    public ArrayList<ImageItem> getAll() {
        ArrayList<ImageItem> imageList = new ArrayList<ImageItem>();
        ImageItem mImage;
        try {
            this.OpenOrCreateStudenDatabase();
            Cursor mCursor;
            mCursor = galleryDb.rawQuery("select * from " + TABLE, null);
            mCursor.moveToFirst();
            for (int studentIndex = 0; studentIndex < mCursor.getCount(); studentIndex++) {
                mImage = new ImageItem();
                mImage.imagePath = mCursor.getString(0);
                mImage.imageStatus = mCursor.getString(1);
                mImage.imageTime = mCursor.getString(2);
                mImage.receipt_id = mCursor.getInt(3);
                imageList.add(mImage);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
        } finally {
            galleryDb.close();
        }
        return imageList;
    }

    public ArrayList<ImageDateItemLocal> getAll_SortByDate() {
        ArrayList<ImageItem> imageList = new ArrayList<>();
        ImageItem mImage;
        try {
            this.OpenOrCreateStudenDatabase();
            Cursor mCursor;
            mCursor = galleryDb.rawQuery("select * from " + TABLE, null);
            mCursor.moveToFirst();
            for (int studentIndex = 0; studentIndex < mCursor.getCount(); studentIndex++) {
                mImage = new ImageItem();
                mImage.imagePath = mCursor.getString(0);
                mImage.imageStatus = mCursor.getString(1);
                mImage.imageTime = mCursor.getString(2);
                mImage.receipt_id = mCursor.getInt(3);
                imageList.add(mImage);
                mCursor.moveToNext();
            }
        } catch (Exception e) {
        } finally {
            galleryDb.close();
        }

        ArrayList<ImageDateItemLocal> imageDateList = new ArrayList<ImageDateItemLocal>();
        int dateCounter = 0;
        String dateString;
        if (imageList.size() > 0) {
            ImageDateItemLocal imageDateItem = new ImageDateItemLocal();
            imageDateItem.imageList = new ArrayList<>();
            imageDateItem.imageList.add(imageList.get(dateCounter));
            imageDateItem.date = Utility.getFormatedImageDate(imageList.get(dateCounter).imageTime);
            imageDateList.add(imageDateItem);
        }
        for (int galleryCounter = 1; galleryCounter < imageList.size(); galleryCounter++) {
            dateString = Utility.getFormatedImageDate(imageList.get(galleryCounter).imageTime);
            if (!dateString.equals(imageDateList.get(dateCounter).date)) {
                ImageDateItemLocal imageDateItem = new ImageDateItemLocal();
                imageDateItem.imageList = new ArrayList<>();
                imageDateItem.imageList.add(imageList.get(galleryCounter));
                imageDateItem.date = dateString;
                imageDateList.add(imageDateItem);
                ++dateCounter;
                continue;
            }
            imageDateList.get(dateCounter).imageList.add(imageList.get(galleryCounter));
        }
        return imageDateList;
    }

    public void removeHistory(String imagePath) {
        try {
            this.OpenOrCreateStudenDatabase();
            galleryDb.execSQL("delete from " + TABLE + " where " + IMAGE_PATH + "='" + imagePath+"'");
        } catch (Exception e) {

        } finally {
            galleryDb.close();
        }
    }

    public void updateStatus(String receiptId, String status) {
        try {
            this.OpenOrCreateStudenDatabase();
            galleryDb.execSQL("update " + TABLE + " set " + IMAGE_STATUS + "='" + status + "' where " + IMAGE_ID + "=" + receiptId);
        } catch (Exception e) {
        } finally {
            galleryDb.close();
        }
    }

    public void updateImageID(String imagePath, int receiptId) {
        try {
            this.OpenOrCreateStudenDatabase();
            galleryDb.execSQL("update " + TABLE + " set " + IMAGE_ID + "=" + receiptId + " where " + IMAGE_PATH + "='" + imagePath+"'");
        } catch (Exception e) {
        } finally {
            galleryDb.close();
        }
    }

    public ImageItem getImageToSync() {
        ImageItem mImage = null;
        try {
            this.OpenOrCreateStudenDatabase();
            Cursor mCursor;
            mCursor = galleryDb.rawQuery("select * from " + TABLE + " where " + IMAGE_ID + "=-1 LIMIT 1", null);
            if(mCursor.moveToFirst()){
                mImage = new ImageItem();
                mImage.imagePath = mCursor.getString(0);
                mImage.imageStatus = mCursor.getString(1);
                mImage.imageTime = mCursor.getString(2);
                mImage.receipt_id = mCursor.getInt(3);
            }
        } catch (Exception e) {
        } finally {
            galleryDb.close();
        }
        return mImage;
    }
}
