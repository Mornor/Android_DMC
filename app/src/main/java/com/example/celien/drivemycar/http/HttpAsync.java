package com.example.celien.drivemycar.http;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.celien.drivemycar.core.AddCar;
import com.example.celien.drivemycar.core.ListPersonnalCars;
import com.example.celien.drivemycar.core.Login;
import com.example.celien.drivemycar.core.ModifyCar;
import com.example.celien.drivemycar.core.Register;
import com.example.celien.drivemycar.fragment.ConfirmRent;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.utils.Action;
import com.example.celien.drivemycar.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
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
    private ListPersonnalCars listPersonnalCarsCaller;

    private TabOperations tabOperationsCaller;


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

    public HttpAsync(ListPersonnalCars caller){
        this.listPersonnalCarsCaller = caller;
    }

    public HttpAsync(TabOperations caller){
        this.tabOperationsCaller = caller;
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
        if(listPersonnalCarsCaller != null)
            listPersonnalCarsCaller.setProgressDialog(ProgressDialog.show(listPersonnalCarsCaller, "Please wait ...", "Deleting car..."));
        if(tabOperationsCaller != null)
            tabOperationsCaller.setProgressDialog(ProgressDialog.show(tabOperationsCaller.getActivity(), "Please wait...", "Confirm rent..."));
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
            return modifyCar();
        else if(params[0].equals(Action.DELETE_CAR.toString()))
            return deleteCar();
        else if(params[0].equals(Action.CONFIRM_RENT.toString()))
            return confirmRent(params[1], params[2]); // [1] = Mileage at start (Str), [2] = id_transaction
        else if(params[0].equals(Action.REFUTE_RENT.toString()))
            return refuteRent(params[1]);
        else
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
        if(listPersonnalCarsCaller != null){
            listPersonnalCarsCaller.getProgressDialog().dismiss();
            listPersonnalCarsCaller.onPostExecuteDeleteCar(object);
        }
        if(tabOperationsCaller != null){
            tabOperationsCaller.getProgressDialog().dismiss();
            tabOperationsCaller.onPostExecuteConfirmRent((int) object);
        }

    }

    private int refuteRent(String idTransaction){
        int success = -1;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.REFFUTE_RENT_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("idTransaction", idTransaction));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    private int confirmRent(String mileage, String idTransaction){
        int success = -1;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.CONFIRM_RENT_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("idTransaction", idTransaction));
            list.add(new BasicNameValuePair("mileage", mileage));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    private int deleteCar(){
        int success = 0;
        Car carToDelete = listPersonnalCarsCaller.getSelectedCarToDelete();
        User user = listPersonnalCarsCaller.getUser();
        user.getCars().remove(carToDelete);

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.DELETE_CAR_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("id", String.valueOf(carToDelete.getId())));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    private int modifyCar(){
        int success = 0;

        // Update the car into current User's List<Car>
        Car car = modifyCarCaller.getCar();

        // Update it into DB.
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.MODIFY_CAR_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("id",            String.valueOf(car.getId())));
            list.add(new BasicNameValuePair("brand",         car.getBrand()));
            list.add(new BasicNameValuePair("model",         car.getModel()));
            list.add(new BasicNameValuePair("licencePlate",  car.getLicencePlate()));
            list.add(new BasicNameValuePair("fuel",          car.getFuel()));
            list.add(new BasicNameValuePair("nbSits",        String.valueOf(car.getNbSits())));
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

    private int saveNewCar(){
        int success = 0;
        InputStream is = null;
        int idCarAfterSave = 0;

        // Save the car into Db
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.SAVE_CAR_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username",      addCarCaller.getUser().getUsername()));
            list.add(new BasicNameValuePair("brand",         addCarCaller.getBrand()));
            list.add(new BasicNameValuePair("model",         addCarCaller.getModel()));
            list.add(new BasicNameValuePair("licencePlate",  addCarCaller.getLicencePlate()));
            list.add(new BasicNameValuePair("fuel",          addCarCaller.getFuel()));
            list.add(new BasicNameValuePair("nbSits",        addCarCaller.getNbSits()));
            list.add(new BasicNameValuePair("avg_cons",      addCarCaller.getFuelCons()));
            list.add(new BasicNameValuePair("c02_cons",      addCarCaller.getC02Cons()));
            list.add(new BasicNameValuePair("htva_price",    addCarCaller.getHtvaPrice()));
            list.add(new BasicNameValuePair("leasing_price", addCarCaller.getLeasingPrice()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            success = response.getStatusLine().getStatusCode();
            // Get the response
            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the car only if success == 200
        if(success == 200){
            Car car  = new Car();
            car.setBrand(addCarCaller.getBrand());
            car.setModel(addCarCaller.getModel());
            car.setFuel(addCarCaller.getFuel());
            car.setNbSits(Integer.valueOf(addCarCaller.getNbSits()));
            car.setLicencePlate(addCarCaller.getLicencePlate());
            car.setAvg_cons(Double.valueOf(addCarCaller.getFuelCons()));
            car.setC02_cons(Double.valueOf(addCarCaller.getC02Cons()));
            car.setHtva_price(Double.valueOf(addCarCaller.getHtvaPrice()));
            car.setLeasing_price(Double.valueOf(addCarCaller.getLeasingPrice()));

            // Get the ID of the car that has just been saved (only way to get the ID)
            JsonParser parser = new JsonParser();
            String jsonString = parser.createJsonStringFromInputStream(is);
            JSONObject idCarJson = parser.createJsonObjectFromString(jsonString);

            try {
                idCarAfterSave = idCarJson.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Add the car to the currentUser and set the new ID from DB
            car.setId(idCarAfterSave);

            // Test if car is not already on User's list.
            if(addCarCaller.getUser().getCars().contains(car))
                success = -1;

            addCarCaller.getUser().getCars().add(car);
        }

        return success;
    }

    public int authenticate(){
        int responseCode =  0;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.AUTHENTICATE_URL);
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
    private int saveNewUser(){
        User temp = registerCaller.getUser();
        int responseCode = 0;
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.SAVE_USER_URL);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name",         temp.getName()));
            list.add(new BasicNameValuePair("username",     temp.getUsername()));
            list.add(new BasicNameValuePair("email",        temp.getEmail()));
            list.add(new BasicNameValuePair("password",     temp.getPassword()));
            list.add(new BasicNameValuePair("bankAccount",  temp.getBankAccount()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
        }catch(Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }

    /*Getters and Setters*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
