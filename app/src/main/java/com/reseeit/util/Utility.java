package com.reseeit.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.reseeit.BuildConfig;
import com.reseeit.R;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static void showKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void startAct(Context packageContext, Class<?> cls, String name, String value, Act position) {
        try {
            Intent intent = new Intent(packageContext, cls);
            if (name != null && value != null)
                intent.putExtra(name, value);
            packageContext.startActivity(intent);
            ((Activity) packageContext).finish();
            if (position == Act.RIGHT_TO_LEFT)
                translateLeft(packageContext);
            else if (position == Act.LEFT_TO_RIGHT)
                translateRight(packageContext);
        } catch (ActivityNotFoundException e) {
            Log.e("have you declared \"" + cls.getSimpleName() + ".java\" activity in your AndroidManifest.xml?");
        }
    }

    public static void startAct(Context packageContext, Intent intent, Act position) {
        try {
            packageContext.startActivity(intent);
            ((Activity) packageContext).finish();
            if (position == Act.RIGHT_TO_LEFT)
                translateLeft(packageContext);
            else if (position == Act.LEFT_TO_RIGHT)
                translateRight(packageContext);
        } catch (ActivityNotFoundException e) {
            Log.e("have you declared \"" + intent.getClass() + ".java\" activity in your AndroidManifest.xml?");
        }
    }

    public enum Act {
        RIGHT_TO_LEFT, LEFT_TO_RIGHT, NONE
    }

    public static void toast(Context context, String msg) {
        try {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alert(Context context, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Html.fromHtml(msg)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void underDevelopment(Context context) {
        toast(context, "Under Development");
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

    public static void showExitDialog(final Activity context) {
        AlertDialog.Builder alertDialogBuilderExit = new AlertDialog.Builder(context);
        alertDialogBuilderExit.setTitle(context.getResources().getString(R.string.app_name)).setMessage("Do you want to Exit?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                context.finish();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilderExit.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static String getAndroidVersionName() {
        String builder = "";
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;
            try {
                fieldValue = field.getInt(new Object());
            } catch (Exception e) {
            }
            if (fieldValue == Build.VERSION.SDK_INT) {
                builder = fieldName;
            }
        }
        return builder;
    }

    public static String getAndroidVersionCode() {
        return Build.VERSION.RELEASE;
    }

    public static String getAndroidDeviceName() {
        return android.os.Build.DEVICE;
    }

    public static String getAndroidDeviceModelName() {
        return android.os.Build.MODEL;
    }

    private static int time;

    public static void showNotification(String notificationTitle, String msg, Context context, int drawableIcon, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(drawableIcon).setContentTitle(notificationTitle).setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg).setAutoCancel(true)
                .setSound(uri);
        mBuilder.setContentIntent(contentIntent);
        PendingIntent clearIntent = PendingIntent.getBroadcast(context, 1, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setDeleteIntent(clearIntent);
        mNotificationManager.notify(1, mBuilder.build());

    }

    /**
     * Indicates whether all type of network connectivity exists and it is possible to establish connections and pass data.
     * <p>
     * Always call this before attempting to perform data transactions.
     *
     * @param context The context to use. Usually your {@link android.app.Application} or {@link android.app.Activity} object.
     * @return {@code true} if network connectivity exists, {@code false} otherwise.
     */
    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cManager == null)
            return false;
        else {
            NetworkInfo[] nis = cManager.getAllNetworkInfo();
            if (nis != null) {
                for (NetworkInfo ni : nis) {
                    if (ni.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo network = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (network != null && network.isConnected()) {
            return true;
        }

        network = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (network != null && network.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void d(String msg) {
        if (BuildConfig.DEBUG)
            Log.d(msg);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        if (BuildConfig.DEBUG)
            Log.e("** " + msg + " **");
    }

    public static String changeFormat(String date) {
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date getdate = sd.parse(date);
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            return df.format(getdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String changeFormathash(String date) {
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MMM-dd");
            Date getdate = sd.parse(date);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(getdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String toHtml(Object object) {
        StringBuilder stringBuilder = new StringBuilder(256);
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object val = field.get(object);
                stringBuilder.append("<b>");
                stringBuilder.append(field.getName().substring(1, field.getName().length()));
                stringBuilder.append(": ");
                stringBuilder.append("</b>");
                stringBuilder.append(val);
                stringBuilder.append("<br>");
            }
        } catch (Exception e) {
            // Do nothing
        }
        return stringBuilder.toString();
    }

    // animation for startactivities
    public static void translateLeft(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.center_to_left, R.anim.right_to_center);
    }

    public static void translateRight(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.center_to_right, R.anim.left_to_center);
    }

    public static void translateTop(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.center_to_top, R.anim.bottom_to_center);
    }

    public static void translateBottom(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.center_to_bottom, R.anim.top_to_center);
    }

    public static void startAct(Context source, Class<?> act) {
        startAct(source, act, null, null, 4);
    }

    public static void startAct(Context source, Class<?> act, int position) {
        startAct(source, act, null, null, position);
    }

    public static void startAct(Context source, Class<?> act, Act position) {
        startAct(source, act, null, null, position);
    }

    public static void startAct(Context source, Class<?> act, String name, String value) {
        startAct(source, act, name, value, 4);
    }

    public static void startAct(Context source, Class<?> act, String name, String value, int position) {
        Intent intent = new Intent(source, act);
        if (name != null && value != null)
            intent.putExtra(name, value);
        source.startActivity(intent);
        ((Activity) source).finish();
        switch (position) {
            case 0:
                translateLeft(source);
                break;
            case 1:
                translateRight(source);
                break;
            case 2:
                translateTop(source);
                break;
            case 3:
                translateBottom(source);
                break;
            default:
                break;
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getCurrentDate() {
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        return s.toString();
    }

    // create a toast message for 10 sec delayd
    public static void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public class toastTime extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            time = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                if (time >= 5)
                    return null;
                try {
                    Thread.sleep(500L);
                    time++;
                } catch (InterruptedException localInterruptedException) {
                    while (true)
                        localInterruptedException.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            time = 10;
        }
    }

    public static boolean isEmailValid(String email) {
        String EMAILADDRESS_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAILADDRESS_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // get device screen size
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Point getScreenSize(Activity context) {
        Point size = new Point();
        WindowManager w = context.getWindowManager();
        Display d = w.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            d.getSize(size);
        } else {
            size.x = d.getWidth();
            size.y = d.getHeight();
        }
        return size;
    }

    public static int getScreenPart(Activity activity, int percent) {
        int i = getScreenSize(activity).y / percent;
        return i;
    }

    public static void vibrate(Context context, View v) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake);
        v.startAnimation(animation);
    }

    public static String getHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }

    public static void sendEmail(Context context, String subject, String message) {
        // Intent referFriendIntent = new Intent(Intent.ACTION_SEND);
        // referFriendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // referFriendIntent.putExtra(Intent.EXTRA_TEXT, message);
        // referFriendIntent.setType("message/rfc822");
        // final PackageManager pm = context.getPackageManager();
        // final List<ResolveInfo> matches = pm.queryIntentActivities(referFriendIntent, 0);
        // ResolveInfo best = null;
        // for (final ResolveInfo info : matches)
        // if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
        // best = info;
        // if (best != null)
        // referFriendIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        // try {
        // context.startActivity(referFriendIntent);
        // } catch (Exception e) {
        // Toast.makeText(context, "Make sure you have installed Gmail Application", 1000).show();
        // }

        sendEmail(context, null, subject, message);
    }

    public static void sendEmail(Context context, String[] toAddress, String subject, String message) {
        Intent referFriendIntent = new Intent(Intent.ACTION_SEND);
        referFriendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        referFriendIntent.putExtra(Intent.EXTRA_EMAIL, toAddress);
        referFriendIntent.putExtra(Intent.EXTRA_TEXT, message);
        referFriendIntent.setType("message/rfc822");
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(referFriendIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            referFriendIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        try {
            context.startActivity(referFriendIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Make sure you have installed Gmail Application", 1000).show();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * method to convert date format
     *
     * @param dateString        : date to be converted
     * @param formatSource      : source date format
     * @param formatDestination : destination date format
     **/
    @SuppressLint("SimpleDateFormat")
    public static String getFormatedDate(String dateString, String formatSource, String formatDestination) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatSource);// "yyyy-MM-dd'T'HH:mm:ssz"
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(dateString);
            String formated = new SimpleDateFormat(formatDestination).format(date);
            return formated;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * method to convert date format
     *
     * @param dateString        : date to be converted
     * @param formatSource      : source date format
     * @param formatDestination : destination date format
     * @param timezone          : timezone of source-date
     **/
    @SuppressLint("SimpleDateFormat")
    public static String getFormatedDate(String dateString, String formatSource, String formatDestination, String timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatSource);// "yyyy-MM-dd'T'HH:mm:ssz"
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            Date date = sdf.parse(dateString);
            String formated = new SimpleDateFormat(formatDestination).format(date);
            return formated;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * method to convert milliseconds into date String
     *
     * @param dateStringMillis  : milliseconds be converted into date String
     **/
    @SuppressLint("SimpleDateFormat")
    public static String getFormatedImageDate(String dateStringMillis) {
        try {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(Long.parseLong(dateStringMillis));
            return mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US) + ", " + mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + mCalendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isPastDate(Calendar selectedDate) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        return selectedDate.compareTo(calendar) < 1 ? true : false;
    }

    public static String bundle2string(Bundle bundle) {
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    public static String getCommaSeparatedString(int value) {
        DecimalFormat myFormatter = new DecimalFormat("#,##,##,##,###");
        return myFormatter.format(value);
    }

    public static String getCommaSeparatedString(String strValue) {
        int value;
        try {
            value = Integer.parseInt(strValue);
        } catch (Exception e) {
            return strValue;
        }
        DecimalFormat myFormatter = new DecimalFormat("#,##,##,##,###");
        return myFormatter.format(value);
    }

    public static String getFormatedMobileNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(phoneNumber);
        if (phoneNumber.length() > 3) {
            sb = sb.insert(3, "-");
        }
        if (sb.length() > 7) {
            sb = sb.insert(7, "-");
        }
        return sb.toString();
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
