package com.ranjay.ribbit.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.ranjay.ribbit.R;
import com.ranjay.ribbit.RibbitApplication;
import com.ranjay.ribbit.R.id;
import com.ranjay.ribbit.R.layout;
import com.ranjay.ribbit.R.string;

public class LoginActivity extends Activity {

	protected TextView mSignUpTextView;
	protected EditText mUserName;
	protected EditText mPassword;
	protected Button mloginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// creating a progress indicator
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		// Hiding the action bar
		ActionBar actionbar = getActionBar();
		actionbar.hide();
		// or
		//getActionBar().hide();
		init();
		
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
				
			}
		});
		
		

	}

	private void init() {
		// TODO Auto-generated method stub
		mSignUpTextView = (TextView) findViewById(R.id.signUpText);
		
		mUserName = (EditText)findViewById(R.id.usernameField);
		mPassword = (EditText)findViewById(R.id.passwordField);
		mloginButton = (Button)findViewById(R.id.loginButton);
		
mloginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = mUserName.getText().toString();
				String password = mPassword.getText().toString();
				
				// Trim white spaces from values
				username = username.trim();
				password = password.trim();
				
				// Check if the values are null
				if(username.isEmpty() || password.isEmpty() ){
					// Creating a dialog box incase users leave a field empty
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(R.string.login_error_message);
					builder.setTitle(R.string.login_error_title);
					builder.setPositiveButton(android.R.string.ok, null); // creates the postive button in the dialogue box
					
					AlertDialog dialog = builder.create();
					dialog.show();
					
				}
				else{
					// Login
					setProgressBarIndeterminateVisibility(true);
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						
						@Override
						public void done(ParseUser user, ParseException e) {
							// if statement to check if we have a user
							setProgressBarIndeterminateVisibility(false);
							if(e == null){
								// success!
								RibbitApplication.updateParseInstallation(user);
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
								
							}else{
								// Creating a dialog box in case users sign up caused an error
								// using e.getMessage()
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
								builder.setMessage(e.getMessage());
								builder.setTitle(R.string.login_error_title);
								builder.setPositiveButton(android.R.string.ok, null); // creates the postive button in the dialogue box
								
								AlertDialog dialog = builder.create();
								dialog.show();
							}
							
						}
					});
					
				}
				
				
			}
		});
		
	}

	
}
