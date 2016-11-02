package com.reseeit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.reseeit.com.reseeit.listeners.InteractionListener;
import com.reseeit.fragments.FavoritesFragment;
import com.reseeit.fragments.FeaturedFragment;
import com.reseeit.fragments.NearbyFragment;
import com.reseeit.models.Coupon;
import com.reseeit.util.CouponType;

import java.util.ArrayList;
import java.util.List;

public class ExplorePagerAdapter extends FragmentPagerAdapter {

    private final FeaturedFragment mFeaturedFragment;
    private final NearbyFragment mNearbyFragment;
    private final FavoritesFragment mFavoritesFragment;

    private ArrayList<Coupon> featuredList, nearByList;
    private InteractionListener mInteractionListener;

    public ExplorePagerAdapter(FragmentManager fm,InteractionListener mInteractionListener) {
        super(fm);

        this.mInteractionListener=mInteractionListener;
        mFeaturedFragment = FeaturedFragment.getInstance();
        mNearbyFragment = NearbyFragment.getInstance();
        mFavoritesFragment = FavoritesFragment.getInstance();

        featuredList = new ArrayList<Coupon>();
        nearByList = new ArrayList<Coupon>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FeaturedFragment  mFeaturedFragment = FeaturedFragment.getInstance();
                mFeaturedFragment.setInteractionListener(mInteractionListener);
                return mFeaturedFragment;
            case 1:
                NearbyFragment  mNearbyFragment = NearbyFragment.getInstance();
                mNearbyFragment.setInteractionListener(mInteractionListener);
                return mNearbyFragment;
            case 2:
                FavoritesFragment   mFavoritesFragment = FavoritesFragment.getInstance();
                return mFavoritesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void setCouponList(List<Coupon> couponList,String couponImagePath) {
        featuredList.clear();
        nearByList.clear();
        for (Coupon coupon : couponList) {
            if (coupon.coupon_type.equals(CouponType.TYPE_NEAR_BY)) {
                nearByList.add(coupon);
            } else if (coupon.coupon_type.equals(CouponType.TYPE_FEATURED)) {
                featuredList.add(coupon);
            }
        }
        mNearbyFragment.setCouponList(nearByList,couponImagePath);
        mFeaturedFragment.setCouponList(featuredList,couponImagePath);
    }
}
