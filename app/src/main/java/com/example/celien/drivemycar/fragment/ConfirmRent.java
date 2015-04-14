package com.example.celien.drivemycar.fragment;

import android.app.AlertDialog;
import android.app. Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.celien.drivemycar.R;

public class ConfirmRent extends DialogFragment{

    private EditText etMileage;
    private Button btnConfirm;
    private Button btnCancel;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Confirm rent");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_rent, container, false);
        init(rootView);
        setListeners();
        return rootView;
    }

    private void init(View v){
        etMileage  = (EditText)v.findViewById(R.id.etSetOdometer);
        btnConfirm = (Button)v.findViewById(R.id.btnOk);
        btnCancel  = (Button)v.findViewById(R.id.btnCancel);
    }

    private void setListeners(){
        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendOdometerValue();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    private void dismissDialog(){
        dismiss();
    }

    private void sendOdometerValue(){
        Toast.makeText(this.getActivity(), "Confirm has been pressed", Toast.LENGTH_SHORT).show();
    }
}
