package com.bielu.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {
  
  private static final String TAG = "QuizActivity";
  private Button mTrueButton;
  private Button mFalseButton;
  private Button mNextButton;
  private Button mCheatButton;
  private TextView mQuestionText;
  private int currentIdx = 0;
  private boolean mIsCheater;
  
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
