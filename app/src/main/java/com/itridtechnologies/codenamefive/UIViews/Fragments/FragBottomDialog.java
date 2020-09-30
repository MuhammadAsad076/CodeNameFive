package com.itridtechnologies.codenamefive.UIViews.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.itridtechnologies.codenamefive.R;

import org.jetbrains.annotations.NotNull;

public class FragBottomDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_dialog, container, false);

        //find views
        TextView openCamera = view.findViewById(R.id.img_btn_camera);
        TextView openGallery = view.findViewById(R.id.img_btn_gallery);
        TextView closeDialog = view.findViewById(R.id.img_btn_close);

        openCamera.setOnClickListener(v -> {
            mListener.onImageButtonClicked(1);
            dismiss();
        });

        openGallery.setOnClickListener(v -> {
            mListener.onImageButtonClicked(2);
            dismiss();
        });

        closeDialog.setOnClickListener(v -> {
            mListener.onImageButtonClicked(3);
            dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
        void onImageButtonClicked(int requestCode);
    }
}
