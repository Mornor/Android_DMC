package com.example.celien.drivemycar.tabs;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.celien.drivemycar.R;

public class TabSearchCar extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_search_car, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

    }
}