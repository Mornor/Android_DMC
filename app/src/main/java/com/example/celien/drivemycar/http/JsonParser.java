package com.example.celien.drivemycar.http;

import android.util.Log;
import com.example.celien.drivemycar.utils.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

// TODO : Make the code of this class much more efficient (URL in parameter)

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

    /*** @param url : Url of the website
     * @return JsonObject returned by the URL called */
    public JSONArray makeGetHttpRequest(String url){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }
        return createJsonArrayFromString(json);
    }

    /** @param url : The url of the webservice
     * @param params : default, params[0] == current username
     * @return JSONArray with the one send bach by Play! */
    public JSONArray makePostHttpRequest(String url, String... params){

        // If we search a specific car, then we specify the following paramaters:
        // params[1] -> Brand, params[2] -> Energy (Petrol, Diesel ...), params[3] -> MaxCons
        // params[4] -> Nbsits, params[5] -> fromDate, params[6} -> toDate, params[7] -> username
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
            } catch (IOException e){
                e.printStackTrace();
            }
            return createJsonArrayFromString(json);
        }
    }

    /*** Called in ListSpecificCar.saveData()
     * listRequest contains the selected user to which the current user wish to rent the car from.
     * So, list Request is like this :
     * owner : Thibaut
     * brand : Porsche
     * model : 911
     * currentUsername is the username of the one who wants the car (the one who is currently using the Android terminal) */
    public JSONArray saveRequest(List<HashMap<String, String>> listRequest, String currentUsername, String dateFrom, String dateTo, boolean isExchange, int idSelectedCar, double mileage) {
        JSONArray toSendToServer = new JSONArray();
        try{
            HttpContext httpContext = new BasicHttpContext();
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.SAVE_REQUEST_URL);

            // Add the username
            JSONObject usernameJson = new JSONObject();
            usernameJson.put("username", currentUsername);
            toSendToServer.put(usernameJson);

            // Add the dates of the wished rental
            JSONObject dateFromJson = new JSONObject();
            dateFromJson.put("dateFrom", dateFrom);
            toSendToServer.put(dateFromJson);
            JSONObject dateToJson = new JSONObject();
            dateToJson.put("dateTo", dateTo);
            toSendToServer.put(dateToJson);

            // Add the choice of exchange or not
            JSONObject isExchangeJson = new JSONObject();
            isExchangeJson.put("isExchange", isExchange);
            toSendToServer.put(isExchangeJson);

            if(isExchange){
                // Add the id of the selected car (in case of an exchange, it is the id of the car the user want to exchange)
                JSONObject idCarToExchange = new JSONObject();
                idCarToExchange.put("idCarToExchange", idSelectedCar);
                toSendToServer.put(idCarToExchange);

                // Add the mileage of the current user's cars in case of exchange
                JSONObject mileageOfCar = new JSONObject();
                mileageOfCar.put("mileageCarToExchange", mileage);
                toSendToServer.put(mileageOfCar);
            }

            // Add every chosen possibilities
            JSONArray array = new JSONArray();
            for(int i = 0 ; i < listRequest.size() ; i++){
                JSONObject temp = new JSONObject();
                temp.put("owner", listRequest.get(i).get("owner"));
                temp.put("brand", listRequest.get(i).get("brand"));
                temp.put("model", listRequest.get(i).get("model"));
                array.put(temp);
            }
            toSendToServer.put(array);

            StringEntity se = new StringEntity(toSendToServer.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    public JSONArray getTransactions(String username){
            try{
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.GET_TRANSACTIONS_URL);
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("username", username));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
                json = createJsonStringFromInputStream(inputStream);
            } catch (IOException e){
                e.printStackTrace();
            }

            return createJsonArrayFromString(json);
    }

    public JSONArray getNotifications(String username, String url, String hasToBeAlreadyRead){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", username));
            list.add(new BasicNameValuePair("hasToBeRead", hasToBeAlreadyRead));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    public JSONArray computeAmountToPay(String idTransaction){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.COMPUTE_AMOUNT_TO_PAY_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("idTransaction", idTransaction));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    public JSONArray setOdometer(String mileage, String idTransaction, String isOwner, String avgCons){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.SET_ODOMETER_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("mileage", mileage));
            list.add(new BasicNameValuePair("idTransaction", idTransaction));
            list.add(new BasicNameValuePair("isOwner", isOwner));
            list.add(new BasicNameValuePair("avgCons", avgCons));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    /*** Update the request status linked to the notification
     * Called in HttpAsynNotif.updateRequestSate
     * @param idNotification : the notification linked
     * @param rentConfirmed : if true, then the owner has accepted the rent. (false, he hasn't)*/
    public JSONArray updateRequestState(int idNotification, boolean rentConfirmed, String url){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("idNotification", String.valueOf(idNotification)));
            list.add(new BasicNameValuePair("confirmedRent", String.valueOf(rentConfirmed)));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            httpClient.execute(httpPost);
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    /***Get the data of the requests of the User
     * @param username : Username of the current Android user.
     * @return JSONArray like this : [{"dateFrom":"value", "dateTo":"value"},{"dateFrom":"value", "dateTo":"value"}]*/
    public JSONArray getRequestsByDate(String username){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.GET_REQUEST_BY_DATE_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", username));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }


    /*** @param username
     * @return JSONArray like this if called from RequestData : [{"success":"value"}, {"nbRequestSent":"value", "nbAccepted":"value", "nbRefuted":"value", "nbNoAnswer":"value"}]
     * or [{"success":"value"}, {"id":"value", {"brand":"value", {"model":"value"}, ...}}] if called from TabOperation
     * etc ... See Tools.convertTransactionToJson in Play Server*/

    public JSONArray getRequestData(String username, String fromDate, String toDate, String url){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", username));
            list.add(new BasicNameValuePair("fromDate", fromDate));
            list.add(new BasicNameValuePair("toDate", toDate));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    /*** @param username
     * @param fromDate : the date from the car is rent
     * @param toDate : the date to the car is rent
     * @return JSONArray like this [{"success":"value"}, {"ownerName":"value", "brand""value", "model""value"}, etc ... ]*/
    public JSONArray getAgreedOwners(String username, String fromDate, String toDate){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.GET_AGREED_OWNERS_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", username));
            list.add(new BasicNameValuePair("fromDate", fromDate));
            list.add(new BasicNameValuePair("toDate", toDate));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    public JSONArray notifySelectedUser(String username, String ownerName, String brand, String model, String fromDate, String toDate){
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.NOTIFY_SELECTED_OWNER_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username", username));
            list.add(new BasicNameValuePair("ownerName", ownerName));
            list.add(new BasicNameValuePair("brand", brand));
            list.add(new BasicNameValuePair("model", model));
            list.add(new BasicNameValuePair("fromDate", fromDate));
            list.add(new BasicNameValuePair("toDate", toDate));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            json = createJsonStringFromInputStream(inputStream);
        } catch (IOException e){
            e.printStackTrace();
        }

        return createJsonArrayFromString(json);
    }

    public static String createJsonStringFromInputStream(InputStream inputStream){
        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = "";
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
            Log.d("Create error ", e.toString());
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
