package com.reseeit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reseeit.R;

public class WelcomePageFragment extends Fragment {

    public WelcomePageFragment(){}

    private View fragmentView;
    public int currentBgIndex = 0;
    public int[] welcomeImages = {R.drawable.img_welcome_one_, R.drawable.img_welcome_two_, R.drawable.img_welcome_three_, R.drawable.img_welcome_five_, R.drawable.img_welcome_four_,0};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.frg_welcome_pager, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView mImg = (ImageView) fragmentView.findViewById(R.id.pager_image);
        mImg.setImageResource(welcomeImages[currentBgIndex]);
        if(currentBgIndex<5)
            fragmentView.setBackgroundResource(R.drawable.bg);
        else
            fragmentView.setBackgroundResource(0);
    }
}
