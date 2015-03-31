package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomSpecificCar;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
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
import java.util.HashMap;
import java.util.Iterator;
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
    private Button btnSendRequest;

    // Variables sent to the server in order to retrieve the cars which fits the choice of the user (from TabSearchCar).
    // But I also use the previous vars.
    private Timestamp from;
    private Timestamp to;

    private ListAdapter adapter;
    private ListView lv;
    private ProgressDialog progressDialog;
    private List<HashMap<String, String>> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_specific_cars);
        selectedItems = new ArrayList<HashMap<String, String>>();
        init();
        getRequestedCars();
    }


    private void setListeners(){
        // Can't click on the button until cars are shown.
        btnSendRequest.setEnabled(true);

        btnSendRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData(){
        if(selectedItems.size() == 0)
            Toast.makeText(this, "Please, select at least 1 item", Toast.LENGTH_SHORT).show();
        for(int i = 0 ; i < selectedItems.size() ; i++){
            Log.d("List size ",  String.valueOf(selectedItems.size()));
            Log.d("List Owner ", selectedItems.get(i).get("owner"));
            Log.d("List Brand ", selectedItems.get(i).get("brand"));
            Log.d("List Model ", selectedItems.get(i).get("model"));
        }

        //else
            //new HttpAsyncNotif(this).execute(Action.SAVE_REQUEST.toString());
    }

    // Maintain a dynamic JSONArray of the selected items via checkbox in CustomSpecificCar
    // If boolean is true, add to list
    // If false, remove
    public void updateClickedUsername(JSONObject currentJson, boolean action){
        HashMap<String, String> list = new HashMap<>();
        try{
            list.put("owner", currentJson.getString("owner"));
            list.put("brand", currentJson.getString("brand"));
            list.put("model", currentJson.getString("model"));
        } catch (JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        if(action)
            selectedItems.add(list);
        else
            selectedItems.remove(list);

/*
        for(Iterator<HashMap<String, String>> iter = selectedItems.iterator() ; iter.hasNext();){
            HashMap<String, String> temp = iter.next();
            if(temp.equals(list))
                iter.remove();
        }
        Log.d("List removed", "ok"); */
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

        btnSendRequest = (Button)findViewById(R.id.btnSendRequestToSelectedPeople);
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
        List<JSONObject> list = new ArrayList<>();
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject temp = array.getJSONObject(i);
                list.add(temp);
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(),"There is no JSONObject in the JSONArray", e);
        }

        // Create and set the custom listView.
        adapter = new CustomSpecificCar(this, list, this);
        lv = (ListView) findViewById(R.id.lvCars);
        lv.setAdapter(adapter);

        setListeners();
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

    public List<HashMap<String, String>> getSelectedItems() {
        return selectedItems;
    }
}
