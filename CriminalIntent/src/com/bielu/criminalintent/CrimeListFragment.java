package com.bielu.criminalintent;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {

  private static final String TAG = "CrimeListFragment";
  private List<Crime> mCrimes;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivity().setTitle(R.string.crimes_title);
    mCrimes = CrimeLab.get(getActivity()).getCrimes();

    ArrayAdapter<Crime> adapter = new CrimeAdapter(mCrimes);
    setListAdapter(adapter);
  }
  
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Crime c = (Crime)getListAdapter().getItem(position);
    Log.d(TAG, c.getTitle() + " was clicked");
  }

  /**
   * Crime Array Adapter
   */
  public class CrimeAdapter extends ArrayAdapter<Crime> {

    public CrimeAdapter(List<Crime> crimes) {
      super(getActivity(), 0, crimes);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
      }
      
      Crime c = getItem(position);
      
      TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
      titleTextView.setText(c.getTitle());
      
      TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
      dateTextView.setText(c.getDate().toString());
      
      CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
      solvedCheckBox.setChecked(c.isSolved());
      
      return convertView;
    }

  }
  
  
}
