package cn.pedant.SweetAlert;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.StateSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.reseeit.R;

@SuppressWarnings("deprecation")
public class SweetAlertDialog extends Dialog implements View.OnClickListener {
    private View mDialogView;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
    private int mAlertType;
    private FrameLayout mErrorFrame;
    private FrameLayout mSuccessFrame;
    private FrameLayout mProgressFrame;
    private Button mConfirmButton;
    private Button mCancelButton;
    private FrameLayout mWarningFrame;
    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private boolean mCloseFromCancel;

    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int PROGRESS_TYPE = 5;

    public static interface OnSweetClickListener {
        public void onClick(SweetAlertDialog sweetAlertDialog);
    }

    public SweetAlertDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    public SweetAlertDialog(Context context, int alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mAlertType = alertType;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
        mSuccessFrame = (FrameLayout) findViewById(R.id.success_frame);
        mProgressFrame = (FrameLayout) findViewById(R.id.progress_dialog);
        mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeAlertType(mAlertType, true);

        if (confirmButtonDrawable != null) {

            // mCancelButton.setBackgroundDrawable(confirmButtonDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mConfirmButton.setBackground(confirmButtonDrawable);
            } else {
                mConfirmButton.setBackgroundDrawable(confirmButtonDrawable);
            }
        }
        if (cancelButtonDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mCancelButton.setBackground(cancelButtonDrawable);
            } else {
                mCancelButton.setBackgroundDrawable(cancelButtonDrawable);
            }
        }
    }

    private void restore() {
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);

        if (confirmButtonDrawable != null) {
            mConfirmButton.setBackgroundDrawable(confirmButtonDrawable);
            // mCancelButton.setBackgroundDrawable(confirmButtonDrawable);
        } else {
            // mCancelButton.setBackgroundResource(R.drawable.blue_button_background);
        }
        if (cancelButtonDrawable != null) {
            mCancelButton.setBackgroundDrawable(cancelButtonDrawable);
        } else {
        }
        mErrorFrame.clearAnimation();
    }

    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case ERROR_TYPE:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TYPE:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    break;
                case WARNING_TYPE:
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case PROGRESS_TYPE:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public int getAlerType() {
        return mAlertType;
    }

    public void changeAlertType(int alertType) {
        changeAlertType(alertType, false);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public SweetAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }


    public String getContentText() {
        return mContentText;
    }

    public SweetAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public SweetAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public boolean isShowContentText() {
        return mShowContent;
    }

    public SweetAlertDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public SweetAlertDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public SweetAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public SweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public SweetAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }


    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    private static StateListDrawable confirmButtonDrawable, cancelButtonDrawable;

    public static void setSecondaryColor(int color) {
        confirmButtonDrawable = new StateListDrawable();
        confirmButtonDrawable.addState(new int[]{android.R.attr.state_pressed}, createStrokeDrawable(color));
        confirmButtonDrawable.addState(StateSet.WILD_CARD, createNormalDrawable(color));

        cancelButtonDrawable = new StateListDrawable();
        cancelButtonDrawable.addState(new int[]{android.R.attr.state_pressed}, createStrokeDrawable(color));
        cancelButtonDrawable.addState(StateSet.WILD_CARD, createNormalDrawable(color));
    }

    public static GradientDrawable createNormalDrawable(int color) {
        GradientDrawable out = new GradientDrawable();
        out.setShape(GradientDrawable.RECTANGLE);
        out.setCornerRadius(6);
        out.setColor(color);
        return out;
    }

    public static GradientDrawable createStrokeDrawable(int color) {
        GradientDrawable out = new GradientDrawable();
        out.setShape(GradientDrawable.RECTANGLE);
        out.setCornerRadius(6);
        out.setColor(color);
        return out;
    }
}