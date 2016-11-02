package com.reseeit.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.reseeit.R;

public class BaseContainerFragment extends Fragment {

    private String currentFrg;

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        currentFrg = fragment.getClass().getSimpleName();
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.add(R.id.container_framelayout, fragment, currentFrg);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        currentFrg = fragment.getClass().getSimpleName();
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.replace(R.id.container_framelayout, fragment, currentFrg);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFragment(Fragment fragment, boolean addToBackStack) {
        currentFrg = fragment.getClass().getSimpleName();
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.add(R.id.container_framelayout, fragment, currentFrg);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFragment(Fragment fragment, boolean addToBackStack, String tag) {
        currentFrg = tag;
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.add(R.id.container_framelayout, fragment, currentFrg);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean popFragment() {
        try {
            boolean isPop = false;
            if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                isPop = true;
                getChildFragmentManager().popBackStack();
            }
            return isPop;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean popAllFragment() {
        try {
        if (getChildFragmentManager().getBackStackEntryCount() == 0)
            return false;
        int count = getChildFragmentManager().getBackStackEntryCount();
        for (int counter = 0; counter < count; counter++)
            getChildFragmentManager().popBackStack();
        return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int popFragmentCount() {
        return getChildFragmentManager().getBackStackEntryCount();
    }

}
