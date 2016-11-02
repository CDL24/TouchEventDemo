package com.reseeit;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.reseeit.com.reseeit.listeners.GcmCallbackListener;
import com.reseeit.models.LoginModel;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.LoginType;
import com.reseeit.util.Utility;
import com.reseeit.util.WebServices;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements LibPostListner {

    private EditText edtEmail, edtPwd;
    private TextView edtEmailError, edtPwdError;
    private ImageView btnBack;
    private PostLibResponse postRequest;
    private int CODE_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        findViews();
        setupAction();
    }

    private void setupAction() {

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtEmail.setHint("Email Address");
                } else {
                    edtEmail.setHint("");
                }
            }
        });

        edtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtPwd.setHint("Password");
                } else {
                    edtPwd.setHint("");
                }
            }
        });

    }

    private void findViews() {
        edtEmail = (EditText) findViewById(R.id.login_et_email);
        edtPwd = (EditText) findViewById(R.id.login_et_password);
        setEditTextFontAvenirItalic(edtPwd, edtEmail);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edtEmailError = (TextView) findViewById(R.id.login_et_email_error);
        setTextViewFontAvenir(edtEmailError);
        edtPwdError = (TextView) findViewById(R.id.login_et_password_error);
        setTextViewFontAvenir(edtPwdError, (TextView) findViewById(R.id.toolbar_done));

        setTextViewFontAvenirBold((TextView) findViewById(R.id.toolbar_title), (TextView) findViewById(R.id.tvLoginEmail), (TextView) findViewById(R.id.tvLoginPassword));

    }

    @Override
    public void onBackPressed() {
        Util.startAct(LoginActivity.this, LoginWithActivity.class, 4);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_done:
                if (isValid())
                    login();
                break;
        }
    }

    private boolean isValid() {
        Utility.hideKeyboard(getApplicationContext(), edtEmail);
        edtEmailError.setVisibility(View.GONE);
        edtPwdError.setVisibility(View.GONE);
        if (!Utility.hasConnection(getApplicationContext())) {
            noConnection();
            return false;
        } else if (edtEmail.getText().toString().trim().equals("")) {
//            edtEmailError.setText("Please Enter Email");
//            edtEmailError.setVisibility(View.VISIBLE);
            edtEmail.requestFocus();
            alertWarning("Please Enter Email");
            return false;
        } else if (!Utility.isEmailValid(edtEmail.getText().toString().trim())) {
//            edtEmailError.setText("Please Enter Valid Email");
//            edtEmailError.setVisibility(View.VISIBLE);
            edtEmail.requestFocus();
            alertWarning("Please Enter Valid Email");
            return false;
        } else if (edtPwd.getText().toString().trim().equals("")) {
//            edtPwdError.setText("Please Enter Password");
//            edtPwdError.setVisibility(View.VISIBLE);
            edtPwd.requestFocus();
            alertWarning("Please Enter Password");
            return false;
        } else if (mPrefs.getGCMKey().equals("")) {
            generateDeviceToken();
//            alertError("Device not identified");
            return false;
        }
        return true;
    }

    private void generateDeviceToken() {
        ((ReSeeItApp) getApplication()).setGcmCallback(new GcmCallbackListener() {
            @Override
            public void onGcmCallback(boolean isSuccess) {
                if (isSuccess) {
                    login();
                } else {
                    alertError("Device not identified", "Make sure your device has working internet connection and updated google play service");
                }
            }
        });
        ((ReSeeItApp) getApplication()).generateDeviceToken();
    }

    private void login() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("email", edtEmail.getText().toString().trim());
        parmas.put("pass", edtPwd.getText().toString().trim());
        parmas.put("device_token", mPrefs.getGCMKey());
        parmas.put("type", LoginType.TYPE_RESEEIT);
        parmas.put("device_type", "android");
        postRequest = new PostLibResponse(LoginActivity.this, new LoginModel(), LoginActivity.this, parmas, WebServices.LOGIN, CODE_LOGIN, true);
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_LOGIN) {
            if (clsGson != null) {
                LoginModel mLoginModel = (LoginModel) clsGson;
                if (mLoginModel.status.equals("1")) {
//                    toast(mLoginModel.message);
                    mPrefs.setUserDetail(mLoginModel.data.get(0));
                    mPrefs.setLogin(true);
                    Util.startAct(LoginActivity.this, MainActivity.class, 4);
                } else {
                    alertError(mLoginModel.message);
                }
            } else {
                alertError("Login In Error");
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_LOGIN) {
            alertError("Login In Error");
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
