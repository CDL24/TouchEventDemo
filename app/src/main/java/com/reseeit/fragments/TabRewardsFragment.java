package com.reseeit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.reseeit.MainActivity;
import com.reseeit.R;
import com.reseeit.adapters.RewardsAdapter;
import com.reseeit.models.RewardItem;
import com.reseeit.models.RewardModel;
import com.reseeit.net.LibPostListnerNew;
import com.reseeit.net.PostLibResponseNew;
import com.reseeit.util.Utility;
import com.reseeit.util.WebServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabRewardsFragment extends BaseFragment implements LibPostListnerNew, SwipeRefreshLayout.OnRefreshListener {

    private View mView;

    private RecyclerView lvRewards;
    private LinearLayoutManager mLayoutManager;
    private RewardsAdapter mAdapter;
    private PostLibResponseNew postRequest;
    private final int CODE_REWARDS_LIST = 0;

//    private FrameLayout dragView;
//    private ImageView imgPoints;
//    private TextView tvPoints;
    float earnedPoints = 0;
    float totalPoints = 0;
//    private View progressView;
    private SwipeRefreshLayout swipeLayout;
    private RewardModel mRewardModel;
    private LocationManager mLocationManager;
    private AlertDialog.Builder dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_tab_rewards, null);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionTitle("Rewards Profile");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        earnedPoints = Integer.parseInt(((MainActivity) getActivity()).earnedPoints);

        mLayoutManager = new LinearLayoutManager(getActivity());
        lvRewards = (RecyclerView) mView.findViewById(R.id.rv_rewards_profile);
        lvRewards.setLayoutManager(mLayoutManager);

        swipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimaryDark);

//        imgPoints = (ImageView) mView.findViewById(R.id.img_points);
//        tvPoints = (TextView) mView.findViewById(R.id.tv_points);
//        dragView = (FrameLayout) mView.findViewById(R.id.fl_drag_view);
//        progressView = mView.findViewById(R.id.viewProgress);

        if (mAdapter != null && mRewardModel!=null && mRewardModel.data!=null) {
            lvRewards.setAdapter(mAdapter);
            lvRewards.setItemViewCacheSize(mAdapter.getItemCount());
            setEarnedPoints(mRewardModel.data);
        } else {
            mAdapter = new RewardsAdapter(getActivity(), TabRewardsFragment.this, new ArrayList<RewardItem>());
            lvRewards.setAdapter(mAdapter);
            lvRewards.setItemViewCacheSize(0);
            if (!Utility.hasConnection(getActivity())) {
                noConnection();
                return;
            } else {
                if (checkLocationSetting()) {
                loadRewardList();}
            }
        }

//        revealView = ((RevealColorView) mView.findViewById(R.id.revealColorView));

//        String rewardsCache = mPrefs.getRewardsCache();
//        if (rewardsCache.equals("")) {

//        } else {
//            try {
//                RewardModel mRewardModel = new Gson().fromJson(rewardsCache, RewardModel.class);
//                mAdapter.setList(mRewardModel.data);
//                setEarnedPoints(mRewardModel.data);
//                lvRewards.setItemViewCacheSize(mRewardModel.data.size());
//            } catch (Exception e) {
//                if (!Utility.hasConnection(getActivity())) {
//                    noConnection();
//                    return;
//                } else {
//                    loadRewardList();
//                }
//            }
//        }
    }

    private void loadRewardList() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager == null) {
            return;
        }
        Location location = null;
            location = getNewLocation(mLocationManager);
            if (location == null) {
                alertWarning("Location not found");
                return;
            }

        Utility.Log.d("latitude : " + location.getLatitude());
        Utility.Log.d("longitude : " + location.getLongitude());

        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("Logged_UserId", userId);
        parmas.put("latitude", "" + location.getLatitude());
        parmas.put("longitude", "" + location.getLongitude());
        postRequest = new PostLibResponseNew(TabRewardsFragment.this, new RewardModel(), getActivity(), parmas, WebServices.REWARD_LIST, CODE_REWARDS_LIST, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLocationManager();
    }
    private void refreshLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
//        ClipData data = ClipData.newPlainText("", "");
//        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//        dragView.startDrag(data, shadowBuilder, dragView, 0);
//        dragView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        break;
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        break;
//                    case DragEvent.ACTION_DRAG_EXITED:
//                        break;
//                    case DragEvent.ACTION_DROP:
//                        earnedPoints = earnedPoints + 2500;
//                        tvPoints.setText((int) earnedPoints + " earnedPoints to redeem reward");
//                        updateProgress();
////                        revealView.startReveal(revealView.getMeasuredWidth()/2,revealView.getMeasuredHeight()/2,Color.parseColor("#33333333"));
//                        break;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
    }

//    private void updateProgress() {
//        float newProgress = (earnedPoints / totalPoints);
//        ScaleAnimation animation = new ScaleAnimation(0, newProgress, 100, 100);
//        animation.setDuration(1000);
//        animation.setFillAfter(true);
//        progressView.startAnimation(animation);
//    }


    @Override
    public void onPostResponseComplete(Object clsGson, String response, int requestCode) {
        if (requestCode == CODE_REWARDS_LIST) {
            if (clsGson != null) {
                RewardModel mRewardModel = (RewardModel) clsGson;
                if (mRewardModel.status.equals("1")) {
                    if (mRewardModel.data != null) {
                        this.mRewardModel = mRewardModel;
// mPrefs.setRewardsCache(response);
                        mAdapter.setList(mRewardModel.data, earnedPoints);
                        setEarnedPoints(mRewardModel.data);
                        lvRewards.setItemViewCacheSize(mRewardModel.data.size());
                    }
                } else {
                    alertError(mRewardModel.message);
                }
            } else {
                alertError("Could Not Load Rewards");
            }
        }
    }

    private void setEarnedPoints(List<RewardItem> data) {
        if (data.size() == 0)
            return;
        try {
            int points = 0;
            try {
                points = Integer.parseInt(data.get(0).reward_point);
            } catch (Exception e) {
            }
            int pos = 0;
            for (int counter = 1; counter < data.size(); counter++) {
                if (this.earnedPoints > points)
                    continue;
                try {
                    int newpoints = Integer.parseInt(data.get(counter).reward_point);
                    if (points > newpoints) {
                        points = newpoints;
                        pos = counter;
                    }
                } catch (Exception e) {
                }
            }
            totalPoints = points;
//            tvPoints.setText(((int) (totalPoints - earnedPoints)) + " points to redeem reward");
//            setPointsImage(imgPoints, data.get(pos).reward_summary_img);
//            updateProgress();
        } catch (Exception e) {
        }
    }

//    private void setPointsImage(ImageView imageView, String imgPath) {
//        if (imgPath != null && !imgPath.equals("")) {
//            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
//            imgLoader.get(imgPath, ImageLoader.getImageListener(imageView, R.drawable.image, R.drawable.image));
//        }
//    }

    @Override
    public void onPostResponseError(String errorMessage, String response, int requestCode) {
        if (requestCode == CODE_REWARDS_LIST) {
            alertError("Could Not Load Rewards");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (postRequest != null) {
            postRequest.onDestroy();
            postRequest = null;
        }
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(false);
        if (!Utility.hasConnection(getActivity())) {
            noConnection();
            return;
        } else {
            if (checkLocationSetting()) {
            loadRewardList();}
        }
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
}
