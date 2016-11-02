package com.reseeit;

import android.os.Bundle;

import com.reseeit.models.LoginModel;
import com.reseeit.models.UserDetail;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.MyPrefs;
import com.reseeit.util.Utility;
import com.reseeit.util.WebServices;

import java.util.HashMap;
import java.util.Map;

public class AutoLoginActivity extends BaseActivity implements LibPostListner {

    private PostLibResponse postRequest;
    private int CODE_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_autologin);
        if (isValid()) {
            showProgressDialog();
            login();
        } else {
            Util.startAct(AutoLoginActivity.this, MainActivity.class, 4);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }


    private boolean isValid() {
        Utility.hideKeyboard(getApplicationContext(), findViewById(R.id.RelativeLayout1));
        if (!Utility.hasConnection(getApplicationContext())) {
//            alertError("The Internet connection appears to be offline.");
//            AutoLoginActivity.this.finish();
            return false;
        }
        return true;
    }

    private void login() {
        Map<String, String> parmas = new HashMap<String, String>();
        UserDetail user = mPrefs.getUserDetail();
        parmas.put("email", user.email);
        parmas.put("device_token", mPrefs.getGCMKey());
        parmas.put("device_type", "android");
        postRequest = new PostLibResponse(AutoLoginActivity.this, new LoginModel(), AutoLoginActivity.this, parmas, WebServices.AUTO_LOGIN, CODE_LOGIN, true);
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_LOGIN) {
            dismissProgressDialog();
            if (clsGson != null) {
                LoginModel mLoginModel = (LoginModel) clsGson;
                if (mLoginModel.status.equals("1")) {
//                    toast(mLoginModel.message);
                    mPrefs.setUserDetail(mLoginModel.data.get(0));
                    mPrefs.setLogin(true);
                    Util.startAct(AutoLoginActivity.this, MainActivity.class, 4);
                } else {
                    alertError(mLoginModel.message);
                    AutoLoginActivity.this.finish();
                }
            } else {
                toast("Login In Error");
                AutoLoginActivity.this.finish();
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_LOGIN) {
//            toast("Login In Error");
//            AutoLoginActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postRequest != null) {
            postRequest.onDestroy();
            postRequest = null;
        }
    }
}
