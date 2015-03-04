package com.example.celien.drivemycar.core;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomListPersonnalCar;
import com.example.celien.drivemycar.models.User;

public class ListPersonnalCars extends ActionBarActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_personnal_cars);
        init();
    }

    private void init(){

        // Get the User (Object) from Login page and send it to TabAccount tab.
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_personnal_cars, menu);
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
