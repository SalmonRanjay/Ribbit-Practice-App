package com.ranjay.ribbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChatMessaging extends Activity {

	protected EditText mMessageField;
	protected Button mSendMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_messaging);
		mMessageField = (EditText)findViewById(R.id.messagingField);
		mSendMessage = (Button)findViewById(R.id.sendMessageButton);
		
		mSendMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String messageData = mMessageField.getText().toString();
				Intent sendData = new Intent(ChatMessaging.this,RecipientsActivity.class);
				sendData.putExtra(ParseConstants.KEY_MESSAGE, messageData);
				sendData.putExtra(ParseConstants.KEY_FILE_TYPE, ParseConstants.TYPE_TEXT);
				startActivity(sendData);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_messaging, menu);
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
