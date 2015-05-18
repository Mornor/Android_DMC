package com.example.celien.drivemycar.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.celien.drivemycar.tabs.TabAccount;
import com.example.celien.drivemycar.tabs.TabOperations;
import com.example.celien.drivemycar.tabs.TabSearchCar;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    private CharSequence titles[];
    private int nbTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int nbTabs){
        super(fm);
        this.titles = titles;
        this.nbTabs = nbTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new TabAccount();
            case 1 :
                return new TabSearchCar();
            case 2 :
                return new TabOperations();
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
    }
