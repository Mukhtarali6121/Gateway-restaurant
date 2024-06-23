package com.example.gatewayrestaurant.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.example.gatewayrestaurant.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class NoInternetBottomSheet extends BottomSheetDialogFragment {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    View view;
    //    DialogNationalitySelectionBinding mBinding;
    Context mContext;
    private bottomSheetListener mListener;
    TextView tvCancel;
    Button btnRetry;

    public NoInternetBottomSheet() {
    }

    @SuppressLint("ValidFragment")
    public NoInternetBottomSheet(@NonNull Context context, bottomSheetListener mListener) {
        mContext = context;
        this.mListener = mListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
        setCancelable(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.bottom_sheet_no_internet, container, false);

        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        tvCancel = view.findViewById(R.id.tvCancel);
        btnRetry = view.findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> {
            mListener.onCardClicked(true);
            dismiss();
        });

        tvCancel.setOnClickListener(v -> {
            mListener.onCardClicked(false);
            dismiss();
        });
        return view;
    }

    public interface bottomSheetListener {
        void onCardClicked(boolean isRetryClicked);
    }
}
