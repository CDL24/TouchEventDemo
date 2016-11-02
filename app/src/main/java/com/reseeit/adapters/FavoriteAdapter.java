package com.reseeit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.reseeit.R;
import com.reseeit.ReSeeItApp;
import com.reseeit.util.Favorite;
import com.reseeit.util.Utility;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private Context activity;
    private FavoriteListener listener;
    private ArrayList<Favorite> favoriteList;

    public FavoriteAdapter(Context activity, FavoriteListener listener, ArrayList<Favorite> favoriteList) {
        this.activity = activity;
        this.listener = listener;
        this.favoriteList = favoriteList;
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.tvName.setText(favoriteList.get(position).data.com_name);
        holder.tvMiles.setText(favoriteList.get(position).data.Distance);
        holder.tvDesc.setText(favoriteList.get(position).data.description);
        holder.tvName2.setText("");
        holder.tvValue.setText(favoriteList.get(position).data.summary);
        holder.tvExpires.setText("Expire " + favoriteList.get(position).data.expiry_date);
        holder.tvLink.setText(Html.fromHtml(favoriteList.get(position).data.com_url));
        holder.tvLink.setMovementMethod(LinkMovementMethod.getInstance());
        setCouponImage(holder.imgCoupon, position);

        holder.btnMore.setTag(String.valueOf(position));
        holder.btnEarnPoints.setTag(String.valueOf(position));
        holder.btnRedeem.setTag(String.valueOf(position));
        holder.btnFavorite.setTag(String.valueOf(position));

        boolean isEarning = listener.hasInteraction(favoriteList.get(position));
        holder.btnEarnPoints.setAlpha(isEarning ? 1.0f : 0.5f);
    }

    private void setCouponImage(ImageView imageView, int postion) {
        if (favoriteList.get(postion).image_path != null && !favoriteList.get(postion).image_path.equals("") && favoriteList.get(postion).data.coupon_img != null && !favoriteList.get(postion).data.coupon_img.equals("")) {
            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
            imgLoader.get(favoriteList.get(postion).image_path + favoriteList.get(postion).data.coupon_img, ImageLoader.getImageListener(imageView, R.drawable.coupon_place_holder, R.drawable.coupon_place_holder));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int resource) {
        View view = LayoutInflater.from(activity).inflate(R.layout.inflater_coupon, parent, false);
        return new MyViewHolder(view);
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

            btnFavorite.setVisibility(View.GONE);

            btnMore.setOnClickListener(this);
            btnRedeem.setOnClickListener(this);
            btnEarnPoints.setOnClickListener(this);

            imgCoupon = (ImageView) view.findViewById(R.id.img_coupon);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvName2 = (TextView) view.findViewById(R.id.tv_name2);
            tvMiles = (TextView) view.findViewById(R.id.tv_mi);
            tvLink = (TextView) view.findViewById(R.id.tv_points_link);
            tvDesc = (TextView) view.findViewById(R.id.tv_points_desc);
            tvValue = (TextView) view.findViewById(R.id.tv_coupon_value);
            tvExpires = (TextView) view.findViewById(R.id.tv_coupon_expire);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_btn_more) {
//                ExpandAnimation mAnimation =  new ExpandAnimation(llMore,500);
//                llMore.startAnimation(mAnimation);
                btnMore.setRotation(llMore.getVisibility() == View.VISIBLE ? 0 : 180);
                if (CouponAdapter.isExpandAnim) {
                    if (llMore.getVisibility() == View.GONE) Utility.expand(llMore);
                    else Utility.collapse(llMore);
                } else {
                    llMore.setVisibility(llMore.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                }
            } else if (v.getId() == R.id.btn_earn_points) {
                boolean isEarning =   listener.onEarnPointClick(favoriteList.get(Integer.parseInt(v.getTag().toString())));
                v.setAlpha(isEarning?1.0f:0.5f);
            } else if (v.getId() == R.id.tv_redeem_coupon) {
                listener.onCouponDetailClick(favoriteList.get(Integer.parseInt(v.getTag().toString())));
            }
        }
    }

    public interface FavoriteListener {
        boolean onEarnPointClick(Favorite favorite);

        void onCouponDetailClick(Favorite favorite);
        boolean hasInteraction(Favorite favorite);
    }
}
