package com.bielu.gcmnotif;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmActivity extends Activity {

  static final String TAG = "GcmActivity";
  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  private static final String PROPERTY_REG_ID = "registration_id";
  private static final String PROPERTY_APP_VERSION = "appVersion";
  private static final String SENDER_ID = "999191256818";
  private TextView mTextView;
  private Button mSubmitButton;
  private GoogleCloudMessaging gcm;
  private String regid;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_gcm);
    mTextView = (TextView) findViewById(R.id.text_view);
    mSubmitButton = (Button) findViewById(R.id.submit_button);

    // Check device for Play Services APK.
    if (checkPlayServices()) {
      gcm = GoogleCloudMessaging.getInstance(this);
      
      //TODO: check if registration on the server side was successful
      // if not, try to register every time we pass here
      regid = getRegistrationId(this);

      if (regid.isEmpty()) {
        registerInBackground();
      }
    } else {
      Log.i(TAG, "No valid Google Play Services APK found.");
    }
  }

  /**
   * Check the device to make sure it has the Google Play Services APK. If it
   * doesn't, display a dialog that allows users to download the APK from the
   * Google Play Store or enable it in the device's system settings.
   */
  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Log.i(TAG, "This device is not supported.");
      }
      return false;
    }
    return true;
  }

  private String getRegistrationId(Context context) {
    final SharedPreferences prefs = getGCMPreferences(context);
    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.i(TAG, "Registration not found.");
      return "";
    }
    // Check if app was updated; if so, it must clear the registration ID
    // since the existing regID is not guaranteed to work with the new
    // app version.
    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    int currentVersion = getAppVersion(context);
    if (registeredVersion != currentVersion) {
      Log.i(TAG, "App version changed.");
      return "";
    }
    return registrationId;
  }

  private void registerInBackground() {
    new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        String msg = "";
        try {
          if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(GcmActivity.this);
          }
          regid = gcm.register(SENDER_ID);
          msg = "Device registered, registration ID=" + regid;
          sendRegistrationIdToBackend();
          storeRegistrationId(GcmActivity.this, regid);
        } catch (IOException ex) {
          msg = "Error :" + ex.getMessage();
        }
        return msg;
      }

      @Override
      protected void onPostExecute(String msg) {
        mTextView.append(msg + "\n");
      }
    }.execute(null, null, null);
  }

  private void sendRegistrationIdToBackend() {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        HttpClient client = AndroidHttpClient.newInstance("Android");
        HttpPost request = new HttpPost("http://playground-pbielicki.rhcloud.com/rest/register");
        try {
          HttpEntity postData = new StringEntity("{\"" + PROPERTY_REG_ID + "\":\"" + regid + "\"}");
          request.setHeader("Content-Type", "application/json");
          request.setEntity(postData);
          HttpResponse response = client.execute(request);
          // TODO: check the response
          // if success add this to the preferences
          //Log.i(response.getEntity().);
        } catch (IOException e) {
          Log.e("Could not register on the server", e.getMessage(), e);
        }
        return null;
      }
    }.execute(null, null, null);
  }

  private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getGCMPreferences(context);
    int appVersion = getAppVersion(context);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PROPERTY_REG_ID, regId);
    editor.putInt(PROPERTY_APP_VERSION, appVersion);
    editor.commit();
  }

  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

  /**
   * @return Application's {@code SharedPreferences}.
   */
  private SharedPreferences getGCMPreferences(Context context) {
    // This sample app persists the registration ID in shared preferences, but
    // how you store the regID in your app is up to you.
    return getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (checkPlayServices()) {
      mTextView.setText("Play Services are available\n");
    } else {
      mTextView.setText("WARNING: Play Services are unavailable\n");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.gcm, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
