package com.ranjay.ribbit.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ranjay.ribbit.R;
import com.ranjay.ribbit.adapters.UserAdapter;
import com.ranjay.ribbit.utils.ParseConstants;

public class EditFriendsActivity extends Activity {

	protected static final String TAG = EditFriendsActivity.class.getSimpleName();
	
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_grid);
		//show the up button in the action bar
		mGridView = (GridView) findViewById(R.id.friendsGrid);
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);
		// allowing multipe items to be checked in the list
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);
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
					
					if(mGridView.getAdapter() == null){
						UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
						mGridView.setAdapter(adapter);
						}else{
							((UserAdapter)mGridView.getAdapter()).refill(mUsers);
						}
					
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
								mGridView.setItemChecked(i, true);
							}
						}
					}
				}else{
					
				}
			}
		});
		
	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ImageView checkImage = (ImageView)view.findViewById(R.id.checkImageView);
			
			 
			if(mGridView.isItemChecked(position)){
				mFriendsRelation.add((mUsers.get(position)));
				checkImage.setVisibility(View.VISIBLE);
				
				
			}else{
				// Remove friend
				mFriendsRelation.remove(mUsers.get(position));
				checkImage.setVisibility(View.INVISIBLE);
				
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
	};

	
	
	// when a list item is clicked this method is run
//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		
//		
//		
//		
//		
//	}
}
