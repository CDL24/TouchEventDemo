package com.reseeit.net;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.reseeit.MainActivity;
import com.reseeit.models.ImageItem;
import com.reseeit.models.ReceiptResponseModel;
import com.reseeit.util.GalleryManager;
import com.reseeit.util.MyPrefs;
import com.reseeit.util.WebServices;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ImageUploadService extends Service {

    private UploadAsync mUploadAsync;
    private GalleryManager galleryManager;
    private String userId = "";

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userId = new MyPrefs(getApplicationContext()).getUserDetail().user_id;
        galleryManager = new GalleryManager(getApplicationContext());
        Log.d("Tag", "Upload Service Started.......");
        uploadNext();
    }

    private void uploadNext() {
        ImageItem uploadItem = galleryManager.getImageToSync();
        if (uploadItem != null) {
            mUploadAsync = new UploadAsync(uploadItem);
            mUploadAsync.execute();
        } else {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Tag", "Upload Service ended.......");
    }

    private class UploadAsync extends AsyncTask<Void, String, String> {

        private ImageItem uploadItem;

        public UploadAsync(ImageItem uploadItem) {
            this.uploadItem = uploadItem;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (getApplicationContext() == null)
                    return null;
                Log.d("Tag", "Upload Image request");
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(WebServices.UPLOAD_RECEIPT);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("receipt_img", new FileBody(new File(uploadItem.imagePath)));
                entity.addPart("user_id", new StringBody(userId));
                entity.addPart("pro_id", new StringBody("1"));
                entity.addPart("img_text", new StringBody("Test Receipt"));
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String sResponse = "", line;
                while ((line = reader.readLine()) != null) {
                    sResponse += line;
                }
                return sResponse;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Tag", "Upload Image error" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            super.onPostExecute(sResponse);
            Log.d("Tag", "Upload Image Response : " + sResponse);
            if (sResponse == null) {
                return;
            }
            try {
                if (sResponse != null && !sResponse.equals("")) {
                    ReceiptResponseModel mStatus = new Gson().fromJson(sResponse, ReceiptResponseModel.class);
                    if (mStatus != null && mStatus.status.equals("1")) {
                        galleryManager.updateImageID(uploadItem.imagePath,mStatus.receipt_id);
                        refreshGalleryList();
//                        NotifyReceiptUpload(uploadItem);
                    }
                }
            } catch (Exception e) {
            }
            finally {
                uploadNext();
            }
        }
    }

    private void refreshGalleryList() {
        try{
        Intent mIntent = new Intent(MainActivity.INTENT_REFRESH_GALLERY_LIST);
        sendBroadcast(mIntent);}catch (Exception e){}
    }

//    public void NotifyReceiptUpload(ImageItem imageItem) {
//        NotificationModel notificationModel = new NotificationModel();
//        notificationModel.userdata = notificationModel.new UserData();
//        notificationModel.userdata.EarningPoint="0";
//        notificationModel.userdata.status=GalleryManager.STATUS_APPROVED;
//        notificationModel.userdata.receipt_id=String.valueOf(imageItem.receipt_id);
//        notificationModel.userdata.user_id=userId;
//
//        Bundle notificationBundle = new Bundle();
//        notificationBundle.putString("0", new Gson().toJson(notificationModel));
//
//        Bundle mBundle = new Bundle();
//        mBundle.putBundle("Extra",notificationBundle);
//
//        Intent mIntent = new Intent(MainActivity.INTENT_REFRESH_GALLERY_STATUS);
//        mIntent.putExtras(mBundle);
//        sendBroadcast(mIntent);
//    }
}