package com.bielu.criminalintent;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {

  private static final String PREFERENCES_HTTP_PASS = "http-pass";
  private static final String PREFERENCES_HTTP_USER = "http-user";
  private static final byte[] USER_KEY = new byte[] { 2, 5, 10, 3, 2, 6, 10, 2, 5, 10, 1 };
  private static final byte[] PASS_KEY = new byte[] { 9, 8, 4, 1, 2, 4, 8, 12, 15, 1, 2 };
  private static final int REQUEST_CREDENTIALS = 0;
  private static final String DIALOG_CREDENTIALS = "Credentials";
  private List<Crime> mCrimes;
  private boolean mSubtitleVisible;
  private String mUser;
  private String mPass;
  private Button mNewCrimeButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkRequiredPreferences();
    setHasOptionsMenu(true);
    getActivity().setTitle(R.string.crimes_title);
    
    mCrimes = CrimeLab.get(getActivity()).getCrimes();
    ArrayAdapter<Crime> adapter = new CrimeAdapter(mCrimes);
    setListAdapter(adapter);
    setRetainInstance(true);
    mSubtitleVisible = false;
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      checkRequiredPreferences();
      return;
    }
    
    if (requestCode == REQUEST_CREDENTIALS) {
      mUser = data.getStringExtra(CredentialsFragment.EXTRA_USER);
      mPass = data.getStringExtra(CredentialsFragment.EXTRA_PASS);
      
      if (mUser == null || mUser.trim().isEmpty() 
          || mPass == null || mPass.trim().isEmpty()) {
        
        checkRequiredPreferences();
        return;
      }
      
      SharedPreferences prefs = getPreferences();
      prefs.edit()
        .putString(PREFERENCES_HTTP_USER, encodePreference(mUser, USER_KEY))
        .putString(PREFERENCES_HTTP_PASS, encodePreference(mPass, PASS_KEY))
        .commit();
    } else {
      checkRequiredPreferences();
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_empty_list, container, false);
    mNewCrimeButton = (Button) v.findViewById(R.id.new_crime_button);
    mNewCrimeButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        newCrime();
      }
    });
    
    ListView listView = (ListView) v.findViewById(android.R.id.list);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      if (mSubtitleVisible) {
        getActivity().getActionBar().setSubtitle(R.string.subtitle);
      }
      
      listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
      listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
          return false;
        }
        
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
        
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
          mode.getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
          return true;
        }
        
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
          switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
              CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
              CrimeLab lab = CrimeLab.get(getActivity());
              for (int i = adapter.getCount() - 1; i >=0; i--) {
                if (getListView().isItemChecked(i)) {
                  lab.deleteCrime(adapter.getItem(i));
                }
              }
              mode.finish();
              adapter.notifyDataSetChanged();
              return true;
          }
          
          return false;
        }
        
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        }
      });
    } else {
      registerForContextMenu(listView);
    }
    return v;
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
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
    Crime crime = adapter.getItem(info.position);
    
    switch (item.getItemId()) {
      case R.id.menu_item_delete_crime:
        CrimeLab.get(getActivity()).deleteCrime(crime);
        adapter.notifyDataSetChanged();
        return true;
    }
    
    return super.onContextItemSelected(item);
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.fragment_crime_list, menu);
    MenuItem showSubtitleButton = menu.findItem(R.id.menu_item_show_subtitle);
    if (showSubtitleButton != null && mSubtitleVisible) {
      showSubtitleButton.setTitle(R.string.hide_subtitle);
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_item_new_crime:
        newCrime();
        return true;
        
      case R.id.menu_item_show_subtitle:
        if (mSubtitleVisible == false) {
          getActivity().getActionBar().setSubtitle(R.string.subtitle);
          item.setTitle(R.string.hide_subtitle);
          mSubtitleVisible = true;
        } else {
          getActivity().getActionBar().setSubtitle(null);
          item.setTitle(R.string.show_subtitle);
          mSubtitleVisible = false;
        }
        
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void newCrime() {
    Crime crime = new Crime();
    CrimeLab.get(getActivity()).addCrime(crime);
    Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
    intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
    startActivity(intent);
  }
  
  private String decodePreference(String string, byte[] userKey) {
    if (string == null) {
      return null;
    }
    return new String(Base64.decode(string.getBytes(), Base64.DEFAULT));
  }
  
  private String encodePreference(String string, byte[] userKey) {
    if (string == null) {
      return null;
    }
    return Base64.encodeToString(string.getBytes(), Base64.DEFAULT);
  }
  
  private void checkRequiredPreferences() {
    SharedPreferences prefs = getPreferences();
    String user = decodePreference(prefs.getString(PREFERENCES_HTTP_USER, null), USER_KEY);
    String pass = decodePreference(prefs.getString(PREFERENCES_HTTP_PASS, null), PASS_KEY);
    
    if (user == null || pass == null) {
      FragmentManager fm = getActivity().getSupportFragmentManager();
      CredentialsFragment dialog = CredentialsFragment.newInstance(user, pass);
      dialog.setTargetFragment(CrimeListFragment.this, REQUEST_CREDENTIALS);
      dialog.setCancelable(false);
      dialog.show(fm, DIALOG_CREDENTIALS);
    }
  }
  
  private SharedPreferences getPreferences() {
    return getActivity().getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
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
