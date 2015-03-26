package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomSpecificCar;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*** Class used to display and do some actions on cars which fit the request made by the user in TabSearchCar */
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

    // Variables sent to the server in order to retrieve the cars which fits the choice of the user (from TabSearchCar).
    // But I also use the previous vars.
    private Timestamp from;
    private Timestamp to;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_specific_cars);
        init();
    }


    private void init(){

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search result");

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

        getRequestedCars();
    }

    private void getRequestedCars(){
        HttpAsyncJson request = new HttpAsyncJson(this);
        request.execute(Action.LOAD_SPECIFIC_CARS.toString());
    }

    // Create an ArrayList<JSONObject> from the JSONArray received from HttpAsyncJsons
    // The JSONArray received is constructed with the following model :
    // [{"brand":"Bmw","model":"335i","owner":"Celien"}]
    // Then, it create the CustomListView (customSpecificCar) with the within items
    public void onPostExecuteSearchRequestedCars(JSONArray array){
        List<JSONObject> list = new ArrayList<>(array.length());
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject temp = array.getJSONObject(i);
                list.add(temp);
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(),"There is no JSONObject in the JSONArray", e);
        }

        // Create and set the custom listView.
        ListAdapter adapter = new CustomSpecificCar(this, list);
        ListView lv = (ListView) findViewById(R.id.lvCars);
        lv.setAdapter(adapter);

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

    private Timestamp createTimestampFromString(String dateStr, String timeStr){
        Timestamp tp = null;
        String timeWithoutSpace = timeStr.replaceAll("\\s", "");
        String timeFromToUse = timeWithoutSpace.substring(0, timeWithoutSpace.length()-1);

        try {
            String dateFromConc = dateStr +" "+timeFromToUse;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = dateFormat.parse(dateFromConc);
            tp = new Timestamp(date.getTime());
        }catch(Exception e){
            e.printStackTrace();
        }
        return tp;
    }

    /*Getters and Setter*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public User getUser() {
        return user;
    }

    public String getNbSits() {
        return nbSits;
    }

    public String getMaxCons() {
        return maxCons;
    }

    public String getEnergy() {
        return energy;
    }

    public String getBrand() {
        return brand;
    }

    // Return a Timestamp made by concatinating DateFrom and TimeFrom
    public Timestamp getDateFrom() {
       return createTimestampFromString(dateFrom, timeFrom);
    }

    // Return a Timestamp made by concatinating DateTo and TimeTo
    public Timestamp getDateTo() {
        return createTimestampFromString(dateTo, timeTo);
    }
}
