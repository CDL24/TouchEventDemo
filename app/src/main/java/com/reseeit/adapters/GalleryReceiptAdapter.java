package com.reseeit.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.reseeit.R;
import com.reseeit.com.reseeit.listeners.ImageClickListener;
import com.reseeit.models.ImageDateItem;
import com.reseeit.models.ReceiptItem;

import java.util.ArrayList;

public class GalleryReceiptAdapter extends RecyclerView.Adapter<GalleryReceiptAdapter.ViewHolder> {
    private Context context;
    private int resource;
    private ArrayList<ImageDateItem> items;
    private ImageClickListener listener;
    private int listHeight;

    public GalleryReceiptAdapter(Context context, int resource, ArrayList<ImageDateItem> items, ImageClickListener listener) {
        this.context = context;
        this.resource = resource;
        this.items = items;
        this.listener = listener;
        // addTempData();
//		items.addAll(new GalleryManager(context).getAll_SortByDate());
        listHeight = (int) ((context.getResources().getDimension(R.dimen.small_margin) * 2) + context.getResources().getDimension(R.dimen.profile_image) + context.getResources().getDimension(R.dimen.textsize16) + 4);
    }

    // private void addTempData() {
    // ImageDateItem imageDateItem = new ImageDateItem();
    // imageDateItem.date = "Friday, Nov 6";
    // imageDateItem.imageList = new ArrayList<ImageItem>();
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // items.add(imageDateItem);
    // imageDateItem = new ImageDateItem();
    // imageDateItem.date = "Monday, Nov 2";
    // imageDateItem.imageList = new ArrayList<ImageItem>();
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // items.add(imageDateItem);
    // imageDateItem = new ImageDateItem();
    // imageDateItem.date = "Wednesday, Oct 28";
    // imageDateItem.imageList = new ArrayList<ImageItem>();
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // imageDateItem.imageList.add(new ImageItem());
    // items.add(imageDateItem);
    // }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    public void setItems(ArrayList<ImageDateItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDate.setText(items.get(position).date);
        LayoutParams param = holder.lvImageList.getLayoutParams();
        param.height = listHeight;
        holder.lvImageList.setLayoutParams(param);
        holder.lvImageList.setAdapter(new GalleryImageAdapter(context, items.get(position).imageList, listener));
        holder.itemView.setTag(position);
    }

    public void updateStatus(String receiptId, String status) {
        for (ImageDateItem imageDateItem:items) {
            for(ReceiptItem receiptItem : imageDateItem.imageList){
                if(receiptItem.receipt_id ==receiptId){
                    receiptItem.status = status;
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

       class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private RecyclerView lvImageList;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.gallery_tv_date);
            lvImageList = (RecyclerView) itemView.findViewById(R.id.gallery_rv_image_list);
            lvImageList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ViewHolder.this.itemView = itemView;
        }
    }
}
