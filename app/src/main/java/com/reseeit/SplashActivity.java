package com.reseeit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.reseeit.util.Utility;

public class SplashActivity extends BaseActivity {

    private int progress = 0;
    private boolean isRunning = true;
    private SplashActivity context;

    //TODO set isDeveloperPreview to false before final build
    public static boolean isDeveloperPreview = false;
    public static boolean isLocalReceipt = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_splash);
        // getAppKeyHash();
        if (isDeveloperPreview)
            Utility.startAct(SplashActivity.this, MainActivity.class, 4);
        else {
            context = SplashActivity.this;
            Thread mThread = new Thread(new SplaseRunnable());
            mThread.start();
        }
    }

    /**
     * Runnable class required for thread
     */
    class SplaseRunnable implements Runnable {
        @Override
        public void run() {
            try {
                while (isRunning) {
                    Thread.sleep(500);
                    progress += 1;
                    if (progress == 5) {
                        isRunning = false;
                        splaseHandler.sendEmptyMessage(progress);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * handler to receive thread callback
     */
    @SuppressLint("HandlerLeak")
    Handler splaseHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (mPrefs.isLogin()) {
                Utility.startAct(context, AutoLoginActivity.class, 4);
            } else {
                if (isDeveloperPreview)
                    Utility.startAct(context, AutoLoginActivity.class, 4);
                else
                    Utility.startAct(context, WelcomeActivity.class, 4);
            }
        }
    };

    @Override
    public void onBackPressed() {
    }

    // private String getAppKeyHash() {
    // try {
    // PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
    // for (Signature signature : info.signatures) {
    // MessageDigest md;
    //
    // md = MessageDigest.getInstance("SHA");
    // md.update(signature.toByteArray());
    // String something = new String(Base64.encode(md.digest(), 0));
    // Log.d("Hash key >>>>> " + something);
    // return something;
    // }
    // } catch (NameNotFoundException e1) {
    // } catch (NoSuchAlgorithmException e) {
    // } catch (Exception e) {
    // }
    // return null;
    //
    // }
}
