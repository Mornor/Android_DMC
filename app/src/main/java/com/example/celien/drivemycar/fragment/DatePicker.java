package com.example.celien.drivemycar.fragment;

import android.app.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.celien.drivemycar.R;
import com.example.celien.drivemycar.tabs.TabSearchCar;

import java.util.Calendar;


public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
        Intent i = new Intent();
        Bundle bdl = new Bundle();
        bdl.putInt("month", monthOfYear);
        i.putExtras(bdl);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }

}
