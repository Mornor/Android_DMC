package com.example.celien.drivemycar.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.core.Home;
import com.example.celien.drivemycar.core.RequestReceived;
import com.example.celien.drivemycar.models.User;


public class TabOperations extends Fragment {

    private User user;

    private Button btnRequests;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_operations, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){

        // Retrieve the user from tab hoster (Home)
        Home homeActivity = (Home)getActivity();
        user = homeActivity.getUser();

        btnRequests = (Button)v.findViewById(R.id.btnCheckRequests);

        btnRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lauchIntentToRequestReceived();
            }
        });
    }

    private void lauchIntentToRequestReceived(){
        Intent i = new Intent(this.getActivity(), RequestReceived.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        i.putExtras(bundle);
        startActivity(i);
    }

}
