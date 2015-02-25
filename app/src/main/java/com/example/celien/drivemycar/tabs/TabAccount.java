package com.example.celien.drivemycar.tabs;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;


public class TabAccount extends Fragment {

    private TextView tvUsername;
    private TextView tvMail;
    private TextView tvSettings;
    private TextView tvLogout;
    private TextView tvEditableRanking;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_account, container, false);
        init(rootView);

        return rootView;
    }

    private void init(View v){
        tvUsername          = (TextView)v.findViewById(R.id.tvUsername);
        tvMail              = (TextView)v.findViewById(R.id.tvMail);
        tvSettings          = (TextView)v.findViewById(R.id.tvSettings);
        tvLogout            = (TextView)v.findViewById(R.id.tvLogout);
        tvEditableRanking   = (TextView)v.findViewById(R.id.tvEditableRanking);

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
    }
}
