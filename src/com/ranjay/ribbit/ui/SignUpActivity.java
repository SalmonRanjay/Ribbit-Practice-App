package com.ranjay.ribbit.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.ranjay.ribbit.R;
import com.ranjay.ribbit.RibbitApplication;
import com.ranjay.ribbit.R.id;
import com.ranjay.ribbit.R.layout;
import com.ranjay.ribbit.R.string;

public class SignUpActivity extends Activity {

	protected EditText mUserName;
	protected EditText mPassword;
	protected EditText mEmail;
	protected Button mSignUpButton;
	protected Button mCancelButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_sign_up);
		
		// Hiding the action bar
				ActionBar actionbar = getActionBar();
				actionbar.hide();
				// or
				//getActionBar().hide();
				
		init();
	}

	private void init() {
		mUserName = (EditText)findViewById(R.id.usernameField);
		mPassword = (EditText)findViewById(R.id.passwordField);
		mEmail = (EditText)findViewById(R.id.emailField);
		mSignUpButton = (Button)findViewById(R.id.signUpButton);
		mCancelButton = (Button)findViewById(R.id.cancelButton);
		
		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = mUserName.getText().toString();
				String password = mPassword.getText().toString();
				String email = mEmail.getText().toString();
				
				// Trim white spaces from values
				username = username.trim();
				password = password.trim();
				email = email.trim();
				
				// Check if the values are null
				if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
					// Creating a dialog box incase users leave a field empty
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
					builder.setMessage(R.string.signup_error_message);
					builder.setTitle(R.string.signup_error_title);
					builder.setPositiveButton(android.R.string.ok, null); // creates the postive button in the dialogue box
					
					AlertDialog dialog = builder.create();
					dialog.show();
					
				}
				else{
					// creating a new user
					setProgressBarIndeterminateVisibility(true);
					ParseUser newUser = new ParseUser();
					newUser.setUsername(username);
					newUser.setPassword(password);
					newUser.setEmail(email);
					newUser.signUpInBackground(new SignUpCallback() {
						
						@Override
						public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							if(e == null){
								// success!
								// notification coder
								RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());
								Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else{
								// Creating a dialog box in case users sign up caused an error
								// using e.getMessage()
								AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
								builder.setMessage(e.getMessage());
								builder.setTitle(R.string.signup_error_title);
								builder.setPositiveButton(android.R.string.ok, null); // creates the postive button in the dialogue box
								
								AlertDialog dialog = builder.create();
								dialog.show();
								
								Log.e("signUpActivity", e.getMessage().toString());
							}
							
						}
					});
					
				}
				
				
			}
		});
		
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	}

	
}
