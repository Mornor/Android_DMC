package com.example.celien.drivemycar.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.celien.drivemycar.tabs.TabAccount;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    private CharSequence titles[];
    private int nbTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int nbTabs){
        super(fm);
        this.titles     = titles;
        this.nbTabs     = nbTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                TabAccount tabAccount = new TabAccount();
                return tabAccount;
            case 1 :
                TabAccount tabAccount1 = new TabAccount();
                return tabAccount1;
            case 2 :
                TabAccount tabAccount2 = new TabAccount();
                return tabAccount2;
        }

        return null; // Should never occurs
    }

    @Override
    public int getCount() {
        return nbTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


    /*Getters and Setters*/
    public CharSequence[] getTitles() {
        return titles;
    }

    public void setTitles(CharSequence[] titles) {
        this.titles = titles;
    }

    public int getNbTabs() {
        return nbTabs;
    }

    public void setNbTabs(int nbTabs) {
        this.nbTabs = nbTabs;
    }
}
