package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsyncTransaction;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;

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
        new HttpAsyncTransaction(this).execute(Action.GET_TRANSACTIONS.toString(), user.getUsername());
    }

    public void onPostExecuteGetTransaction(JSONArray array){

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
}
