package com.reseeit.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.reseeit.R;
import com.reseeit.SelectImage;
import com.reseeit.com.reseeit.listeners.ActivityResult;


public class SelectProfileImageDialog extends Dialog implements
        View.OnClickListener {

    private LinearLayout llCameraSelect, llGallerySelect;
    public static boolean isCamera;
    private Activity act;
    private ActivityResult activityResult;
    private SelectImage selectImage;

    public SelectProfileImageDialog(Activity act, ActivityResult activityResult) {
        super(act);
        this.act = act;
        this.activityResult = activityResult;
    }

    public SelectProfileImageDialog(Activity act) {
        this(act, (ActivityResult) act);
    }

    @SuppressWarnings("deprecation")
    private void roundRectShape() {

        GradientDrawable gdRoundRect = new GradientDrawable();
        gdRoundRect.setCornerRadius(5);
        ((LinearLayout) findViewById(R.id.ll_dialog_main))
                .setBackgroundDrawable(gdRoundRect);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_profile_image);
        getWindow().getAttributes().windowAnimations = R.style.SelectImageDialogAnimation;
        initializeViews();
        roundRectShape();
        llCameraSelect.setOnClickListener(this);
        llGallerySelect.setOnClickListener(this);
    }

    private void initializeViews() {
        llCameraSelect = (LinearLayout) findViewById(R.id.ll_dialog_select_camera);
        llGallerySelect = (LinearLayout) findViewById(R.id.ll_dialog_select_gallery);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dialog_select_camera:
                selectImage = new SelectImage(activityResult, act, SelectImage.CAMERA_SELECTED);
                isCamera = true;
                break;

            case R.id.ll_dialog_select_gallery:
                isCamera = false;
                selectImage = new SelectImage(activityResult, act, SelectImage.GALLERY_SELECTED);
                break;
        }
        this.dismiss();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        selectImage.onActivityResult(requestCode, resultCode, data);
    }
}