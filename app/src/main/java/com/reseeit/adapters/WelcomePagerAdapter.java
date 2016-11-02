package com.reseeit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.reseeit.fragments.WelcomePageFragment;

public class WelcomePagerAdapter extends FragmentPagerAdapter {

    private final int size;

    public WelcomePagerAdapter(FragmentManager fm,int size) {
        super(fm);
        this.size=size;
    }
    public WelcomePagerAdapter(FragmentManager fm) {
        super(fm);
        this.size=5;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Fragment getItem(int pos) {
		WelcomePageFragment myFragment = new WelcomePageFragment();
		myFragment.currentBgIndex = pos;
		return myFragment;
    }
}