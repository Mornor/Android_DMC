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
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Login extends ActionBarActivity {

    private Button btnLogin;
    private TextView tvNyr; // Not Yet Register
    private EditText etLogin;
    private EditText etPassword;
    private TextView tvError;
    private ProgressDialog pbLogin;
    private String login;
    private String password;

    // The user (if this one exist)
    User user;

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

    /*Here I check if the user is authorized*/
    public void onPostExecuteAuthenticate(Object object){
        int responseAuth = (int) object;
        if(responseAuth == 200){ // HTTP 1.0/200 -> OK (So, the user is well authenticate and exist)
            HttpAsyncJson request = new HttpAsyncJson(this);
            request.execute(Action.LOAD_USER.toString(), login);
        }
        else
            createAndShowResult("Wrong password or username", "Retry");
    }

    /*Here I get some user info (id, name, username, email, phone_number) but NOT yet the his/her List<Car> */
    /*The length of the JSONArray is 0 (only one user)*/
    public void onPostExecuteLoadUser(JSONArray json){
        user = new User();
        try {
            for(int i = 0 ; i < json.length() ; i++){
                JSONObject object = json.getJSONObject(i);
                user.setId(object.getInt("id"));
                user.setName(object.getString("name"));
                user.setUsername(object.getString("username"));
                user.setEmail(object.getString("email"));
                user.setPhoneNumber(object.getString("phone_number"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpAsyncJson request = new HttpAsyncJson(this, true); // Send true in order to differentiante the 2 instances.
        request.execute(Action.LOAD_CARS.toString(), login);
    }

    /*Here, I get the List<Car> of the user (no cars mean array is empty)*/
    public void onPostExecuteLoadCars(JSONArray array){
        List<Car> cars = new ArrayList<>();
        try {
            for(int i = 0 ; i < array.length() ; i++){
                JSONObject object = array.getJSONObject(i);
                Car temp = new Car();
                temp.setId(object.getInt("id"));
                temp.setBrand(object.getString("brand"));
                temp.setModel(object.getString("model"));
                temp.setFuel(object.getString("fuel"));
                temp.setAvg_cons(object.getDouble("avg_cons"));
                temp.setC02_cons(object.getDouble("co2_cons"));
                temp.setHtva_price(object.getDouble("htva_price"));
                temp.setLeasing_price(object.getDouble("leasing_price"));
                cars.add(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user.setCars(cars);
        Intent i = new Intent(this, Home.class);
        i.putExtra("username", login);
        startActivity(i);
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
