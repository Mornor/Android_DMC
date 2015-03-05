package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.celien.drivemycar.utils.Action;;

public class ModifyCar  extends ActionBarActivity {

    // Current Car
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
        setListener();
    }

    private void init(){

        // Get the current car.
        Car currentCar = (Car)getIntent().getParcelableExtra("car");
        if(currentCar != null)
            this.car = currentCar;

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

    private void setListener(){
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

    public void onPostExecute(Object object){
        if((int) object != 200)
            createAndShowResult("Error when saving the car", "Retry", false);
        else{
            createAndShowResult("Car is succesfully registered", "Ok", true);
        }

    }

    private void createAndShowResult(String title, String btntext, final boolean success){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton(btntext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(success) Log.d("Success", "");
                    }
                }).show();
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
