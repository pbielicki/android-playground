package com.bielu.criminalintent;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {

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
    Intent i = new Intent(getActivity(), CrimePagerActivity.class);
    i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
    startActivity(i);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
  }

  /**
   * Crime Array Adapter
   */
  public class CrimeAdapter extends ArrayAdapter<Crime> {

    public CrimeAdapter(List<Crime> crimes) {
      super(getActivity(), 0, crimes);
    }
    
    @SuppressLint("InflateParams") @Override
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
