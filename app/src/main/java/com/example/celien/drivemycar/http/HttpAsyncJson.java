package com.example.celien.drivemycar.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.core.Register;
import com.example.celien.drivemycar.tabs.TabSearchCar;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used when the request send back a Json reponse
 */

public class HttpAsyncJson extends AsyncTask<String, Void, JSONArray>{

    private Login loginCaller;
    private boolean choice; // If choice is true, then the instance of Login caller is used to retrieve the car.
    private Register registerCaller;
    private TabSearchCar tabSearchCarCaller;
    private ListSpecificCars listSpecificCarsCaller;

    private static final String LOAD_USER_URL           = "http://cafca.ngrok.com/android/get_user";
    private static final String LOAD_CARS_URL           = "http://cafca.ngrok.com/android/get_cars";
    private static final String CHECK_USER_UNIQUE_URL   = "http://cafca.ngrok.com/android/username_unique";
    private static final String LOAD_ALL_CARS_BRAND     = "http://cafca.ngrok.com/android/get_all_cars_brand";
    private static final String LOAD_SPECIFIC_CARS      = "http://cafca/ngrok.com/android/get_specific_cars";

    public HttpAsyncJson(Login loginCaller){
        this.loginCaller = loginCaller;
    }

    public HttpAsyncJson(Login loginCaller, boolean choice){
        this.loginCaller = loginCaller;
        this.choice = choice;
    }

    public HttpAsyncJson(TabSearchCar caller){
        this.tabSearchCarCaller = caller;
    }

    public HttpAsyncJson(ListSpecificCars caller){
        this.listSpecificCarsCaller = caller;
    }

    public HttpAsyncJson(Register caller){
        this.registerCaller = caller;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals(Action.LOAD_USER.toString()))
            return loadUser(params[1]); // param[1] contain the username.
        if(params[0].equals(Action.LOAD_CARS.toString()))
            return loadCars(params[1]); // also contains the username.
        if(params[0].equals(Action.CHECK_USERNAME.toString()))
            return checkUsernameUnique(params[1]);
        if(params[0].equals(Action.GET_BRAND.toString()))
            return getBrands();
        if(params[0].equals(Action.LOAD_SPECIFIC_CARS))
            return getSpecificCars();
        return null;
    }

    private JSONArray getBrands(){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makeGetHttpRequest(LOAD_ALL_CARS_BRAND);
        return result;
    }

    private JSONArray checkUsernameUnique(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(CHECK_USER_UNIQUE_URL, username);
        return result;
    }

    private JSONArray loadCars(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(LOAD_CARS_URL, username);
        return result;
    }

    private JSONArray loadUser(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(LOAD_USER_URL, username);
        return result;
    }

    private JSONArray getSpecificCars(){
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        if(loginCaller != null){
            if(choice) {  // If choice, then return the Car JSONArray. (final stuff to return when log in)
                loginCaller.getProgressDialog().dismiss();
                loginCaller.onPostExecuteLoadCars(jsonArray);
            }
            else
                loginCaller.onPostExecuteLoadUser(jsonArray);
        }
        if(registerCaller != null){
            try {
                JSONObject jsonResult = jsonArray.getJSONObject(0);
                boolean success = jsonResult.getBoolean("success");
                registerCaller.getRing().dismiss();
                registerCaller.onPostExecuteUsernameUnique(success);
            } catch (JSONException e) {
                Log.d("HttpAsyncJson ", e.toString());
                e.printStackTrace();
            }
        }
        if(tabSearchCarCaller != null){
            tabSearchCarCaller.getSearchBrandCar().dismiss();
            tabSearchCarCaller.onPostExecuteSearchBrand(jsonArray);
        }
        if(listSpecificCarsCaller != null){
            listSpecificCarsCaller.onPostExecuteSearchRequestedCars(jsonArray);
            listSpecificCarsCaller.getProgressDialog().dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        if(registerCaller != null)
            registerCaller.setRing(ProgressDialog.show(registerCaller, "Please wait ...", "Check if username unique ..."));
        if(tabSearchCarCaller != null)
            tabSearchCarCaller.setSearchBrandCar(ProgressDialog.show(tabSearchCarCaller.getActivity(), "Please wait ...", "Search available brands ..."));
        if(listSpecificCarsCaller != null)
            listSpecificCarsCaller.setProgressDialog(ProgressDialog.show(listSpecificCarsCaller, "Please wait ...", "Searching requested cars ..."));
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
