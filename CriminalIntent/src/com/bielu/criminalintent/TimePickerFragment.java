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
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {
  
  private Date mDate;
  
  public static TimePickerFragment newInstance(Date date) {
    Bundle args = new Bundle();
    args.putSerializable(DatePickerFragment.EXTRA_DATE, date);
    
    TimePickerFragment fragment = new TimePickerFragment();
    fragment.setArguments(args);
    
    return fragment;
  }


  @SuppressLint("InflateParams") @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mDate = (Date) getArguments().getSerializable(DatePickerFragment.EXTRA_DATE);
    Calendar cal = Calendar.getInstance();
    cal.setTime(mDate);
    
    final int year = cal.get(Calendar.YEAR);
    final int month = cal.get(Calendar.MONTH);
    final int day = cal.get(Calendar.DAY_OF_MONTH);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
    
    TimePicker timePicker = (TimePicker) v.findViewById(R.id.dialog_time_timePicker);
    timePicker.setIs24HourView(true);
    timePicker.setCurrentHour(hour);
    timePicker.setCurrentMinute(minute);
    timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
      
      @Override
      public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mDate = new GregorianCalendar(year, month, day, hourOfDay, minute).getTime();
        getArguments().putSerializable(DatePickerFragment.EXTRA_DATE, mDate);
      }
    });
    
    return new AlertDialog.Builder(getActivity())
        .setView(v)
        .setTitle(R.string.time_picker_title)
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
    i.putExtra(DatePickerFragment.EXTRA_DATE, mDate);
    getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
  }
}
