package com.reseeit.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.reseeit.R;
import com.reseeit.ReSeeItApp;
import com.reseeit.models.Coupon;
import com.reseeit.util.Utility;

import java.util.ArrayList;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {
    private Context activity;
    private RedeemClickListener listener;
    private ArrayList<Coupon> couponList;
    private String couponBasePath;
    public static boolean isExpandAnim = false;

    public CouponAdapter(Context activity, RedeemClickListener listener, ArrayList<Coupon> couponList) {
        this.activity = activity;
        this.listener = listener;
        this.couponList = couponList;
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.tvName.setText(couponList.get(position).com_name);
//        holder.tvName2.setText(couponList.get(position).summary);
//        holder.tvValue.setText("Maximum coupon value is $" + couponList.get(position).max_number);
        holder.tvName2.setText("");
        holder.tvValue.setText(couponList.get(position).summary);

        holder.tvMiles.setText(couponList.get(position).Distance);
        holder.tvDesc.setText(couponList.get(position).description);
        holder.tvExpires.setText("Expire " + couponList.get(position).expiry_date);
        holder.tvLink.setText(Html.fromHtml(couponList.get(position).com_url));
        holder.tvLink.setMovementMethod(LinkMovementMethod.getInstance());
        setCouponImage(holder.imgCoupon, position);

        holder.btnMore.setTag(String.valueOf(position));
        holder.btnEarnPoints.setTag(String.valueOf(position));
        holder.btnRedeem.setTag(String.valueOf(position));
        holder.btnFavorite.setTag(String.valueOf(position));

//        if (listener.hasInteraction(couponList.get(position))) {
//            holder.btnEarnPoints.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnEarnPoints.setVisibility(View.GONE);
//        }
        boolean isEarning = listener.hasInteraction(couponList.get(position));
        holder.btnEarnPoints.setAlpha(isEarning ? 1.0f : 0.5f);
    }

    private void setCouponImage(ImageView imageView, int postion) {
        if (couponBasePath != null && !couponBasePath.equals("") && couponList.get(postion).coupon_img != null && !couponList.get(postion).coupon_img.equals("")) {
            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
            imgLoader.get(couponBasePath + couponList.get(postion).coupon_img, ImageLoader.getImageListener(imageView, R.drawable.coupon_place_holder, R.drawable.coupon_place_holder));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int resource) {
        View view = LayoutInflater.from(activity).inflate(R.layout.inflater_coupon, parent, false);
        return new MyViewHolder(view);
    }

    public void setCouponList(ArrayList<Coupon> couponList, String couponBasePath) {
        this.couponBasePath = couponBasePath;
        this.couponList = couponList;
        notifyDataSetChanged();
    }

    public void removeCoupon(String couponIdToRemove) {
        for (int couponCounter = 0; couponCounter < this.couponList.size(); couponCounter++) {
            if (this.couponList.get(couponCounter).coupon_id.equals(couponIdToRemove)) {
                this.couponList.remove(couponCounter);
                break;
            }
        }
        notifyDataSetChanged();
    }

//    public void onCouponRedeemed(Coupon couponToRedeem) {
//        for (Coupon coupon : couponList) {
//            if (coupon.coupon_id.equals(couponToRedeem.coupon_id)) {
//                coupon.isRedeemed = true;
//                break;
//            }
//        }
//        notifyDataSetChanged();
//    }

    public void setTextViewFontAvenir(TextView... textViews) {
        Typeface type = Typeface.createFromAsset(activity.getAssets(), "avenir-light.ttf");
        for (int viewCounter = 0; viewCounter < textViews.length; viewCounter++) {
            textViews[viewCounter].setTypeface(type);
        }
    }

    public void setTextViewFontAvenirBold(TextView... textViews) {
        Typeface type = Typeface.createFromAsset(activity.getAssets(), "avenir_black.ttf");
        for (int viewCounter = 0; viewCounter < textViews.length; viewCounter++) {
            textViews[viewCounter].setTypeface(type, Typeface.BOLD);
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView btnMore, btnFavorite;
        TextView btnRedeem;
        LinearLayout llMore;
        RelativeLayout btnEarnPoints;

        ImageView imgCoupon;
        TextView tvName, tvName2, tvMiles, tvLink, tvDesc, tvValue, tvExpires;

        public MyViewHolder(View view) {
            super(view);
            btnFavorite = (ImageView) view.findViewById(R.id.img_favorite);
            btnMore = (ImageView) view.findViewById(R.id.img_btn_more);
            btnRedeem = (TextView) view.findViewById(R.id.tv_redeem_coupon);
            btnEarnPoints = (RelativeLayout) view.findViewById(R.id.btn_earn_points);
            llMore = (LinearLayout) view.findViewById(R.id.ll_detail_view);

            btnMore.setOnClickListener(this);
            btnRedeem.setOnClickListener(this);
            btnEarnPoints.setOnClickListener(this);
            btnFavorite.setOnClickListener(this);

            imgCoupon = (ImageView) view.findViewById(R.id.img_coupon);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvName2 = (TextView) view.findViewById(R.id.tv_name2);
            tvMiles = (TextView) view.findViewById(R.id.tv_mi);
            tvLink = (TextView) view.findViewById(R.id.tv_points_link);
            tvDesc = (TextView) view.findViewById(R.id.tv_points_desc);
            tvValue = (TextView) view.findViewById(R.id.tv_coupon_value);
            tvExpires = (TextView) view.findViewById(R.id.tv_coupon_expire);

            setTextViewFontAvenir(tvLink, tvDesc, tvExpires, tvValue);
            setTextViewFontAvenirBold(tvName,tvName2, tvMiles, (TextView)view.findViewById(R.id.tv_earn),(TextView)view.findViewById(R.id.tv_redeem_coupon));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_btn_more) {
//                ExpandAnimation mAnimation =  new ExpandAnimation(llMore,500);
//                llMore.startAnimation(mAnimation);
                btnMore.setRotation(llMore.getVisibility() == View.VISIBLE ? 0 : 180);
                if (isExpandAnim) {
                    if (llMore.getVisibility() == View.GONE) Utility.expand(llMore);
                    else Utility.collapse(llMore);
                } else {
                    llMore.setVisibility(llMore.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                }
            } else if (v.getId() == R.id.btn_earn_points) {
                boolean isEarning = listener.onEarnPointClick(couponList.get(Integer.parseInt(v.getTag().toString())));
                v.setAlpha(isEarning ? 1.0f : 0.5f);
            } else if (v.getId() == R.id.tv_redeem_coupon) {
                listener.onCouponDetailClick(couponList.get(Integer.parseInt(v.getTag().toString())));
            } else if (v.getId() == R.id.img_favorite) {
                listener.onFavoriteClick(couponList.get(Integer.parseInt(v.getTag().toString())));
            }
        }
    }

    public interface RedeemClickListener {
        boolean onEarnPointClick(Coupon coupon);

        void onCouponDetailClick(Coupon coupon);

        void onFavoriteClick(Coupon coupon);

        boolean hasInteraction(Coupon coupon);
    }
}
