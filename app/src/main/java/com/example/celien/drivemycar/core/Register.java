package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;


public class Register extends ActionBarActivity {

    private TextView tvError;
    private EditText etName;
    private EditText etUsername;
    private EditText etBankAccount;
    private EditText etMail;
    private EditText etConfirmMail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    // Value of the fields
    private User temp;
    private String bankAccount;
    private String name;
    private String username;
    private String mail;
    private String password;

    // Usefull var
    boolean formError;
    boolean confirmMailError;
    boolean confirmPasswordError;
    boolean usernameError;

    // Usefull GUI stuff
    ProgressDialog ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");

        tvError             = (TextView)findViewById(R.id.tvError);
        etBankAccount       = (EditText)findViewById(R.id.etBankAccount);
        etName              = (EditText)findViewById(R.id.etName);
        etUsername          = (EditText)findViewById(R.id.etUsername);
        etMail              = (EditText)findViewById(R.id.etMail);
        etConfirmMail       = (EditText)findViewById(R.id.etConfirmMail);
        etPassword          = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword   = (EditText)findViewById(R.id.etConfirmPassword);
    }

    // Called when register button is clicked.
    public void onClickRegister(View v){
        name                    = etName.getText().toString().trim();
        username                = etUsername.getText().toString().trim();
        bankAccount             = etBankAccount.getText().toString().trim();
        mail                    = etMail.getText().toString().trim();
        String confirmMail      = etConfirmMail.getText().toString().trim();
        password                = etPassword.getText().toString().trim();
        String confirmPassword  = etConfirmPassword.getText().toString().trim();

        // If confirmation failed
        String error = checkConfirmation(mail, confirmMail, password, confirmPassword, username, bankAccount);
        if(formError){
            if(confirmMailError)
                tvError.setText(error);
        }

        // If confirmation succeed
        else
            checkUsernameUnique(username);

    }

    private void checkUsernameUnique(String username){
        HttpAsyncJson request = new HttpAsyncJson(this);
        request.execute(Action.CHECK_USERNAME);
    }

    /*Last step, result is returned by onPostExecute in HttpAsynJson*/
    public void onPostExecuteUsernameUnique(Boolean result){
        if(result)
            tvError.setText("Username already exist");
        else{
            tvError.setText("");
            HttpAsync httpAsync = new HttpAsync(this);
            temp = new User(name, mail, username, password, bankAccount);
            httpAsync.execute(Action.SAVE_USER);
        }

    }

    /**
     * @param params, in this order : [0] -> mail, [1] -> confirmMail, [2] -> password, [3] -> confirmPassword, [4] -> bankAccount
     * @return a message with errors if errors occurs, null otherwise.
     */
    private String checkConfirmation(String... params){
        formError = false;
        usernameError = false;
        confirmPasswordError = false;
        confirmMailError = false;
        String result = "Please, confirm the following error(s):\n";
        if(!params[0].equals(params[1])){
            result += "- Mail and its confirmation does not match\n";
            formError = true;
            confirmMailError = true;
        }
        if(!params[2].equals(params[3])){
            result += "- Password and its confirmation does not match\n";
            formError = true;
            confirmPasswordError = true;
        }
        if(params[4].isEmpty())
            formError = true;
        if(formError)
            return result;
        else
            return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void onPostExecute(int success){
        if(success == 200)
            createAndShowResult("Register sucessfull, please log in", "Ok", true);
        else
            createAndShowResult("Register failed", "Retry", false);
    }

    private void createAndShowResult(String title, String btntext, final boolean success){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(btntext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if(success) launchIntentToLogin();
                    }
                }).show();
    }

    private void launchIntentToLogin(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        finish(); // Kill the Register activity.
    }

    /*Getters and Setters*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser(){
       return temp;
    }

    public ProgressDialog getRing(){
        return ring;
    }

    public void setRing(ProgressDialog ring){
        this.ring = ring;
    }

    public String getUsername() {
        return username;
    }
}
