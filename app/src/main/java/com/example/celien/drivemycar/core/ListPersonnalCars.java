package com.example.celien.drivemycar.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.CustomListPersonnalCar;
import com.example.celien.drivemycar.http.HttpAsync;
import com.example.celien.drivemycar.models.Car;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.utils.Action;

public class ListPersonnalCars extends ActionBarActivity {

    private User user;
    private ActionMode mActionMode;
    private Car selectedCarToDelete;
    private ProgressDialog deleteCar;

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

        // Set listener (short press)
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Car clickedCar = (Car) parent.getItemAtPosition(position);
                        launchIntentToModifyCar(user, getPositionCarInList(clickedCar));
                    }
                }
        );

        // Set listener (long press)
        lv.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if(mActionMode != null)
                            return false;

                        // Start the CAB using the actionModeCallBack defined below
                        mActionMode = ListPersonnalCars.this.startActionMode(actionModeCallBack);
                        view.setSelected(true);
                        selectedCarToDelete = (Car) parent.getItemAtPosition(position);
                        return true;
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

    private void deleteCar(){
        HttpAsync request = new HttpAsync(this);
        request.execute(Action.DELETE_CAR.toString());
    }

    public void onPostExecuteDeleteCar(Object resp){
        if((int) resp != 200)
            createAndShowResult("Error when deleting the car", "Retry", false);
        else{
            createAndShowResult("Car is succesfully deleted", "Ok", true);
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

    // Set the ActionMode.Callback method in order to implement a custiom bar menu
    private ActionMode.Callback actionModeCallBack = new ActionMode.Callback(){

        // Called when actionMode is create (id startActionMode() was called)
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the correct Menu
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_actions_car, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_delete_car:
                    deleteCar();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exit action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    /*Getters and Setters*/
    public Car getSelectedCarToDelete() {
        return selectedCarToDelete;
    }

    public ProgressDialog getProgressDialog() {
        return deleteCar;
    }

    public void setProgressDialog(ProgressDialog deleteCar) {
        this.deleteCar = deleteCar;
    }

    public User getUser() {
        return user;
    }

}
