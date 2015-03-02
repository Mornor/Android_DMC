package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.utils.Action;

/*TODO before everything : Check if the user already have a car in DB.*/

public class CarSettings extends ActionBarActivity implements NumberPicker.OnValueChangeListener {

    // Items on activity
    private EditText etBrand;
    private EditText etModel;
    private Spinner spFuel;
    private TextView tvFuelCons;
    private TextView tvCo2Cons;
    private TextView tvHtvaPrice;
    private TextView tvLeasePrice;
    private Button btnSaveCar;
    private ImageView redDotBrand;
    private ImageView redDotModel;
    private ImageView redDotFuel;
    private ImageView redDotCo2;
    private ImageView redDotHtvaPrice;
    private ImageView redDotLeasePrice;

    // Value of the item
    private String brand;
    private String model;
    private String fuel;
    private String fuelCons;
    private String c02Cons;
    private String htvaPrice;
    private String leasingPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_settings);
        init();
        setListeners();
    }

    private void init(){
        etBrand         = (EditText)findViewById(R.id.etBrand);
        etModel         = (EditText)findViewById(R.id.etModel);
        spFuel          = (Spinner)findViewById(R.id.spFuelList);
        tvFuelCons      = (TextView)findViewById(R.id.tvFuelCons);
        tvCo2Cons       = (TextView)findViewById(R.id.tvC02Cons);
        tvHtvaPrice     = (TextView)findViewById(R.id.tvPriceHtva);
        tvLeasePrice    = (TextView)findViewById(R.id.tvLeasePrice);
        btnSaveCar      = (Button)findViewById(R.id.btnSaveCar);
        redDotBrand     = (ImageView)findViewById(R.id.ivRedDotBrand);
        redDotModel     = (ImageView)findViewById(R.id.ivRedDotModel);
        redDotFuel      = (ImageView)findViewById(R.id.ivRedDotFuel);
        redDotCo2       = (ImageView)findViewById(R.id.ivRedDotCo2);
        redDotHtvaPrice = (ImageView)findViewById(R.id.ivRedDotHtvaPrice);
        redDotLeasePrice= (ImageView)findViewById(R.id.ivRedDotLeasePrice);
    }

    private void setListeners(){
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
        brand           = etBrand.getText().toString();
        model           = etModel.getText().toString();
        fuel            = spFuel.getSelectedItem().toString();
        fuelCons        = tvFuelCons.getText().toString();
        c02Cons         = tvCo2Cons.getText().toString();
        leasingPrice    = tvLeasePrice.getText().toString();
        htvaPrice       = tvLeasePrice.getText().toString();
    }

    private boolean checkFields(){
        if(etBrand.getText().toString().isEmpty() || etModel.getText().toString().isEmpty()){
            redDotBrand.setVisibility(View.VISIBLE);
            redDotModel.setVisibility(View.VISIBLE);
            return false;
        }

        // If no fuel consumption and Electricity fuel not selected.
        if(tvFuelCons.getText().toString().equals("0.0") && !spFuel.getSelectedItem().toString().equals("Electricity")){
            redDotFuel.setVisibility(View.VISIBLE);
            return false;
        }

        // If not Co2 consumption and Electricty not selected.
        if(tvCo2Cons.getText().toString().equals("0.0") && !spFuel.getSelectedItem().toString().equals("Electricity")){
            redDotCo2.setVisibility(View.GONE);
            return false;
        }

        // If lease price or htva price != 0
        if(!tvHtvaPrice.getText().toString().equals("0.0") || !tvHtvaPrice.getText().toString().equals("0.0")){
            redDotLeasePrice.setVisibility(View.VISIBLE);
            redDotHtvaPrice.setVisibility(View.VISIBLE);
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
