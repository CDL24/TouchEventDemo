package com.reseeit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.reseeit.util.MyPrefs;
import com.reseeit.util.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseActivity extends FragmentActivity {// RoboFragmentActivity

    	private static String MSG_TITLE = "";
    public MyPrefs mPrefs;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
		MSG_TITLE = getString(R.string.app_name);
        mPrefs = new MyPrefs(BaseActivity.this);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void toast(String msg) {
        try {
            Utility.toast(BaseActivity.this, "" + msg);
        } catch (Exception e) {
        }
    }
//
//	public void alert(String msg) {
//		toast(msg);
////		new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();
//	}

    public void alertSuccess(String msg) {
//        toast(msg);
        new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();
    }

    public void alertError(String msg) {
//        toast(msg);
        new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();
    }

    public void alertError(String title, String msg) {
//        toast(msg);
        new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText(title).showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();
    }

    public void alertWarning(String msg) {
//        toast(msg);
        new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();
    }

//	public void alertProgress(String msg) {
//		new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();
//	}

//	public void underDevelopment() {
//		toast("Under Development");
//	}

    public void noConnection() {
//        toast("The Internet connection appears to be offline.");
        new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText("The Internet connection appears to be offline.").show();
    }

//	public static void noConnection(Context context) {
//		try {
//			Utility.toast(context, "The Internet connection appears to be offline.");
//		} catch (Exception e) {
//		}//new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText(MSG_TITLE).showContentText(true).setContentText("The Internet connection appears to be offline.").show();
//	}

    public SweetAlertDialog mProgressDialog;

    public void showProgressDialog() {
        mProgressDialog = new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.setTitleText("Please Wait").show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public static class Log {
        public static void d(String msg) {
            if (BuildConfig.DEBUG)
                android.util.Log.d("tag", "" + msg);
        }

        public static void w(String msg) {
            if (BuildConfig.DEBUG)
                android.util.Log.w("tag", "" + msg);
        }

        public static void e(String msg) {
            if (BuildConfig.DEBUG)
                android.util.Log.e("tag", "" + msg);
        }

        public static void d(int msg) {
            if (BuildConfig.DEBUG)
                android.util.Log.d("tag", "" + msg);
        }

        public static void d(Throwable throwable) {
            if (BuildConfig.DEBUG)
                android.util.Log.d("tag", "Throw:", throwable);
        }

        public static void d(Exception e) {
            if (BuildConfig.DEBUG)
                android.util.Log.d("tag", "Error:", e);
        }
    }

    public void setTextViewFontAvenir(TextView... textViews) {
        Typeface type = Typeface.createFromAsset(getAssets(), "avenir-light.ttf");
        for (int viewCounter = 0; viewCounter < textViews.length; viewCounter++) {
            textViews[viewCounter].setTypeface(type);
        }
    }

    public void setTextViewFontAvenirBold(TextView... textViews) {
        Typeface type = Typeface.createFromAsset(getAssets(), "avenir_black.ttf");
        for (int viewCounter = 0; viewCounter < textViews.length; viewCounter++) {
            textViews[viewCounter].setTypeface(type, Typeface.BOLD);
        }
    }

    public void setEditTextFontAvenirItalic(EditText... editTexts) {
        Typeface type = Typeface.createFromAsset(getAssets(), "avenir-light.ttf");
        for (int viewCounter = 0; viewCounter < editTexts.length; viewCounter++) {
            editTexts[viewCounter].setTypeface(type, Typeface.ITALIC);
        }
    }

    public void setTextViewFontAvenirItalic(TextView... textViews) {
        Typeface type = Typeface.createFromAsset(getAssets(), "avenir-light.ttf");
        for (int viewCounter = 0; viewCounter < textViews.length; viewCounter++) {
            textViews[viewCounter].setTypeface(type, Typeface.ITALIC);
        }
    }

}
