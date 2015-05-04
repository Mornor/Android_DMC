package com.example.celien.drivemycar.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CustomRequestData extends ArrayAdapter<JSONObject> {

    private RequestData caller;
    private List<JSONObject> list;
    private JSONObject currentJson;
    private TextView tvRequestSent;
    private TextView tvAccepted;
    private TextView tvRefuted;
    private TextView tvNoAnswer;
    private Button btnSelectOwner;

    public CustomRequestData(Context ctxt, List<JSONObject> list, RequestData caller){
        super(ctxt, R.layout.custom_request_data, list);
        this.caller = caller;
        this.list   = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_request_data, parent, false);

        // Get the item from layout
        currentJson     = getItem(position);
        tvRequestSent   = (TextView)v.findViewById(R.id.tvRequestSentEditable);
        tvAccepted      = (TextView)v.findViewById(R.id.tvNbAcceptedEditable);
        tvRefuted       = (TextView)v.findViewById(R.id.tvNbRefutedEditable);
        tvNoAnswer      = (TextView)v.findViewById(R.id.tvNbNoAnswerEditable);
        btnSelectOwner  = (Button)v.findViewById(R.id.btnSelectOwner);

        // Update value of fields
        try{
            tvRequestSent.setText(currentJson.getString("requestSent"));
            tvAccepted.setText(currentJson.getString("nbAccepted"));
            tvRefuted.setText(currentJson.getString("nbRefuted"));
            tvNoAnswer.setText(currentJson.getString("nbNoAnswer"));
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        return v;
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

