package com.example.celien.drivemycar.http;

import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;

/**
 * Class used when the request send back a Json reponse
 */

public class HttpAsyncJson extends AsyncTask<String, Void, JSONArray>{

    private Login loginCaller;

    private static final String LOAD_USER_URL = "http://cafca.ngrok.com/android/get_user";

    public HttpAsyncJson(Login loginCaller){
        this.loginCaller = loginCaller;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.LOAD_USER.toString()))
            return loadUser(params[1]); // param[1] contain the username.
        return null;
    }

    private JSONArray loadUser(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(LOAD_USER_URL, username);
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        if(loginCaller != null) {
            loginCaller.onPostExecuteLoadUser(jsonArray);
            Log.d("HttpAsynJson", jsonArray.toString());
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
