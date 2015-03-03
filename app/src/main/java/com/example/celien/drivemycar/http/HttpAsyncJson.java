package com.example.celien.drivemycar.http;

import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used when the request send back a Json reponse
 */

public class HttpAsyncJson extends AsyncTask<String, Void, JSONArray>{

    private Login loginCaller;

    private static final String LOAD_USER_URL = "http://cafca.ngrok.com/android/get_user";
    private static final String LOAD_CARS_URL = "http://cafca.ngrok.com/android/get_cars";

    public HttpAsyncJson(Login loginCaller){
        this.loginCaller = loginCaller;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.LOAD_USER.toString()))
            return loadUser(params[1]); // param[1] contain the username.
        if(params[0].equals(Action.LOAD_CARS.toString()))
            return loadCars(params[1]); // also contains the username.
        return null;
    }

    private JSONArray loadCars(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(LOAD_CARS_URL, username);
        Log.d("Cars : ", result.toString());
        return result;
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
        if(loginCaller != null){
            try{
                if(jsonArray.getJSONObject(0).getString("username") == null ) { // If username field is empty, then it's the car list.
                    loginCaller.onPostExecuteLoadCars(jsonArray);
                }
                else{
                    loginCaller.onPostExecuteLoadUser(jsonArray);
                    Log.d("Sent cars", jsonArray.toString());
                }

            } catch (JSONException e){
                e.printStackTrace();
            }

        }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
