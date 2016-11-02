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
import com.reseeit.models.ImageDateItemLocal;
import com.reseeit.models.ImageItem;
import com.reseeit.util.GalleryManager;

import java.util.ArrayList;
import java.util.Collections;

public class GalleryReceiptAdapterLocal extends RecyclerView.Adapter<GalleryReceiptAdapterLocal.ViewHolder> {
    private Context context;
    private int resource;
    private ArrayList<ImageDateItemLocal> items;
    private ImageClickListener listener;
    private int listHeight;

    public GalleryReceiptAdapterLocal(Context context, int resource, ImageClickListener listener) {
        this.context = context;
        this.resource = resource;
        this.listener = listener;
        items = new ArrayList<>();
        items.addAll(new GalleryManager(context).getAll_SortByDate());
        Collections.reverse(items);
        for (ImageDateItemLocal imageDateItemLocal : items) {
            Collections.reverse(imageDateItemLocal.imageList);
        }
        listHeight = (int) ((context.getResources().getDimension(R.dimen.small_margin) * 2) + context.getResources().getDimension(R.dimen.profile_image) + context.getResources().getDimension(R.dimen.textsize16) + 4);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
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
        holder.galleryImageAdapterLocal = new GalleryImageAdapterLocal(context, items.get(position).imageList, listener);
        holder.lvImageList.setAdapter(holder.galleryImageAdapterLocal);
        holder.itemView.setTag(position);
    }

    public ArrayList<ImageItem> getSelectedImages() {
        ArrayList<ImageItem> itemSelected = new ArrayList<>();
        for (ImageDateItemLocal imgItem : items) {
            for (ImageItem img : imgItem.imageList) {
                if (img.isSelected) {
                    itemSelected.add(img);
                }
            }
        }
        return itemSelected;
    }


    public void updateStatus(String receiptId, String status) {
        for (ImageDateItemLocal imageDateItem : items) {
            for (ImageItem receiptItem : imageDateItem.imageList) {
                if (String.valueOf(receiptItem.receipt_id).equals(receiptId)) {
                    receiptItem.imageStatus = status;
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
        GalleryImageAdapterLocal galleryImageAdapterLocal;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.gallery_tv_date);
            lvImageList = (RecyclerView) itemView.findViewById(R.id.gallery_rv_image_list);
            lvImageList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ViewHolder.this.itemView = itemView;
        }
    }
}
