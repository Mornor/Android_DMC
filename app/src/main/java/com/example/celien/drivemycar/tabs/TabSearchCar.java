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
import android.widget.TextView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.fragment.DatePicker;
import com.example.celien.drivemycar.fragment.TimePicker;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private String mileage;

    // Create a HashMap to get an easy access on Date and Time TextView (efficiency matters)
    private HashMap<String, TextView> hmTextView;

    // Calls variable (usefull for the DatePicker and TimePicker)
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
        btnSearch       = (Button)v.findViewById(R.id.btnSearch);

        // Create a HashMap to get an easy access on Date and Time TextView (efficiency matters)
        hmTextView = new HashMap<>();
        hmTextView.put("dateFrom", dateFrom);
        hmTextView.put("timeFrom", timeFrom);
        hmTextView.put("dateTo", dateTo);
        hmTextView.put("timeTo", timeTo);
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
                    makeRequest();
            }
        });
    }

    // If we arrived there, we already know that period's stuff and brand are picked up, we can now send request to the server.
    private void makeRequest(){

    }

    private boolean checkFields(){
        boolean result = false;
        // Search is only possible if Date, Time and Brand are picked up.
        if (brand.isEmpty())
            tvBrandChoose.setTextColor(Color.RED);
        if(dateFromStr.isEmpty())
            dateFrom.setTextColor(Color.RED);
        if(timeFromStr.isEmpty())
            timeFrom.setTextColor(Color.RED);
        if(dateToStr.isEmpty())
            dateTo.setTextColor(Color.RED);
        if(timeToStr.isEmpty())
            timeTo.setTextColor(Color.RED);
        if (!(brand.isEmpty() || dateFromStr.isEmpty() || timeFromStr.isEmpty() || timeToStr.isEmpty()))
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
                    String date = day+ "/" +month+ "/" +year;
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

    // Array receive contains all the brand in array.getJSONObject(0)
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
            alert = buildBrandDialog("No available car so far").create();
        else
            alert = buildBrandDialog("Pick up a brand").create();
        alert.show();
    }

    private AlertDialog.Builder buildBrandDialog(String title){
        AlertDialog.Builder brandDialog = new AlertDialog.Builder(TabSearchCar.this.getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = (View) inflater.inflate(R.layout.brand_dialog, null);
        brandDialog.setView(v);
        brandDialog.setTitle(title);
        ListView lvBrand = (ListView)v.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, brands);
        lvBrand.setAdapter(adapter);
        lvBrand.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object clickedCar = parent.getItemAtPosition(position);
                        setBrandText(clickedCar.toString());
                    }
                }
        );
        return brandDialog;
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
