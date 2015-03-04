package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomListPersonnalCar;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import java.util.Arrays;

/*TODO before everything : Check if the user already have a car in DB.*/

public class CarSettings extends ActionBarActivity implements NumberPicker.OnValueChangeListener {

    // From TabAccount
    private User user;

    // Items on activity
    private EditText etBrand;
    private EditText etModel;
    private Spinner spFuel;
    private TextView tvFuelCons;
    private TextView tvCo2Cons;
    private TextView tvHtvaPrice;
    private TextView tvLeasePrice;
    private Button btnSaveCar;

    // Value of the item
    private String brand;
    private String model;
    private String fuel;
    private String fuelCons;
    private String c02Cons;
    private String htvaPrice;
    private String leasingPrice;

    private ProgressDialog savingCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the User from TabAccount (which is the logged one)
        retrieveUser();

        // Test if the user already have some cars, and display the right template.
        if(userHaveCars()) {
            setContentView(R.layout.activity_car_settings);
            initHaveNoCar();
            setListenersHaveNoCar();
        }
        else{
            setContentView(R.layout.activity_list_personnal_cars);
            initHaveCars();
        }

    }

    private void initHaveCars(){

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personnal Car(s)");

         // Create and set the Custom list adapter
        String[] cars = makeListOfCars();
        ListAdapter adapter = new CustomListPersonnalCar(this, cars);
        ListView lv = (ListView)findViewById(R.id.lvCars);
        lv.setAdapter(adapter);

        // Set listener
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String clickedItem = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(getBaseContext(), clickedItem, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private String[] makeListOfCars(){
        String result[] = new String[user.getCars().size()]; // Size > 0 because the user have at least one car here
        for(int i = 0 ; i < user.getCars().size() ; i++){
            result[i] = user.getCars().get(i).getBrand() + " " + user.getCars().get(i).getModel();
        }
        return result;
    }

    private void retrieveUser(){
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;
    }

    private void initHaveNoCar(){

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a car");

        etBrand         = (EditText)findViewById(R.id.etBrand);
        etModel         = (EditText)findViewById(R.id.etModel);
        spFuel          = (Spinner)findViewById(R.id.spFuelList);
        tvFuelCons      = (TextView)findViewById(R.id.tvFuelCons);
        tvCo2Cons       = (TextView)findViewById(R.id.tvC02Cons);
        tvHtvaPrice     = (TextView)findViewById(R.id.tvPriceHtva);
        tvLeasePrice    = (TextView)findViewById(R.id.tvLeasePrice);
        btnSaveCar      = (Button)findViewById(R.id.btnSaveCar);
    }

    /**
     * @return true if the user have no car in DB, false if the user does have some car.
     */
    private boolean userHaveCars(){
        return user.getCars().isEmpty();
    }

    private void setListenersHaveNoCar(){
        tvFuelCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set fuel consumption", 0, 30, 0, 9, tvFuelCons);
            }
        });

        tvCo2Cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set C02 consumption", 0, 700, 0, 9, tvCo2Cons);
            }
        });

        tvHtvaPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set price exc. TVA", 0, 1000000, 0, 99999, tvHtvaPrice);
            }
        });

        tvLeasePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set leasing price", 0, 1000, 0, 999, tvLeasePrice);
            }
        });

        btnSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()){
                    setFieldsValues();
                    HttpAsync httpAsync = new HttpAsync(CarSettings.this); // Anonymous inner class contain a ref to the instance of the class they are created in
                    httpAsync.execute(Action.SAVE_CAR.toString());
                }
            }
        });
    }

    private void setFieldsValues(){
        brand           = etBrand.getText().toString().trim();
        model           = etModel.getText().toString().trim();
        fuel            = spFuel.getSelectedItem().toString();
        fuelCons        = tvFuelCons.getText().toString();
        c02Cons         = tvCo2Cons.getText().toString();
        leasingPrice    = tvLeasePrice.getText().toString();
        htvaPrice       = tvLeasePrice.getText().toString();
    }

    private boolean checkFields(){
        if(etBrand.getText().toString().isEmpty()){
            etBrand.setHintTextColor(Color.RED);
            return false;
        }

        if(etModel.getText().toString().isEmpty()){
            etModel.setHintTextColor(Color.RED);
            return false;
        }

        // If no fuel consumption and Electricity fuel not selected.
        if(tvFuelCons.getText().toString().equals("0.0") && !spFuel.getSelectedItem().toString().equals("Electricity")){
            tvFuelCons.setTextColor(Color.RED);
            return false;
        }

        // If not Co2 consumption and Electricty not selected.
        if(tvCo2Cons.getText().toString().equals("0.0") && !spFuel.getSelectedItem().toString().equals("Electricity")){
            tvCo2Cons.setTextColor(Color.RED);
            return false;
        }

        // If lease price or htva price != 0
        if(tvHtvaPrice.getText().toString().equals("0.0")){
            tvHtvaPrice.setTextColor(Color.RED);
            return false;
        }

        if(tvLeasePrice.getText().toString().equals("0.0")){
            tvLeasePrice.setTextColor(Color.RED);
            return false;
        }

        return true;
    }

    private void showNumberPicker(String title, int minValUnit, int maxValUnit, int minValTenth, int maxValTenth, final TextView field){
        // Create the dialog
        final Dialog np = new Dialog(CarSettings.this);
        np.setTitle(title);
        np.setContentView(R.layout.number_picker_dialog);

        // Get the number pickers and the buttons
        final NumberPicker npUnit  = (NumberPicker)np.findViewById(R.id.npUnit);
        final NumberPicker npTenth = (NumberPicker)np.findViewById(R.id.npTenth);
        Button btnSet              = (Button)np.findViewById(R.id.btnSet);
        Button btnCancel           = (Button)np.findViewById(R.id.btnCancel);

        // Set the range and some parameters;
        npUnit.setMinValue(minValUnit);
        npUnit.setMaxValue(maxValUnit);
        npUnit.setOnValueChangedListener(this);

        npTenth.setMinValue(minValTenth);
        npTenth.setMaxValue(maxValTenth);
        npTenth.setOnValueChangedListener(this);

        // Set the listeners
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field.setText(String.valueOf(npUnit.getValue()) + "." + String.valueOf(npTenth.getValue()));
                np.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                np.dismiss();
            }
        });

        // Show the dialog
        np.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_settings, menu);
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

    // Call when HttpAsyn has done everything
    public void onPostExecute(Object object){
            if((int) object != 200)
                createAndShowResult("Error when saving the car", "Retry", false);
            else{
                createAndShowResult("Car is succesfully registered", "Ok", true);
                finish();
            }

    }

    private void createAndShowResult(String title, String btntext, final boolean success){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(btntext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(success) launchIntent();
                    }
                }).show();
    }

    private void launchIntent(){
        Intent i = new Intent(this, Home.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    /*Getters and Setters*/

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getFuelCons() {
        return fuelCons;
    }

    public String getC02Cons() {
        return c02Cons;
    }

    public String getHtvaPrice() {
        return htvaPrice;
    }

    public String getLeasingPrice() {
        return leasingPrice;
    }

    public ProgressDialog getSavingCar() {
        return savingCar;
    }

    public void setSavingCar(ProgressDialog savingCar) {
        this.savingCar = savingCar;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
