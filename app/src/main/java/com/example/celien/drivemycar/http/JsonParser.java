package com.example.celien.drivemycar.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    static InputStream inputStream;
    static JSONArray jsonArray;
    static JSONObject jsonObject;
    static String json;

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
        return createJsonFromString(json);
    }

    public JSONArray makePostHttpRequest(String url, String... params){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name", params[0]));
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
        return createJsonFromString(json);
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

    private JSONArray createJsonFromString(String json){
        try{
            jsonArray = new JSONArray(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonArray;
    }

}
