package be.iba.carswop.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import be.iba.carswop.R;
import be.iba.carswop.adapter.CustomSpecificCar;
import be.iba.carswop.http.HttpAsyncJson;
import be.iba.carswop.http.HttpAsyncNotif;
import be.iba.carswop.models.User;
import be.iba.carswop.utils.Action;
import be.iba.carswop.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
    private int idSelectedCar; // In case of an exchange, represent the Id of the Car the User wish to exchange. (bind with DB)
    private boolean isExchange;
    private Button btnSendRequest;

    private double mileage;
    private ProgressDialog progressDialog;
    private List<HashMap<String, String>> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_specific_cars);
        selectedItems = new ArrayList<>();
        init();
        getRequestedCars();
    }


    private void setListeners(){
        // Can't click on the button until cars are shown.
        btnSendRequest.setEnabled(true);

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequest();
            }
        });
    }

    private void setRequest(){
        if(selectedItems.size() == 0)
            Toast.makeText(this, "Please, select at least 1 item", Toast.LENGTH_SHORT).show();
        // Before sending the request to play, ask for current mileage of the car only if an exchange is wanted
        else{
            if(isExchange)
                askMileageInCaseOfExchange();
            else
                new HttpAsyncNotif(this).execute(Action.SAVE_REQUEST);
        }
    }

    private void askMileageInCaseOfExchange(){
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        new AlertDialog.Builder(this)
                .setTitle("Please, set the mileage of your car")
                .setView(input)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().isEmpty())
                            Toast.makeText(ListSpecificCars.this.getParent(), "Please, set your mileage", Toast.LENGTH_SHORT).show();
                        else {
                            ListSpecificCars.this.setMileage(Double.valueOf(input.getText().toString()));
                            saveRequest();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private void saveRequest() {
        new HttpAsyncNotif(this).execute(Action.SAVE_REQUEST);
    }

    public void onPostExecuteSendRequest(JSONArray array){
        launchIntentToHome();
    }

    private void launchIntentToHome(){
        Intent i = new Intent(this, Home.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        finish();
        startActivity(i);
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
    }


    private void init(){

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search result");

        // Get the User (Object).
        User currentUser = getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user       = currentUser;
            this.brand      = getIntent().getStringExtra("brand");
            this.nbSits     = getIntent().getStringExtra("nbSits");
            this.energy     = getIntent().getStringExtra("energy");
            this.maxCons    = getIntent().getStringExtra("maxCons");
            this.dateFrom   = getIntent().getStringExtra("dateFrom");
            this.timeFrom   = getIntent().getStringExtra("timeFrom");
            this.dateTo     = getIntent().getStringExtra("dateTo");
            this.isExchange = getIntent().getBooleanExtra("isExchange", false); // false as a default value if nothing has been found in the Intent.
            this.timeTo     = getIntent().getStringExtra("timeTo");
            if(this.isExchange)
                this.idSelectedCar = getIntent().getIntExtra("idSelectedCar", -1); // -1 as a default value. Id of the car of the user;
        }

        btnSendRequest = (Button)findViewById(R.id.btnSendRequestToSelectedPeople);
    }

    private void getRequestedCars(){
        new HttpAsyncJson(this).execute(Action.LOAD_SPECIFIC_CARS);
    }

    // Create an ArrayList<JSONObject> from the JSONArray received from HttpAsyncJson
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
            Log.e(e.getClass().getName(),"JSONException", e);
        }

        // Create and set the custom listView.
        ListAdapter adapter = new CustomSpecificCar(this, list, this);
        ListView lv         = (ListView) findViewById(R.id.lvCars);
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


    /*Getters and Setter*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public int getIdSelectedCar() {
        return idSelectedCar;
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

    // Return a Timestamp made by concatenating DateFrom and TimeFrom
    public Timestamp getDateFrom() {
       return Tools.createTimestampFromString(dateFrom, timeFrom);
    }

    // Return a Timestamp made by concatenating DateTo and TimeTo
    public Timestamp getDateTo() {
        return Tools.createTimestampFromString(dateTo, timeTo);
    }

    public List<HashMap<String, String>> getSelectedItems() {
        return selectedItems;
    }

    public boolean isExchange() {
        return isExchange;
    }

    public double getMileage() {
        return mileage;
    }

    // Also set the mileage of the User's car
    public void setMileage(double mileage) {
        for(int i = 0 ; i < user.getCars().size() ; i++) {
            if (user.getCars().get(i).getId() == idSelectedCar)
                user.getCars().get(i).setMileage(mileage);
        }
        this.mileage = mileage;
    }
}
