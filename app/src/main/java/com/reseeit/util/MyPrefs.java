package com.reseeit.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.reseeit.models.UserDetail;

public class MyPrefs {

    SharedPreferences myPrefs;
    SharedPreferences.Editor prefEditor;
    Context context;

    // ------------------------------------------------------------------costructor
    public MyPrefs(Context context) {
        this.context = context;
        myPrefs = context.getSharedPreferences("ReSeeIt", 0);
        prefEditor = myPrefs.edit();
    }

    // -----------------------------------------------------------------Gcm
    public void setGCMKey(String gcmKey) {
        prefEditor.putString("registration_id", gcmKey);
        prefEditor.commit();
    }

    public String getGCMKey() {
        return myPrefs.getString("registration_id", "");
    }

    public void setMacKey(String mackid) {
        prefEditor.putString("physical_id", mackid);
        prefEditor.commit();
    }

    public String getMacKey() {
        return myPrefs.getString("physical_id", "");
    }

    public void setImei(String imei) {
        prefEditor.putString("imei_id", imei);
        prefEditor.commit();
    }

    public String getImei() {
        return myPrefs.getString("imei_id", "");
    }

    public void setGCMRegisterVersion(int version) {
        prefEditor.putInt("appVersion", version);
        prefEditor.commit();
    }

    public int getGCMRegisterVersion() {
        return myPrefs.getInt("appVersion", Integer.MIN_VALUE);
    }

    public void setPlayServicesFlag(boolean flag) {
        prefEditor.putBoolean("servicesFlag", flag);
        prefEditor.commit();
    }

    // --End---------------------------------------------------------------Gcm

    // -------------------------------------------------------------------Settings

    public void registerForGcm() {
        prefEditor.putBoolean("gcm_state", true);
        prefEditor.commit();
    }

    public boolean isRegisteredForGcm() {
        return myPrefs.getBoolean("gcm_state", false);
    }

    // --End---------------------------------------------------------------Settings

    // -----------------------------------------------------------------User
    public void setLogin(boolean isLogin) {
        prefEditor.putBoolean("isLogin", isLogin);
        if (!isLogin)
            prefEditor.putBoolean("gcm_state", false);
        prefEditor.commit();
    }

    public boolean isLogin() {
        return myPrefs.getBoolean("isLogin", false);
    }

    public void setUserDetail(UserDetail userDetail) {
        prefEditor.putString("user_detail", new Gson().toJson(userDetail));
        prefEditor.commit();
    }

    public UserDetail getUserDetail() {
        String userString = myPrefs.getString("user_detail", null);
        if (userString == null)
            return null;
        UserDetail user = new Gson().fromJson(userString, UserDetail.class);
        return user;
    }

    // --End---------------------------------------------------------------User

    // -----------------------------------------------------------------Database
    public void setDbState(boolean isFirstTime) {
        prefEditor = myPrefs.edit();
        prefEditor.putBoolean("DbLoadFirstTime", isFirstTime);
        prefEditor.commit();
    }

    public boolean isDbStateFresh() {
        return myPrefs.getBoolean("DbLoadFirstTime", true);
    }

    public void setDbStateFavourite(boolean isFirstTime) {
        prefEditor = myPrefs.edit();
        prefEditor.putBoolean("DbLoadFirstTimeFavourite", isFirstTime);
        prefEditor.commit();
    }

    public boolean isDbStateFreshFavourite() {
        return myPrefs.getBoolean("DbLoadFirstTimeFavourite", true);
    }

//    public void setExploreCache(String clsGson) {
//        prefEditor = myPrefs.edit();
//        prefEditor.putString("ExploreCache", clsGson);
//        prefEditor.commit();
//    }
//
//    public String getExploreCache() {
//        return myPrefs.getString("ExploreCache", "");
//    }

    public void setRewardsCache(String clsGson) {
        prefEditor = myPrefs.edit();
        prefEditor.putString("RewardsCache", clsGson);
        prefEditor.commit();
    }

    public String getRewardsCache() {
        return myPrefs.getString("RewardsCache", "");
    }
    // --End---------------------------------------------------------------Database
}
