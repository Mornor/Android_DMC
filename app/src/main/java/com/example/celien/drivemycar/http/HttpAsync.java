package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.AddCar;
import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.core.ModifyCar;
import com.example.celien.drivemycar.core.Register;
import com.example.celien.drivemycar.models.Car;
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
    private AddCar addCarCaller;
    private ModifyCar modifyCarCaller;

    private final static String SAVE_USER_URL        = "http://cafca.ngrok.com/register";
    private final static String RETRIEVE_DATA_URL    = "http://chat.ngrok.com/android_messages";
    private final static String AUTHENTICATE_URL     = "http://cafca.ngrok.com/android/login";
    private final static String SAVE_CAR_URL         = "http://cafca.ngrok.com/android/save_car";
    private final static String MODIFY_CAR_URL       = "http://cafca.ngrok.com/android/modify_car";;

    // Default constructor
    public HttpAsync(){}


    // Only simple way to retrieve the caller.
    public HttpAsync(Register caller){
        this.registerCaller = caller;
    }

    public HttpAsync(Login caller){
        this.loginCaller = caller;
    }

    public HttpAsync(AddCar addCar){
        this.addCarCaller = addCar;
    }

    public HttpAsync(ModifyCar modifyCar){
        this.modifyCarCaller = modifyCar;
    }

    @Override
    protected void onPreExecute() {
        // Get the current caller class
        if(loginCaller != null)
            loginCaller.setProgressBar(ProgressDialog.show(loginCaller, "Please wait ...", "Login ..."));
        if(registerCaller != null)
            registerCaller.setRing(ProgressDialog.show(registerCaller, "Please wait ...", "Register ..."));
        if(addCarCaller != null)
            addCarCaller.setSavingCar(ProgressDialog.show(addCarCaller, "Please wait ...", "Saving the car ..."));
        if(modifyCarCaller != null)
            modifyCarCaller.setModifyCar(ProgressDialog.show(modifyCarCaller, "Please wait ...", "Modifying car..."));
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
        else if(params[0].equals(Action.MODIFY_CAR.toString()))
            return modifycar();
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        // Chek to know which instance has been called, and so to know who is the current instance.
        if(loginCaller != null){
            if((int) object != 200)
                loginCaller.getProgressDialog().dismiss();
            loginCaller.onPostExecuteAuthenticate(object); // When the httpCall is over, send the httpResponse. (which is the http response status)
        }
        if(registerCaller != null){
            registerCaller.getRing().dismiss();
            registerCaller.onPostExecute((int) object);
        }
        if(addCarCaller != null){
            addCarCaller.getSavingCar().dismiss();
            addCarCaller.onPostExecute(object); // 200 if ok (HttpResponse)
        }
        if(modifyCarCaller != null){
            modifyCarCaller.getModifyCar().dismiss();
            modifyCarCaller.onPostExecute(object);
        }

    }

    public int modifycar(){
        int success = 0;

        // Update the car into current User's List<Car>
        Car car = modifyCarCaller.getCar();
        Log.d("Leasing price : ", String.valueOf(car.getLeasing_price()));

        // Update it into DB.
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(MODIFY_CAR_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("idUser",        String.valueOf(modifyCarCaller.getUser().getId())));
            list.add(new BasicNameValuePair("brand",         car.getBrand()));
            list.add(new BasicNameValuePair("model",         car.getModel()));
            list.add(new BasicNameValuePair("fuel",          car.getFuel()));
            list.add(new BasicNameValuePair("avg_cons",      String.valueOf(car.getAvg_cons())));
            list.add(new BasicNameValuePair("c02_cons",      String.valueOf(car.getC02_cons())));
            list.add(new BasicNameValuePair("htva_price",    String.valueOf(car.getHtva_price())));
            list.add(new BasicNameValuePair("leasing_price", String.valueOf(car.getLeasing_price())));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public int saveNewCar(){
        int success = 0;

        // Create the car
        Car car  = new Car();
        car.setBrand(addCarCaller.getBrand());
        car.setModel(addCarCaller.getModel());
        car.setFuel(addCarCaller.getFuel());
        car.setAvg_cons(Double.valueOf(addCarCaller.getFuelCons()));
        car.setC02_cons(Double.valueOf(addCarCaller.getC02Cons()));
        car.setHtva_price(Double.valueOf(addCarCaller.getHtvaPrice()));
        car.setLeasing_price(Double.valueOf(addCarCaller.getLeasingPrice()));

        // Add the car to the currentUser
        addCarCaller.getUser().getCars().add(car);

        // Save the car into Db
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SAVE_CAR_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username",      addCarCaller.getUser().getUsername()));
            list.add(new BasicNameValuePair("brand",         car.getBrand()));
            list.add(new BasicNameValuePair("model",         car.getModel()));
            list.add(new BasicNameValuePair("fuel",          car.getFuel()));
            list.add(new BasicNameValuePair("avg_cons",      String.valueOf(car.getAvg_cons())));
            list.add(new BasicNameValuePair("c02_cons",      String.valueOf(car.getC02_cons())));
            list.add(new BasicNameValuePair("htva_price",    String.valueOf(car.getHtva_price())));
            list.add(new BasicNameValuePair("leasing_price", String.valueOf(car.getLeasing_price())));
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
        int responseCode = 0;
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SAVE_USER_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name",     temp.getName()));
            list.add(new BasicNameValuePair("username", temp.getUsername()));
            list.add(new BasicNameValuePair("email",    temp.getEmail()));
            list.add(new BasicNameValuePair("password", temp.getPassword()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
        }catch(Exception e){
            e.printStackTrace();
        }
        return responseCode;
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
