package com.reseeit.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MobileDataStateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int state = NetworkUtil.getConnectivityStatus(context);
            if (state == NetworkUtil.TYPE_WIFI || state == NetworkUtil.TYPE_MOBILE) {
                context.startService(new Intent(context, ImageUploadService.class));
            } else if (state == NetworkUtil.TYPE_NOT_CONNECTED) {
                context.stopService(new Intent(context, ImageUploadService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NetworkUtil {

        static final int TYPE_WIFI = 1;
        static final int TYPE_MOBILE = 2;
        static final int TYPE_NOT_CONNECTED = 0;

        public static int getConnectivityStatus(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }
            return TYPE_NOT_CONNECTED;
        }
    }
}