package be.iba.carswop.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import be.iba.carswop.R;
import be.iba.carswop.http.HttpAsync;
import be.iba.carswop.models.Car;
import be.iba.carswop.models.User;
import be.iba.carswop.utils.Action;
import be.iba.carswop.utils.Tools;

public class ModifyCar  extends ActionBarActivity {

    // Current User
    private User user;
    private Car car;

    // Items from layout.
    private EditText etBrand;
    private EditText etModel;
    private EditText etLicencePlate;
    private TextView tvNbsits;
    private Spinner spFuel;
    private TextView tvFuelCons;
    private TextView tvCo2Cons;
    private TextView tvHtvaPrice;
    private TextView tvLeasePrice;
    private Button btnSaveCar;
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
        etLicencePlate  = (EditText)findViewById(R.id.etLicencePlate);
        spFuel          = (Spinner)findViewById(R.id.spFuelList);
        tvNbsits        = (TextView)findViewById(R.id.tvNbSitsChoose);
        tvFuelCons      = (TextView)findViewById(R.id.tvFuelCons);
        tvCo2Cons       = (TextView)findViewById(R.id.tvC02Cons);
        tvHtvaPrice     = (TextView)findViewById(R.id.tvPriceHtva);
        tvLeasePrice    = (TextView)findViewById(R.id.tvLeasePrice);
        btnSaveCar      = (Button)findViewById(R.id.btnSaveCar);

        etBrand.setText(car.getBrand());
        etModel.setText(car.getModel());
        etLicencePlate.setText(car.getLicencePlate());
        tvFuelCons.setText(String.valueOf(car.getAvg_cons()));
        tvNbsits.setText(String.valueOf(car.getNbSits()));
        tvCo2Cons.setText(String.valueOf(car.getC02_cons()));
        tvHtvaPrice.setText(String.valueOf(car.getHtva_price()));
        tvLeasePrice.setText(String.valueOf(car.getLeasing_price()));
        spFuel.setSelection(getPositionItemSpinner(car.getFuel()));
        btnSaveCar.setText("Modify car");
    }

    private int getPositionItemSpinner(String carEnergy){
        int pos = 0;
        String[] energyList = getResources().getStringArray(R.array.fuel_array);
        for(int i = 0 ; i < energyList.length ; i++)
            if(energyList[i].equals(carEnergy)) pos = i;
        return pos;
    }

    private void setFieldsValues(){
        car.setBrand(etBrand.getText().toString().trim());
        car.setModel(etModel.getText().toString().trim());
        car.setLicencePlate(etLicencePlate.getText().toString().trim());
        car.setFuel(spFuel.getSelectedItem().toString());
        car.setNbSits(Integer.valueOf(tvNbsits.getText().toString().trim()));
        car.setAvg_cons(Double.valueOf(tvFuelCons.getText().toString().trim()));
        car.setC02_cons(Double.valueOf(tvCo2Cons.getText().toString().trim()));
        car.setHtva_price(Double.valueOf(tvHtvaPrice.getText().toString().trim()));
        car.setLeasing_price(Double.valueOf(tvLeasePrice.getText().toString().trim()));
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

        if(tvNbsits.getText().toString().isEmpty() || !Tools.isInteger(tvNbsits.getText().toString())){
            tvNbsits.setTextColor(Color.RED);
            return false;
        }

        if(etLicencePlate.getText().toString().isEmpty()){
            etLicencePlate.setHintTextColor(Color.RED);
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

        tvNbsits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set number of sits", tvNbsits);
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
                    new HttpAsync(ModifyCar.this).execute(Action.MODIFY_CAR);
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

}
