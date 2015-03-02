package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.celien.drivemycar.core.CarSettings;
import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.core.Register;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask is closed when the activity is closed.
 * 1st Parameter : Types of parameter passed in doInBackGround() --> Thread != Main Thread.
 * 2nd Parameter : Types of parameter passed in onProgressUpdate() --> Executed on the Main Thread.
 * 3rd Parameter : Types of parameter passed in onPostExecute() and parameter returned by doInBackground().
 */
public class HttpAsync extends AsyncTask<String, Void, Object>{

    private String name;
    private String message;
    private JSONArray json;

    private Register registerCaller;
    private Login loginCaller;
    private CarSettings carSettingsCaller;

    public final static String SAVE_USER_URL        = "http://cafca.ngrok.com/register";
    public final static String RETRIEVE_DATA_URL    = "http://chat.ngrok.com/android_messages";
    public final static String AUTHENTICATE_URL     = "http://cafca.ngrok.com/android/login";
    public final static String SAVE_CAR_URL         = "http://cafca.ngrok.com/android/save_car";

    // Default constructor
    public HttpAsync(){}


    // Only simple way to retrieve the caller.
    public HttpAsync(Register caller){
        this.registerCaller = caller;
    }

    public HttpAsync(Login caller){
        this.loginCaller = caller;
    }

    public HttpAsync(CarSettings carSettings){
        this.carSettingsCaller = carSettings;
    }

    @Override
    protected void onPreExecute() {
        // Get the current caller class
        if(loginCaller != null)
            loginCaller.setProgressBar(ProgressDialog.show(loginCaller, "Please wait ...", "Login ..."));
        if(registerCaller != null)
            registerCaller.setRing(ProgressDialog.show(registerCaller, "Please wait ...", "Register ..."));
        if(carSettingsCaller != null)
            carSettingsCaller.setSavingCar(ProgressDialog.show(carSettingsCaller, "Please wait ...", "Saving the car ..."));
    }

    /**
     * @param params
     * @return JSONArray which is passed to onPostExecute();
     */
    @Override
    protected Object doInBackground(String... params) {
        if(params[0].equals(Action.SAVE_USER.toString()))
            return saveNewUser();
        else if(params[0].equals(Action.AUTHENTICATE.toString()))
            return authenticate();
        else if(params[0].equals(Action.SAVE_CAR.toString()))
            return saveNewCar();
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        // Chek to know which instance has been called, and so to know who is the current instance.
        if(loginCaller != null){
            loginCaller.getProgressDialog().dismiss();
            loginCaller.onPostExecute(object); // When the httpCall is over, send the httpResponse. (which is the http response status)
        }
        if(registerCaller != null){
            registerCaller.getRing().dismiss();
            boolean success = ( (int) object) == 0 ? true : false;
            registerCaller.onPostExecute(success);
        }
        if(carSettingsCaller != null){
            carSettingsCaller.getSavingCar().dismiss();
            carSettingsCaller.onPostExecute(object); // 0 if success, -1 if failure
        }

    }

    public int saveNewCar(){
        int success = 0;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SAVE_CAR_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", carSettingsCaller.getUsername()));
            list.add(new BasicNameValuePair("brand", carSettingsCaller.getBrand()));
            list.add(new BasicNameValuePair("model", carSettingsCaller.getModel()));
            list.add(new BasicNameValuePair("fuel", carSettingsCaller.getFuel()));
            list.add(new BasicNameValuePair("avg_cons", carSettingsCaller.getFuelCons()));
            list.add(new BasicNameValuePair("c02_cons", carSettingsCaller.getC02Cons()));
            list.add(new BasicNameValuePair("htva_price", carSettingsCaller.getHtvaPrice()));
            list.add(new BasicNameValuePair("leasing_price", carSettingsCaller.getLeasingPrice()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public int authenticate(){
        int responseCode =  0;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(AUTHENTICATE_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", loginCaller.getLogin()));
            list.add(new BasicNameValuePair("password", loginCaller.getPassword()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseCode;
    }

    /*Save the name and the message into remote DB*/
    public int saveNewUser(){
        User temp = registerCaller.getUser();
        int success;
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SAVE_USER_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name", temp.getName()));
            list.add(new BasicNameValuePair("username", temp.getUsername()));
            list.add(new BasicNameValuePair("email", temp.getEmail()));
            list.add(new BasicNameValuePair("password", temp.getPassword()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = 0;
        }catch(Exception e){
            e.printStackTrace();
            success = -1;
        }
        return success;
    }

    /*Retrieve names and messages into remote DB*/
    public JSONArray doGet(){
        JsonParser parser = new JsonParser();
        JSONArray json = parser.makeGetHttpRequest(RETRIEVE_DATA_URL);
        return json;
    }

    /*Getters and Setters*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
