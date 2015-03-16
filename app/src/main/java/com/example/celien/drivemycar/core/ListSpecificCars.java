package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Class used to display and do some actions on cars which fit the request made by the user in TabSearchCar */
public class ListSpecificCars extends ActionBarActivity {

    // Variables which come from TabSearchCar
    private User user;
    private String nbSits;
    private String maxCons;
    private String energy;
    private String brand;
    private String dateFrom;
    private String timeFrom;
    private String dateTo;
    private String timeTo;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_specific_cars);
        init();
    }


    private void init(){
        // Get the User (Object).
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user       = currentUser;
            this.brand      = getIntent().getStringExtra("brand");
            this.nbSits     = getIntent().getStringExtra("nbSits");
            this.energy     = getIntent().getStringExtra("energy");
            this.maxCons    = getIntent().getStringExtra("maxCons");
            this.dateFrom   = getIntent().getStringExtra("dateFrom");
            this.timeFrom   = getIntent().getStringExtra("timeFrom");
            this.dateTo     = getIntent().getStringExtra("dateTo");
            this.timeTo     = getIntent().getStringExtra("timeTo");
        }
        //getRequestedCars();
    }

    private void getRequestedCars(){
        HttpAsyncJson request = new HttpAsyncJson(this);
        request.execute(Action.LOAD_SPECIFIC_CARS.toString());
    }

    // Create an ArrayList<Car> with the JSONArray received from HttpAsyncJsons
    public ArrayList<Car> onPostExecuteSearchRequestedCars(JSONArray array){
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_specific_cars, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*Getters and Setter*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
}
