package com.reseeit.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reseeit.R;
import com.reseeit.adapters.CouponAdapter;
import com.reseeit.com.reseeit.listeners.InteractionListener;
import com.reseeit.com.reseeit.listeners.InteractionSeenListener;
import com.reseeit.com.reseeit.listeners.RedeemListener;
import com.reseeit.models.Coupon;
import com.reseeit.models.FavouriteModel;
import com.reseeit.models.Interaction;
import com.reseeit.models.RedeemModel;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.Favorite;
import com.reseeit.util.FavoriteManager;
import com.reseeit.util.WebServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyFragment extends BaseFragment implements CouponAdapter.RedeemClickListener, LibPostListner, RedeemListener, InteractionSeenListener, SwipeRefreshLayout.OnRefreshListener {

    private View mView;

    private RecyclerView rvNearBy;
    private LinearLayoutManager mLayoutManager;
    private CouponAdapter mAdapter;
    private ArrayList<Coupon> nearByList;
    private String couponBasePath;

    private PostLibResponse postRequest;
    private final int CODE_REDEEM_COUPON = 0, CODE_FAVOURITE = 1;
    private InteractionListener mInteractionListener;
    private FavoriteManager favoriteManager;
    private SwipeRefreshLayout swipeLayout;
    private Coupon couponToFavourite;
    public static Interaction interaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_nearby, null);
        return mView;
    }

    private static NearbyFragment mFragment;

    public static NearbyFragment getInstance() {
        if (mFragment == null)
            mFragment = new NearbyFragment();
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvNearBy = (RecyclerView) mView.findViewById(R.id.rv_nearby);
        rvNearBy.setLayoutManager(mLayoutManager);
        if (mAdapter == null) {
            nearByList = new ArrayList<Coupon>();
            mAdapter = new CouponAdapter(getActivity(), NearbyFragment.this, nearByList);
            rvNearBy.setAdapter(mAdapter);
        } else {
            rvNearBy.setAdapter(mAdapter);
        }
        rvNearBy.setItemViewCacheSize(nearByList.size());
        favoriteManager = new FavoriteManager(getActivity());
        swipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimaryDark);
    }

    //    private Coupon couponToRedeem;
    @Override
    public void onRedeemClick(Coupon coupon) {
//        couponToRedeem=coupon;
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("user_id", userId);
        parmas.put("coupon_id", coupon.coupon_id);
        parmas.put("point", coupon.max_number);
        postRequest = new PostLibResponse(NearbyFragment.this, new RedeemModel(), getActivity(), parmas, WebServices.USER_COUPON_REWARD, CODE_REDEEM_COUPON, true);
    }

    @Override
    public boolean onEarnPointClick(Coupon coupon) {
        interaction = mInteractionListener.getInteraction(coupon.user_id);
        if (interaction == null) {
            return false;
        } else if (interaction.link_to_interaction_type == null || interaction.link_to_interaction == null) {
            if (!interaction.Type.equals("default")) {
                return false;
            }
        } else {
            if (interaction.Status.equals("1")) {
                return false;
            }
        }
        InteractionFragmentDialog mDialog = new InteractionFragmentDialog(interaction, NearbyFragment.this, coupon.coupon_id);
        mDialog.show(getChildFragmentManager(), mDialog.getClass().getSimpleName());
        return true;
    }

    @Override
    public boolean hasInteraction(Coupon coupon) {
        Interaction interaction = mInteractionListener.getInteraction(coupon.user_id);
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

    @Override
    public void onCouponDetailClick(Coupon coupon) {
        CouponDetailFragmentDialog mAdDialog = new CouponDetailFragmentDialog(coupon, couponBasePath, NearbyFragment.this);
        mAdDialog.show(getChildFragmentManager(), mAdDialog.getClass().getSimpleName());
    }


    @Override
    public void onFavoriteClick(Coupon coupon) {
        couponToFavourite = coupon;
        Map<String, String> parmas = new HashMap<>();
        parmas.put("Logged_UserId", userId);
        parmas.put("coupon_id", coupon.coupon_id);
        postRequest = new PostLibResponse(NearbyFragment.this, new FavouriteModel(), getActivity(), parmas, WebServices.COUPON_FAVORITE, CODE_FAVOURITE, true);
    }

    public void setCouponList(ArrayList<Coupon> nearByList, String couponBasePath) {
        this.nearByList = nearByList;
        this.couponBasePath = couponBasePath;
        try {
            mAdapter.setCouponList(nearByList, couponBasePath);
            rvNearBy.setItemViewCacheSize(this.nearByList.size());
        } catch (Exception e) {
        }
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_REDEEM_COUPON) {
            if (clsGson != null) {
                RedeemModel mRedeemModel = (RedeemModel) clsGson;
                alertSuccess(mRedeemModel.message);
                if (mRedeemModel.status.equals("1")) {
//                    ((MainActivity)getActivity()).mRedeemModel(mRedeemModel.Earnpoint);
//                    mAdapter.onCouponRedeemed(couponToRedeem);
                }
            } else {
                alertError("Could Not Redeem Coupon");
            }
        } else if (requestCode == CODE_FAVOURITE) {
            if (clsGson != null) {
                FavouriteModel mFavouriteModel = (FavouriteModel) clsGson;
                alertSuccess(mFavouriteModel.message);
                if (mFavouriteModel.status.equals("1")) {
                    Favorite mFavorite = new Favorite();
                    mFavorite.image_path = couponBasePath;
                    couponToFavourite.coupon_favorite_id = mFavouriteModel.coupon_favorite_id;
                    mFavorite.data = couponToFavourite;
                    mFavorite.interactions = mInteractionListener.getInteractionListForCoupon(couponToFavourite.user_id);
                    int status = favoriteManager.add(couponToFavourite.coupon_id, mFavorite.toString());
                    if (status == 1) {
                        mAdapter.removeCoupon(couponToFavourite.coupon_id);
                        refreshFavoriteList();
                    } else if (status == 0) {
                        alertError("Coupon already added to favorite list");
                    } else if (status == -1) {
                        alertError("Coupon not added to favorite list");
                    }
                }
            } else {
                alertError("Favorite Coupon not added");
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_REDEEM_COUPON) {
            alertError("Could Not Redeem Coupon");
        } else if (requestCode == CODE_FAVOURITE) {
            alertError("Favorite Coupon not added");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (postRequest != null) {
            postRequest.onDestroy();
            postRequest = null;
        }
    }

    public void setInteractionListener(InteractionListener mInteractionListener) {
        this.mInteractionListener = mInteractionListener;
    }

    @Override
    public void onInteractionSeen(Interaction interaction, String couponId) {
        mInteractionListener.onInteractionSeen(interaction);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(false);
        refreshCouponList();
    }
}
