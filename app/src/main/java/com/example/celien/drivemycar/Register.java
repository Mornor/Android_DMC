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
        String name             = etName.getText().toString();
        String username         = etUsername.getText().toString();
        String mail             = etMail.getText().toString();
        String confirmMail      = etConfirmMail.getText().toString();
        String password         = etPassword.getText().toString();
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
        else if(error == null){
            tvError.setText("OKK");
        }
    }

    /**
     * @param params, in this order : [0] -> mail, [1] -> confirmMail, [2] -> password, [3] -> confirmPassword
     * @return a message with errors if errors occurs, null otherwise.
     */
    private String checkConfirmation(String... params){
        boolean isError = false;
        String result = "";
        if(!params[0].equals(params[1])){
            result += "Mail and its confirmation";
            isError = true;
        }
        if(!params[2].equals(params[3])){
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
}
