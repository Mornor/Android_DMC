package com.example.celien.drivemycar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.models.Car;

import java.util.ArrayList;
import java.util.List;

public class CustomListPersonnalCar extends ArrayAdapter<Car>{

    public CustomListPersonnalCar(Context context, List<Car> cars) {
        super(context, R.layout.activity_custom_personnal_car_row, cars);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.activity_custom_personnal_car_row, parent, false);

        // Set the needed reference
        Car selectedCar         = getItem(position);
        TextView tvCarName      = (TextView)customView.findViewById(R.id.tvCarName);
        ImageView ivCarColor    = (ImageView)customView.findViewById(R.id.ivCarColor);

        // Set text
        tvCarName.setText(selectedCar.getBrand() + " " + selectedCar.getModel());

        // Dynamically change the displayed image (color of the car)
        if(selectedCar.getC02_cons() < 150)
            ivCarColor.setImageResource(R.drawable.green_car);
        if(selectedCar.getC02_cons() > 150 && selectedCar.getC02_cons() < 300)
            ivCarColor.setImageResource(R.drawable.orange_car);
        if(selectedCar.getC02_cons() > 300)
            ivCarColor.setImageResource(R.drawable.red_car);

        return customView;
    }
}
