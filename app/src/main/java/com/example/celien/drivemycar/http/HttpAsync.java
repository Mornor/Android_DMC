package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;

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
 * 1st Parameter : Types of parameter passed in doInBackGround
 * 2nd Parameter : Types of parameter passed in onProgressUpdate
 * 3rd Parameter : Types of parameter passed in onPostExecute
 */
public class HttpAsync extends AsyncTask<String, Void, Object>{

    private String name;
    private String message;
    private JSONArray json;

    private Register registerCaller;
    private Login loginCaller;

    public final static String SAVE_USER_URL        = "http://cafca.ngrok.com/register";
    public final static String RETRIEVE_DATA_URL    = "http://chat.ngrok.com/android_messages";
    public final static String AUTHENTICATE_URL     = "http://cafca.ngrok.com/android_login";

    // Default constructor
    public HttpAsync(){}


    // Only simple way to retrieve the caller.
    public HttpAsync(Register caller){
        this.registerCaller = caller;
    }

    public HttpAsync(Login caller){
        this.loginCaller = caller;
    }

    @Override
    protected void onPreExecute() {
        // If caller is Login
        if(loginCaller != null)
            loginCaller.setProgressBar(ProgressDialog.show(loginCaller, "Please wait ...", "Login ..."));
        if(registerCaller != null)
            registerCaller.setRing(ProgressDialog.show(registerCaller, "Please wait ...", "Register ..."));
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
        else
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
