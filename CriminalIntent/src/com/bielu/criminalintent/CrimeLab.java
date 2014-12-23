package com.bielu.criminalintent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {
  
  private static CrimeLab mCrimeLab;
  private List<Crime> mCrimes;

  private CrimeLab(Context context) {
    mCrimes = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      mCrimes.add(new Crime(UUID.randomUUID(), "Crime #" + i, i % 2 == 0));
    }
  }
  
  public static CrimeLab get(Context context) {
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
