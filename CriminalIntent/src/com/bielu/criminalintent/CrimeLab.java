package com.bielu.criminalintent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.bielu.criminalintent.protobuf.Crimes.CrimeList;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class CrimeLab {
  
  private static final String TAG = "CriminalIntent";
  private static CrimeLab mCrimeLab;
  private List<Crime> mCrimes;

  private CrimeLab(Context context) {
    try {
      final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
      mCrimes = new AsyncTask<Void, Void, List<Crime>>() {
        @Override
        protected List<Crime> doInBackground(Void... params) {
          List<Crime> result = new ArrayList<>();
          HttpClient client = AndroidHttpClient.newInstance(TAG);
          HttpUriRequest request = new HttpGet("http://playground.bielu.com/rest/crime");
          request.setHeader("Accept", "application/x-protobuf");
          try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte b[] = new byte[1024];
            int count = 0;
            while ((count = in.read(b)) != -1) {
              out.write(b, 0, count);
            }
            in.close();
            CrimeList list = com.bielu.criminalintent.protobuf.Crimes.CrimeList.parseFrom(out.toByteArray());
            int i = 0;
            for (com.bielu.criminalintent.protobuf.Crimes.CrimeList.Crime c : list.crimes) {
              Crime crime = new Crime(UUID.fromString(c.uuid), c.title, c.solved);
              crime.setDate(format.parse(c.date));
              result.add(crime);
              if (i++ > 2) {
                break;
              }
            }
          } catch (IOException | ParseException e) {
            Log.e(TAG, "Unable to parse response", e);
          }
          return result;
        }
      }.execute(null, null).get();
    } catch (InterruptedException | ExecutionException e) {
      Log.e(TAG, "Unable to retrieve crimes from the server", e);
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
    
    return new Crime(null, "error", false);
  }

  public void addCrime(Crime crime) {
    mCrimes.add(crime);
  }

}
