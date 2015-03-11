package com.example.celien.drivemycar.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.example.celien.drivemycar.R;

import java.util.Calendar;


public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Use this instance of the interface to send action events to the caller's activity.
    DatePickerListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c    = Calendar.getInstance();
        int year            = c.get(Calendar.YEAR);
        int month           = c.get(Calendar.MONTH);
        int day             = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mListener.onDateSelected(year, monthOfYear, dayOfMonth);
    }

    /* Interface is used to send callback event to the caller activity.
     * That means the caller activity have to implements this interface */
    public interface DatePickerListener{
        public void onDateSelected(int year, int month, int day);
    }

    // Override to instantiate the DatePickerListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (DatePickerListener) activity; // Instantiate it so that we can send event to the activity's caller.
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +" must implements DatePickerListener");
        }

    }

}
