package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;;

public class ModifyCar  extends ActionBarActivity {

    // Current User
    private User user;
    private Car car;

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

    private ProgressDialog modifyCar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_settings);
        init();
        setListeners();
    }

    private void init(){

        // Get the current user and the car to modify.
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null){
            this.user = currentUser;
            this.car = user.getCars().get(getIntent().getExtras().getInt("posCar"));
        }

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modify my car");

        etBrand         = (EditText)findViewById(R.id.etBrand);
        etModel         = (EditText)findViewById(R.id.etModel);
        spFuel          = (Spinner)findViewById(R.id.spFuelList);
        tvFuelCons      = (TextView)findViewById(R.id.tvFuelCons);
        tvCo2Cons       = (TextView)findViewById(R.id.tvC02Cons);
        tvHtvaPrice     = (TextView)findViewById(R.id.tvPriceHtva);
        tvLeasePrice    = (TextView)findViewById(R.id.tvLeasePrice);
        btnSaveCar      = (Button)findViewById(R.id.btnSaveCar);


        etBrand.setText(car.getBrand());
        etModel.setText(car.getModel());
        tvFuelCons.setText(String.valueOf(car.getAvg_cons()));
        tvCo2Cons.setText(String.valueOf(car.getC02_cons()));
        tvHtvaPrice.setText(String.valueOf(car.getHtva_price()));
        tvLeasePrice.setText(String.valueOf(car.getLeasing_price()));
        btnSaveCar.setText("Modify car");
    }

    private void setFieldsValues(){
        brand           = etBrand.getText().toString().trim();
        model           = etModel.getText().toString().trim();
        fuel            = spFuel.getSelectedItem().toString();
        fuelCons        = tvFuelCons.getText().toString().trim();
        c02Cons         = tvCo2Cons.getText().toString().trim();
        leasingPrice    = tvLeasePrice.getText().toString().trim();
        htvaPrice       = tvLeasePrice.getText().toString().trim();

        car.setBrand(brand);
        car.setModel(model);
        car.setFuel(fuel);
        car.setAvg_cons(Double.valueOf(fuelCons));
        car.setC02_cons(Double.valueOf(c02Cons));
        car.setHtva_price(Double.valueOf(htvaPrice));
        car.setLeasing_price(Double.valueOf(leasingPrice));
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

    private void setListeners(){
        tvFuelCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set fuel consumption", tvFuelCons);
            }
        });

        tvCo2Cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set C02 consumption", tvCo2Cons);
            }
        });

        tvHtvaPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set price exc. TVA", tvHtvaPrice);
            }
        });

        tvLeasePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set leasing price", tvLeasePrice);
            }
        });

        btnSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()){
                    setFieldsValues();
                    HttpAsync request = new HttpAsync(ModifyCar.this);
                    request.execute(Action.MODIFY_CAR.toString());
                }
            }
        });
    }

    private void showNumberPicker(String title, final TextView field){
        // Create the dialog
        final Dialog np = new Dialog(ModifyCar.this);
        np.setTitle(title);
        np.setContentView(R.layout.number_picker_dialog);

        // Get the number pickers and the buttons
        final EditText unit         = (EditText)np.findViewById(R.id.etUnit);
        Button btnSet               = (Button)np.findViewById(R.id.btnSet);
        Button btnCancel            = (Button)np.findViewById(R.id.btnCancel);

        // Set the listeners
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field.setText(unit.getText().toString());
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


    public void onPostExecute(Object object){
        if((int) object != 200)
            createAndShowResult("Error when updating the car", "Retry", false);
        else{
            createAndShowResult("Car is succesfully updated", "Ok", true);
        }
    }

    private void createAndShowResult(String title, String btntext, final boolean success){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(btntext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(success) launchIntentToHome();
                    }
                }).show();
    }

    private void launchIntentToHome(){
        Intent i = new Intent(this, Home.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }

    /*Getters and Setters*/
    public ProgressDialog getModifyCar() {
        return modifyCar;
    }

    public void setModifyCar(ProgressDialog modifyCar) {
        this.modifyCar = modifyCar;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getBrand() {
        return brand;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setFuelCons(String fuelCons) {
        this.fuelCons = fuelCons;
    }

    public String getC02Cons() {
        return c02Cons;
    }

    public void setC02Cons(String c02Cons) {
        this.c02Cons = c02Cons;
    }

    public String getHtvaPrice() {
        return htvaPrice;
    }

    public void setHtvaPrice(String htvaPrice) {
        this.htvaPrice = htvaPrice;
    }

    public String getLeasingPrice() {
        return leasingPrice;
    }

    public void setLeasingPrice(String leasingPrice) {
        this.leasingPrice = leasingPrice;
    }
}
