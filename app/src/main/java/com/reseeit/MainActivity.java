package com.reseeit;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nobrain.android.permissions.AndroidPermissions;
import com.nobrain.android.permissions.Checker;
import com.nobrain.android.permissions.Result;
import com.reseeit.fragments.TabCameraContainerFragment;
import com.reseeit.fragments.TabExploreFragment;
import com.reseeit.fragments.TabFavoritesFragment;
import com.reseeit.fragments.TabProfileContainerFragment;
import com.reseeit.fragments.TabProfileFragment;
import com.reseeit.fragments.TabRewardsFragment;
import com.reseeit.models.NotificationModel;
import com.reseeit.models.UserDetail;
import com.reseeit.net.MobileDataStateChangedReceiver;

import java.io.File;
import java.util.Calendar;


public class MainActivity extends BaseActivity implements OnTabChangeListener {
    public static final int REQUEST_CODE = 102;
    private static final String TAG = MainActivity.class.getName();

    public static final String INTENT_REFRESH_FAVORITE = "Intent.RefreshFavorite";
    public static final String INTENT_REFRESH_GALLERY_STATUS = "Intent.RefreshGalleryStatus";
    public static final String INTENT_REFRESH_GALLERY_LIST = "Intent.RefreshGalleryList";
    public static final String INTENT_REFRESH_COUPONS = "Intent.RefreshCoupons";
    public static final String INTENT_CAMERA_BUTTON_CLICK = "Intent.CameraButton";
    private TextView tvTitle;
    private RelativeLayout rlTitle, rlTitleProfile;
    private FragmentTabHost mTabHost;
    private String[] tabLabels = new String[5];
    public String userName = "", userId = "", profileUrl;
    public static String PATH_CAMERA_IMAGE = "";
    private MobileDataStateChangedReceiver mobileDataReceiver;
//    private RevealColorView revealView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_main);
        try {
            registerReceiver(statusUpdateReceiver, new IntentFilter(MainActivity.INTENT_REFRESH_GALLERY_STATUS));
        } catch (Exception e) {
        }
        permissionCheck();
        init();
//        if (Utility.hasConnection(MainActivity.this))
//            try {
//                startService(new Intent(MainActivity.this, ImageUploadService.class));
//            } catch (Exception e) {
//            }
    }

    private void permissionCheck() {
        AndroidPermissions.check(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.CAMERA)
                .hasPermissions(new Checker.Action0() {
                    @Override
                    public void call(String[] permissions) {
                        String msg = "Permission has " + permissions[0];
                        Log.d(msg);
                    }
                })
                .noPermissions(new Checker.Action1() {
                    @Override
                    public void call(String[] permissions) {
                        String msg = "Permission has no " + permissions[0];
                        Log.d(msg);

                        ActivityCompat.requestPermissions(MainActivity.this
                                , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.CAMERA}
                                , REQUEST_CODE);
                    }
                })
                .check();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, final String[] permissions, int[] grantResults) {
        AndroidPermissions.result(MainActivity.this)
                .addPermissions(REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.CAMERA)
                .putActions(REQUEST_CODE, new Result.Action0() {
                    @Override
                    public void call() {
                        String msg = "Request Success : " + permissions[0];
                        Log.d(msg);

                    }
                }, new Result.Action1() {
                    @Override
                    public void call(String[] hasPermissions, String[] noPermissions) {
                        String msg = "Request Fail : " + noPermissions[0];
                        Log.d(msg);

                    }
                })
                .result(requestCode, permissions, grantResults);
    }


    private int paddingSize, tabTextSize;
    public String earnedPoints = "0";

    private void init() {
        PATH_CAMERA_IMAGE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/." + getResources().getString(R.string.app_name) + "/";
        File mFile = new File(MainActivity.PATH_CAMERA_IMAGE);
        mFile.mkdirs();
        UserDetail mUserDetail = mPrefs.getUserDetail();
        if (mUserDetail != null) {
            String firstWord = null;
            if (mUserDetail.fullname.contains(" ")) {
                firstWord = mUserDetail.fullname.substring(0, mUserDetail.fullname.indexOf(" "));
            }
            if (firstWord == null) {
                userName = mUserDetail.fullname;
            } else {
                userName = firstWord;
            }
            userId = mUserDetail.user_id;
            profileUrl = mUserDetail.user_img;
            earnedPoints = mUserDetail.EarningPoint;
            if (earnedPoints == null || earnedPoints.equals("null") || earnedPoints.equals(""))
                earnedPoints = mUserDetail.total_point;
            if (earnedPoints == null || earnedPoints.equals("null") || earnedPoints.equals(""))
                earnedPoints = "0";
            ((TextView) findViewById(R.id.tv_earning_point)).setText(earnedPoints);
        }

//        revealView = ((RevealColorView) findViewById(R.id.revealColorView));
        paddingSize = (int) (getResources().getDimension(R.dimen.small_margin));
        tabTextSize = (int) (getResources().getDimension(R.dimen.small_margin)) / 2;
        tvTitle = (TextView) findViewById(R.id.tv_title);
        setTextViewFontAvenirBold(tvTitle);
        rlTitleProfile = (RelativeLayout) findViewById(R.id.rl_title_profile);
        setTextViewFontAvenirBold((TextView) findViewById(R.id.tvProfile));
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        tabLabels[0] = "Explore";
        tabLabels[1] = "Favorites";
        tabLabels[2] = "Camera";
        tabLabels[3] = "Rewards";
        tabLabels[4] = "Profile";

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(getTabSpec(tabLabels[0], R.drawable.ic_tab_explore), TabExploreFragment.class, null);
        mTabHost.addTab(getTabSpec(tabLabels[1], R.drawable.ic_tab_favorites), TabFavoritesFragment.class, null);
        mTabHost.addTab(getTabSpec(tabLabels[2], R.drawable.ic_tab_camera), TabCameraContainerFragment.class, null);
        mTabHost.addTab(getTabSpec(tabLabels[3], R.drawable.ic_tab_rewards), TabRewardsFragment.class, null);
        mTabHost.addTab(getTabSpec(tabLabels[4], R.drawable.ic_tab_profile), TabProfileContainerFragment.class, null);
        mTabHost.setOnTabChangedListener(MainActivity.this);
//		mTabHost.setCurrentTab(1);
//		mTabHost.postDelayed(new Runnable() {
//			@Override
//			public void run() {
        mTabHost.setCurrentTab(2);
//			}
//		},500);
        try {
            currentContainerCamera = (TabCameraContainerFragment) getSupportFragmentManager().findFragmentByTag(tabLabels[2]);
        } catch (Exception e) {
        }
        cameraBroadcastReceiverIntent = new Intent(MainActivity.INTENT_CAMERA_BUTTON_CLICK);
        mTabHost.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabHost.getCurrentTab() == 2) {
                    if (currentContainerCamera != null) {
                        if (!currentContainerCamera.popFragment()) {
                            sendBroadcast(cameraBroadcastReceiverIntent);
                        } else {
                            imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera_capture);
                        }
                    } else {
                        sendBroadcast(cameraBroadcastReceiverIntent);
//                        onTabChanged(tabLabels[2]);
                        imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera_capture);
                    }
                } else {
                    imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera_capture);
                }
                mTabHost.setCurrentTab(2);
                onTabChanged(tabLabels[2]);
            }
        });
    }

    Intent cameraBroadcastReceiverIntent;

    private ImageView imgCameraTabIcon = null;

    private TabHost.TabSpec getTabSpec(String name, int drawableId) {
        TabHost.TabSpec spec = mTabHost.newTabSpec(name);
        View tabIndicator = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_indicator, mTabHost.getTabWidget(), false);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tab_indicator_img);
        icon.setImageResource(drawableId);
        TextView tab_indicator = (TextView) tabIndicator.findViewById(R.id.tab_indicator_tv);
        tab_indicator.setText(name);
        setTextViewFontAvenir(tab_indicator);
