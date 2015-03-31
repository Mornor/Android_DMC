package com.example.celien.drivemycar.http;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    static InputStream inputStream;
    static JSONArray jsonArray;
    static JSONObject jsonObject;
    static String json;
    static  JSONObject result;

    public JsonParser(){
        inputStream = null;
        jsonObject  = null;
        jsonArray   = null;
        json        = "";
    }

    /**
     * @param url : Url of the website
     * @return JsonObject returned by the URL called
     */
    public JSONArray makeGetHttpRequest(String url){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        }catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return createJsonArrayFromString(json);
    }

    /***
     * @param url
     * @param params : default, params[0] == current username
     * @return JSONArray with the one send bach by Play! */
    public JSONArray makePostHttpRequest(String url, String... params){

        // If we search a specific car, then we specify the following paramaters:
        // params[1] -> Brand, params[2] -> Energy (Petrol, Diesel ...), params[3] -> MaxCons
        // params[4] -> Nbsits, params[5] -> fromDate, params[6} -> toDate
        if(params[0].equals("car")){
            try{
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("brand", params[1]));
                list.add(new BasicNameValuePair("energy", params[2]));
                list.add(new BasicNameValuePair("maxCons", params[3]));
                list.add(new BasicNameValuePair("nbSits", params[4]));
                list.add(new BasicNameValuePair("fromDate", params[5]));
                list.add(new BasicNameValuePair("toDate", params[6]));
                list.add(new BasicNameValuePair("username", params[7]));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
                json = createJsonStringFromInputStream(inputStream);
            }catch (ClientProtocolException e){
               e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            return createJsonArrayFromString(json);
        }

        else{
            try{
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("username", params[0]));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
                json = createJsonStringFromInputStream(inputStream);
            }catch (ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return createJsonArrayFromString(json);
        }
    }

    /*** Called in ListSpecificCar.saveData()*/
    public JSONArray saveRequest(String url, List<HashMap<String, String>> listRequest, String currentUsername) {
        JSONArray toSendToServer = new JSONArray();
        try{
            HttpContext httpContext = new BasicHttpContext();
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            // Add the username before sending the JSON.
            JSONObject usernameJson = new JSONObject();
            usernameJson.put("username", currentUsername);
            toSendToServer.put(usernameJson);
            for(int i = 0 ; i < listRequest.size() ; i++){
                JSONObject temp = new JSONObject();
                temp.put("owner", listRequest.get(i).get("owner"));
                temp.put("brand", listRequest.get(i).get("brand"));
                temp.put("model", listRequest.get(i).get("model"));
                toSendToServer.put(temp);
            }
            StringEntity se = new StringEntity(toSendToServer.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        }catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }


        return createJsonArrayFromString(json);
    }

    public static String createJsonStringFromInputStream(InputStream inputStream){
        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
                sb.append(line + "\n");
            inputStream.close();
            json = sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    private JSONArray createJsonArrayFromString(String json){
        try{
            jsonArray = new JSONArray(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONObject createJsonObjectFromString(String json){
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
