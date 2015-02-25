package com.example.celien.drivemycar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;


public class Register extends ActionBarActivity {

    // Items on activity_register
    private Button btnRegister;
    private TextView tvError;
    private EditText etName;
    private EditText etUsername;
    private EditText etMail;
    private EditText etConfirmMail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    // Value of the fields
    private User temp;
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

        btnRegister         = (Button)findViewById(R.id.btnRegister);
        tvError             = (TextView)findViewById(R.id.tvError);
        etName              = (EditText)findViewById(R.id.etName);
        etUsername          = (EditText)findViewById(R.id.etUsername);
        etMail              = (EditText)findViewById(R.id.etMail);
        etConfirmMail       = (EditText)findViewById(R.id.etConfirmMail);
        etPassword          = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword   = (EditText)findViewById(R.id.etConfirmPassword);
    }

    // Called when register button is clicked.
    public void onClickRegister(View v){
        name                    = etName.getText().toString();
        username                = etUsername.getText().toString();
        mail                    = etMail.getText().toString();
        String confirmMail      = etConfirmMail.getText().toString(); // Declared here because used just to confirm the fields.
        password                = etPassword.getText().toString();
        String confirmPassword  = etConfirmPassword.getText().toString();

        // If confirmation failed
        String error = checkConfirmation(mail, confirmMail, password, confirmPassword);
        if(formError){
            if(confirmMailError)
                tvError.setText(error);
        }

        // If confirmation succeed
        // Save the User into db using instance of HttpAsync
        else if(!formError){
            tvError.setText("");
            HttpAsync httpAsync = new HttpAsync(this);
            temp = new User(name, mail, username, password);
            httpAsync.execute(Action.SAVE_USER.toString());
        }
    }

    /**
     * @param params, in this order : [0] -> mail, [1] -> confirmMail, [2] -> password, [3] -> confirmPassword
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

    public void onPostExecute(boolean success){
        if(success)
            createAndShowResult("Register sucessfull", "Ok");
        else
            Log.d("Register", "Failed");
    }

    private void createAndShowResult(String title, String btntext){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(btntext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // When btn is clicked
                        Log.d("Ok", "Ok has  been clicked");
                    }
                }).show();
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
}
