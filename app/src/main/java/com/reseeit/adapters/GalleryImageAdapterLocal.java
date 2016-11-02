package com.reseeit.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reseeit.R;
import com.reseeit.com.reseeit.listeners.ImageClickListener;
import com.reseeit.models.ImageItem;
import com.reseeit.util.GalleryManager;

import java.io.File;
import java.util.ArrayList;

public class GalleryImageAdapterLocal extends RecyclerView.Adapter<GalleryImageAdapterLocal.ViewHolder> {
    private Context context;
    private ArrayList<ImageItem> items;
    private ImageClickListener listener;
    private int tagPos;

    public GalleryImageAdapterLocal(Context context, ArrayList<ImageItem> items, ImageClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public ArrayList<ImageItem> getSelectedPath() {
        ArrayList<ImageItem> itemSelected = new ArrayList<>();
        for (ImageItem img : items) {
            itemSelected.add(img);
        }
        return itemSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.inflater_gallery_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.get(position).imagePath != null && !items.get(position).imagePath.equals("")) {
            holder.img.setImageURI(Uri.fromFile(new File(items.get(position).imagePath)));
//            Picasso.with(context).load(new File(items.get(position).imagePath)).resizeDimen(R.dimen.profile_image, R.dimen.profile_image).centerCrop().placeholder(R.drawable.ic_img_box).error(R.drawable.ic_img_box).into(holder.img);
        }
        if (items.get(position).imageStatus.equals(GalleryManager.STATUS_PENDING)) {
            holder.imgStatus.setImageResource(R.drawable.ic_img_status_pending);
        } else if (items.get(position).imageStatus.equals(GalleryManager.STATUS_APPROVED)) {
            holder.imgStatus.setImageResource(R.drawable.ic_img_status_offer);
        } else {//if (items.get(position).imageStatus.equals(GalleryManager.STATUS_NOT_APPROVED)) {
            holder.imgStatus.setImageResource(R.drawable.ic_img_status_no_offer);
        }
        holder.btnSelector.setTag(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private ImageView img, imgStatus, btnSelector;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.gallery_img);
            imgStatus = (ImageView) itemView.findViewById(R.id.gallery_img_status);
            btnSelector = (ImageView) itemView.findViewById(R.id.gallery_img_selector);
            itemView.setOnClickListener(this);
            btnSelector.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == btnSelector.getId()) {
                btnSelector.setSelected(!btnSelector.isSelected());
                items.get(Integer.parseInt(v.getTag().toString())).isSelected = btnSelector.isSelected();
                if (btnSelector.isSelected()) {
                    btnSelector.setImageResource(R.drawable.img_selector);
                } else {
                    btnSelector.setImageResource(0);
                }
                listener.onImageClicked(btnSelector.isSelected());
            }
        }
    }
}
