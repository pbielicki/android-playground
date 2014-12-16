package com.bielu.geoquiz;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bielu.protobuf.GeoDataNano.GeoData;
import com.sun.xml.fastinfoset.sax.SAXDocumentParser;

public class QuizActivity extends Activity {
  
  private static final String TAG = "QuizActivity";
  private Button mTrueButton;
  private Button mFalseButton;
  private Button mNextButton;
  private Button mCheatButton;
  private TextView mQuestionText;
  private int currentIdx = 0;
  private boolean mIsCheater;
  private GeoData mGeoData = null;
  private AsyncTask<Void, Void, GeoData> mGeoDataTask;
  
  private static TrueFalse[] question = new TrueFalse[] {
      new TrueFalse(R.string.question_one, true),
      new TrueFalse(R.string.question_two, true),
      new TrueFalse(R.string.question_three, false),
      new TrueFalse(R.string.question_four, false),
      new TrueFalse(R.string.question_five, true),
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mGeoDataTask = new AsyncTask<Void, Void, GeoData>() {
      @Override
      protected GeoData doInBackground(Void... params) {
        HttpClient client = AndroidHttpClient.newInstance(TAG);
        HttpUriRequest request = new HttpGet("http://playground-pbielicki.rhcloud.com/rest/geoIp/172.20.10.40");
        request.setHeader("Accept", "application/xml+fastinfoset");
        try {
          HttpResponse response = client.execute(request);
          HttpEntity entity = response.getEntity();
          InputStream in = entity.getContent();
          
          final GeoData data = new GeoData();
          XMLReader saxReader = new SAXDocumentParser();
          saxReader.setContentHandler(new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
              
              for (int i = 0; i < attributes.getLength(); i++) {
                switch (attributes.getLocalName(i).toLowerCase(Locale.ENGLISH)) {
                  case "country":
                    data.country = attributes.getValue(i);
                    break;
                  case "city":
                    data.city = attributes.getValue(i);
                    break;
                  case "latitude":
                    data.latitude = attributes.getValue(i);
                    break;
                  case "longitude":
                    data.longitude = attributes.getValue(i);
                    break;
                }
              }
            }
          });
          
          saxReader.parse(new InputSource(in));
          
          return data;          
        } catch (IOException e) {
          Log.w(TAG, e);
        } catch (SAXException e) {
          Log.w(TAG, e);
        } catch (RuntimeException e) {
          Log.e(TAG, "Error", e);
        } finally {
          client.getConnectionManager().closeExpiredConnections();
          client.getConnectionManager().closeIdleConnections(1, TimeUnit.MINUTES);
        }
        
        return null;
      }
    }.execute();
    
    if (savedInstanceState != null) {
      currentIdx = savedInstanceState.getInt(TAG, 0);
    }
    
    Log.d(TAG, "onCreate");
    setContentView(R.layout.activity_quiz);
    mTrueButton = (Button) findViewById(R.id.true_button);
    mFalseButton = (Button) findViewById(R.id.false_button);
    mNextButton = (Button) findViewById(R.id.next_button);
    mCheatButton = (Button) findViewById(R.id.cheat_button);
    mQuestionText = (TextView) findViewById(R.id.question_text);
    mQuestionText.setText(question[currentIdx].getQuestion());
    
    mTrueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAnswer(true);
      }
    });

    mFalseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAnswer(false);
      }
    });
    
    mNextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mIsCheater = false;
        currentIdx = (currentIdx + 1) % question.length;
        mQuestionText.setText(question[currentIdx].getQuestion());
        
        if (mGeoData == null) {
          try {
            mGeoData = mGeoDataTask.get(1, TimeUnit.MILLISECONDS);
          } catch (Exception e) {
            Log.w(TAG, e);
          }
        }

        if (mGeoData != null) {
          Toast.makeText(QuizActivity.this, QuizActivity.this.toString(mGeoData), Toast.LENGTH_SHORT).show();
        }
      }
    });
    
    mCheatButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(QuizActivity.this, CheatActivity.class);
        i.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, question[currentIdx].isTrueQuestion());
        startActivityForResult(i, 0);
      }
    });
  }
  
  private String toString(GeoData gd) {
    return gd.city + ", " + gd.country + ", " + gd.latitude + " " + gd.longitude;
  }
  
  private void showAnswer(boolean trueButton) {
    int id = trueButton == question[currentIdx].isTrueQuestion() ? R.string.correct_toast : R.string.incorrect_toast;
    
    if (mIsCheater) {
      id = R.string.judgment_toast;
    }
    
    Toast
      .makeText(QuizActivity.this, id, Toast.LENGTH_SHORT)
      .show();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null) {
      mIsCheater = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
    }
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(TAG, currentIdx);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.quiz, menu);
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
