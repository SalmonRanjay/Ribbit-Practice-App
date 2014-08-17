package com.ranjay.ribbit.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ranjay.ribbit.R;
import com.ranjay.ribbit.adapters.UserAdapter;
import com.ranjay.ribbit.utils.FileHelper;
import com.ranjay.ribbit.utils.ParseConstants;

public class RecipientsActivity extends Activity {

	protected static final String TAG = RecipientsActivity.class
			.getSimpleName();
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	protected MenuItem mSendMenuItem; // creating a menu item object

	protected Uri mMediaUri;
	protected String mFileType;
	protected String mTextMessageVal;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_grid);
		mGridView = (GridView)findViewById(R.id.friendsGrid);
		mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		mMediaUri = getIntent().getData();
		mFileType = getIntent().getExtras().getString(
				ParseConstants.KEY_FILE_TYPE);
		mTextMessageVal = getIntent().getExtras().getString(
				ParseConstants.KEY_MESSAGE);
		
		mGridView.setOnItemClickListener(mOnItemClickListener);
		
		TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		setProgressBarIndeterminateVisibility(true);
		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);

		// Retreving a list of users from the backend
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mFriends = friends;

					String[] usernames = new String[mFriends.size()];
					int i = 0;
					for (ParseUser user : mFriends) {
						usernames[i] = user.getUsername();
						i++;
					}
					
					
					if(mGridView.getAdapter() == null){
						UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
						}else{
							((UserAdapter)mGridView.getAdapter()).refill(mFriends);
						}
					

				} else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RecipientsActivity.this);
					builder.setMessage(e.getMessage());
					builder.setTitle(R.string.error_title);
					// create the positive button for the dialog
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);

		mSendMenuItem = menu.getItem(0);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_send:
			if (mTextMessageVal != null) {
				ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
				message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser()
						.getObjectId());
				message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser()
						.getUsername());
				message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());
				message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
				
				message.put(ParseConstants.KEY_SMS_MESSAGE, mTextMessageVal);
				
				send(message);
				Intent returnToMainActivity = new Intent(RecipientsActivity.this, MainActivity.class);
				returnToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				returnToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(returnToMainActivity);

			} else {
				ParseObject message = createMessage();
				if (message == null) {
					// error
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
							.setMessage(R.string.error_selecting_file)
							.setTitle(R.string.error_selecting_file_title)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					send(message);
					finish(); // finishes the activity and return you to main
								// activity
				}

			}
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long) used along with the
	 * android.simple_list.item_checked layout for if a list item is check
	 */
	

	protected void send(ParseObject message) {
		// saving to the backend
		message.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// succes
					Toast.makeText(RecipientsActivity.this,
							R.string.success_message, Toast.LENGTH_LONG).show();
					
					//sending push notifications
					sendPushNotifications();

				} else {
					// error failed
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RecipientsActivity.this)
							.setMessage(R.string.error_sending_message)
							.setTitle(R.string.error_selecting_file_title)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();

				}

			}
		});

	}

	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser()
				.getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser()
				.getUsername());
		message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

		byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

		if (fileBytes == null) {
			return null;
		} else {
			if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);
			}

			String fileName = FileHelper
					.getFileName(this, mMediaUri, mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);

			message.put(ParseConstants.KEY_FILE, file);
		}
		return message;
	}

	protected ArrayList<String> getRecipientsIds() {
		ArrayList<String> recipientIds = new ArrayList<String>();
		for (int i = 0; i < mGridView.getCount(); i++) {
			if (mGridView.isItemChecked(i)) {
				recipientIds.add(mFriends.get(i).getObjectId());
			}
		}

		return recipientIds;
	}
	
	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mGridView.getCheckedItemCount() > 0) {
				mSendMenuItem.setVisible(true);
			} else {
				mSendMenuItem.setVisible(false);
			}
			
			ImageView checkImage = (ImageView)view.findViewById(R.id.checkImageView);
			if(mGridView.isItemChecked(position)){
				
				checkImage.setVisibility(View.VISIBLE);
				
				
			}else{
				// Remove friend
				
				checkImage.setVisibility(View.INVISIBLE);
				
			}
			
		}
		
	};
	
	// sending push notfications to users
	protected void sendPushNotifications(){
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		
		// setting up the query to notify users who user id match the recipientes id
		query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientsIds());
		
		// send push notification
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message, 
				ParseUser.getCurrentUser().getUsername()));
		push.sendInBackground();
		
	}
}
