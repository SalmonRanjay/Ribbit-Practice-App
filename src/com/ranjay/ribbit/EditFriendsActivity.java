package com.ranjay.ribbit;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends ListActivity {

	protected static final String TAG = EditFriendsActivity.class.getSimpleName();
	
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_friends);
		//show the up button in the action bar
		
		// allowing multipe items to be checked in the list
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		setProgressBarIndeterminateVisibility(true);
		ParseQuery<ParseUser > query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000); // limit the amount of values to query
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if(e == null){
					// succes
					mUsers = users;
					String[] usernames = new String[mUsers.size()];
					int i = 0;
					for(ParseUser user : mUsers){
						usernames[i] = user.getUsername();
						i++;
					}
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, 
							android.R.layout.simple_list_item_checked, usernames);
					setListAdapter(adapter);
					
					// 
					addFriendCheckMarks();
					
				}
				else{
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
					builder.setMessage(e.getMessage());
					builder.setTitle(R.string.error_title);
					builder.setPositiveButton(android.R.string.ok, null); // creates the postive button in the dialogue box
					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				
			}
		});
	}

	protected void addFriendCheckMarks() {
		
		// Retreving a list of users from the backend
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if(e == null){
					// list returned look for match
					for (int i = 0; i < mUsers.size(); i++){
						ParseUser user = mUsers.get(i);
						
						for(ParseUser friend : friends){
							if(friend.getObjectId().equals(user.getObjectId())){
								getListView().setItemChecked(i, true);
							}
						}
					}
				}else{
					
				}
			}
		});
		
	}

	

	
	
	// when a list item is clicked this method is run
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// adding a friend relation
		if(getListView().isItemChecked(position)){
			mFriendsRelation.add((mUsers.get(position)));
			
			
		}else{
			// Remove friend
			mFriendsRelation.remove(mUsers.get(position));
			
		}
		// save relation to backend
		mCurrentUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null){
					Log.e(TAG, e.getMessage());
				}
				
				
			}
		});
		
		
		
	}
}
