package com.example.celien.drivemycar.adapter;


import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.ListSpecificCars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomSpecificCar extends ArrayAdapter<JSONObject>{
    ListSpecificCars caller;
    private  JSONObject currentJson;
    private CheckBox cbSelectedCar;
    private List<JSONObject> list;

    public CustomSpecificCar(Context ctxt, List<JSONObject> list, ListSpecificCars caller){
        super(ctxt, R.layout.custom_specific_car_row, list);
        this.list = list;
        this.caller = caller;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_specific_car_row, parent, false);

        try{
            for(int i = 0 ; i < this.list.size() ; i++){
                Log.d("In List custom size ", String.valueOf(list.size()));
                Log.d("In List custom owner " ,list.get(i).getString("owner"));
                Log.d("In List custom brand " ,list.get(i).getString("brand"));
                Log.d("In List custom model " ,list.get(i).getString("model"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


        // Set the reference of the layout
        currentJson                       = getItem(position);
        cbSelectedCar                     = (CheckBox)customView.findViewById(R.id.cbSelectedCar);
        TextView tvBrand                  = (TextView)customView.findViewById(R.id.tvBrand);
        TextView tvModel                  = (TextView)customView.findViewById(R.id.tvModel);
        TextView tvOwnerEditable          = (TextView)customView.findViewById(R.id.tvOwnerEditable);
        TextView tvPriceEditable          = (TextView)customView.findViewById(R.id.tvEstimatedPriceEditable);

        try {
            tvBrand.setText(currentJson.getString("brand"));
            tvModel.setText(currentJson.getString("model"));
            tvOwnerEditable.setText(currentJson.getString("owner"));
            cbSelectedCar.setTag(position);
            cbSelectedCar.setChecked(currentJson.getBoolean("checked"));
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        cbSelectedCar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    caller.updateClickedUsername(getItem(position), true); // Add to the List
                    try {
                        Log.d("Has been checked model ", getItem(position).getString("model"));
                        Log.d("Has been checked brand ", getItem(position).getString("brand"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                    caller.updateClickedUsername(currentJson, false); // Delete from the List
            }
        });

        return customView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
