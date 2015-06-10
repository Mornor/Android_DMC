package com.example.celien.drivemycar.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import java.util.Calendar;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c    = Calendar.getInstance();
        int year            = c.get(Calendar.YEAR);
        int month           = c.get(Calendar.MONTH); // because January is considered as 0.
        int day             = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Create an Intent and send it back to TabSearchCar.onActivityResult.
        String tagRcvd = getTag();
        Intent i = new Intent();
        Bundle bdl = new Bundle();
        bdl.putString("tag", tagRcvd);
        bdl.putInt("year", year);
        bdl.putInt("month", monthOfYear + 1);
        bdl.putInt("day", dayOfMonth);
        i.putExtras(bdl);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }

}
