package com.example.celien.drivemycar.http;


import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class HttpAsyncNotif extends AsyncTask<String, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;


    private static final String SAVE_REQUEST_URL = "http://cafca.ngrok.com/android/set_request";

    public HttpAsyncNotif(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
    }

    private JSONArray saveRequest(){
        // List is like :
        // 0 -> "celien", "bmw", "335i"
        //JsonParser parser = new JsonParser();
        //JSONArray result = parser.saveRequest(SAVE_REQUEST_URL, listSpecificCarsCaller.getSelectedItems());
        List<HashMap<String, String>> receivedList = listSpecificCarsCaller.getSelectedItems();
        for(int i = 0 ; i < receivedList.size() ; i++){
            Log.d("In list http owner ", receivedList.get(i).get("owner"));
            Log.d("In list http brand ", receivedList.get(i).get("brand"));
            Log.d("In list http model ", receivedList.get(i).get("model"));
        }

        return null;
    }
}
