package com.example.celien.drivemycar.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.core.Register;
import com.example.celien.drivemycar.receiver.NotificationUser;
import com.example.celien.drivemycar.service.Notification;
import com.example.celien.drivemycar.tabs.TabSearchCar;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used when the request send back a Json reponse
 */

public class HttpAsyncJson extends AsyncTask<String, Void, JSONArray>{

    private Login loginCaller;
    private boolean choiceCarFromLogin; // If choice is true, then the instance of Login caller is used to retrieve the car.
    private boolean choiceCarFromNotificationUser;
    private Register registerCaller;
    private TabSearchCar tabSearchCarCaller;
    private ListSpecificCars listSpecificCarsCaller;
    private Notification notificationCaller;
    private NotificationUser notificationUserCaller;

    public HttpAsyncJson(Notification caller){
        this.notificationCaller = caller;
    }

    public HttpAsyncJson(Login loginCaller){
        this.loginCaller = loginCaller;
    }

    public HttpAsyncJson(Login loginCaller, boolean choice){
        this.loginCaller = loginCaller;
        this.choiceCarFromLogin = choice;
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

    public HttpAsyncJson(NotificationUser caller){
            this.notificationUserCaller = caller;
    }

    public HttpAsyncJson(NotificationUser caller, boolean choice){
        this.notificationUserCaller = caller;
        this.choiceCarFromNotificationUser = choice;
    }


    @Override
    protected void onPreExecute() {
        if(registerCaller != null)
            registerCaller.setRing(ProgressDialog.show(registerCaller, "Please wait ...", "Check if username unique ..."));
        if(tabSearchCarCaller != null)
            tabSearchCarCaller.setSearchBrandCar(ProgressDialog.show(tabSearchCarCaller.getActivity(), "Please wait ...", "Search available brands ..."));
        if(listSpecificCarsCaller != null)
            listSpecificCarsCaller.setProgressDialog(ProgressDialog.show(listSpecificCarsCaller, "Please wait ...", "Searching requested cars ..."));
        //if(notificationUserCaller != null)
            //notificationUserCaller.setProgressDialog(ProgressDialog.show(notificationCaller, "Please wait...", "Fetching data ..."));
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
        if(params[0].equals(Action.LOAD_SPECIFIC_CARS.toString()))
            return getSpecificCars();
        if(params[0].equals(Action.GET_NOTIFS.toString()))
            return getNotifs(params[1], params[2]); // Username and String to say that we have or not take into account that the notification has been read or not.
        return null;
    }

    private JSONArray getNotifs(String username, String hasToBeAlreadyRead){
        JsonParser parser = new JsonParser();
        return parser.getNotifications(username, Constants.GET_NOTIFS_URL, hasToBeAlreadyRead);
    }

    private JSONArray getBrands(){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makeGetHttpRequest(Constants.LOAD_ALL_CARS_BRAND);
        return result;
    }

    private JSONArray checkUsernameUnique(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(Constants.CHECK_USER_UNIQUE_URL, username);
        return result;
    }

    private JSONArray loadCars(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(Constants.LOAD_CARS_URL, username);
        return result;
    }

    private JSONArray loadUser(String username){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(Constants.LOAD_USER_URL, username);
        return result;
    }


    private JSONArray getSpecificCars(){
        JsonParser parser = new JsonParser();
        JSONArray result = parser.makePostHttpRequest(Constants.LOAD_SPECIFIC_CARS_URL, "car",
                listSpecificCarsCaller.getBrand(),
                listSpecificCarsCaller.getEnergy(),
                listSpecificCarsCaller.getMaxCons(),
                listSpecificCarsCaller.getNbSits(),
                listSpecificCarsCaller.getDateFrom().toString(),
                listSpecificCarsCaller.getDateTo().toString(),
                listSpecificCarsCaller.getUser().getUsername());
        return result;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        if(loginCaller != null){
            if(choiceCarFromLogin) {  // If choice, then return the Car JSONArray. (final stuff to return when log in)
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
        if(notificationCaller != null){
            notificationCaller.onPostExecuteLoadNotif(jsonArray);
        }
        if(notificationUserCaller != null){
            if(choiceCarFromNotificationUser){
                //notificationUserCaller.getProgressDialog().dismiss();
                notificationUserCaller.onPostExecuteLoadCars(jsonArray);
            }
            else
                notificationUserCaller.onPostExecuteLoadUser(jsonArray);
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
