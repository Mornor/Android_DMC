package be.iba.carswop.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import be.iba.carswop.R;
import be.iba.carswop.adapter.CustomTabOperation;
import be.iba.carswop.fragment.OwnerConfirmRent;
import be.iba.carswop.http.HttpAsyncTransaction;
import be.iba.carswop.models.User;
import be.iba.carswop.utils.Action;
import be.iba.carswop.utils.NotificationTypeConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AcceptedRequest extends ActionBarActivity {

    private User user;
    private ListView lv;
    private ListAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);
        init();
    }

    private void init(){

        // Get the User (Object).
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Accepted requests");

        // Get items from the layout
        lv = (ListView)findViewById(R.id.lvAcceptedRequests);

        // Get the transaction accepted by both sides (requester and owner)
        getTransactions();
    }

    private void getTransactions(){
        new HttpAsyncTransaction(this).execute(Action.GET_TRANSACTIONS);
    }

    public void onPostExecuteGetTransaction(JSONArray array){
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

        adapter = new CustomTabOperation(this, list);
        lv.setAdapter(adapter);

        // Set listener to ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObjectClicked = (JSONObject) parent.getItemAtPosition(position);
                launchConfirmRentDialog(jsonObjectClicked);
            }
        });
    }

    /*Launch the confirm rent Dialog only if it is necessary for the owner to se the odometer*/
    private void launchConfirmRentDialog(JSONObject object){
        String status = "";
        try {
            status = object.getString("status");
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), "There is not status field", e);
        }

        if(status.equals(NotificationTypeConstants.DRIVER_WAITING_FOR_OWNER_KEY)){
            OwnerConfirmRent ownerConfirmRent = new OwnerConfirmRent();
            Bundle bdl = new Bundle();
            bdl.putString("json", object.toString());
            ownerConfirmRent.setArguments(bdl);
            ownerConfirmRent.show(getSupportFragmentManager(), "");
        }

        else
            Toast.makeText(this, "Nothing left to do with this one", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accepted_request, menu);
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
}