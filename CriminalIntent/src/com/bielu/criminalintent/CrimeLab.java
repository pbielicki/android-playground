package com.bielu.criminalintent;

import java.util.List;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {
  
  private Context mContext;
  private CrimeLab mCrimeLab;
  private List<Crime> mCrimes;

  private CrimeLab(Context context) {
    mContext = context;
    
    for (int i = 0; i < 100; i++) {
      mCrimes.add(new Crime(UUID.randomUUID(), "Crime #" + i));
    }
  }
  
  public CrimeLab get(Context context) {
    if (mCrimeLab == null) {
      mCrimeLab = new CrimeLab(context.getApplicationContext());
    }
    
    return mCrimeLab;
  }
  
  public List<Crime> getCrimes() {
    return mCrimes;
  }
  
  public Crime getCrime(UUID id) {
    for (Crime crime : mCrimes) {
      if (crime.getId().equals(id)) {
        return crime;
      }
    }
    
    return null;
  }

}
