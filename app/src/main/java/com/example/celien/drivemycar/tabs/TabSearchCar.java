package com.example.celien.drivemycar.tabs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.http.HttpAsyncJson;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TabSearchCar extends Fragment{

    private User user;

    // Fragment variables
    private EditText etBrand;
    private Spinner spEnergy;
    private TextView tvConsoFuel;
    private EditText etNbSits;
    private TextView dateFrom;
    private TextView timeFrom;
    private TextView dateTo;
    private TextView timeTo;

    // Usefull Progress Dialog
    private ProgressDialog searchBrandCar;
    private ProgressDialog searchCorrespondingCar;

    // Usefull variables
    private String[] brand;
    private AlertDialog alert;

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
        etBrand     = (EditText)v.findViewById(R.id.etBrand);
        spEnergy    = (Spinner)v.findViewById(R.id.spFuelList);
        tvConsoFuel = (TextView)v.findViewById(R.id.tvMaxConsFuel);
        etNbSits    = (EditText)v.findViewById(R.id.etNbSits);
        dateFrom    = (TextView)v.findViewById(R.id.tvPickDateFrom);
        timeFrom    = (TextView)v.findViewById(R.id.tvChooseTimeFrom);
        dateTo      = (TextView)v.findViewById(R.id.tvPickDateTo);
        timeTo      = (TextView)v.findViewById(R.id.tvChooseTimeTo);
    }

    private void setListeners(){
        etBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncJson request = new HttpAsyncJson(TabSearchCar.this);
                request.execute(Action.GET_BRAND.toString());
            }
        });

        tvConsoFuel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showNumberPicker("Set max. consumption", tvConsoFuel);
            }
        });

        etNbSits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker("Set wished number of sits", etNbSits);
            }
        });

        dateFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        timeFrom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        timeTo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

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
            Log.d("Brand size tab", String.valueOf(array.getJSONObject(0).length()));
            brand = new String[array.getJSONObject(0).length()];
            for(int i = 0 ; i < array.getJSONObject(0).length() ; i++) {
                JSONObject object = array.getJSONObject(0);
                brand[i] = object.getString(String.valueOf(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alert = buildBrandDialog().create();
        alert.show();
    }

    private AlertDialog.Builder buildBrandDialog(){
        AlertDialog.Builder brandDialog = new AlertDialog.Builder(TabSearchCar.this.getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = (View) inflater.inflate(R.layout.brand_dialog, null);
        brandDialog.setView(v);
        ListView lvBrand = (ListView)v.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, brand);
        lvBrand.setAdapter(adapter);
        //brandDialog.show();
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
        etBrand.setText(brand);
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
