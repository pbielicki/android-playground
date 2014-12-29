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
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.bielu.criminalintent.protobuf.Crimes.CrimeList;
import com.google.protobuf.nano.MessageNano;

public class CrimeLab {
  
  private static final String APPLICATION_X_PROTOBUF = "application/x-protobuf";
  private static final String HTTP_CRIME_URL = "http://playground.bielu.com/rest/crime";
  private static final String TAG = "CriminalIntent";
  private static CrimeLab mCrimeLab;
  private List<Crime> mCrimes;
  private AndroidHttpClient mHttpClient;
  private SimpleDateFormat mDateFormat;

  private CrimeLab(Context context) {
    mHttpClient = AndroidHttpClient.newInstance(TAG);
    mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    try {
      mCrimes = new AsyncTask<Void, Void, List<Crime>>() {
        @Override
        protected List<Crime> doInBackground(Void... params) {
          List<Crime> result = new ArrayList<>();
          HttpUriRequest request = new HttpGet(HTTP_CRIME_URL);
          request.setHeader("Accept", APPLICATION_X_PROTOBUF);
          try {
            HttpResponse response = mHttpClient.execute(request);
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
              crime.setDate(mDateFormat.parse(c.date));
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

  public void addCrime(final Crime crime) {
    mCrimes.add(crime);
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        HttpPost request = new HttpPost(HTTP_CRIME_URL);
        request.setHeader("Accept", APPLICATION_X_PROTOBUF);
        request.setHeader("Content-Type", APPLICATION_X_PROTOBUF);
        CrimeList.Crime c = new CrimeList.Crime();
        c.date = mDateFormat.format(crime.getDate());
        c.title = crime.getTitle();
        c.solved = crime.isSolved();
        c.uuid = crime.getId().toString();
        request.setEntity(new ByteArrayEntity(MessageNano.toByteArray(c)));
        try {
          HttpResponse response = mHttpClient.execute(request);
          if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            // do something?
          }
        } catch (IOException e) {
          Log.e(TAG, "Unable to parse response", e);
        }
        return null;
      }
    }.execute(null, null);
  }

}
