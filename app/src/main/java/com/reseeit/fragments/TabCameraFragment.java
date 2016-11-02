package com.reseeit.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.reseeit.BaseActivity;
import com.reseeit.MainActivity;
import com.reseeit.R;
import com.reseeit.ReSeeItApp;
import com.reseeit.models.Ad;
import com.reseeit.models.AdModel;
import com.reseeit.models.ImageItem;
import com.reseeit.models.ReceiptResponseModel;
import com.reseeit.net.ImageUploadService;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.GalleryManager;
import com.reseeit.util.Utility;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("deprecation")
public class TabCameraFragment extends BaseFragment implements Callback, LibPostListner {

    private View mView;
    private ImageView btnGallery, imgAds;
    private GalleryManager mGalleryManager;
    ImageButton btnFlash, btnFFC;
    private PostLibResponse postRequest;
    private final int CODE_ADS = 1;
    private String imagePath;
    private UploadAsync uploadAsync;
    private String imageName;
    private LinearLayout llSaving;
    private AdModel adModel;
    private long adInterval = 1000 * 30;
    private final int MAX_WIDTH = 800;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_tab_camera, null);
        try {
            getActivity().registerReceiver(cameraButtonReceiver, new IntentFilter(MainActivity.INTENT_CAMERA_BUTTON_CLICK));
        } catch (Exception e) {
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionTitle(getGreetingsMessage());
        initSurface();
        llSaving = (LinearLayout) mView.findViewById(R.id.ll_saving);
        mGalleryManager = new GalleryManager(getActivity());
        imgAds = (ImageView) mView.findViewById(R.id.camera_img_banner);
        btnGallery = (ImageView) mView.findViewById(R.id.camera_btn_gallery);
        btnGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mFragment = new ReceiptListFragment();
                ((MainActivity) getActivity()).setSubGalleryFragment(mFragment, true);
            }
        });
        btnFlash = (ImageButton) mView.findViewById(R.id.camera_btn_flash);
        btnFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFlash.setSelected(!btnFlash.isSelected());
                isFlashEnabled = btnFlash.isSelected();
            }
        });
        btnFFC = (ImageButton) mView.findViewById(R.id.camera_btn_ffc);
        btnFFC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFFC.setSelected(!btnFFC.isSelected());
                isFFC = btnFFC.isSelected();
                btnFlash.setSelected(false);
                isFlashEnabled = false;
                surfaceDestroyed(null);
                surfaceCreated(null);
            }
        });
        if (adModel == null || adModel.data == null || adModel.data.size() == 0)
            loadAds();
        else
            reloadAds();
    }

    private void loadAds() {
        if (postRequest != null) {
            postRequest.cancel(true);
            postRequest = null;
        }
        postRequest = new PostLibResponse(TabCameraFragment.this, new AdModel(), getActivity(), new HashMap<String, String>(), WebServices.IN_APP_ADS, CODE_ADS, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAds();
    }

    private void uploadReceipt() {
//        uploadAsync = new UploadAsync();
//        uploadAsync.execute();

//        Picasso.with(getActivity()).load(new File(imagePath)).resize(50, 50).into(mTarget);
        ImageItem imageItem = new ImageItem();
        imageItem.receipt_id = -1;
        imageItem.imagePath = imagePath;
        imageItem.imageStatus = GalleryManager.STATUS_PENDING;
        imageItem.imageTime = imageName.replace(".jpg", "");
        mGalleryManager.add(imageItem);
//        alertSuccess("Receipt Added Successfully.");
        if (Utility.hasConnection(getActivity()))
            try {
                getActivity().startService(new Intent(getActivity(), ImageUploadService.class));
            } catch (Exception e) {
            }
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_ADS) {
            if (clsGson != null) {
                AdModel mAdModel = (AdModel) clsGson;
                if (mAdModel.status.equals("1")) {
                    if (mAdModel.data != null) {
                        this.adModel = mAdModel;
                    }
                }
            }
            reloadAds();
        }
    }

    private void reloadAds() {
        if (adModel == null || adModel.data == null || adModel.data.size() == 0) {
            imgAds.setVisibility(View.INVISIBLE);
            return;
        }
        if (currentAdIndex == -1) {
            reOrderAds();
            currentAdIndex = 0;
        }
        if (adModel.data.get(currentAdIndex) != null && adModel.data.get(currentAdIndex).app_img != null && !adModel.data.get(currentAdIndex).app_img.equals("")) {
            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
            imgLoader.get(adModel.image_path + adModel.data.get(currentAdIndex).app_img, ImageLoader.getImageListener(imgAds, 0, 0));
        }
        imgAds.setVisibility(View.VISIBLE);
        AdsTimer.start();
    }

    private void printAds() {
        String ids = "";
        for (Ad ad : adModel.data) {
            ids += " " + ad.app_ads_id;
        }
//        Log.d("Tag",ids);
    }

    private void reOrderAds() {
        Collections.shuffle(adModel.data);
        printAds();
    }

    private int currentAdIndex = -1;
    private CountDownTimer AdsTimer = new CountDownTimer(adInterval, adInterval) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (currentAdIndex == adModel.data.size() - 1) {
                currentAdIndex = -1;
                reOrderAds();
            }
            currentAdIndex++;
            Log.d("Tag", adModel.data.get(currentAdIndex).app_ads_id);
            if (adModel.data.get(currentAdIndex) != null && adModel.data.get(currentAdIndex).app_img != null && !adModel.data.get(currentAdIndex).app_img.equals("")) {
                ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
                imgLoader.get(adModel.image_path + adModel.data.get(currentAdIndex).app_img, ImageLoader.getImageListener(imgAds, 0, 0));
            }
        }

        @Override
        public void onFinish() {
            start();
        }
    };

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        reloadAds();
    }

    private class UploadAsync extends AsyncTask<Void, String, String> {
        private boolean isRunning = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning = true;
            showProgressDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (getActivity() == null)
                    return null;
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(WebServices.UPLOAD_RECEIPT);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("receipt_img", new FileBody(new File(imagePath)));
                entity.addPart("user_id", new StringBody(userId));
                entity.addPart("pro_id", new StringBody("1"));
                entity.addPart("img_text", new StringBody("Test Receipt"));
                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost, localContext);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String sResponse = "", line = null;
                while ((line = reader.readLine()) != null) {
                    sResponse += line;
                }
                return sResponse;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            super.onPostExecute(sResponse);
            dismissProgressDialog();
            BaseActivity.Log.d(sResponse);
            if (sResponse == null) {
                isRunning = false;
                return;
            }
            try {
                if (sResponse != null && !sResponse.equals("")) {
                    ReceiptResponseModel mStatus = new Gson().fromJson(sResponse, ReceiptResponseModel.class);
                    if (mStatus != null && mStatus.status.equals("1")) {
//                        alertSuccess(mStatus.message);
                        ImageItem imageItem = new ImageItem();
                        imageItem.imagePath = imagePath;
                        imageItem.imageStatus = GalleryManager.STATUS_PENDING;
                        imageItem.imageTime = imageName.replace(".jpg", "");
                        imageItem.receipt_id = mStatus.receipt_id;
                        mGalleryManager.add(imageItem);
                    } else {
                        alertError(mStatus.message);
                    }
                }
            } catch (Exception e) {
                alertError("Receipt upload failed");
            }
            isRunning = false;
        }
    }

    private boolean isFlashEnabled, isFFC;

    /******************
     * Surface code
     *******************/
    private Camera camera;
    private Camera.Parameters param;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private PictureCallback jpegCallback;
    private boolean hasFlash;
    private boolean hasAutoFocus;
    private boolean hasFFC;
    public static int degrees;

    public void captureImage() throws IOException {
        // take the picture
        isCapturing = true;
        llSaving.setVisibility(View.VISIBLE);
        if (hasFlash) {
            param.setFlashMode(isFlashEnabled ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
        }
        if (hasAutoFocus)
            camera.autoFocus(autoFocusCallback);
        else
            camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }
        if (hasFlash && param != null) {
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        refreshCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(isFFC ? (hasFFC ? 1 : 0) : 0);
            setCameraDisplayOrientation();
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }
        param = camera.getParameters();
        if (hasFlash)
            param.setFlashMode(isFlashEnabled ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
        param.setRotation(degrees);
        List<Size> sizes = param.getSupportedPreviewSizes();
        for (Size size : sizes) {
            if (size.width <= MAX_WIDTH) {
                param.setPreviewSize(size.width, size.height);
                break;
            }
        }
        sizes = param.getSupportedPictureSizes();
        for (Size size : sizes) {
            if (size.width <= MAX_WIDTH) {
                param.setPictureSize(size.width, size.height);
                break;
            }
        }
        // List<Size> sizes = param.getSupportedPictureSizes();
        // param.setPreviewSize(sizes.get(0).width, sizes.get(0).height);
        // param.setPictureSize(800, 600);

        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        camera = null;
        isCapturing = false;
    }

    private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.autoFocus(null);
            camera.takePicture(null, null, jpegCallback);
        }
    };
    private AutoFocusCallback refreshFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.autoFocus(null);
        }
    };

    public void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(isFFC ? (hasFFC ? 1 : 0) : 0, info);
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            degrees = result;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
            degrees = result;
        }
        camera.setDisplayOrientation(result);
        // degrees = info.orientation;
    }

    private boolean isCapturing = false;

    private void initSurface() {
        degrees = 0;
        surfaceView = (SurfaceView) mView.findViewById(R.id.camera_surface);
        surfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(null);
//                try {
//                    if (isCapturing == false)
//                        captureImage();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    isCapturing = false;
//                }
            }
        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceHolder.addCallback(TabCameraFragment.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try {
            hasFlash = getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        } catch (Exception e) {
        }
        try {
            hasAutoFocus = getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
        } catch (Exception e) {
        }
        try {
            hasFFC = getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
            btnFFC.setEnabled(hasFFC);
        } catch (Exception e) {
        }
        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    ((ReSeeItApp) getActivity().getApplication()).playCameraSound();
                    imageName = getFileName();
                    imagePath = MainActivity.PATH_CAMERA_IMAGE + imageName;
                    outStream = new FileOutputStream(imagePath);
                    outStream.write(resizeImage(data));
                    outStream.close();
                    llSaving.setVisibility(View.GONE);
//                    toast("Receipt captured");
                    uploadReceipt();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                isCapturing = false;
                refreshCamera();
            }
        };
    }

    byte[] resizeImage(byte[] input) {
        Bitmap original = BitmapFactory.decodeByteArray(input, 0, input.length);
        int[] bitmapSize = new int[]{original.getWidth(), original.getHeight()};
//        bitmapSize= calculateBitmapSampleSize(bitmapSize[0],bitmapSize[1]);

//        Bitmap resized = Bitmap.createScaledBitmap(original, original.getWidth(), original.getHeight(), false);
        int w = original.getWidth();
        int h = original.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degrees);

        Bitmap resized =  Bitmap.createBitmap(original, 0, 0, w, h, mtx, true);
