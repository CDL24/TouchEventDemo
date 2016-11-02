package com.reseeit.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.reseeit.BaseActivity;
import com.reseeit.MainActivity;
import com.reseeit.R;
import com.reseeit.SplashActivity;
import com.reseeit.adapters.GalleryReceiptAdapter;
import com.reseeit.adapters.GalleryReceiptAdapterLocal;
import com.reseeit.com.reseeit.listeners.ImageClickListener;
import com.reseeit.models.ImageDateItem;
import com.reseeit.models.ImageItem;
import com.reseeit.models.NotificationModel;
import com.reseeit.models.ReceiptItem;
import com.reseeit.models.ReceiptModel;
import com.reseeit.net.LibPostListner;
import com.reseeit.net.PostLibResponse;
import com.reseeit.util.GalleryManager;
import com.reseeit.util.WebServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReceiptListFragment extends BaseFragment implements ImageClickListener, LibPostListner {

    private View mView;
    private GalleryReceiptAdapter galleryReceiptAdapter;
    private GalleryReceiptAdapterLocal galleryReceiptAdapterLocal;
    private RecyclerView lvGalleryReceipts;
    private PostLibResponse postRequest;
    private final int CODE_RECEIPT_LIST = 0;
    private ImageView btnDelete;
//    private FloatingActionButton btnDeleteFab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frg_receipt_list, null);
        try {
            getActivity().registerReceiver(statusUpdateReceiver, new IntentFilter(MainActivity.INTENT_REFRESH_GALLERY_STATUS));
        } catch (Exception e) {
        }
         try {
            getActivity().registerReceiver(refreshReceiver, new IntentFilter(MainActivity.INTENT_REFRESH_GALLERY_LIST));
        } catch (Exception e) {
        }
        return mView;
    }

    private static ReceiptListFragment mFragment;

    public static Fragment getInstance() {
        if (mFragment == null)
            mFragment = new ReceiptListFragment();
        return mFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionTitle(getGreetingsMessage());
        init();
        if (!SplashActivity.isLocalReceipt)
            loadReceipts();
    }

    private GalleryManager galleryManager;

    private void init() {
        lvGalleryReceipts = (RecyclerView) mView.findViewById(R.id.lv_gallery_receipts);
        lvGalleryReceipts.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (!SplashActivity.isLocalReceipt) {
            galleryReceiptAdapter = new GalleryReceiptAdapter(getActivity(), R.layout.inflater_gallery, new ArrayList<ImageDateItem>(), ReceiptListFragment.this);
            lvGalleryReceipts.setAdapter(galleryReceiptAdapter);
        } else {
            galleryReceiptAdapterLocal = new GalleryReceiptAdapterLocal(getActivity(), R.layout.inflater_gallery, ReceiptListFragment.this);
            lvGalleryReceipts.setAdapter(galleryReceiptAdapterLocal);
        }
        galleryManager = new GalleryManager(getActivity());
//        btnDeleteFab = (FloatingActionButton) mView.findViewById(R.id.btn_trash_fab);
        btnDelete = (ImageView) mView.findViewById(R.id.btn_trash);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SplashActivity.isLocalReceipt) {
                    ArrayList<ImageItem> selectedItems = galleryReceiptAdapterLocal.getSelectedImages();
                    BaseActivity.Log.d("selectedItems size = " + selectedItems.size());
                    if (selectedItems.size() == 0)
                        return;
                    for (ImageItem image : selectedItems) {
                        if (image != null)
                            galleryManager.removeHistory(image.imagePath);
                    }
//                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE).setTitleText("Are yi").showContentText(true).setContentText(Html.fromHtml(msg).toString()).show();

                    if (selectedItems.size() == 1)
                        toast(selectedItems.size() + " Receipt deleted");
                    else
                        toast(selectedItems.size() + " Receipts deleted");
                    galleryReceiptAdapterLocal = new GalleryReceiptAdapterLocal(getActivity(), R.layout.inflater_gallery, ReceiptListFragment.this);
                    lvGalleryReceipts.setAdapter(galleryReceiptAdapterLocal);
//                    btnDeleteFab.hide();
                }
            }
        });
    }

    private void loadReceipts() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("type", "all");
        parmas.put("user_id", userId);
        postRequest = new PostLibResponse(ReceiptListFragment.this, new ReceiptModel(), getActivity(), parmas, WebServices.RECEIPT_LIST, CODE_RECEIPT_LIST, true);
    }

    @Override
    public void onImageClicked() {
    }

    @Override
    public void onImageClicked(boolean isSelected) {
//        if (isSelected) {
////            btnDelete.setVisibility(View.VISIBLE);
//            btnDeleteFab.show();
//        } else {
//            if (galleryReceiptAdapterLocal.getSelectedImages().size() == 0) {
////                btnDelete.setVisibility(View.GONE);
//                btnDeleteFab.hide();
//            }
//        }
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        if (requestCode == CODE_RECEIPT_LIST) {
            if (clsGson != null) {
                ReceiptModel mReceiptModel = (ReceiptModel) clsGson;
                if (mReceiptModel.status.equals("1")) {
                    if (mReceiptModel.data != null) {
                        galleryReceiptAdapter.setItems(sortListByDate(mReceiptModel.data));
                    }
                } else {
                    alertError(mReceiptModel.message);
                }
            } else {
                alertError("Could Not Load Receipts");
            }
        }
    }

    private ArrayList<ImageDateItem> sortListByDate(List<ReceiptItem> data) {
        ArrayList<ImageDateItem> items = new ArrayList<ImageDateItem>();
        if (data.size() == 0)
            return items;
        ImageDateItem imageDateItem = new ImageDateItem();
        imageDateItem.date = getDate(data.get(0).added_date);
        imageDateItem.imageList = new ArrayList<ReceiptItem>();
        imageDateItem.imageList.add(data.get(0));
        for (int counter = 1; counter < data.size(); counter++) {
            String date = getDate(data.get(counter).added_date);
            if (!date.equals(imageDateItem.date)) {
                items.add(imageDateItem);
                imageDateItem = new ImageDateItem();
                imageDateItem.date = getDate(data.get(counter).added_date);
                imageDateItem.imageList = new ArrayList<ReceiptItem>();
            }
            imageDateItem.imageList.add(data.get(counter));
        }
        items.add(imageDateItem);
        return items;
    }

    private Calendar mCalendar;

    private String getDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// "2015-11-10 11:56:54"
        try {
            Date date = sdf.parse(dateString);
            if (mCalendar == null) {
                mCalendar = Calendar.getInstance();
            }
            mCalendar.setTime(date);
            return mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US) + ", " + mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + mCalendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {
        if (requestCode == CODE_RECEIPT_LIST) {
            alertError("Could Not Load Receipts");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().unregisterReceiver(statusUpdateReceiver);
        } catch (Exception e) {
        }
         try {
            getActivity().unregisterReceiver(refreshReceiver);
        } catch (Exception e) {
        }
        if (postRequest != null) {
            postRequest.onDestroy();
            postRequest = null;
        }
    }

    BroadcastReceiver statusUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String notificationData = intent.getBundleExtra("extra").getString("0", "");
            NotificationModel notificationModel = new Gson().fromJson(notificationData, NotificationModel.class);
            if (galleryReceiptAdapterLocal != null) {
                galleryReceiptAdapterLocal.updateStatus(notificationModel.userdata.receipt_id, notificationModel.userdata.status);
            }
        }
    };
    BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            galleryReceiptAdapterLocal = new GalleryReceiptAdapterLocal(getActivity(), R.layout.inflater_gallery, ReceiptListFragment.this);
            lvGalleryReceipts.setAdapter(galleryReceiptAdapterLocal);
        }
    };
}