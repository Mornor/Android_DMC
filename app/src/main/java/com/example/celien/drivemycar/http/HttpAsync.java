package com.example.celien.drivemycar.http;


import android.os.AsyncTask;
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
public class HttpAsync extends AsyncTask<String, Void, JSONArray>{

    private String name;
    private String message;
    private JSONArray json;

    public final static String SAVE_USER_URL        = "http://localhost:9000/register";
    public final static String RETRIEVE_DATA_URL    = "http://chat.ngrok.com/android_messages";

    public HttpAsync(String name, String message){
        this.name    = name;
        this.message = message;
    }

    // Default constructor
    public HttpAsync(){}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected JSONArray doInBackground(String... params) {
        if(params[0].equals("GET"))
            return doGet();
        else if(params[0].equals("POST")){
            doPost();
            return null;
        }
        else
            return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {

    }

    /*Save the name and the message into remote DB*/
    public void doPost(){
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(SAVE_USER_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name", name));
            list.add(new BasicNameValuePair("message", message));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /*Retrieve names and messages into remote DB*/
    public JSONArray doGet(){
        JsonParser parser = new JsonParser();
        JSONArray json = parser.makeGetHttpRequest(RETRIEVE_DATA_URL);
        return json;
    }

    /*Getters and Setters*/
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONArray getJson() {
        return json;
    }

    public void setJson(JSONArray json) {
        this.json = json;
    }

}
