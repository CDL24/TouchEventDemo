package com.reseeit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reseeit.R;

public class WelcomePagerFragmentOld extends Fragment {

    public WelcomePagerFragmentOld(){}

    private View fragmentView;
    public int currentBgIndex = 0;
    public int[] welcomeImages = {R.drawable.img_welcome_one, R.drawable.img_welcome_two, R.drawable.img_welcome_three, R.drawable.img_welcome_four, R.drawable.img_welcome_five};

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
    }
}
