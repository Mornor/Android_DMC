package com.example.celien.drivemycar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.tabs.TabOperations;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomTabOperation extends ArrayAdapter<JSONObject> {

    private JSONObject currentJson;
    private TabOperations caller;
    private List<JSONObject> list;
    private TextView tvFromDate;
    private TextView tvToDate;


    public CustomTabOperation(Context context, List<JSONObject> list, TabOperations caller){
        super(context, R.layout.custom_fragment_tab_operations, list);
        this.list = list;
        this.caller = caller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.custom_fragment_tab_operations, parent, false);

        currentJson     = getItem(position);
        tvFromDate      = (TextView)rootView.findViewById(R.id.tvFromEditable);
        tvToDate        = (TextView)rootView.findViewById(R.id.tvToEditable);

        try{
            tvFromDate.setText(currentJson.getString("fromDate").substring(0, 10));
            tvToDate.setText(currentJson.getString("toDate").substring(0, 10));
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        return rootView;
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
