package com.example.celien.drivemycar.core;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class AcceptOwner extends ActionBarActivity {

    private User user;
    private JSONObject jsonObject; // Contains just the dates of the requests.
    private ListView lvAgreedOwners;
    private Button btnSelectOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_owner);
        init();
    }

    private void init(){

        // Get the current user and the json clicked in TabOperations (which contains the dates of the wanted request)
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user       = currentUser;
            try {
                this.jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }
        }

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Owner");

        // Retrieve items from layout
        btnSelectOwner = (Button)findViewById(R.id.btnSelectOwner);
        lvAgreedOwners = (ListView)findViewById(R.id.lvAgreedUsers);

        loadRequestData();
    }

    private void loadRequestData(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accept_owner, menu);
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
}
