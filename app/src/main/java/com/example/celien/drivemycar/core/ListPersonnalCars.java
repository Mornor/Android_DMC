package com.example.celien.drivemycar.core;

import android.content.Intent;
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
        ListAdapter adapter = new CustomListPersonnalCar(this, user.getCars());
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
                launchIntent();
                break;
            case R.id.menuSettings:
                break;
            case R.id.menuExit:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchIntent(){
        Intent i = new Intent(this, AddCar.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
    }


}
