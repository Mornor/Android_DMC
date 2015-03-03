package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.tabs.TabAccount;
import com.example.celien.drivemycar.utils.Action;

import java.util.Iterator;
import java.util.StringTokenizer;


public class Login extends ActionBarActivity {

    private Button btnLogin;
    private TextView tvNyr; // Not Yet Register
    private EditText etLogin;
    private EditText etPassword;
    private TextView tvError;
    private ProgressDialog pbLogin;
    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setListeners();
    }

    private void init(){
        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Drive My Car");

        // Get the items on activity_login
        tvNyr       = (TextView)findViewById(R.id.tvNyr);
        btnLogin    = (Button)findViewById(R.id.btnLogin);
        etLogin     = (EditText)findViewById(R.id.etLogin);
        etPassword  = (EditText)findViewById(R.id.etPassword);
        tvError     = (TextView)findViewById(R.id.tvError);
    }

    // Set the listener for the TextView.
    private void setListeners(){
        tvNyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the home activity
                Intent i = new Intent(v.getContext(), Register.class);
                startActivity(i);
            }
        });
    }

    public void onClickLogin(View v){
        HttpAsync httpAsync = new HttpAsync(this);
        login    = etLogin.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        httpAsync.execute(Action.AUTHENTICATE.toString());
     }

    public void onPostExecute(Object object){
        int responseAuth = (int) object;
        if(responseAuth == 200){ // HTTP 1.0/200 -> OK.
            Intent i = new Intent(this, Home.class);
            i.putExtra("username", login);
            finish();
            startActivity(i);
        }
        else
            createAndShowResult("Wrong password or username", "Retry");
    }

    private void createAndShowResult(String title, String btntext){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(btntext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*Getters and Setters*/
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public ProgressDialog getProgressDialog(){
        return pbLogin;
    }

    public void setProgressBar(ProgressDialog pb){
        this.pbLogin = pb;
    }

}
