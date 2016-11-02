package com.reseeit.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.reseeit.R;
import com.reseeit.ReSeeItApp;
import com.reseeit.com.reseeit.listeners.RedeemListener;
import com.reseeit.models.Coupon;

public class CouponDetailFragmentDialog extends DialogFragment {

    private View mView;
    private TextView btnRedeem;
    private Coupon coupon;
    private String couponBasePath;
    private RedeemListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_coupon_detail_dialog, null);
        mView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return mView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private static CouponDetailFragmentDialog mFragment;

    public static CouponDetailFragmentDialog getInstance() {
        if (mFragment == null)
            mFragment = new CouponDetailFragmentDialog();
        return mFragment;
    }

    public CouponDetailFragmentDialog() {
    }

    public CouponDetailFragmentDialog(Coupon coupon, String couponBasePath, RedeemListener listener) {
        this.coupon = coupon;
        this.couponBasePath = couponBasePath;
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView imgCoupon = (ImageView) mView.findViewById(R.id.img_coupon_detail);
        imgCoupon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CouponDetailFragmentDialog.this.dismiss();
            }
        });
        btnRedeem = (TextView) mView.findViewById(R.id.btn_redeem);
        btnRedeem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRedeemClick(coupon);
                CouponDetailFragmentDialog.this.dismiss();
            }
        });

        if (coupon != null && couponBasePath != null && !couponBasePath.equals("") && coupon.detailed_coupon_img != null && !coupon.detailed_coupon_img.equals("")) {
//            Picasso.with(getActivity()).load(couponBasePath + coupon.detailed_coupon_img).error(R.drawable.image).placeholder(R.drawable.image).into(imgCoupon);
            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
            imgLoader.get(couponBasePath + coupon.detailed_coupon_img, ImageLoader.getImageListener(((ImageView) mView.findViewById(R.id.img_coupon_detail)), R.drawable.reedim_place, R.drawable.reedim_place));
        }
    }
}
