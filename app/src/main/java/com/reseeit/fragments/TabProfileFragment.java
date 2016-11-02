package com.reseeit.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.reseeit.MainActivity;
import com.reseeit.R;
import com.reseeit.ReSeeItApp;
import com.reseeit.com.reseeit.listeners.ActivityResult;
import com.reseeit.models.ProfileUploadModel;
import com.reseeit.models.UserDetail;
import com.reseeit.util.MyPrefs;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class TabProfileFragment extends BaseFragment implements ActivityResult {

    private View mView;
    private TextView edtFullName, edtEmail;
    private TextView btnContact, btnPrivacy, btnTerms;
    private CircleImageView imgProfile;
    private MyPrefs mPrefs;
    private RelativeLayout btnTutorial;
    private SelectProfileImageDialog selectImageDialog;
    private UploadAsync uploadAsync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_tab_profile, null);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionTitle("Profile");
        findViews();
        init();
    }

    private void init() {
        mPrefs = new MyPrefs(getActivity());
        UserDetail user = mPrefs.getUserDetail();
        if (user == null)
            return;
        edtFullName.setText(user.fullname);
        edtEmail.setText(user.email);

    }

    private void findViews() {
        imgProfile = (CircleImageView) mView.findViewById(R.id.imgProfile);
        String profileImgUrl = ((MainActivity) getActivity()).profileUrl;
        if (profileImgUrl != null && !profileImgUrl.equals("") && !profileImgUrl.equals("null")) {
            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
            imgLoader.get(profileImgUrl, ImageLoader.getImageListener(imgProfile, R.drawable.ic_profile, R.drawable.ic_profile));
//            Picasso.with(getActivity()).load(profileImgUrl).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imgProfile);
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialog = new SelectProfileImageDialog(getActivity(), TabProfileFragment.this);
                selectImageDialog.show();
            }
        });
//        Bitmap b = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
//        imgProfile.setImageBitmap(scaleCenterCrop(b));
        edtEmail = (TextView) mView.findViewById(R.id.profile_tv_email);
        edtFullName = (TextView) mView.findViewById(R.id.profile_tv_fullname);
        btnContact = (TextView) mView.findViewById(R.id.btnContact);
        btnPrivacy = (TextView) mView.findViewById(R.id.btnPrivacy);
        btnTerms = (TextView) mView.findViewById(R.id.btnTerms);
        btnTutorial = (RelativeLayout) mView.findViewById(R.id.rl_tutorial);
        setFontAvenir((TextView) mView.findViewById(R.id.tvNeedHelp), (TextView) mView.findViewById(R.id.tvReSeeIt), (TextView) mView.findViewById(R.id.tvAnd));
        setFontAvenirItalic(edtEmail, edtFullName);
        setFontAvenirBold((TextView) mView.findViewById(R.id.tvEmail), (TextView) mView.findViewById(R.id.tvFullName), btnContact, btnTerms, btnPrivacy, (TextView) mView.findViewById(R.id.btn_tutorial));
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.sendEmail(getActivity(), new String[]{WebServices.EMAIL_ID_CONTACT_US}, "Contact Us", "");
            }
        });
        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServices.URL_PRIVACY));
                    startActivity(browserIntent);
                } catch (Exception e) {
                }
            }
        });
        btnTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServices.URL_TERMS));
                    startActivity(browserIntent);
                } catch (Exception e) {
                }
            }
        });
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialFragment mFragment = new TutorialFragment();
                ((MainActivity) getActivity()).setSubProfileFragment(mFragment, true);
