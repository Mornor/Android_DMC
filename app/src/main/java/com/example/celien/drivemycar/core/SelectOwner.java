package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomSelectOwner;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectOwner extends ActionBarActivity {

    private User user;
    private String fromDate;
    private String toDate;
    private ListView lv;
    private Button btnSelectOwner;
    private ProgressDialog progressDialog;

    // HashMap updated via CustomSelectOwner
    HashMap<String, String> selectedOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_owner);
        init();
    }

    private void init(){

        // Get the current user and the json clicked in RequestData (which contains the dates of the wanted request)
        User currentUser = getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user = currentUser;
            try {
                JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("json"));
                this.fromDate   = jsonObject.getString("fromDate");
                this.toDate     = jsonObject.getString("toDate");
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }
        }

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agreed Owners");

        // Get the items on the layout
        lv              = (ListView)findViewById(R.id.lvOwners);
        btnSelectOwner  = (Button)findViewById(R.id.btnSelectOwner);

        // Init the HashMap of the SelectedOnwers
        selectedOwner = new HashMap<>();

        getAgreedOwners();
    }

    private void getAgreedOwners(){
        new HttpAsyncNotif(this, false).execute(Action.GET_AGREED_OWNERS);
    }

    public void onOnPostAgreedOnwers(JSONArray array){
        List<JSONObject> list = new ArrayList<>();
        try {

            if(!array.getJSONObject(0).getBoolean("success"))
                Log.e("Error", "JSON empty");
            else {
                // Start from 1 because 0 is the JSON to indicate if array is empty (true) or not
                for (int i = 1; i < array.length(); i++) {
                    JSONObject temp = array.getJSONObject(i);
                    list.add(temp);
                }
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        ListAdapter adapter = new CustomSelectOwner(this, list, this);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // Make the ListView only able to select one single row

        // Set the listener to the button
        btnSelectOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyOwnerSelected();
            }
        });
    }

    private void notifyOwnerSelected(){
        if(selectedOwner.size() == 0)
            Toast.makeText(this, "Please select at least one owner", Toast.LENGTH_SHORT).show();
        else{
            new HttpAsyncNotif(this, true).execute(Action.NOTIFY_SELECTED_ONWER);
        }
    }

    public void onPostNotifySelectedOwner(JSONArray array){
        try{
            if(array.getJSONObject(0).getBoolean("success"))
                launchIntentToHome();
            else
                Toast.makeText(this, "Error encountered, please retry", Toast.LENGTH_SHORT).show();
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }
    }

    /*** Create and maintain the HashMap with the selected user/brand/model selected in CustomSelectedOwner*/
    public void maintainItemClicked(JSONObject json){
        try{
            selectedOwner.clear();
            selectedOwner.put("ownerName", json.getString("ownerName"));
            selectedOwner.put("brand", json.getString("brand"));
            selectedOwner.put("model", json.getString("model"));
        }catch (JSONException e) {
            Log.e(e.getClass().getName(), "JSONException", e);
        }
    }

    private void launchIntentToHome(){
        Toast.makeText(this, "Notification successfully sent", Toast.LENGTH_SHORT).show();
        finish();
        Intent i = new Intent(this, Home.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_owner, menu);
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

    /*Getters and Setters*/
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public User getUser() {
        return user;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public HashMap<String, String> getSelectedOwner() {
        return selectedOwner;
    }
}
