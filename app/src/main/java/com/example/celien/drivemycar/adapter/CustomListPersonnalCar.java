package com.example.celien.drivemycar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.celien.drivemycar.R;

public class CustomListPersonnalCar extends ArrayAdapter<String>{

    public CustomListPersonnalCar(Context context, String[] cars) {
        super(context, R.layout.activity_custom_personnal_car_row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.activity_custom_personnal_car_row, parent, false);

        // Set the needed reference
        String singleCarItem    = getItem(position);
        TextView tvCarName      = (TextView)customView.findViewById(R.id.tvCarName);
        ImageView ivCarColor    = (ImageView)customView.findViewById(R.id.ivCarColor);

        // Set text
        tvCarName.setText(singleCarItem);

        // Dynamically change the displayed image

        return customView;
    }
}
