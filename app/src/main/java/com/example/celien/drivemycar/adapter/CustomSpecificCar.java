package com.example.celien.drivemycar.adapter;


import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.ListSpecificCars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomSpecificCar extends ArrayAdapter<JSONObject> implements View.OnClickListener{
    ListSpecificCars caller;
    private JSONObject currentJson;
    private CheckBox cbSelectedCar;
    private int position;

    public CustomSpecificCar(Context ctxt, List<JSONObject> list, ListSpecificCars caller){
        super(ctxt, R.layout.custom_specific_car_row, list);
        this.caller = caller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_specific_car_row, parent, false);
        this.position = position;

        // Set the reference of the layout
        currentJson                       = getItem(this.position);
        cbSelectedCar                     = (CheckBox)customView.findViewById(R.id.cbSelectedCar);
        TextView tvBrand                  = (TextView)customView.findViewById(R.id.tvBrand);
        TextView tvModel                  = (TextView)customView.findViewById(R.id.tvModel);
        TextView tvOwnerEditable          = (TextView)customView.findViewById(R.id.tvOwnerEditable);
        TextView tvPriceEditable          = (TextView)customView.findViewById(R.id.tvEstimatedPriceEditable);


        try {
            tvBrand.setText(currentJson.getString("brand"));
            tvModel.setText(currentJson.getString("model"));
            tvOwnerEditable.setText(currentJson.getString("owner"));
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        return customView;
    }

    public void onClick(View v) {

        if(v.getId() == R.id.cbSelectedCar){
            if(cbSelectedCar.isChecked())
                caller.updateClickedUsername(currentJson, position, true);
            else if(!cbSelectedCar.isChecked())
                caller.updateClickedUsername(currentJson, position, false);
        }

    }

}
