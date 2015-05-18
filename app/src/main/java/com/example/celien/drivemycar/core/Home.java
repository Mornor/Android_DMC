package com.example.celien.drivemycar.core;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.adapter.ViewPagerAdapter;
import com.example.celien.drivemycar.googletabs.SlidingTabLayout;
import com.example.celien.drivemycar.models.User;

/** Main page of the app, host sliding tabs. */
public class Home extends ActionBarActivity {

    private User user;
    private int tabTopOpen; // Come from NotificationUser

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabTopOpen = -1;

        // Get the User (Object)
        User currentUser = (User)getIntent().getParcelableExtra("user");
        if(currentUser != null)
            this.user = currentUser;

        // Get the tab to open if there is one, and if there is, get the JSONArray of current's user notification
        Bundle data = getIntent().getExtras();
        if(data != null){
            String tab = data.getString("tabToOpen");
            if(tab != null)
                tabTopOpen = Integer.valueOf(tab);
        }

        init();
    }

    private void init(){

        // Init parameters
        int nbTabs = 3;
        CharSequence[] titles           = new CharSequence[]{"Account", "Rent", "Transaction"};
        Toolbar toolbar                 = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ViewPagerAdapter pagerAdapter   = new ViewPagerAdapter(getSupportFragmentManager(), titles, nbTabs);
        ViewPager pager                 = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        // Go to right tab
        if(tabTopOpen != -1)
            pager.setCurrentItem(tabTopOpen);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
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
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
