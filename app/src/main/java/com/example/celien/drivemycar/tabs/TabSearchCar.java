package com.example.celien.drivemycar.tabs;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.celien.drivemycar.R;

import org.w3c.dom.Text;

public class TabSearchCar extends Fragment{

    // Fragment variables
    EditText etBrand;
    Spinner spEnergy;
    TextView tvConsoFuel;
    EditText etNbSits;
    TextView dateFrom;
    TextView timeFrom;
    TextView dateTo;
    TextView timeTo;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_search_car, container, false);
        init(rootView);
        setListeners();
        return rootView;
    }

    private void init(View v){
        etBrand     = (EditText)v.findViewById(R.id.etBrand);
        spEnergy    = (Spinner)v.findViewById(R.id.spFuelList);
        tvConsoFuel = (TextView)v.findViewById(R.id.tvFuelCons);
        etNbSits    = (EditText)v.findViewById(R.id.etNbSits);
        dateFrom    = (TextView)v.findViewById(R.id.tvPickDateFrom);
        timeFrom    = (TextView)v.findViewById(R.id.tvChooseTimeFrom);
        dateTo      = (TextView)v.findViewById(R.id.tvPickDateTo);
        timeTo      = (TextView)v.findViewById(R.id.tvChooseTimeTo);
    }

    private void setListeners(){
        etBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvConsoFuel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        etNbSits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dateFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        timeFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        timeTo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

    }
}
