package com.example.celien.drivemycar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.RequestReceived;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.utils.Action;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class CustomRequestReceived extends ArrayAdapter<JSONObject> {

    private List<JSONObject> list;
    private Action choice; // Refute or Confirm
    private String currentNotificationId;

    public CustomRequestReceived(Context context, List<JSONObject> list) {
        super(context, R.layout.custom_request_received, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_request_received, parent, false);

        // Declare it final, so that I can access it in the btn.OnclickMethod().
        final int pos = position;

        // Get the elements on the layouts
        JSONObject currentJson      = getItem(position);
        TextView tvUserSource       = (TextView) v.findViewById(R.id.tvUserSource);
        TextView tvBrand            = (TextView) v.findViewById(R.id.tvBrand);
        TextView tvModel            = (TextView) v.findViewById(R.id.tvModel);
        TextView tvFromDate         = (TextView) v.findViewById(R.id.tvDateFrom);
        TextView tvInCaseOfExchange = (TextView) v.findViewById(R.id.tvInCaseOfExchange);
        TextView tvToDate           = (TextView) v.findViewById(R.id.tvDateTo);
        TextView tvCanI             = (TextView) v.findViewById(R.id.tvCanIUse);
        Button btnValidate          = (Button) v.findViewById(R.id.btnValidate);
        Button btnCancel            = (Button) v.findViewById(R.id.btnCancel);

        // Set the value of the fields
        try {
            tvUserSource.setText(currentJson.getString("userSource"));
            tvFromDate.setText(currentJson.getString("fromDate").substring(0, 10) + " at" + currentJson.getString("fromDate").substring(10, currentJson.getString("fromDate").toString().length() - 5) + "h ");
            tvToDate.setText(currentJson.getString("toDate").substring(0, 10) + " at" + currentJson.getString("toDate").substring(10, currentJson.getString("toDate").toString().length() - 5) + "h ");
            if(currentJson.getBoolean("isExchange")) {
                tvCanI.setText("Would you like to exchange your " +currentJson.getString("brand")+ " " +currentJson.getString("model"));
                tvInCaseOfExchange.setVisibility(View.VISIBLE);
                tvInCaseOfExchange.setText("with his/her "+currentJson.getString("requesterCarBrand")+ " " +currentJson.getString("requesterCarModel")+ " ("+currentJson.getDouble("requesterCarMileage")+ " Kms)");
            }
            else
                tvCanI.setText("Can I rent your " +currentJson.getString("brand")+ " " +currentJson.getString("model"));
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancel(getItem(pos));
            }
        });

        return v;
    }

    private void onClickValidate(JSONObject object){
        try{
            currentNotificationId = object.getString("idNotification");
        } catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        choice = Action.CONFIRM_RENT;
        // Update the request value into DB
        new HttpAsyncNotif(this).execute(Action.UPDATE_REQUEST_STATE);

        // Update ListView
        updateListView(object);
    }

    private void onClickCancel(JSONObject object){
        choice = Action.REFUTE_RENT;
        try{
            currentNotificationId = object.getString("idNotification");
        } catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }
        new HttpAsyncNotif(this).execute(Action.UPDATE_REQUEST_STATE);
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

    /*Getters and Setters*/
    public Action getChoice() {
        return choice;
    }

    public String getCurrentNotificationId() {
        return currentNotificationId;
    }
}
