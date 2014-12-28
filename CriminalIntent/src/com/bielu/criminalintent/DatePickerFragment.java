package com.bielu.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {
  
  public static final String EXTRA_DATE = "DatePickerFragment.Date";
  private Date mDate;
  
  public static DatePickerFragment newInstance(Date date) {
    Bundle args = new Bundle();
    args.putSerializable(EXTRA_DATE, date);
    
    DatePickerFragment fragment = new DatePickerFragment();
    fragment.setArguments(args);
    
    return fragment;
  }


  @SuppressLint("InflateParams") @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
    Calendar cal = Calendar.getInstance();
    cal.setTime(mDate);
    
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    final int hour = cal.get(Calendar.HOUR_OF_DAY);
    final int minute = cal.get(Calendar.MINUTE);
    View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
    
    DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
    datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
      @Override
      public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDate = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute).getTime();
        getArguments().putSerializable(EXTRA_DATE, mDate);
      }
    });
    
    return new AlertDialog.Builder(getActivity())
        .setView(v)
        .setTitle(R.string.date_picker_title)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            sendResult(Activity.RESULT_OK);
          }
        })
        .create();
  }
  
  private void sendResult(int result) {
    if (getTargetFragment() == null) {
      return;
    }
    
    Intent i = new Intent();
    i.putExtra(EXTRA_DATE, mDate);
    getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
  }
}
