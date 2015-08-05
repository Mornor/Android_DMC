package be.iba.carswop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import be.iba.carswop.R;
import be.iba.carswop.tabs.TabAccount;
import be.iba.carswop.tabs.TabOperations;
import be.iba.carswop.tabs.TabSearchCar;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[];
    private int nbTabs;
    private Context currentContext;
    private int[] imageResId = {R.drawable.icon_myaccount, R.drawable.icon_rent, R.drawable.icon_transaction};


    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int nbTabs, Context context) {
        super(fm);
        this.titles = titles;
        this.nbTabs = nbTabs;
        this.currentContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabAccount();
            case 1:
                return new TabSearchCar();
            case 2:
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
        Drawable image = currentContext.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, 50, 50);
        SpannableString sb = new SpannableString(" \n "+titles[position]+" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
        // return titles[position];
    }
}
