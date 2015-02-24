package com.example.celien.drivemycar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    String name;
    String username;
    String mail;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
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
        if(error != null){
            tvError.setText(error);
            switch (error){
                case "Mail and its confirmation\ndoes not match, please correct it.":
                    etConfirmMail.setText("");
                    etConfirmMail.setHint("New mail confirmation");
                    break;
                case "\nPassword and its confirmation\ndoes not match, please correct it.":
                    etConfirmPassword.setText("");
                    etConfirmPassword.setHint("New pwd confirmation");
                    break;
                case "Mail and its confirmation\nPassword and its confirmation\ndoes not match, please correct it.":
                    etConfirmMail.setText("");
                    etConfirmMail.setHint("New mail confirmation");
                    etConfirmPassword.setText("");
                    etConfirmPassword.setHint("New pwd confirmation");
                    break;
            }
        }

        // If confirmation succeed
        // Save the User into db using instance of HttpAsync
        else if(error == null){
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
        boolean isError = false;
        String result = "";
        if(!params[1].equals(params[2])){
            result += "Mail and its confirmation";
            isError = true;
        }
        if(!params[3].equals(params[4])){
            result += "\nPassword and its confirmation";
            isError = true;
        }
        if(isError)
            return result + "\ndoes not match, please correct it.";
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
}
