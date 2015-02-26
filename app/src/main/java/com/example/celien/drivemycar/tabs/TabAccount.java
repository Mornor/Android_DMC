package com.example.celien.drivemycar.tabs;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;

import org.w3c.dom.Text;


public class TabAccount extends Fragment {

    private TextView tvUsername;
    private TextView tvMail;
    private TextView tvSettings;
    private TextView tvLogout;
    private TextView tvEditableRanking;
    private ImageView ivMyCar;
    private TextView tvMyCar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_account, container, false);

        if(getArguments() == null)
            Log.d("Null", "arguments");
        else {
            String args = this.getArguments().getString("username");
            Log.d("Username received : ", args);
        }
        init(rootView);
        return rootView;
    }

    private void init(View v){
        tvUsername          = (TextView)v.findViewById(R.id.tvUsername);
        tvMail              = (TextView)v.findViewById(R.id.tvMail);
        tvSettings          = (TextView)v.findViewById(R.id.tvSettings);
        tvLogout            = (TextView)v.findViewById(R.id.tvLogout);
        tvEditableRanking   = (TextView)v.findViewById(R.id.tvEditableRanking);
        tvMyCar             = (TextView)v.findViewById(R.id.tvMyCar);
        ivMyCar             = (ImageView)v.findViewById(R.id.ivMyCar);


        tvSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Settings", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        tvMyCar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "tvMyCar", Toast.LENGTH_SHORT).show();
            }
        });

        ivMyCar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "ivMyCar", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
