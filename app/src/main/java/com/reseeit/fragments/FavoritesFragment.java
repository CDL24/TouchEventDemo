package com.reseeit.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reseeit.MainActivity;
import com.reseeit.R;
import com.reseeit.adapters.FavoriteAdapter;
import com.reseeit.com.reseeit.listeners.InteractionSeenListener;
import com.reseeit.com.reseeit.listeners.RedeemListener;
import com.reseeit.models.Coupon;
import com.reseeit.models.Interaction;
import com.reseeit.models.InteractionModel;
import com.reseeit.models.StatusModel;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.Favorite;
import com.reseeit.util.FavoriteManager;
import com.reseeit.util.WebServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FavoritesFragment extends BaseFragment implements FavoriteAdapter.FavoriteListener, RedeemListener, LibPostListner, InteractionSeenListener, SwipeRefreshLayout.OnRefreshListener {

    private View mView;

    private RecyclerView lvFavorites;
    private LinearLayoutManager mLayoutManager;
    private FavoriteAdapter mAdapter;
    private ArrayList<Favorite> favoriteList;

    private PostLibResponse postRequest;
    private final int CODE_REDEEM_COUPON = 0;
    private final int CODE_INTERACTION_WATCH = 1;
    private SwipeRefreshLayout swipeLayout;
    private FavoriteManager favoriteManager;
    public static Interaction interaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_favorites, null);
        try {
            getActivity().registerReceiver(refreshReceiver, new IntentFilter(MainActivity.INTENT_REFRESH_FAVORITE));
        } catch (Exception e) {
        }
        return mView;
    }

    private static FavoritesFragment mFragment;

    public static FavoritesFragment getInstance() {
        if (mFragment == null)
            mFragment = new FavoritesFragment();
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        lvFavorites = (RecyclerView) mView.findViewById(R.id.rv_favorites);
        lvFavorites.setLayoutManager(mLayoutManager);

        favoriteList = new FavoriteManager(getActivity()).getAll();
        mAdapter = new FavoriteAdapter(getActivity(), FavoritesFragment.this, favoriteList);
        lvFavorites.setAdapter(mAdapter);
        lvFavorites.setItemViewCacheSize(favoriteList.size());
        swipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimaryDark);
    }

    @Override
    public boolean onEarnPointClick(Favorite favorite) {
        interaction = getInteraction(favorite);
        if (interaction == null){
            return false;
        } else if(interaction.link_to_interaction_type == null || interaction.link_to_interaction == null) {
            if (!interaction.Type.equals("default")) {
                return false;
            }
        } else {
            if (interaction.Status.equals("1")) {
                return false;
            }
        }
        InteractionFragmentDialog mDialog = new InteractionFragmentDialog(interaction, FavoritesFragment.this, favorite.data.coupon_id);
        mDialog.show(getChildFragmentManager(), mDialog.getClass().getSimpleName());
        return true;
    }

    public Interaction getInteraction(Favorite favorite) {
        String userId = favorite.data.user_id;
        Interaction interactions = null;
        if (favorite == null || favorite.interactions == null)
            return null;
        for (Interaction interaction : favorite.interactions) {
            if (interaction.user_id.equals(userId)) {
                    interactions = interaction;
            }
        }
        return interactions;
    }

    @Override
    public boolean hasInteraction(Favorite favorite) {
        Interaction interaction = getInteraction(favorite);
        if (interaction == null) {
            return false;
        } else if (interaction.link_to_interaction_type == null || interaction.link_to_interaction == null) {
            if (interaction.Type.equals("default")) {
                return true;
            }
        } else {
            if (interaction.Status.equals("1")) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static boolean isExpired(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");// 12/31/2015
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date == null) {
            return false;
        }

        Calendar selectedDate = Calendar.getInstance(Locale.getDefault());
        selectedDate.setTime(date);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return selectedDate.compareTo(calendar) >= 0 ? true : false;
    }

    @Override
    public void onCouponDetailClick(Favorite favorite) {
        CouponDetailFragmentDialog mAdDialog = new CouponDetailFragmentDialog(favorite.data, favorite.image_path, FavoritesFragment.this);
        mAdDialog.show(getChildFragmentManager(), mAdDialog.getClass().getSimpleName());
    }

    @Override
    public void onRedeemClick(Coupon coupon) {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("user_id", userId);
        parmas.put("coupon_id", coupon.coupon_id);
        parmas.put("point", coupon.max_number);
        postRequest = new PostLibResponse(FavoritesFragment.this, new StatusModel(), getActivity(), parmas, WebServices.USER_COUPON_REWARD, CODE_REDEEM_COUPON, true);
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_REDEEM_COUPON) {
            if (clsGson != null) {
                StatusModel mStatusModel = (StatusModel) clsGson;
                if (mStatusModel.status.equals("1")) {
                    alertSuccess(mStatusModel.message);
                } else {
                    alertError(mStatusModel.message);
                }
            } else {
                alertError("Could Not Redeem Coupon");
            }
        }
        else if (requestCode == CODE_INTERACTION_WATCH) {
            if (clsGson != null) {
                InteractionModel mInteractionModel = (InteractionModel) clsGson;
                if (mInteractionModel.status.equals("1")) {
//                    alertSuccess(mInteractionModel.message);
//                    ((MainActivity)getActivity()).mRedeemModel(Integer.parseInt(mInteractionModel.Earnpoint));
                } else {
                    alertError(mInteractionModel.message);
                }
            } else {
                alertError("Interaction failed");
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_REDEEM_COUPON) {
            alertError("Could Not Redeem Coupon");
        }
//        else if (requestCode == CODE_INTERACTION_WATCH) {
//            alertError("Interaction failed");
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().unregisterReceiver(refreshReceiver);
        } catch (Exception e) {
        }
        if (postRequest != null) {
            postRequest.onDestroy();
            postRequest = null;
        }
    }

    @Override
    public void onInteractionSeen(Interaction seenInteraction, String couponId) {
        for (Favorite favorite : favoriteList) {
            if (couponId.equals(favorite.data.coupon_id)) {
                for (Interaction interaction : favorite.interactions) {
                    if (interaction.loyalty_interaction_id.equals(seenInteraction.loyalty_interaction_id)) {
                        interaction.Status = "1";
                        if (favoriteManager == null)
                            favoriteManager = new FavoriteManager(getActivity());
                        favoriteManager.update(couponId, favorite.toString());
                        break;
                    }
                }
            }
        }

        mAdapter.notifyDataSetChanged();

        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("user_id", seenInteraction.user_id);
        parmas.put("interaction_id", seenInteraction.loyalty_interaction_id);
        parmas.put("Logged_UserId", userId);
        parmas.put("award_point", seenInteraction.award_points);
        postRequest = new PostLibResponse(FavoritesFragment.this, new InteractionModel(), getActivity(), parmas, WebServices.INTERACTION_WATCH, CODE_INTERACTION_WATCH, false);
    }

    BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            favoriteList = new FavoriteManager(getActivity()).getAll();
            mAdapter = new FavoriteAdapter(getActivity(), FavoritesFragment.this, favoriteList);
            lvFavorites.setAdapter(mAdapter);
            lvFavorites.setItemViewCacheSize(favoriteList.size());
        }
    };

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        favoriteList = new FavoriteManager(getActivity()).getAll();
        mAdapter = new FavoriteAdapter(getActivity(), FavoritesFragment.this, favoriteList);
        lvFavorites.setAdapter(mAdapter);
        lvFavorites.setItemViewCacheSize(favoriteList.size());
        swipeLayout.setRefreshing(false);
    }
}
