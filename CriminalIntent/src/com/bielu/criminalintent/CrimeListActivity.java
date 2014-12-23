package com.bielu.criminalintent;

import android.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

  @Override
  protected Fragment createFragment() {
    return new CrimeListFragment();
  }

}
