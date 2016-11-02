package com.reseeit;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.reseeit.com.reseeit.listeners.GcmCallbackListener;
import com.reseeit.util.MyPrefs;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReSeeItApp extends Application {

    private GoogleCloudMessaging gcm;
    private MyPrefs prefs;
    private String regid;
    private String SENDER_ID = "236175858743";
    private static ReSeeItApp mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private GcmCallbackListener mGcmCallback;

    @Override
    public void onCreate() {
        super.onCreate();
//        try {
//            if (!SplashActivity.isDeveloperPreview)
//                Fabric.with(this, new Crashlytics());
//        }catch (Exception e){}
        mInstance = this;
        // RoboGuice.setUseAnnotationDatabases(false);
        SweetAlertDialog.setSecondaryColor(getResources().getColor(R.color.colorPrimary));

        generateDeviceToken();
    }

    public void generateDeviceToken(){
        prefs = new MyPrefs(this.getApplicationContext());
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());
            regid = getRegistrationId(this.getApplicationContext());
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                storeRegistrationId(regid);
            }
        } else {
//            Toast.makeText(getApplicationContext(),"Please update your google play service",Toast.LENGTH_SHORT).show();
            if (mGcmCallback != null)
                mGcmCallback.onGcmCallback(false);
        }
    }



    public static synchronized ReSeeItApp getInstance() {
        return mInstance;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    private class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

        // Note: Your consumer key and secret should be obfuscated in your
        // source code before shipping.
        public LruBitmapCache() {
            super((int) ((Runtime.getRuntime().maxMemory() / 1024) / 8));
        }

        public LruBitmapCache(int sizeInKiloBytes) {
            super(sizeInKiloBytes);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS && resultCode != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            return false;
        } else {
            return true;
        }

//        if (resultCode == ConnectionResult.SUCCESS && resultCode != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
//            return true;
//        } else {
//            return false;
//        }
    }

    private String getRegistrationId(Context context) {
        String registrationId = prefs.getGCMKey();
        if (registrationId.isEmpty()) {
            return "";
        }
        int registeredVersion = prefs.getGCMRegisterVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                storeRegistrationId(regid);
            }

            ;
        }.execute();
    }

    private void storeRegistrationId(String regId) {
        Log.d("Tag", "Key : " + regId);
        prefs.setGCMKey(regId);
        prefs.setGCMRegisterVersion(getAppVersion(getApplicationContext()));
        if (mGcmCallback != null) {
            if (regId == null)
                mGcmCallback.onGcmCallback(false);
            else
                mGcmCallback.onGcmCallback(true);
        }
    }

    public void setGcmCallback(GcmCallbackListener mGcmCallback) {
        this.mGcmCallback = mGcmCallback;
    }

    public MediaPlayer cameraPlayer;

    public void playCameraSound() {
        if (cameraPlayer != null) {
            cameraPlayer.reset();
        } else {
            cameraPlayer = new MediaPlayer();
        }
        AssetFileDescriptor mFileDescriptor;
        try {
            mFileDescriptor = getAssets().openFd("camera_sound.mp3");
            cameraPlayer.setDataSource(mFileDescriptor.getFileDescriptor(), mFileDescriptor.getStartOffset(), mFileDescriptor.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        cameraPlayer.setLooping(false);
        cameraPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        cameraPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                cameraPlayer.reset();
            }
        });
        try {
            cameraPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraPlayer.start();
    }
}
