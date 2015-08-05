package be.iba.carswop.tabs;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import be.iba.carswop.R;
import be.iba.carswop.core.AddCar;
import be.iba.carswop.core.Home;
import be.iba.carswop.core.ListPersonnalCars;
import be.iba.carswop.core.Login;
import be.iba.carswop.models.User;
import be.iba.carswop.utils.Tools;

public class TabAccount extends Fragment {

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_account, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View v){
        TextView tvUsername     = (TextView) v.findViewById(R.id.tvUsername);
        TextView tvMail         = (TextView) v.findViewById(R.id.tvMail);
        TextView tvMyCars       = (TextView) v.findViewById(R.id.tvMyCars);
        TextView tvSettings     = (TextView) v.findViewById(R.id.tvSettings);
        TextView tvLogout       = (TextView) v.findViewById(R.id.tvLogout);
        ImageView ivMyCars      = (ImageView) v.findViewById(R.id.ivMyCars);
        ImageView ivSettings    = (ImageView) v.findViewById(R.id.ivSettings);
        ImageView ivLogout      = (ImageView) v.findViewById(R.id.ivLogout);

        Home homeActivity = (Home)getActivity();
        user = homeActivity.getUser();
        tvUsername.setText(user.getUsername());
        tvMail.setText(user.getEmail());


        tvMyCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntentToCarSettings();
            }
        });

        ivMyCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntentToCarSettings();
            }
        });

        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    // Logout the user (clear SharedPref) and go to Login activity.
    private void logout(){
        Tools.clearSharedPrefUserLoginData(this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE));
        Tools.clearSharedPrefUserData(this.getActivity().getSharedPreferences("notifInfo", Context.MODE_PRIVATE));
        getActivity().finish();
        Intent i = new Intent(this.getActivity(), Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    // Launch the correct intent depends of the users have cars or not.
    private void launchIntentToCarSettings(){

        // If user have no car
        if(user.getCars().isEmpty()){
            Intent i = new Intent(getActivity(), AddCar.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            i.putExtras(bundle);
            startActivity(i);
        }else{
            Intent i = new Intent(getActivity(), ListPersonnalCars.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            i.putExtras(bundle);
            startActivity(i);
        }

    }
}
