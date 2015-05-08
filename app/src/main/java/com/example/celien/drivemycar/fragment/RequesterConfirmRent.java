package com.example.celien.drivemycar.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.celien.drivemycar.R;

import org.json.JSONObject;

public class RequesterConfirmRent extends DialogFragment {

    private JSONObject transaction; // Contains data of the current Transaction.

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Set the final milage");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requester_confirm_rent, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

    }
}
