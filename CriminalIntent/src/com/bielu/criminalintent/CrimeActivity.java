package com.bielu.criminalintent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class CrimeActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crime);
    setTitle(R.string.title_activity_crime);
    
    FragmentManager fm = getFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.fragment_container);
    
    if (fragment == null) {
      fragment = new CrimeFragment();
      fm.beginTransaction()
        .add(R.id.fragment_container, fragment)
        .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.crime, menu);
    return true;
  }

}
