package com.example.celien.drivemycar.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.tabs.TabSearchCar;

import java.util.Calendar;


public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Retrieve current time to show in the time picker dialog
        final Calendar c    = Calendar.getInstance();
        int hour            = c.get(Calendar.HOUR_OF_DAY);
        int minute          = c.get(Calendar.MINUTE);

        // Create new instance of picker and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

    }
}
