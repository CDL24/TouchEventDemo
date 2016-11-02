package com.reseeit.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.reseeit.BaseActivity;
import com.reseeit.MainActivity;
import com.reseeit.R;
import com.reseeit.adapters.ExplorePagerAdapter;
import com.reseeit.com.reseeit.listeners.InteractionListener;
import com.reseeit.models.CouponModel;
import com.reseeit.models.Interaction;
import com.reseeit.models.InteractionModel;
import com.reseeit.net.LibPostListnerNew;
import com.reseeit.net.PostLibResponseNew;
import com.reseeit.util.CouponType;
import com.reseeit.util.Utility;
import com.reseeit.util.WebServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TabExploreFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener, LibPostListnerNew, InteractionListener {

    private View mView;
    public ViewPager viewPager = null;
    private ExplorePagerAdapter pagerAdapter;
    private TextView btnFeatured, btnNearBy, btnFavourites;

    private PostLibResponseNew postRequest;
    private final int CODE_COUPON_LIST = 0;
    private final int CODE_INTERACTION_WATCH = 1;
    private CouponModel mCouponModel;
    private LocationManager mLocationManager;
    private AlertDialog.Builder dialog;

//    private MyPrefs mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_tab_explore, null);
        try {
            getActivity().registerReceiver(refreshCouponReceiver, new IntentFilter(MainActivity.INTENT_REFRESH_COUPONS));
        } catch (Exception e) {
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionTitle(getGreetingsMessage());
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        init();
        setSelection(selectedPosition);
//        String exploreCache = mPrefs.getExploreCache();
//        if (exploreCache.equals("")) {

        if (pagerAdapter != null) {
            viewPager.setAdapter(pagerAdapter);
        } else {
            pagerAdapter = new ExplorePagerAdapter(getChildFragmentManager(), TabExploreFragment.this);
            viewPager.setAdapter(pagerAdapter);
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    if (viewPager != null)
                        viewPager.setCurrentItem(0);
                }
            });

            if (!Utility.hasConnection(getActivity())) {
                noConnection();
                return;
            } else {
                if (checkLocationSetting()) {
                    loadCouponList();
                }
            }
        }
