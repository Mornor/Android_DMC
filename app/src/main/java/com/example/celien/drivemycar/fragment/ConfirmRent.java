package com.example.celien.drivemycar.fragment;

import android.app.AlertDialog;
import android.app. Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import com.example.celien.drivemycar.R;

public class ConfirmRent extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_rent, container, false);
        return rootView;
    }
}