//		((TextView) tabIndicator.findViewById(R.id.tab_indicator_tv)).setTextSize(tabTextSize);
        if (name.equals(tabLabels[2])) {
            imgCameraTabIcon = icon;
            tabIndicator.findViewById(R.id.tab_indicator_tv).setVisibility(View.GONE);
            tabIndicator.setPadding(0, paddingSize / 2, 0, paddingSize / 2);
        } else {
            tabIndicator.setPadding(0, paddingSize, 0, paddingSize / 2);
        }
        spec.setIndicator(tabIndicator);
        return spec;
    }

    // @Override
    // public void onBackPressed() {
    // if (getSupportFragmentManager().getBackStackEntryCount() > 0)
    // super.onBackPressed();
    // else {
    // new SweetAlertDialog(MainActivity.this,
    // SweetAlertDialog.CUSTOM_IMAGE_TYPE).setTitleText(getString(R.string.app_name)).setCancelText("NO").setConfirmText("YES").showContentText(true).setContentText("Do you want to Exit?")
    // .setConfirmClickListener(new OnSweetClickListener() {
    // @Override
    // public void onClick(SweetAlertDialog sweetAlertDialog) {
    // MainActivity.this.finish();
    // sweetAlertDialog.dismiss();
    // }
    // }).show();
    // }
    // }

    public void setActionTitle(String title) {
        if (title.equals("Profile")) {
            rlTitle.setVisibility(View.GONE);
            rlTitleProfile.setVisibility(View.VISIBLE);
        } else {
            rlTitle.setVisibility(View.VISIBLE);
            rlTitleProfile.setVisibility(View.GONE);
            tvTitle.setText(title);
        }
    }

    // private static final int p1 = 7, p2 = 15;

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals(tabLabels[2]))
            imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera_capture);
        else imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera);
        if (currentContainerCamera != null) {
            currentContainerCamera.popFragment();
        }
        if (currentContainerProfile != null) {
            currentContainerProfile.popFragment();
        }
