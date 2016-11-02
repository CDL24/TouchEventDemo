package com.reseeit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.reseeit.com.reseeit.listeners.ActivityResult;

import java.io.File;

public class SelectImage {
    private Uri fileUri;
    private Activity activity;
    private ActivityResult activityResult;
    private static SelectImage mInstance;
    public static final char GALLERY_SELECTED = 'G';
    public static final char CAMERA_SELECTED = 'C';

    public SelectImage(ActivityResult activityResult, Activity activity,
                       char cameraOrGallery) {
        this.activity = activity;
        this.activityResult = activityResult;
        mInstance = this;
        if (cameraOrGallery == CAMERA_SELECTED) {
            cameraSelected();
        } else if (cameraOrGallery == GALLERY_SELECTED) {
            gallerySelected();
        }
    }

    public static SelectImage getInstance() {
        return mInstance;
    }

    private boolean isDeviceSupportCamera() {
        if (activity.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    private void cameraSelected() {
        if (isDeviceSupportCamera()) {
            captureImage();
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        activity.startActivityForResult(intent, 100);
    }

    private void gallerySelected() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, 1);
    }

    private File getOutputMediaFile() {
        String folderName = activity.getResources()
                .getString(R.string.app_name);
        if (folderName == null) {
            folderName = "img";
        }
        folderName = "." + folderName;

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                folderName);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("DirectoryCreate", folderName + " directory Error");
                return null;
            }
        }

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "img_" + timeStamp + ".jpg");
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "profile_captured.jpg");

        return mediaFile;
    }

    private String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = activity.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();

        return imgDecodableString;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mInstance = null;
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                activityResult.onResult(fileUri);
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            fileUri = Uri.fromFile(new File(getPath(data.getData())));
            activityResult.onResult(fileUri);
        }

    }

}
