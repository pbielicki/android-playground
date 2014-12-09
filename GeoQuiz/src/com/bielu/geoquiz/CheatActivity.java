package com.bielu.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends Activity {

  protected static final String EXTRA_ANSWER_IS_TRUE = "com.bielu.geoquiz.answer_is_true";
  protected static final String EXTRA_ANSWER_SHOWN = "com.bielu.geoquiz.answer_shown";
  
  private boolean mAnswerIsTrue;
  private Button mShowAnswerButton;
  private TextView mAnswerText;
  private TextView mApiVersionText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cheat);
    
    mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
    mAnswerText = (TextView) findViewById(R.id.answer_text_view);
    mApiVersionText = (TextView) findViewById(R.id.api_version_text_view);
    mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
    mApiVersionText.setText("API level " + Build.VERSION.SDK_INT);
    
    setAnswerShownResult(false);
    
    mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setAnswerShownResult(true);
        if (mAnswerIsTrue) {
          mAnswerText.setText(R.string.true_button);
        } else {
          mAnswerText.setText(R.string.false_button);
        }
      }
    });
  }
  
  private void setAnswerShownResult(boolean isAnswerShown) {
    Intent data = new Intent();
    data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
    setResult(RESULT_OK, data);
  }
}
