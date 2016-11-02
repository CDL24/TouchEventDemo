package com.reseeit;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.reseeit.adapters.WelcomePagerAdapter;

public class TutorialActivity extends FragmentActivity implements OnPageChangeListener {
    RadioGroup rGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tutorial);
        findViews();
    }

    private void findViews() {
        rGroup = (RadioGroup) findViewById(R.id.radioGroup);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new WelcomePagerAdapter(getSupportFragmentManager(), 6));
        pager.setOnPageChangeListener(this);

//        InkPageIndicator inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
//        inkPageIndicator.setViewPager(pager);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int pos) {
        if (pos == rGroup.getChildCount() - 1) {
            TutorialActivity.this.finish();
        }
        ((RadioButton) rGroup.getChildAt(pos)).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.left_out);
    }

    @Override
    protected void onPause() {
        finish();
        overridePendingTransition(0, R.anim.left_out);
        super.onPause();
    }
}
