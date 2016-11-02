package com.reseeit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;

import com.reseeit.MainActivity;
import com.reseeit.util.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class BaseFragment extends Fragment {

    protected MainActivity mainActivity;
    private SweetAlertDialog mProgressDialog;
    protected String userName = "", userId = "";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
        userName = this.mainActivity.userName;
        userId = this.mainActivity.userId;
    }

    protected void setActionTitle(String title) {
        mainActivity.setActionTitle(title);
    }

    @Override
    public void onDestroyView() {
        Utility.hideKeyboard(getActivity(), getView());
        super.onDestroyView();
    }

    protected void noConnection() {
        mainActivity.noConnection();
    }

    protected void toast(String msg) {
        mainActivity.toast(msg);
    }

//    public void alert(String msg) {
//        if (mainActivity != null)
//            mainActivity.alert(msg);
//    }

    public void alertSuccess(String msg) {
        if (mainActivity != null)
            mainActivity.alertSuccess(msg);
    }

    public void alertError(String msg) {
        if (mainActivity != null)
            mainActivity.alertError(msg);
    }

    public void alertWarning(String msg) {
        if (mainActivity != null)
            mainActivity.alertWarning(msg);
    }

//    public void alertProgress(String msg) {
//        if (mainActivity != null)
//            mainActivity.alertProgress(msg);
//    }

    public void showProgressDialog() {
        mProgressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.setTitleText("Please Wait").show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    protected String getGreetingsMessage() {
        return mainActivity.getGreetingsMessage();
    }


    protected void refreshFavoriteList() {
        Intent mIntent = new Intent(MainActivity.INTENT_REFRESH_FAVORITE);
        getActivity().sendBroadcast(mIntent);
    }

    protected void refreshCouponList() {
        Intent mIntent = new Intent(MainActivity.INTENT_REFRESH_COUPONS);
        getActivity().sendBroadcast(mIntent);
    }

    protected void setFontAvenir(TextView... textViews) {
        mainActivity.setTextViewFontAvenir(textViews);
    }

    protected void setFontAvenirBold(TextView... textViews){
        mainActivity.setTextViewFontAvenirBold(textViews);
    }

    protected void setFontAvenirItalic(TextView... textViews){
        mainActivity.setTextViewFontAvenirItalic(textViews);
    }
}