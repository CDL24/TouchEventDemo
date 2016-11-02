package com.reseeit;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.reseeit.com.reseeit.listeners.GcmCallbackListener;
import com.reseeit.models.LoginModel;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.LoginType;
import com.reseeit.util.Utility;
import com.reseeit.util.WebServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginWithActivity extends BaseActivity implements LibPostListner, OnConnectionFailedListener, FacebookCallback<LoginResult> {

    private GoogleApiClient mGoogleApiClient;
    private int CODE_LOGIN_GPLUS = 0, CODE_LOGIN_FB = 1;
    private PostLibResponse postRequest;
    private static final int RC_SIGN_IN = 9001;
    private AccessToken accessToken;
    private CallbackManager callbackManager;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login_with);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        setTextViewFontAvenirBold((TextView) findViewById(R.id.tvLoginCreate), (TextView) findViewById(R.id.tvLoginOr), (TextView) findViewById(R.id.toolbar_title), (TextView) findViewById(R.id.tvLoginEarn));
        setTextViewFontAvenir((TextView) findViewById(R.id.tvLoginPermission), (TextView) findViewById(R.id.tvLoginGoogle), (TextView) findViewById(R.id.tvLoginEmail), (TextView) findViewById(R.id.tvLoginFacebook), (TextView) findViewById(R.id.tvDontHaveAC));

    }

    @Override
    public void onBackPressed() {
        Util.startAct(LoginWithActivity.this, WelcomeActivity.class, 4);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llLoginFb:
                if (!Utility.hasConnection(LoginWithActivity.this)) {
                    noConnection();
                    return;
                }
                showProgressDialog();
                FacebookSdk.sdkInitialize(this);
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager, this);
                ArrayList<String> alPermission = new ArrayList<String>();
                alPermission.add("email");
                alPermission.add("user_birthday");
                alPermission.add("public_profile");
                LoginManager.getInstance().logInWithReadPermissions(this, alPermission);
                break;
            case R.id.llLoginG:
                if (!Utility.hasConnection(LoginWithActivity.this)) {
                    noConnection();
                    return;
                }
                showProgressDialog();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            case R.id.llLoginEmail:
                Util.startAct(LoginWithActivity.this, LoginActivity.class, 4);
                break;

            case R.id.tvLoginCreate:
                SignupActivity.isFromLogin = true;
                Util.startAct(LoginWithActivity.this, SignupActivity.class, 4);
                break;
        }
    }

    @Override
    public void onSuccess(LoginResult result) {
        accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("FB >>>  " + object.toString());
                    if (object.has("name")) {
                        String name = object.getString("name");
                        String email = "";
                        if (object.has("email"))
                            email = object.getString("email");
                        if (email.equals(""))
                            email = object.getString("id") + "@facebook.com";
                        String image = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                        loginSocial(name, email, CODE_LOGIN_FB, LoginType.TYPE_FB, image);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, link, email, gender, birthday, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        dismissProgressDialog();
        alertError("Login Canceled");
    }

    @Override
    public void onError(FacebookException error) {
        dismissProgressDialog();
        alertError("Login Error");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            if (resultCode == RESULT_OK) {
                try {
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            this.acct = acct;
            signOut();
        } else {
            dismissProgressDialog();
            alertError("Login Canceled");
        }
    }

    private GoogleSignInAccount acct;

    private void signOut() {
        mGoogleApiClient.registerConnectionCallbacks(mGplusConnectionCallback);
        mGoogleApiClient.connect();
    }

    private ConnectionCallbacks mGplusConnectionCallback = new ConnectionCallbacks() {
        @Override
        public void onConnectionSuspended(int arg0) {
            mGoogleApiClient.unregisterConnectionCallbacks(mGplusConnectionCallback);
        }

        @Override
        public void onConnected(Bundle arg0) {
            mGoogleApiClient.unregisterConnectionCallbacks(mGplusConnectionCallback);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {//if (acct != null)
                    String photoUrl = acct.getPhotoUrl() == null ? "" : acct.getPhotoUrl().toString();
                    loginSocial(acct.getDisplayName(), acct.getEmail(), CODE_LOGIN_GPLUS, LoginType.TYPE_GPLUS, photoUrl);
                }
            });
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        dismissProgressDialog();
        alertError("Connection Failed");
    }

    private void loginSocial(String name, String email, int requestCode, String loginType, String imageUrl) {
        if (mPrefs.getGCMKey().equals("")) {
            generateDeviceToken(name, email, requestCode, loginType, imageUrl);
//            alertError("Device not identified");
            return;
        }
        dismissProgressDialog();
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("email", email);
        parmas.put("pass", WebServices.DEFAULT_PASSWORD);
        parmas.put("device_token", mPrefs.getGCMKey());
        parmas.put("type", loginType);
        parmas.put("fullname", name);
        parmas.put("user_img", imageUrl);
        parmas.put("device_type", "android");
        postRequest = new PostLibResponse(LoginWithActivity.this, new LoginModel(), LoginWithActivity.this, parmas, WebServices.LOGIN, requestCode, true);
    }

    private void generateDeviceToken(final String name, final String email, final int requestCode, final String loginType, final String imageUrl) {
        ((ReSeeItApp) getApplication()).setGcmCallback(new GcmCallbackListener() {
            @Override
            public void onGcmCallback(boolean isSuccess) {
                dismissProgressDialog();
                if (isSuccess) {
                    Map<String, String> parmas = new HashMap<String, String>();
                    parmas.put("email", email);
                    parmas.put("pass", WebServices.DEFAULT_PASSWORD);
                    parmas.put("device_token", mPrefs.getGCMKey());
                    parmas.put("type", loginType);
                    parmas.put("fullname", name);
                    parmas.put("user_img", imageUrl);
                    parmas.put("device_type", "android");
                    postRequest = new PostLibResponse(LoginWithActivity.this, new LoginModel(), LoginWithActivity.this, parmas, WebServices.LOGIN, requestCode, true);
                } else {
                    alertError("Device not identified", "Make sure your device has working internet connection and updated google play service");
                }
            }
        });
        ((ReSeeItApp) getApplication()).generateDeviceToken();
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_LOGIN_GPLUS) {
            if (clsGson != null) {
                LoginModel mLoginModel = (LoginModel) clsGson;
                if (mLoginModel.status.equals("1")) {
//                    toast(mLoginModel.message);
                    mPrefs.setUserDetail(mLoginModel.data.get(0));
                    mPrefs.setLogin(true);
                    Util.startAct(LoginWithActivity.this, MainActivity.class, 4);
                } else {
                    alertError(mLoginModel.message);
                }
            } else {
                alertError("Login Error");
            }
        } else if (requestCode == CODE_LOGIN_FB) {
            if (clsGson != null) {
                LoginModel mLoginModel = (LoginModel) clsGson;
                if (mLoginModel.status.equals("1")) {
//                    toast(mLoginModel.message);
                    mPrefs.setUserDetail(mLoginModel.data.get(0));
                    mPrefs.setLogin(true);
                    Util.startAct(LoginWithActivity.this, MainActivity.class, 4);
                } else {
                    alertError(mLoginModel.message);
                }
            } else {
                alertError("Login Error");
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_LOGIN_GPLUS)
            alertError("Login Error");
        else if (requestCode == CODE_LOGIN_FB)
            alertError("Login Error");
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
