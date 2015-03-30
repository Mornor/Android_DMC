package com.example.celien.drivemycar.http;


import android.os.AsyncTask;

import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;

import java.util.List;

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
        List<List<String>> listToSave = listSpecificCarsCaller.getSelectedItems();
        return null;
    }
}
