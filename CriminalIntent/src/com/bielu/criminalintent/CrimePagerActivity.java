package com.bielu.criminalintent;

import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrimePagerActivity extends FragmentActivity {
  
  private ViewPager mViewPager;
  private List<Crime> mCrimes;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewPager = new ViewPager(this);
    mViewPager.setId(R.id.viewPager);
    setContentView(mViewPager);
    
    mCrimes = CrimeLab.get(this).getCrimes();
    
    FragmentManager fm = getSupportFragmentManager();
    mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
      @Override
      public int getCount() {
        return mCrimes.size();
      }
      
      @Override
      public Fragment getItem(int index) {
        return CrimeFragment.newInstance(mCrimes.get(index).getId());
      }
    });
    
    UUID id = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
    for (int i = 0; i < mCrimes.size(); i++) {
      Crime c = mCrimes.get(i);
      if (id.equals(c.getId())) {
        mViewPager.setCurrentItem(i);
        setTitle(c.getTitle());
        break;
      }
    }
    
    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageSelected(int pos) {
        Crime c = mCrimes.get(pos);
        if (c.getTitle() != null) {
          setTitle(c.getTitle());
        }
      }
      
      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
      }
      
      @Override
      public void onPageScrollStateChanged(int arg0) {
      }
    });
  }

}
