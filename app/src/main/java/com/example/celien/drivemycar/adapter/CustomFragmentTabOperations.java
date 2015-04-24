package com.example.celien.drivemycar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.tabs.TabOperations;

import org.json.JSONObject;

import java.util.List;

public class CustomFragmentTabOperations extends ArrayAdapter<JSONObject> {

    private TabOperations caller;

    public CustomFragmentTabOperations(Context context, List<JSONObject> list, TabOperations caller) {
        super(context, R.layout.custom_fragment_tab_operations, list);
        this.caller = caller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_fragment_tab_operations, parent, false);

        return customView; 
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public JSONObject getItem(int position) {
        return super.getItem(position);
    }
}
