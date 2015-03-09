package com.example.celien.drivemycar.core;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomListPersonnalCar;
import com.example.celien.drivemycar.models.Car;
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

        // Get the User (Object).
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;

        // Set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personnal Car(s)");

        // Create and set the Custom list adapter
        ListAdapter adapter = new CustomListPersonnalCar(this, user.getCars());
        ListView lv = (ListView)findViewById(R.id.lvCars);
        lv.setAdapter(adapter);

        // Set listener
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Car clickedCar = (Car) parent.getItemAtPosition(position);
                        launchIntentToModifyCar(user, getPositionCarInList(clickedCar));
                    }
                }
        );
    }

    // Return position of the car in the list, -1 if error.
    private int getPositionCarInList(Car car){
        int carPosition = -1;
        for(int i = 0 ; i < user.getCars().size() ; i++){
            if(user.getCars().get(i).equals(car))
                carPosition = i;
        }
        return carPosition;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_personnal_cars, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.menuAddCar:
                launchIntentToAddCar();
                break;
            case R.id.menuSettings:
                break;
            case R.id.menuExit:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchIntentToAddCar(){
        Intent i = new Intent(this, AddCar.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }

    private void launchIntentToModifyCar(User user, int posCar){
        Intent i = new Intent(this, ModifyCar.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putInt("posCar", posCar);
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }


}
