package com.example.celien.drivemycar.http;


import android.os.AsyncTask;

import com.example.celien.drivemycar.core.ListSpecificCars;

import org.json.JSONArray;

public class HttpAsyncNotif extends AsyncTask<String, Void, JSONArray>{

    private ListSpecificCars listSpecificCarsCaller;

    public HttpAsyncNotif(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONArray doInBackground(String... params) {

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
    }
}
