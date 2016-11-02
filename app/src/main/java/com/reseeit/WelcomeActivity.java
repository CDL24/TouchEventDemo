package com.reseeit;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.reseeit.adapters.WelcomePagerAdapter;

public class WelcomeActivity extends FragmentActivity implements OnPageChangeListener {
    RadioGroup rGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);
        findViews();
    }

    private void findViews() {
        rGroup = (RadioGroup) findViewById(R.id.radioGroup);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new WelcomePagerAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);
        setTextViewFontAvenir((TextView) findViewById(R.id.splash_login));
        setTextViewFontAvenir((TextView)findViewById(R.id.splash_signup));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_login:
                Util.startAct(WelcomeActivity.this, LoginWithActivity.class, 4);
                break;
            case R.id.splash_signup:
                SignupActivity.isFromLogin = false;
                Util.startAct(WelcomeActivity.this, SignupActivity.class, 4);
                break;

            default:
                break;
        }
    }

    public void setTextViewFontAvenir(TextView... textViews) {
        Typeface type = Typeface.createFromAsset(getAssets(), "avenir-light.ttf");
        for (int viewCounter = 0; viewCounter < textViews.length; viewCounter++) {
            textViews[viewCounter].setTypeface(type);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int pos) {
        ((RadioButton)rGroup.getChildAt(pos)).setChecked(true);
    }
}
