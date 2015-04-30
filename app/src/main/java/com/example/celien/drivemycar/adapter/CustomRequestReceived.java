package com.example.celien.drivemycar.adapter;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.RequestReceived;
import com.example.celien.drivemycar.fragment.ConfirmRent;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class CustomRequestReceived extends ArrayAdapter<JSONObject> {

    private JSONObject currentJson;
    private RequestReceived caller;
    private TextView tvUserSource;
    private TextView tvBrand;
    private TextView tvModel;
    private TextView tvFromDate;
    private TextView tvToDate;
    private Button btnValidate;
    private Button btnCancel;
    private List<JSONObject> list;

    public CustomRequestReceived(Context context, List<JSONObject> list, RequestReceived caller) {
        super(context, R.layout.custom_request_received, list);
        this.caller = caller;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_request_received, parent, false);

        // Declare it final, so that I can access it in the btn.OnclickMethod().
        final int pos = position;

        // Get the elements on the layouts
        currentJson  = getItem(position);
        tvUserSource = (TextView)v.findViewById(R.id.tvUserSource);
        tvBrand      = (TextView)v.findViewById(R.id.tvBrand);
        tvModel      = (TextView)v.findViewById(R.id.tvModel);
        tvFromDate   = (TextView)v.findViewById(R.id.tvDateFrom);
        tvToDate     = (TextView)v.findViewById(R.id.tvDateTo);
        btnValidate  = (Button)v.findViewById(R.id.btnValidate);
        btnCancel    = (Button)v.findViewById(R.id.btnCancel);

        // Set the value of the fields
        try {
            tvUserSource.setText(currentJson.getString("userSource"));
            tvBrand.setText(currentJson.getString("brand"));
            tvModel.setText(currentJson.getString("model"));
            tvFromDate.setText(currentJson.getString("fromDate").substring(0, 10)+" at" +currentJson.getString("fromDate").substring(10, currentJson.getString("fromDate").toString().length() - 5)+ "h ");
            tvToDate.setText(currentJson.getString("toDate").substring(0, 10)+" at" +currentJson.getString("toDate").substring(10, currentJson.getString("toDate").toString().length() - 5)+ "h ");
        }catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Set the button listeners
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickValidate(getItem(pos));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickCancel(getItem(pos));
            }
        });

        return v;
    }

    private void onClickValidate(JSONObject object){
        String currentNotificationId = "";
        try{
            currentNotificationId = object.getString("idNotification");
        } catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        // Update the request value into DB
        new HttpAsyncNotif(this).execute(Action.UPDATE_REQUEST_STATE.toString(), currentNotificationId, Action.CONFIRM_RENT.toString());

        // Update ListView
        updateListView(object);
    }

    private void onClickCancel(JSONObject object){
        String currentNotificationId = "";
        try{
            currentNotificationId = object.getString("idNotification");
        } catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }
        new HttpAsyncNotif(this).execute(Action.UPDATE_REQUEST_STATE.toString(), currentNotificationId, Action.REFUTE_RENT.toString());
        updateListView(object);
    }

    /*** Delete item from the List<JSONObject> and update the related ListView row
     * @param item : item to be deleted (or not)**/
    private void updateListView(JSONObject item){
        // Remove the object from the list
        list.remove(item);
        this.notifyDataSetChanged();
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
