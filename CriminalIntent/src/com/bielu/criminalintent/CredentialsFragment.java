package com.bielu.criminalintent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

public class CredentialsFragment extends DialogFragment {

  public static final String EXTRA_USER = "CredentialsFragment.User";
  public static final String EXTRA_PASS = "CredentialsFragment.Pass";
  
  private EditText mUserText;
  private EditText mPassText;

  public static CredentialsFragment newInstance(String user, String pass) {
    Bundle args = new Bundle();
    args.putString(EXTRA_USER, user);
    args.putString(EXTRA_USER, pass);
    
    CredentialsFragment fragment = new CredentialsFragment();
    fragment.setArguments(args);
    return fragment;
  }
  
  @SuppressLint("InflateParams") @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_credentials, null);
    mUserText = (EditText) v.findViewById(R.id.user_text);
    mPassText = (EditText) v.findViewById(R.id.password_text);
    
    mUserText.setText(getArguments().getString(EXTRA_USER));
    mPassText.setText(getArguments().getString(EXTRA_PASS));
    
    return new AlertDialog.Builder(getActivity())
      .setView(v)
      .setTitle(R.string.credentials_title)
      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          sendResult(Activity.RESULT_OK);
        }
      })
      .create();
  }

  private void sendResult(int result) {
    if (getTargetFragment() == null) {
      return;
    }
    
    Intent i = new Intent();
    i.putExtra(EXTRA_USER, mUserText.getText().toString());
    i.putExtra(EXTRA_PASS, mPassText.getText().toString());
    getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
  }
}