//        revealView.startReveal(((revealView.getMeasuredWidth() / tabLabels.length) * getRipplePoint(tabId)) + (revealView.getMeasuredWidth() / (tabLabels.length * 2)), revealView.getMeasuredHeight() / 2, Color.parseColor("#33333333"));
//        Log.d("Tab Changed : " + tabId);
        // mTabHost.getTabWidget().getChildAt(0).setPadding(p2, p2, p2, p2);
        // mTabHost.getTabWidget().getChildAt(1).setPadding(p2, p2, p2, p2);
        // mTabHost.getTabWidget().getChildAt(2).setPadding(p2, p2, p2, p2);
        // mTabHost.getTabWidget().getChildAt(3).setPadding(p2, p2, p2, p2);
        // mTabHost.getTabWidget().getChildAt(4).setPadding(p2, p2, p2, p2);
        // if (tabId.equals(tabLabels[0])) {
        // mTabHost.getTabWidget().getChildAt(0).setPadding(p1, p1, p1, p1);
        // } else if (tabId.equals(tabLabels[1])) {
        // mTabHost.getTabWidget().getChildAt(1).setPadding(p1, p1, p1, p1);
        // } else if (tabId.equals(tabLabels[2])) {
        // mTabHost.getTabWidget().getChildAt(2).setPadding(p1, p1, p1, p1);
        // } else if (tabId.equals(tabLabels[3])) {
        // mTabHost.getTabWidget().getChildAt(3).setPadding(p1, p1, p1, p1);
        // } else if (tabId.equals(tabLabels[4])) {
        // mTabHost.getTabWidget().getChildAt(4).setPadding(p1, p1, p1, p1);
        // }
    }

    private int getRipplePoint(String tabId) {
        for (int counter = 0; counter < tabLabels.length; counter++)
            if (tabLabels[counter].equals(tabId))
                return (counter);
        return 1;
    }

    public String getGreetingsMessage() {
        Calendar cal = Calendar.getInstance();
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        String greeting = "", name = ", " + userName + "!";
        if (hours >= 0 && hours < 3) {
            greeting = "Hello" + name;
        } else if (hours >= 3 && hours < 12) {
            greeting = "Good Morning" + name;
        } else if (hours >= 12 && hours < 18) {
            greeting = "Good Afternoon" + name;
        } else if (hours >= 18 && hours < 21) {
            greeting = "Good Evening" + name;
        } else if (hours >= 21 && hours <= 24) {
            greeting = "Hello" + name;
        }
        return greeting;
    }

    private TabCameraContainerFragment currentContainerCamera;

    public void setSubGalleryFragment(Fragment mFragment, boolean isAdd) {
        try {
            if (currentContainerCamera == null)
                currentContainerCamera = (TabCameraContainerFragment) getSupportFragmentManager().findFragmentByTag(tabLabels[2]);
            if (currentContainerCamera != null) {
                currentContainerCamera.popAllFragment();
                currentContainerCamera.setFragment(mFragment, isAdd);
                imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera);
            }
            // else
            // setFragmentContainer(frg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCameraStackEmpty() {
        boolean isPopFragment = false;
        if (currentContainerCamera == null)
            return isPopFragment;
        isPopFragment = currentContainerCamera.popFragment();
        return isPopFragment;
    }

    private TabProfileContainerFragment currentContainerProfile;

    public void setSubProfileFragment(Fragment mFragment, boolean isAdd) {
        try {
            if (currentContainerProfile == null)
                currentContainerProfile = (TabProfileContainerFragment) getSupportFragmentManager().findFragmentByTag(tabLabels[4]);
            if (currentContainerProfile != null) {
                currentContainerProfile.popAllFragment();
                currentContainerProfile.setFragment(mFragment, isAdd);
            }
            // else
            // setFragmentContainer(frg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isProfileStackEmpty() {
        boolean isPopFragment = false;
        if (currentContainerProfile == null)
            return isPopFragment;
        isPopFragment = currentContainerProfile.popFragment();
        return isPopFragment;
    }

    @Override
    public void onBackPressed() {
        if (isCameraStackEmpty()) {
            imgCameraTabIcon.setImageResource(R.drawable.ic_tab_camera_capture);
        } else if (!isProfileStackEmpty())
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (currentContainerProfile == null)
                currentContainerProfile = (TabProfileContainerFragment) getSupportFragmentManager().findFragmentByTag(tabLabels[4]);
            if (currentContainerProfile != null) {
                TabProfileFragment tabProfileFragment = (TabProfileFragment) currentContainerProfile.getChildFragmentManager().getFragments().get(0);//findFragmentByTag(tabLabels[4]);
                if (tabProfileFragment == null)
                    return;
                tabProfileFragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(statusUpdateReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    BroadcastReceiver statusUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String notificationData = intent.getBundleExtra("Extra").getString("0", "");
                NotificationModel notificationModel = new Gson().fromJson(notificationData, NotificationModel.class);
                UserDetail user = mPrefs.getUserDetail();
                user.EarningPoint = notificationModel.userdata.EarningPoint;
                user.total_point = notificationModel.userdata.EarningPoint;
                earnedPoints = notificationModel.userdata.EarningPoint;
                ((TextView) findViewById(R.id.tv_earning_point)).setText(earnedPoints);
            } catch (Exception e) {
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        mobileDataReceiver = new MobileDataStateChangedReceiver();
        permissionCheck();
        try {
            registerReceiver(mobileDataReceiver, filter);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mobileDataReceiver != null)
                unregisterReceiver(mobileDataReceiver);
        } catch (Exception e) {
        }
    }

    public void mRedeemModel(int earnpoint) {
        UserDetail mUserDetail = mPrefs.getUserDetail();
        if (mUserDetail != null) {
            String earnedPoints = mUserDetail.EarningPoint;
            if (earnedPoints == null || earnedPoints.equals("null") || earnedPoints.equals(""))
                earnedPoints = mUserDetail.total_point;
            if (earnedPoints == null || earnedPoints.equals("null") || earnedPoints.equals(""))
                earnedPoints = "0";
            this.earnedPoints = String.valueOf(Integer.parseInt(earnedPoints) + earnpoint);
            mUserDetail.total_point = this.earnedPoints;
            ((TextView) findViewById(R.id.tv_earning_point)).setText(this.earnedPoints);
        }
    }
}