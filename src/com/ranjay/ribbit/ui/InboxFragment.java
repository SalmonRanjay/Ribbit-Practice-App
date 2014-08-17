package com.ranjay.ribbit.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.ranjay.ribbit.R;
import com.ranjay.ribbit.adapters.MessageAdapter;
import com.ranjay.ribbit.utils.ParseConstants;

public class InboxFragment extends ListFragment {

	protected List<ParseObject> mMessages;
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(
				R.color.swipeRefresh1, 
				R.color.swipeRefresh2, 
				R.color.swipeRefresh3, 
				R.color.swipeRefresh4
				);

		return rootView;

	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setProgressBarIndeterminateVisibility(true);
		retrieveMessages();
	}

	private void retrieveMessages() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_MESSAGES);
		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser
				.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				// setting the refresh listening to false
				if(mSwipeRefreshLayout.isRefreshing()){
					mSwipeRefreshLayout.setRefreshing(false);
				}
				if (e == null) {
					// messages found in parse object messages
					mMessages = messages; // member variable to messages
											// received

					String[] usernames = new String[mMessages.size()];
					int i = 0;
					for (ParseObject message : mMessages) {
						usernames[i] = message
								.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					if (getListView().getAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(
								getListView().getContext(), mMessages);
						setListAdapter(adapter);

					} else {
						// refil adapter
						((MessageAdapter) getListView().getAdapter())
								.refill(mMessages);
					}

				}

			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		String smsMessage = message.getString(ParseConstants.KEY_SMS_MESSAGE);
		if (smsMessage != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getListView()
					.getContext());
			builder.setMessage(smsMessage);
			builder.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();

		} else {
			Uri fileUri = Uri.parse(file.getUrl());
			if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
				// View Image
				Intent intent = new Intent(getActivity(),
						ViewImagectivity.class);
				intent.setData(fileUri);
				startActivity(intent);
			} else if (messageType.equals(ParseConstants.TYPE_VIDEO)) {
				// Starting a video via intent
				Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
				intent.setDataAndType(fileUri, "video/*");
				startActivity(intent);
			}
			
			//Delete it
			List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
			
			if(ids.size() == 1){
				// last recipient - delete the whole thing
				message.deleteInBackground();
			}else{
				//remove the recipient and save
				ids.remove(ParseUser.getCurrentUser().getObjectId());
				
				ArrayList<String> idsToRemove = new ArrayList<String>();
				idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
				message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
				message.saveInBackground();
				
			}
			
		}
	}
	
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			retrieveMessages();
			
		}
	};

}
