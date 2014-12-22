package com.bielu.gcmnotif;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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
import com.google.android.gms.common.api.StatusCreator;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmActivity extends Activity {

  static final String TAG = "GcmActivity";
  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  private static final String PROPERTY_REGISTERED = "registred";
  private static final String PROPERTY_REG_ID = "registration_id";
  private static final String PROPERTY_APP_VERSION = "appVersion";
  private static final String SENDER_ID = "999191256818";
  private TextView mTextView;
  private Button mSubmitButton;
  private GoogleCloudMessaging gcm;
  private String regid;
  private boolean registered = false;

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
      registered = isRegisteredOnServer(this);

      if (regid.isEmpty() || registered == false) {
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
        Log.w(TAG, "This device is not supported.");
      }
      return false;
    }
    return true;
  }
  
  private boolean isRegisteredOnServer(Context context) {
    return getPreferences(context).getBoolean(PROPERTY_REGISTERED, false);
  }

  private String getRegistrationId(Context context) {
    final SharedPreferences prefs = getPreferences(context);
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
        boolean registered = false;
        try {
          HttpEntity postData = new StringEntity("{\"" + PROPERTY_REG_ID + "\":\"" + regid + "\"}");
          request.setHeader("Content-Type", "application/json");
          request.setEntity(postData);
          HttpResponse response = client.execute(request);
          if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            if (out.toString().contains("\"status\":\"unable") == false
                && out.toString().contains("\"" + PROPERTY_REG_ID + "\":\"" + regid + "\"")) {
              
              registered = true;
            }
          } else {
            Log.d(TAG, "Unable to register on the server: " + response.toString());
          }
        } catch (IOException e) {
          Log.e(TAG, "Could not register on the server", e);
        }
        
        storeRegisteredOnServerFlag(GcmActivity.this, registered);
        return null;
      }
    }.execute(null, null, null);
  }
  
  private void storeRegisteredOnServerFlag(Context context, boolean registered) {
    final SharedPreferences prefs = getPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(PROPERTY_REGISTERED, registered);
    editor.commit();
  }

  private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getPreferences(context);
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

  private SharedPreferences getPreferences(Context context) {
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
    getMenuInflater().inflate(R.menu.gcm, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