//        matrix.setRotate(getRotation());
//        Bitmap resized = Bitmap.createBitmap(original,0,0, original.getWidth(), original.getHeight(),matrix, false);
//        Bitmap resized = Bitmap.createBitmap(original, 0, 0, bitmapSize[0], bitmapSize[1], matrix, false);
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 92, blob);
        return blob.toByteArray();
    }


    private int[] calculateBitmapSampleSize(int outWidth, int outHeight) {
        int maxHeight = 1280;
        int maxWidth = 720;
        if (outHeight <= maxHeight || outWidth <= maxWidth) {
            return new int[]{outWidth, outHeight};
        }
        try {
            return new int[]{outWidth - ((outWidth % maxWidth) * maxWidth), outHeight - ((outHeight % maxHeight) * maxHeight)};
        } catch (Exception e) {
            return new int[]{maxWidth, maxHeight};
        }
    }

//    private int getImageOrientation() {
//        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
//        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
//        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                imageColumns, null, null, imageOrderBy);
//
//        if (cursor.moveToFirst()) {
//            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
//            cursor.close();
//            return orientation;
//        } else {
//            return 0;
//        }
//    }

//    Target mTarget = new Target() {
//        @Override
//        public void onPrepareLoad(Drawable arg0) {
//        }
//
//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
//            // btnGallery.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
//            btnGallery.setImageBitmap(bitmap);
//
//        }
//
//        @Override
//        public void onBitmapFailed(Drawable arg0) {
//        }
//    };

    private String getFileName() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpg";
    }

    @Override
    public void onDestroyView() {
        try {
            getActivity().unregisterReceiver(cameraButtonReceiver);
        } catch (Exception e) {
        }
        if (uploadAsync != null && uploadAsync.isRunning) {
            uploadAsync.cancel(true);
            uploadAsync = null;
        }
        if (postRequest != null) {
            postRequest.cancel(true);
            postRequest = null;
        }
        isFlashEnabled = false;
        super.onDestroyView();
    }

    private BroadcastReceiver cameraButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isCapturing == false)
                    captureImage();
            } catch (IOException e) {
                e.printStackTrace();
                isCapturing = false;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        AdsTimer.cancel();
    }
}