//        } else {
//            try {
//                mCouponModel = new Gson().fromJson(exploreCache, CouponModel.class);
//                pagerAdapter = new ExplorePagerAdapter(getChildFragmentManager(), TabExploreFragment.this);
//                pagerAdapter.setCouponList(mCouponModel.data, mCouponModel.image_path);
//                viewPager.setAdapter(pagerAdapter);
//                viewPager.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (viewPager != null)
//                            viewPager.setCurrentItem(1);
//                    }
//                });
//            } catch (Exception e) {
//                if (!Utility.hasConnection(getActivity())) {
//                    noConnection();
//                    return;
//                } else {
//                    if (checkLocationSetting()) {
//                        loadCouponList();
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLocationManager();
    }

    private void loadCouponList() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager == null) {
            return;
        }
        Location location = getCurrentLocation();
        if (location == null) {
            location = getNewLocation(mLocationManager);
            if (location == null) {
                alertWarning("Location not found");
                return;
            }
        }

        Utility.Log.d("latitude : " + location.getLatitude());
        Utility.Log.d("longitude : " + location.getLongitude());
        BaseActivity.Log.d("coupon request");
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("coupon_type", CouponType.TYPE_ALL);
        parmas.put("latitude", "" + location.getLatitude());
        parmas.put("longitude", "" + location.getLongitude());
        parmas.put("Logged_UserId", userId);
        postRequest = new PostLibResponseNew(TabExploreFragment.this, new CouponModel(), getActivity(), parmas, WebServices.COUPON_LIST, CODE_COUPON_LIST, true);
    }

    private Location getCurrentLocation() {
//        List<String> providers = mLocationManager.getProviders(true);
//        for (String provider : providers) {
//            Location l = mLocationManager.getLastKnownLocation(provider);
//            if (l != null)
//                return l;
//        }
        return null;
    }

    private Location getNewLocation(LocationManager locationManager) {
        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager
                                .PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
//                Log.d("Network", "Network Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        return location;
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
//                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            return location;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void init() {
        btnFavourites = (TextView) mView.findViewById(R.id.btn_favourites);
        btnFeatured = (TextView) mView.findViewById(R.id.btn_featured);
        btnNearBy = (TextView) mView.findViewById(R.id.btn_nearby);
        btnFavourites.setOnClickListener(this);
        btnFeatured.setOnClickListener(this);
        btnNearBy.setOnClickListener(this);
        viewPager = (ViewPager) mView.findViewById(R.id.vp_explore);
        viewPager.setOnPageChangeListener(TabExploreFragment.this);
        viewPager.setOffscreenPageLimit(3);
        setFontAvenir(btnFavourites, btnFeatured, btnNearBy);
//        mPrefs = new MyPrefs(getActivity());
    }

    private int selectedPosition = 0;

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        setSelection(position);
    }

    private void setSelection(int position) {
        selectedPosition = position;
        btnFavourites.setBackgroundColor(Color.TRANSPARENT);
        btnFeatured.setBackgroundColor(Color.TRANSPARENT);
        btnNearBy.setBackgroundColor(Color.TRANSPARENT);
        if (position == 0) {
            btnFeatured.setBackgroundColor(Color.parseColor("#66000000"));
        } else if (position == 1) {
            btnNearBy.setBackgroundColor(Color.parseColor("#66000000"));
        } else if (position == 2) {
            btnFavourites.setBackgroundColor(Color.parseColor("#66000000"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_favourites:
                viewPager.setCurrentItem(2);
                break;
            case R.id.btn_featured:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_nearby:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPostResponseComplete(Object clsGson, String response, int requestCode) {
        if (requestCode == CODE_COUPON_LIST) {
            if (clsGson != null) {
                CouponModel mCouponModel = (CouponModel) clsGson;
                if (mCouponModel.status.equals("1")) {
                    if (mCouponModel.data != null) {
//                        mPrefs.setExploreCache(response);
                        this.mCouponModel = mCouponModel;
                        if (pagerAdapter == null)
                            pagerAdapter = new ExplorePagerAdapter(getChildFragmentManager(), TabExploreFragment.this);
                        pagerAdapter.setCouponList(mCouponModel.data, mCouponModel.image_path);
                    }
                } else {
                    alertError(mCouponModel.message);
                }
            } else {
                alertError("Could Not Load Coupons");
            }
        }
//        else if (requestCode == CODE_INTERACTION_WATCH) {
//            if (clsGson != null) {
//                InteractionModel mInteractionModel = (InteractionModel) clsGson;
//                if (mInteractionModel.status.equals("1")) {
//                    alertSuccess(mInteractionModel.message);
//                } else {
//                    alertError(mInteractionModel.message);
//                }
//            } else {
//                alertError("Interaction failed");
//            }
//        }
    }

    @Override
    public void onPostResponseError(String errorMessage, String response, int requestCode) {
        BaseActivity.Log.d(errorMessage);
        if (requestCode == CODE_COUPON_LIST) {
            alertError("Could Not Load Coupons");
        }
//        else if (requestCode == CODE_INTERACTION_WATCH) {
//            alertError("Interaction failed");
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().unregisterReceiver(refreshCouponReceiver);
        } catch (Exception e) {
        }
        if (postRequest != null) {
            postRequest.onDestroy();
            postRequest = null;
        }
    }

    @Override
    public Interaction getInteraction(String userId) {
        Interaction interactions = null;
        if (mCouponModel == null || mCouponModel.interactions == null)
            return null;
        for (Interaction interaction : mCouponModel.interactions) {
            if (interaction.user_id.equals(userId)) {
//                if (interaction.Type.equals("default") || isExpired(interaction.expiry_date)) {
//                    if (interaction.Status == null || interaction.Status.equals("0"))
//                        return interaction;
//                }
//                if (interaction.Status.equals("0"))
//                    interactions = interaction;
                interactions = interaction;
            }

        }
        return interactions;
    }

    @Override
    public ArrayList<Interaction> getInteractionListForCoupon(String userId) {
        ArrayList<Interaction> interactions = new ArrayList<Interaction>();
        if (mCouponModel == null || mCouponModel.interactions == null)
            return null;
        for (Interaction interaction : mCouponModel.interactions) {
            if (interaction.user_id.equals(userId)) {
                interactions.add(interaction);
            }
        }
        return interactions;
    }

    public static boolean isExpired(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");// 12/31/2015
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date == null) {
            return false;
        }

        Calendar selectedDate = Calendar.getInstance(Locale.getDefault());
        selectedDate.setTime(date);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return selectedDate.compareTo(calendar) >= 0 ? true : false;
    }

    @Override
    public void onInteractionSeen(Interaction seenInteraction) {
        if (mCouponModel == null || mCouponModel.interactions == null || seenInteraction == null)
            return;
        for (Interaction interaction : mCouponModel.interactions) {
            if (interaction != null && interaction.loyalty_interaction_id != null && seenInteraction.loyalty_interaction_id != null && interaction.loyalty_interaction_id.equals(seenInteraction.loyalty_interaction_id)) {
                interaction.Status = "1";
                break;
            }
        }

        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("user_id", seenInteraction.user_id);
        parmas.put("interaction_id", seenInteraction.loyalty_interaction_id);
        parmas.put("Logged_UserId", userId);
        parmas.put("award_point", seenInteraction.award_points);
        postRequest = new PostLibResponseNew(TabExploreFragment.this, new InteractionModel(), getActivity(), parmas, WebServices.INTERACTION_WATCH, CODE_INTERACTION_WATCH, false);
    }

    private boolean checkLocationSetting() {
        boolean network_enabled = false;
        boolean gps_enabled = false;
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("Your GPS seems to be disabled, do you want to enable it?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            AlertDialog alert = dialog.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();
            return false;
        }
        return true;
    }

    private void refreshLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private BroadcastReceiver refreshCouponReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Utility.hasConnection(getActivity())) {
                noConnection();
                return;
            } else {
                if (checkLocationSetting()) {
                    loadCouponList();
                }
            }
        }
    };
}
