package com.reseeit.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.reseeit.R;
import com.reseeit.adapters.WelcomePagerAdapter;

public class TutorialFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private View mView;
    RadioGroup rGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.act_tutorial, null);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
    }

    private void findViews() {
        rGroup = (RadioGroup) mView.findViewById(R.id.radioGroup);
        ViewPager pager = (ViewPager) mView.findViewById(R.id.viewpager);
        pager.setAdapter(new WelcomePagerAdapter(getChildFragmentManager(), 6));
        pager.setOnPageChangeListener(this);
//        pager.setOffscreenPageLimit(6);

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
            getActivity().onBackPressed();
        }
        ((RadioButton) rGroup.getChildAt(pos)).setChecked(true);
    }
}
