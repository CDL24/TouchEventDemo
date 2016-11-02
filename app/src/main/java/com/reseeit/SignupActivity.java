package com.reseeit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.reseeit.models.LoginModel;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.LoginType;
import com.reseeit.util.Utility;
import com.reseeit.util.WebServices;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends BaseActivity implements LibPostListner {


    public static boolean isFromLogin;
    private EditText edtEmail, edtPwd, edtName, edtZip;
    private TextView edtEmailError, edtPwdError, edtNameError, edtZipError, btnPrivacy, btnTerms, btnContact;
    private ImageView btnBack;
    private PostLibResponse postRequest;
    private final int CODE_REGISTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_signup);
        findViews();
        setupAction();
    }

    private void setupAction() {
        edtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtName.setHint("John Deo");
                } else {
                    edtName.setHint("");
                }
            }
        });
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtEmail.setHint("johndeo@reseeit.com");
                } else {
                    edtEmail.setHint("");
                }
            }
        });
        edtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtPwd.setHint("At least one uppercase letter and number");
                } else {
                    edtPwd.setHint("");
                }
            }
        });
        edtZip.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtZip.setHint("11235");
                } else {
                    edtZip.setHint("");
                }
            }
        });

    }

    private void findViews() {
        edtEmail = (EditText) findViewById(R.id.signup_et_email);
        edtPwd = (EditText) findViewById(R.id.signup_et_password);
        edtName = (EditText) findViewById(R.id.signup_et_fullname);
        edtZip = (EditText) findViewById(R.id.signup_et_zip);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnContact = (TextView) findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.sendEmail(SignupActivity.this, new String[]{WebServices.EMAIL_ID_CONTACT_US}, "Contact Us", "");
            }
        });
        edtEmailError = (TextView) findViewById(R.id.signup_et_email_error);
        edtPwdError = (TextView) findViewById(R.id.signup_et_password_error);
        edtNameError = (TextView) findViewById(R.id.signup_et_fullname_error);
        edtZipError = (TextView) findViewById(R.id.signup_et_zip_error);

        btnPrivacy = (TextView) findViewById(R.id.btnPrivacy);
        btnTerms = (TextView) findViewById(R.id.btnTerms);
        btnPrivacy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServices.URL_PRIVACY));
                    startActivity(browserIntent);
                } catch (Exception e) {
                }
            }
        });
        btnTerms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebServices.URL_TERMS));
                    startActivity(browserIntent);
                } catch (Exception e) {
                }
            }
        });
        setTextViewFontAvenir((TextView) findViewById(R.id.tvNeedHelp), (TextView) findViewById(R.id.textView2), (TextView) findViewById(R.id.textView4), (TextView) findViewById(R.id.tvDontKnowZip), edtEmailError, edtPwdError, edtNameError, edtZipError, (TextView) findViewById(R.id.toolbar_done));
        setTextViewFontAvenirBold((TextView) findViewById(R.id.tvEmail), (TextView) findViewById(R.id.tvFullName), (TextView) findViewById(R.id.tvPassword), (TextView) findViewById(R.id.tvZipCode), (TextView) findViewById(R.id.toolbar_title), btnContact, btnPrivacy, btnTerms);
        setEditTextFontAvenirItalic(edtEmail, edtPwd, edtName, edtZip);
    }

    @Override
    public void onBackPressed() {
        if (isFromLogin)
            Util.startAct(SignupActivity.this, LoginWithActivity.class, 4);
        else
            Util.startAct(SignupActivity.this, WelcomeActivity.class, 4);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_done:
                if (isValid()) {
                    signup();
                }
                break;

            default:
                break;
        }
    }

    private void signup() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("fullname", edtName.getText().toString().trim());
        parmas.put("email", edtEmail.getText().toString().trim());
        parmas.put("pass", edtPwd.getText().toString().trim());
        parmas.put("zipcode", edtZip.getText().toString().trim());
        parmas.put("device_token", mPrefs.getGCMKey());
        parmas.put("type", LoginType.TYPE_RESEEIT);
        parmas.put("device_type", "android");
        postRequest = new PostLibResponse(SignupActivity.this, new LoginModel(), SignupActivity.this, parmas, WebServices.REGISTER, CODE_REGISTER, true);
    }

    private boolean isValid() {
        Utility.hideKeyboard(getApplicationContext(), edtName);
        edtEmailError.setVisibility(View.GONE);
        edtNameError.setVisibility(View.GONE);
        edtPwdError.setVisibility(View.GONE);
        edtZipError.setVisibility(View.GONE);
        if (!Utility.hasConnection(getApplicationContext())) {
            noConnection();
            return false;
        } else if (edtName.getText().toString().trim().equals("")) {
//            edtNameError.setText("Please Enter Name");
//            edtNameError.setVisibility(View.VISIBLE);
            edtName.requestFocus();
            alertWarning("Please Enter Name");
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
        } else if (!isValidPwd()) {
//            edtPwdError.setText("Please Enter at least one uppercase letter and number");
//            edtPwdError.setVisibility(View.VISIBLE);
            edtPwd.requestFocus();
            alertWarning("Please Enter at least one uppercase letter and number");
            return false;
        } else if (edtZip.getText().toString().trim().equals("")) {
//            edtZipError.setText("Please Enter Zip Code");
//            edtZipError.setVisibility(View.VISIBLE);
            edtZip.requestFocus();
            alertWarning("Please Enter Zip Code");
            return false;
        } else if (mPrefs.getGCMKey().equals("")) {
            alertError("Device not identified");
            return false;
        }
        return true;
    }

    private boolean isValidPwd() {
        String pwd = edtPwd.getText().toString().trim();
        boolean hasUppercaseLetter = false, hasNumber = false;
        for (int pwdCharCounter = 0; pwdCharCounter < pwd.length(); pwdCharCounter++) {
            char pwdChar = pwd.charAt(pwdCharCounter);
            if (pwdChar >= 65 && pwdChar <= 90) {
                hasUppercaseLetter = true;
                continue;
            }
            if (pwdChar >= 48 && pwdChar <= 57) {
                hasNumber = true;
                continue;
            }
        }
        return (hasNumber && hasUppercaseLetter);
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_REGISTER) {
            if (clsGson != null) {
                LoginModel mLoginModel = (LoginModel) clsGson;
                if (mLoginModel.status.equals("1")) {
//                    toast(mLoginModel.message);
                    mPrefs.setUserDetail(mLoginModel.data.get(0));
                    mPrefs.setLogin(true);
                    Util.startAct(SignupActivity.this, MainActivity.class, 4);
                } else {
                    alertError(mLoginModel.message);
                }
            } else {
                alertError("Not Registered");
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_REGISTER) {
            alertError("Not Registered");
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
