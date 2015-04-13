package com.example.celien.drivemycar.tabs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.utils.Tools;

import java.util.HashMap;

public class TabOperations extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

        // Get the notification data from SharedPref and test is there is indeed something.
        HashMap<String, String> notificationData = Tools.getNotificationData(getActivity().getSharedPreferences("notifInfo", Context.MODE_PRIVATE));
        if(notificationData.get("userSource").equals(""))
            Log.e(getClass().getName(), "There is nothing in SharedPref.notifInfo");
    }
}
