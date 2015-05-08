package com.example.celien.drivemycar.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;

import org.w3c.dom.Text;


public class Paiement extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Synthesis of your rent");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paiement, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

        String ownerName = "";
        double amountToPay = 0.0;

        // Get the Bundle data
        Bundle rcvd = getArguments();
        if(rcvd != null){
            ownerName   = rcvd.getString("ownerName");
            amountToPay = rcvd.getDouble("amountToPay");
        }

        // Get items from the layout
        TextView tvPaiement     = (TextView)v.findViewById(R.id.tvPaiement);
        TextView tvAmountToPay  = (TextView)v.findViewById(R.id.tvAmountToPay);
        Button btnOk            = (Button)v.findViewById(R.id.btnOk);
        Button btnCancel        = (Button)v.findViewById(R.id.btnDoItLater);

        // Set their value
        tvPaiement.setText("You have to pay to " +ownerName+ ": ");
        tvAmountToPay.setText(String.valueOf(amountToPay)+ " e");

        // Set the listeners
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransactionSatus();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    private void updateTransactionSatus(){
        Toast.makeText(getActivity(), "Update status", Toast.LENGTH_SHORT).show();
    }

    private void dismissDialog(){
        dismiss();
    }
}

