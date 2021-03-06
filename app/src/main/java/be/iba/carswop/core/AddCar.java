package be.iba.carswop.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import be.iba.carswop.R;
import be.iba.carswop.http.HttpAsync;
import be.iba.carswop.models.User;
import be.iba.carswop.utils.Action;

public class AddCar extends ActionBarActivity {

    // From TabAccount
    private User user;

    // Items on activity
    private EditText etBrand;
    private EditText etModel;
    private EditText etLicencePlate;
    private TextView tvNbSits;
    private Spinner spFuel;
    private TextView tvFuelCons;
    private TextView tvCo2Cons;
    private TextView tvHtvaPrice;
    private TextView tvLeasePrice;
    private Button btnSaveCar;

    // Value of the item
    private String brand;
    private String model;
    private String licencePlate;
    private String nbSits;
    private String fuel;
    private String fuelCons;
    private String c02Cons;
    private String htvaPrice;
    private String leasingPrice;

    private ProgressDialog savingCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_settings);
        init();
        setListeners();
    }

    private void init(){

        // Get the User (Object).
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a car");

        etBrand         = (EditText)findViewById(R.id.etBrand);
        etModel         = (EditText)findViewById(R.id.etModel);
        etLicencePlate  = (EditText)findViewById(R.id.etLicencePlate);
        spFuel          = (Spinner)findViewById(R.id.spFuelList);
        tvNbSits        = (TextView)findViewById(R.id.tvNbSitsChoose);
        tvFuelCons      = (TextView)findViewById(R.id.tvFuelCons);
        tvCo2Cons       = (TextView)findViewById(R.id.tvC02Cons);
        tvHtvaPrice     = (TextView)findViewById(R.id.tvPriceHtva);
        tvLeasePrice    = (TextView)findViewById(R.id.tvLeasePrice);
        btnSaveCar      = (Button)findViewById(R.id.btnSaveCar);
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

        tvNbSits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set number of sits", tvNbSits);
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
                if (checkFields()) {
                    setFieldsValues();
                    HttpAsync httpAsync = new HttpAsync(AddCar.this); // Anonymous inner class contain a ref to the instance of the class they are created in
                    httpAsync.execute(Action.SAVE_CAR);
                }
            }
        });
    }

    private void setFieldsValues(){
        brand           = etBrand.getText().toString().trim();
        model           = etModel.getText().toString().trim();
        licencePlate    = etLicencePlate.getText().toString().trim();
        fuel            = spFuel.getSelectedItem().toString();
        nbSits          = tvNbSits.getText().toString().trim();
        fuelCons        = tvFuelCons.getText().toString();
        c02Cons         = tvCo2Cons.getText().toString();
        leasingPrice    = tvLeasePrice.getText().toString();
        htvaPrice       = tvHtvaPrice.getText().toString();
    }

    private boolean checkFields(){
        if(etBrand.getText().toString().isEmpty()){
            etBrand.setHintTextColor(Color.RED);
            return false;
        }

        if(tvNbSits.getText().toString().equals(getResources().getString(R.string.hChoose))){
            tvNbSits.setTextColor(Color.RED);
            return false;
        }

        if(etModel.getText().toString().isEmpty()){
            etModel.setHintTextColor(Color.RED);
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

    private void showNumberPicker(String title, final TextView field){
        // Create the dialog
        final Dialog np = new Dialog(AddCar.this);
        np.setTitle(title);
        np.setContentView(R.layout.number_picker_dialog);

        // Get the number pickers and the buttons
        final EditText unit  = (EditText)np.findViewById(R.id.etUnit);
        Button btnSet        = (Button)np.findViewById(R.id.btnSet);
        Button btnCancel     = (Button)np.findViewById(R.id.btnCancel);

        // Set the listeners
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unit.getText().toString().trim().isEmpty())
                    showToast();
                else {
                    field.setText(unit.getText().toString());
                    np.dismiss();
                }
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

    private void showToast(){
        Toast.makeText(this, "Please, set a value", Toast.LENGTH_SHORT).show();
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

    // Call when HttpAsync has done everything
    public void onPostExecute(Object object){
            if((int) object != 200)
                createAndShowResult("Error when saving the car", "Please, verify your licence plate is unique", false);
            else if((int) object == -1) // Then car is already present in User's List<Cars> (cf. HttpAsync.saveNewCar)
                createAndShowResult("Error when saving the car", "You already have it", false);
            else
                createAndShowResult("Car is succesfully registered", "Ok", true);
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
        finish();
        startActivity(i);
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

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getNbSits() {
        return nbSits;
    }

    public void setNbSits(String nbSits) {
        this.nbSits = nbSits;
    }
}
