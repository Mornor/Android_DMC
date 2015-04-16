package com.example.celien.drivemycar.receiver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*** This class re-create a User when the notification has been clicked (by sent request to DB and getting back the answer)and launch an Intent
 * to Home activity. **/
public class NotificationUser extends Activity{

    private User user;
    private String username;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_user);

        // Retrieve the current username in SharedPref
        String userInfo[] = Tools.getUsernamePassword(getSharedPreferences("userInfo", Context.MODE_PRIVATE));

        // So, if there is a user who has already logged in before (and not logout)
        if (!userInfo[0].equals("")){
            username = userInfo[0];
            // Retrieve the current User
            HttpAsyncJson httpAsyncJson = new HttpAsyncJson(this);
            httpAsyncJson.execute(Action.LOAD_USER.toString(), username);
        }
    }

    public void onPostExecuteLoadUser(JSONArray json){
        user = new User();
        try {
            for(int i = 0 ; i < json.length() ; i++){
                JSONObject object = json.getJSONObject(i);
                user.setId(object.getInt("id"));
                user.setName(object.getString("name"));
                user.setUsername(object.getString("username"));
                user.setEmail(object.getString("email"));
                user.setBankAccount(object.getString("bankAccount"));
                user.setPhoneNumber(object.getString("phone_number"));
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        new HttpAsyncJson(this, true).execute(Action.LOAD_CARS.toString(), username);
    }

    public void onPostExecuteLoadCars(JSONArray array){
        List<Car> cars = new ArrayList<>();
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject object = array.getJSONObject(i);
                Car temp = new Car();
                temp.setId(object.getInt("id"));
                temp.setBrand(object.getString("brand"));
                temp.setModel(object.getString("model"));
                temp.setLicencePlate(object.getString("licencePlate"));
                temp.setFuel(object.getString("fuel"));
                temp.setNbSits(object.getInt("nbSits"));
                temp.setAvg_cons(object.getDouble("avg_cons"));
                temp.setC02_cons(object.getDouble("co2_cons"));
                temp.setHtva_price(object.getDouble("htva_price"));
                temp.setLeasing_price(object.getDouble("leasing_price"));
                cars.add(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user.setCars(cars);
        launchIntentToTransactionTab();
    }

    private void launchIntentToTransactionTab(){
        Intent i = new Intent(this, Home.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putString("tabToOpen", "3"); // Open the TabOperations
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }


    /*Getters and Setters*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
