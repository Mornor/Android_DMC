package com.example.celien.drivemycar.core;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.ViewPagerAdapter;
import com.example.celien.drivemycar.googletabs.SlidingTabLayout;
import com.example.celien.drivemycar.models.User;
import com.example.celien.drivemycar.tabs.TabAccount;

/**
 * Main page of the app, with sliding tabs.
 */
public class Home extends ActionBarActivity{

    private Toolbar toolbar;
    private ViewPagerAdapter pagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private CharSequence titles[];
    private int nbTabs;
    private String username; // From login page.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get the username from Login or from CarSettings
        /*Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            this.username = bundle.getString("username");*/

        // Get the User (Object) from Login page
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.username = currentUser.getUsername();

        init();

        // Send username to TabAccount
        /*Bundle bundleToTabAccount = new Bundle();
        bundleToTabAccount.putString("username", username);
        TabAccount tab = new TabAccount();
        tab.setArguments(bundle);*/
    }

    private void init(){
        // Init parameters
        nbTabs = 3;
        titles = new CharSequence[]{"Account", "Manage", "Cars"};
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, nbTabs);
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        tabs = (SlidingTabLayout)findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    /*Getters and Setters*/
    public String getUsername(){
        return username;
    }
}
