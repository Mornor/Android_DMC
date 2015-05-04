package com.example.celien.drivemycar.core;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsyncNotif;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestData extends ActionBarActivity {

    private User user;
    private JSONObject jsonObject; // Contains just the dates of the requests. (fromDate and toDate)
    private String fromDate;
    private String toDate;
    private Button btnSelectOwner;
    private TextView tvNbRequest;
    private TextView tvNbAccepted;
    private TextView tvNbRefuted;
    private TextView tvNbNoAnswer;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_data);
        init();
    }

    private void init(){

        // Get the current user and the json clicked in TabOperations (which contains the dates of the wanted request)
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user       = currentUser;
            try {
                this.jsonObject = new JSONObject(getIntent().getStringExtra("json"));
                this.fromDate   = this.jsonObject.getString("fromDate");
                this.toDate     = this.jsonObject.getString("toDate");
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), "JSONException", e);
            }
        }

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request Data");

        // Retrieve items from layout
        tvNbRequest    = (TextView)findViewById(R.id.tvRequestSentEditable);
        tvNbAccepted   = (TextView)findViewById(R.id.tvNbAcceptedEditable);
        tvNbRefuted    = (TextView)findViewById(R.id.tvNbRefutedEditable);
        tvNbNoAnswer   = (TextView)findViewById(R.id.tvNbNoAnswerEditable);
        btnSelectOwner = (Button)findViewById(R.id.btnSelectOwner);

        btnSelectOwner.setEnabled(false);
        loadRequestData();
    }

    private void loadRequestData(){
        new HttpAsyncNotif(this).execute(Action.GET_REQUEST_DATA.toString(), user.getUsername(), fromDate, toDate);
    }

    public void onPostExecuteLoadRequestData(JSONArray array){
        try{
            if(!array.getJSONObject(0).getBoolean("success"))
                Log.e("JSONExcetpion", "JsonReceived is empty");
            tvNbRequest.setText(array.getJSONObject(1).getString("nbRequestSent"));
            tvNbAccepted.setText(array.getJSONObject(1).getString("nbAccepted"));
            tvNbRefuted.setText(array.getJSONObject(1).getString("nbRefuted"));
            tvNbNoAnswer.setText(array.getJSONObject(1).getString("nbNoAnswer"));
        }catch(JSONException e){
            Log.e(e.getClass().getName(), "JSONException", e);
        }

        btnSelectOwner.setEnabled(true);
        btnSelectOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lauchIntentToSelectOwner();
            }
        });
    }

    private void lauchIntentToSelectOwner(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_data, menu);
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
