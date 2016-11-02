package com.reseeit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.reseeit.R;
import com.reseeit.ReSeeItApp;
import com.reseeit.models.RewardItem;
import com.reseeit.util.Utility;

import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.MyViewHolder> {
    private List<RewardItem> data;
    private Context activity;
    private View.OnCreateContextMenuListener contextMenuListener;
    private int earnedPoints = 0;

    public RewardsAdapter(Context activity, View.OnCreateContextMenuListener contextMenuListener, List<RewardItem> data) {
        this.activity = activity;
        this.contextMenuListener = contextMenuListener;
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.tvName.setText(data.get(position).com_name);
//        holder.tvName2.setText(data.get(position).summary);
        holder.tvMiles.setText(data.get(position).distance);

        int rewardPoints = (int) earnedPoints;
        try {
            rewardPoints = Integer.parseInt(data.get(position).reward_point);
        } catch (Exception e) {
        }
        holder.rewardPoints = rewardPoints;
        holder.tvValue.setText((rewardPoints - earnedPoints) + " points to redeem reward");
        setCouponImage(holder.imgRewaed, data.get(position).reward_img);
        setCouponImage(holder.imgRewaedDetail, data.get(position).reward_summary_img);
        holder.btnMore.setTag(position);
    }

    private void setCouponImage(ImageView imageView, String imgPath) {
        if (imgPath != null && !imgPath.equals("")) {
            ImageLoader imgLoader = ReSeeItApp.getInstance().getImageLoader();
            imgLoader.get(imgPath, ImageLoader.getImageListener(imageView, R.drawable.coupon_place_holder, R.drawable.coupon_place_holder));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int resource) {
        View view = LayoutInflater.from(activity).inflate(R.layout.inflater_rewards, parent, false);
        return new MyViewHolder(view);
    }

    public void setList(List<RewardItem> data, float earnedPoints) {
        this.data = data;
        this.earnedPoints = (int) earnedPoints;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView btnMore, imgRewaed, imgRewaedDetail;
        LinearLayout llMore;
        TextView tvName, tvName2, tvMiles, tvValue;
        private View progressView;
        int rewardPoints=0;

        public MyViewHolder(View view) {
            super(view);
            imgRewaed = (ImageView) view.findViewById(R.id.img_reward);
            imgRewaedDetail = (ImageView) view.findViewById(R.id.img_reward_detail);
            btnMore = (ImageView) view.findViewById(R.id.img_btn_more);
            btnMore.setOnClickListener(this);
            llMore = (LinearLayout) view.findViewById(R.id.ll_detail_view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvName2 = (TextView) view.findViewById(R.id.tv_name2);
            tvMiles = (TextView) view.findViewById(R.id.tv_mi);
            tvValue = (TextView) view.findViewById(R.id.tv_p);
            view.setOnCreateContextMenuListener(contextMenuListener);
            progressView = view.findViewById(R.id.viewProgress);
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

//                int rewardPoints = 0;
//                try {
//                    rewardPoints = Integer.parseInt(data.get(Integer.parseInt(v.getTag().toString())).reward_point);
//                } catch (Exception e) {
//                }
                updateProgress(progressView, rewardPoints);
            }
        }
    }

    private void updateProgress(View progressView, int totalPoints) {
        float newProgress = ((float)earnedPoints / (float)totalPoints);
//        ViewGroup.LayoutParams param = progressView.getLayoutParams();
//        param.width = (int)(progressView.getMeasuredWidth()*newProgress);
//        progressView.setLayoutParams(param);
        ScaleAnimation animation = new ScaleAnimation(0, newProgress, 100, 100);
        animation.setDuration(500);
        animation.setFillAfter(true);
        progressView.startAnimation(animation);
    }
}
