package com.reseeit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reseeit.R;
import com.reseeit.com.reseeit.listeners.ImageClickListener;
import com.reseeit.models.ReceiptItem;
import com.reseeit.util.GalleryManager;

import java.util.ArrayList;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder> {
	private Context context;
	private ArrayList<ReceiptItem> items;
	private ImageClickListener listener;

	public GalleryImageAdapter(Context context, ArrayList<ReceiptItem> items, ImageClickListener listener) {
		this.context = context;
		this.items = items;
		this.listener = listener;
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
		if (items.get(position).receipt_img != null && !items.get(position).receipt_img.equals("")) {
//			Picasso.with(context).load(new File(items.get(position).receipt_img)).resizeDimen(R.dimen.profile_image, R.dimen.profile_image).centerCrop().placeholder(R.drawable.ic_img_box).error(R.drawable.ic_img_box).into(holder.img);
		}
		if (items.get(position).status.equals(GalleryManager.STATUS_PENDING)) {
			holder.imgStatus.setImageResource(R.drawable.ic_img_status_pending);
		} else if (items.get(position).status.equals(GalleryManager.STATUS_APPROVED)) {
			holder.imgStatus.setImageResource(R.drawable.ic_img_status_offer);
		} else if (items.get(position).status.equals(GalleryManager.STATUS_NOT_APPROVED)) {
			holder.imgStatus.setImageResource(R.drawable.ic_img_status_no_offer);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
		private ImageView img, imgStatus;

		public ViewHolder(View itemView) {
			super(itemView);
			img = (ImageView) itemView.findViewById(R.id.gallery_img);
			imgStatus = (ImageView) itemView.findViewById(R.id.gallery_img_status);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			listener.onImageClicked();
		}
	}
}