//                startActivity(new Intent(getActivity(), TutorialActivity.class));
//                getActivity().overridePendingTransition(R.anim.right_in, 0);
            }
        });

    }

    public Bitmap scaleCenterCrop(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
            dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0, srcBmp.getHeight(), srcBmp.getHeight());
        } else {
            dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2, srcBmp.getWidth(), srcBmp.getWidth());
        }

        return dstBmp;
    }

    @Override
    public void onResult(Uri uriImagePath) {
        if (uriImagePath == null) {
            alertWarning("Unsupported file");
            return;
        }
        uriImagePath = copyImage(new File(uriImagePath.getPath()));
//        String mimeExtension = MimeTypeMap.getFileExtensionFromUrl(uriImagePath.getPath());
//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeExtension);

        File file = new File(uriImagePath.getPath());

        if ((file.length() / 1024) > 500) {
            Bitmap bitmap = ShrinkBitmap(uriImagePath.getPath(), 720, 1280);
            try {
                FileOutputStream fos = new FileOutputStream(uriImagePath.getPath());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                android.graphics.Matrix matrix = new android.graphics.Matrix();
//                matrix.setRotate(getExifRotateDegree(file.getPath()));
                String name = Build.MANUFACTURER;
                if (name.contains("samsung"))
                    matrix.setRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (Exception e) {
            }
        }
        file = new File(uriImagePath.getPath());
        Log.d("Tag", "" + file.length());


//        if (mimeType == null || !mimeType.contains("image")) {
//            if (!(file.getName().endsWith("jpg") || file.getName().endsWith("jpeg") || file.getName().endsWith("png") || file.getName().endsWith("tif"))) {
//                alertWarning("Unsupported file");
//                return;
//            }
//        }


        if (uploadAsync != null && uploadAsync.isRunning)
            uploadAsync.cancel(true);
        uploadAsync = new UploadAsync(uriImagePath);
        uploadAsync.execute();

//        if (uriImagePath != null && !uriImagePath.equals("") && !uriImagePath.equals("null"))
//            Picasso.with(getActivity()).load(uriImagePath).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imgProfile);
    }


    public static int getExifRotateDegree(String imagePath) {
        try {
            ExifInterface exif;
            exif = new ExifInterface(imagePath);
            String orientstring = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientstring != null ? Integer.parseInt(orientstring) : ExifInterface.ORIENTATION_NORMAL;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                return 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                return 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                return 270;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    Bitmap ShrinkBitmap(String file, int width, int height) {
        android.graphics.BitmapFactory.Options bmpFactoryOptions = new android.graphics.BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = android.graphics.BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    private Uri copyImage(File src) {
        File dst = new File(MainActivity.PATH_CAMERA_IMAGE, "profile.jpg");
//        if (!dst.exists()) {
//            dst.mkdirs();
//        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            return null;
        }
        return Uri.fromFile(dst);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectImageDialog.onActivityResult(requestCode, resultCode, data);
    }

    private class UploadAsync extends AsyncTask<Void, String, String> {
        private boolean isRunning = false;
        Uri uriImagePath;

        public UploadAsync(Uri uriImagePath) {
            this.uriImagePath = uriImagePath;
        }

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
                HttpPost httpPost = new HttpPost(WebServices.UPLOAD_PROFILE_IMAGE);

//                MultipartEntityBuilder entity = MultipartEntityBuilder.create();
//                entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("user_img", new FileBody(new File(uriImagePath.getPath())));
                entity.addPart("Logged_UserId", new StringBody(userId));

                httpPost.setEntity(entity);
//                httpPost.setEntity(entity.build());
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
            Log.d("Tag", "Upload Image Response : " + sResponse);
            if (sResponse == null) {
                isRunning = false;
                return;
            }
            try {
                if (sResponse != null && !sResponse.equals("")) {
                    ProfileUploadModel mProfileModel = new Gson().fromJson(sResponse, ProfileUploadModel.class);
                    if (mProfileModel != null && mProfileModel.status.equals("1")) {
//                        alertSuccess(mProfileModel.message);
                        ((MainActivity) getActivity()).profileUrl = mProfileModel.user_img;
                        UserDetail user = mPrefs.getUserDetail();
                        user.user_img = mProfileModel.user_img;
                        mPrefs.setUserDetail(user);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgProfile.setImageURI(uriImagePath);
//                                String profileImgUrl = ((MainActivity) getActivity()).profileUrl;
//                                if (profileImgUrl != null && !profileImgUrl.equals("") && !profileImgUrl.equals("null"))
//                                    Picasso.with(getActivity()).load(profileImgUrl).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imgProfile);
                            }
                        });
                    } else {
                        alertError(mProfileModel.message);
                    }
                }
            } catch (Exception e) {
                alertError("Profile update failed");
            }
            isRunning = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (uploadAsync != null && uploadAsync.isRunning) {
            uploadAsync.cancel(true);
            uploadAsync = null;
        }
    }
}
