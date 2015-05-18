package com.example.celien.drivemycar.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import java.util.Calendar;


public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
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
        // Create an Intent and send it back to TabSearchCar.onActivityResult.
        String tagRcvd = getTag();
        Intent i = new Intent();
        Bundle bdl = new Bundle();
        bdl.putString("tag", tagRcvd);
        bdl.putInt("hour", hourOfDay);
        bdl.putInt("minute", minute);
        i.putExtras(bdl);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }
}
