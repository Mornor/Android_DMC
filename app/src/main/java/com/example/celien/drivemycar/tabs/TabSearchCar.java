package com.example.celien.drivemycar.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.core.ListSpecificCars;
import com.example.celien.drivemycar.fragment.DatePicker;
import com.example.celien.drivemycar.fragment.TimePicker;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class TabSearchCar extends Fragment {

    private User user;

    // Fragment variables
    private TextView tvBrandChoose;
    private Spinner spEnergy;
    private TextView tvConsoFuel;
    private TextView tvNbSitsChoose;
    private TextView dateFrom;
    private TextView timeFrom;
    private TextView dateTo;
    private TextView timeTo;
    private TextView tvMileage;
    private Switch sExchange;
    private Button btnSearch;

    // Usefull Progress Dialog
    private ProgressDialog searchBrandCar;
    private ProgressDialog searchCorrespondingCar;

    // Usefull variables
    private String[] brands;
    private AlertDialog alert;
    private String dateFromStr;
    private String timeFromStr;
    private String dateToStr;
    private String timeToStr;
    private String energy;
    private String brand;
    private String fuelCons;
    private String nbSits;
    private Intent i; // Have to declare it as a global class var because it is launch from different places depending on the exchange or not;
    private String mileage;

    // Create a HashMap to get an easy access on Date and Time TextView (efficiency matters)
    private HashMap<String, TextView> hmTextView;

    // Calls variable (useful for the DatePicker and TimePicker)
    public static final int DATE_PICKER_FRAGMENT = 1;
    public static final int TIME_PICKER_FRAGMENT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_search_car, container, false);
        init(rootView);
        setListeners();
        return rootView;
    }


    private void init(View v){
        // Get current User
        Home homeActivity = (Home)getActivity();
        user = homeActivity.getUser();

        // Get fields
        tvBrandChoose   = (TextView)v.findViewById(R.id.tvBrandChoose);
        spEnergy        = (Spinner)v.findViewById(R.id.spFuelList);
        tvConsoFuel     = (TextView)v.findViewById(R.id.tvMaxConsFuel);
        tvMileage       = (TextView)v.findViewById(R.id.tvChooseMileage);
        tvNbSitsChoose  = (TextView)v.findViewById(R.id.tvNbSitsChoose);
        dateFrom        = (TextView)v.findViewById(R.id.tvPickDateFrom);
        timeFrom        = (TextView)v.findViewById(R.id.tvChooseTimeFrom);
        dateTo          = (TextView)v.findViewById(R.id.tvPickDateTo);
        timeTo          = (TextView)v.findViewById(R.id.tvChooseTimeTo);
        sExchange       = (Switch)v.findViewById(R.id.sExchange);
        btnSearch       = (Button)v.findViewById(R.id.btnSearch);

        // Create a HashMap to get an easy access on Date and Time TextView (efficiency matters)
        hmTextView = new HashMap<>();
        hmTextView.put("dateFrom", dateFrom);
        hmTextView.put("timeFrom", timeFrom);
        hmTextView.put("dateTo", dateTo);
        hmTextView.put("timeTo", timeTo);
    }

    // If we arrived there, we already know that period's stuff are picked up, we can now go to
    // ListSpecificCar and make the request to the server in this class
    private void launchIntentToSpecificCars(){
        i = new Intent(getActivity(), ListSpecificCars.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);

        // If the user has not choose a specific brand, then empty String is sent
        if(brand.equals(getResources().getString(R.string.hChoose)))
            i.putExtra("brand", "");
        else if(! brand.equals(getResources().getString(R.string.hChoose)))
            i.putExtra("brand", brand);

        // Same for the others fields, except date and time stuff and energy
        if(fuelCons.equals(getResources().getString(R.string.tvMaxCons)))
            i.putExtra("maxCons", "");
        else if(!fuelCons.equals(getResources().getString(R.string.tvMaxCons)))
            i.putExtra("maxCons", fuelCons);

        if(nbSits.equals(getResources().getString(R.string.hChoose)))
            i.putExtra("nbSits", "");
        else if(!nbSits.equals(getResources().getString(R.string.hChoose)))
            i.putExtra("nbSits", nbSits);

        i.putExtra("isExchange", sExchange.isChecked());
        i.putExtra("energy", energy);
        i.putExtra("dateFrom", dateFromStr);
        i.putExtra("timeFrom", timeFromStr);
        i.putExtra("dateTo", dateToStr);
        i.putExtra("timeTo", timeToStr);

        // Add some test on date and time fields (which are mandatory, for the app not to crash)
        if(dateFrom.getText().toString().equals("Pick date") || dateTo.getText().toString().equals("PickDate") || timeFrom.getText().toString().equals("Choose time") || timeTo.getText().toString().equals("Choose time"))
            Toast.makeText(getActivity(), "Please, set the dates fields correctly", Toast.LENGTH_SHORT).show();

        // Check if it is an exchange or not.
        else{
            if(sExchange.isChecked()){
                alert = buildDialog("Choose the car you want to exchange", getStringArrayOfUserCar(), false).create();
                alert.show();
            }
            else
                startActivity(i);
        }

    }

    private String[] getStringArrayOfUserCar(){
        String[] result = new String[user.getCars().size()];
        for(int i = 0 ; i < user.getCars().size() ; i++)
            result[i] = user.getCars().get(i).getBrand()+ " " +user.getCars().get(i).getModel();
        return result;
    }

    private void setListeners(){
        tvBrandChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncJson request = new HttpAsyncJson(TabSearchCar.this);
                request.execute(Action.GET_BRAND.toString());
            }
        });

        tvMileage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showNumberPicker("Set est. mileage", tvMileage);
            }
        });

        tvConsoFuel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showNumberPicker("Set max. consumption", tvConsoFuel);
            }
        });

        tvNbSitsChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set wished number of sits", tvNbSitsChoose);
            }
        });

        dateFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDatePicker("dateFrom"); // Be sure to send which is *equals* to the TextView name.
            }
        });

        timeFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showTimePicker("timeFrom");
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker("dateTo");
            }
        });

        timeTo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showTimePicker("timeTo");
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setFieldsValue();
                if(checkFields())
                    launchIntentToSpecificCars();
            }
        });
    }

    private boolean checkFields(){
        boolean result = false;
        // Search is only possible if Date, Time are picked up.
        if(dateFromStr.isEmpty())
            dateFrom.setTextColor(Color.RED);
        if(timeFromStr.isEmpty())
            timeFrom.setTextColor(Color.RED);
        if(dateToStr.isEmpty())
            dateTo.setTextColor(Color.RED);
        if(timeToStr.isEmpty())
            timeTo.setTextColor(Color.RED);
        if (!(dateFromStr.isEmpty() || dateToStr.isEmpty() || timeFromStr.isEmpty() || timeToStr.isEmpty()))
            result = true;
        return result;
    }

    private void setFieldsValue(){
        brand           = tvBrandChoose.getText().toString();
        energy          = spEnergy.getSelectedItem().toString();
        fuelCons        = tvConsoFuel.getText().toString();
        nbSits          = tvNbSitsChoose.getText().toString();
        mileage         = tvMileage.getText().toString();
        dateFromStr     = dateFrom.getText().toString();
        dateToStr       = dateTo.getText().toString();
        timeFromStr     = timeFrom.getText().toString();
        timeToStr       = timeTo.getText().toString();
    }

    private void showTimePicker(String tag){
        DialogFragment fragment = new TimePicker();
        fragment.setTargetFragment(this, TIME_PICKER_FRAGMENT);
        fragment.show(getFragmentManager(), tag);
    }

    private void showDatePicker(String tag){
        DialogFragment fragment = new DatePicker();
        fragment.setTargetFragment(this, DATE_PICKER_FRAGMENT);
        fragment.show(getFragmentManager().beginTransaction(), tag);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        switch (reqCode){
            case DATE_PICKER_FRAGMENT:
                if(resCode == Activity.RESULT_OK){
                    // Retrieve data from DatePicker
                    Bundle bdl  = data.getExtras();
                    String tag  = bdl.getString("tag");
                    int year    = bdl.getInt("year");
                    int month   = bdl.getInt("month");
                    int day     = bdl.getInt("day");

                    // Create the String and update corresponding TextView
                    String date = year+ "-" +month+ "-" +day;
                    TextView tv = hmTextView.get(tag);
                    tv.setText(date);
                }
                break;
            case TIME_PICKER_FRAGMENT:
                if(resCode == Activity.RESULT_OK){
                    // Retrieve data from TimePicker
                    Bundle bdl  = data.getExtras();
                    String tag  = bdl.getString("tag");
                    int hour    = bdl.getInt("hour");
                    int minute  = bdl.getInt("minute");

                    // Create the String and update corresponding TextView
                    String time = hour+ " : " +minute+ " h";
                    TextView tv = hmTextView.get(tag);
                    tv.setText(time);
                }
                break;
            default:
                break;
        }

    }

    private void showNumberPicker(String title, final TextView field){
        // Create the dialog
        final Dialog np = new Dialog(TabSearchCar.this.getActivity());
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

    // Array received contains all the brand in array.getJSONObject(0)
    // For example, to get the first brand : array.getJSONObject(0).getString("1");
    public void onPostExecuteSearchBrand(JSONArray array){
        try {
            brands = new String[array.getJSONObject(0).length()];
            for(int i = 0 ; i < array.getJSONObject(0).length() ; i++) {
                JSONObject object = array.getJSONObject(0);
                brands[i] = object.getString(String.valueOf(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // length == 0, then there are no cars in DB.
        if(brands.length == 0)
            alert = buildDialog("No available car so far", brands, true).create();
        else
            alert = buildDialog("Pick up a brand", brands, true).create();
        alert.show();
    }

    /** @param isBrand : if true, then we setText of the brandTextField
     * if false : case of an exchange, user have to choose one of this car to exchange. */
    private AlertDialog.Builder buildDialog(String title, final String[] list, final boolean isBrand){
        AlertDialog.Builder brandDialog = new AlertDialog.Builder(TabSearchCar.this.getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = (View) inflater.inflate(R.layout.brand_dialog, null);
        brandDialog.setView(v);
        brandDialog.setTitle(title);
        ListView lvBrand = (ListView)v.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        lvBrand.setAdapter(adapter);
        lvBrand.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object clickedCar = parent.getItemAtPosition(position);
                        if (isBrand)
                            setBrandText(clickedCar.toString());
                        else
                            getIdOfSelectedCar(clickedCar.toString());
                    }
                }
        );
        return brandDialog;
    }

    private void getIdOfSelectedCar(String brandModel){
        String[] brandModelArray = brandModel.split(" ");
        int pos = -1;
        for(int i = 0 ; i < user.getCars().size() ; i++)
            if(user.getCars().get(i).getBrand().equals(brandModelArray[0]) && user.getCars().get(i).getModel().equals(brandModelArray[1]))
                pos = i;
        i.putExtra("idSelectedCar", user.getCars().get(pos).getId());
        alert.dismiss();
        startActivity(i);
    }

    private void setBrandText(String brand){
        alert.dismiss();
        tvBrandChoose.setText(brand);
    }

    /*Getters and Setters*/
    public ProgressDialog getSearchBrandCar() {
        return searchBrandCar;
    }

    public void setSearchBrandCar(ProgressDialog searchBrandCar) {
        this.searchBrandCar = searchBrandCar;
    }

    public ProgressDialog getSearchCorrespondingCar() {
        return searchCorrespondingCar;
    }

    public void setSearchCorrespondingCar(ProgressDialog searchCorrespondingCar) {
        this.searchCorrespondingCar = searchCorrespondingCar;
    }
}
