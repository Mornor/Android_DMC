package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;

import org.json.JSONArray;

public class HttpAsyncNotif extends AsyncTask<String, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;

    public HttpAsyncNotif(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(listSpecificCarsCaller != null)
            listSpecificCarsCaller.setProgressDialog(ProgressDialog.show(listSpecificCarsCaller, "Please wait...", "Send request..." ));
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.SAVE_REQUEST.toString()))
            return saveRequest();
        return null;
    }


    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        if(listSpecificCarsCaller != null) {
            listSpecificCarsCaller.getProgressDialog().dismiss();
            listSpecificCarsCaller.onPostExecuteSendRequest(jsonArray);
        }
    }

    private JSONArray saveRequest(){
        // List<HashMaps<String, String> is like :
        // 0 -> "owner":"celien", "brand":"bmw", "model":"335i"
        JsonParser parser = new JsonParser();
        JSONArray result = parser.saveRequest(
                Constants.SAVE_REQUEST_URL,
                listSpecificCarsCaller.getSelectedItems(),
                listSpecificCarsCaller.getUser().getUsername(),
                listSpecificCarsCaller.getDateFrom().toString(),
                listSpecificCarsCaller.getDateTo().toString(),
                listSpecificCarsCaller.isExchange());
        return null;
    }
}
