package com.itridtechnologies.codenamefive.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class UniversalDialog extends DialogFragment {

    private String dialogTitle;
    private String dialogMessage;
    private String btnPositive;
    private String btnNegative;
    private int dialogIcon;
    private int actionCode;
    private int cancelCode;

    private DialogListener mListener;

    public UniversalDialog(String dialogTitle, String dialogMessage, String btnPositive, String btnNegative,
                           int dialogIcon, int actionCode, int cancelCode) {
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.btnPositive = btnPositive;
        this.btnNegative = btnNegative;
        this.dialogIcon = dialogIcon;
        this.actionCode = actionCode;
        this.cancelCode = cancelCode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //build dialog
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setIcon(dialogIcon)
                .setCancelable(false)
                .setPositiveButton(btnPositive, (dialog, which) -> {
                    mListener.onDialogButtonClicked(actionCode);
                    dismiss();
                })
                .setNegativeButton(btnNegative, (dialog, which) -> {
                    mListener.onDialogButtonClicked(cancelCode);
                    dismiss();
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    public interface DialogListener {
        void onDialogButtonClicked(int code);
    }
}//end class
