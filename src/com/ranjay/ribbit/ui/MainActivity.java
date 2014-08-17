package com.ranjay.ribbit.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import com.ranjay.ribbit.ChatMessaging;
import com.ranjay.ribbit.R;
import com.ranjay.ribbit.R.array;
import com.ranjay.ribbit.R.id;
import com.ranjay.ribbit.R.layout;
import com.ranjay.ribbit.R.menu;
import com.ranjay.ribbit.R.string;
import com.ranjay.ribbit.adapters.SectionsPagerAdapter;
import com.ranjay.ribbit.utils.ParseConstants;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static final String TAG = MainActivity.class.getSimpleName();
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int PICK_VIDEO_REQUEST = 3;

	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;

	public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; // 10 MB
	
	

	protected Uri mMediaUri;

	// Creating a dialog listner to pass into the alert dialog which displays
	// the options to click on
	protected DialogInterface.OnClickListener mDialogLister = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// which contains the item clicked on
			switch (which) {
			case 0: // Take Picture
				Intent takePhotoIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if (mMediaUri == null) {
					Toast.makeText(MainActivity.this,
							R.string.error_external_storeage, Toast.LENGTH_LONG)
							.show();
					;
				} else {
					takePhotoIntent
							.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
				}
				break;
			case 1: // Take Video
				Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				if (mMediaUri == null) {
					Toast.makeText(MainActivity.this,
							R.string.error_external_storeage, Toast.LENGTH_LONG)
							.show();
					;
				} else {
					videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
					// setting
					// the
					// duration
					// length
					// to
					// 10
					// secs
					// setting the video quality to low
					// because the free version of parse only allows 10 mb of
					// files to be transfered
					videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
					startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
				}
				break;
			case 2: // Choose picture
				Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotoIntent.setType("image/*");
				startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
				break;
			case 3: // Choose Video
				Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				chooseVideoIntent.setType("video/*");
				Toast.makeText(MainActivity.this,
						R.string.video_file_size_warning, Toast.LENGTH_LONG)
						.show();
				startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
				break;

			}

		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.
			String appName = MainActivity.this.getString(R.string.app_name);
			if (isExternalStoreageAvailable()) {
				// 1.Get the external storeage directory
				File mediaStoreageDir = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						appName);

				// 2. create our subdirectory
				if (!mediaStoreageDir.exists()) {
					if (!mediaStoreageDir.mkdir()) {
						Log.e(TAG, "Failed to reate directory");
					}
				}
				// 3. create the file name
				// 4. create file
				File mediaFile;
				Date now = new Date(); // getting the date to append a time
										// stamp;
				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
						Locale.US).format(now);
				String path = mediaStoreageDir.getPath() + File.separator; // getting
																			// the
																			// path
																			// to
																			// where
																			// to
																			// store
																			// the
																			// files
				if (mediaType == MEDIA_TYPE_IMAGE) {
					mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
				} else if (mediaType == MEDIA_TYPE_VIDEO) {
					mediaFile = new File(path + "VID_" + timestamp + ".mp4");
				} else {
					return null;
				}
				Log.d(TAG, "File.." + Uri.fromFile(mediaFile));
				// 5. Return the file's URI
				return Uri.fromFile(mediaFile);
			} else {
				return null;
			}
		}

		private boolean isExternalStoreageAvailable() {
			String state = Environment.getExternalStorageState();

			if (state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			} else {
				return false;
			}

		}
	};

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ParseAnalytics.trackAppOpened(getIntent());
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			navigateToLogin();

		} else {
			Log.i(TAG, currentUser.getUsername());
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					//.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setIcon(mSectionsPagerAdapter.getIcon(i))
					.setTabListener(this));
		}
	}
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			// add image to gallery success
			// adding image to the gallery
			if (requestCode == PICK_PHOTO_REQUEST
					|| requestCode == PICK_VIDEO_REQUEST) {
				// check if user requested to select a photo from gallery
				if (data == null) {
					Toast.makeText(this, getString(R.string.general_error),
							Toast.LENGTH_LONG).show();
				} else {
					mMediaUri = data.getData();
				}
				Log.i(TAG, "Media URI: " + mMediaUri);

				if (requestCode == PICK_VIDEO_REQUEST) {
					// make sure the file is less than 10 mb
					// checking the file size
					//
					int fileSize = 0;
					InputStream inputStream = null;
					try {
						inputStream = getContentResolver().openInputStream(
								mMediaUri);
						fileSize = inputStream.available();
					} catch (FileNotFoundException e) {
						Toast.makeText(this, R.string.error_opening_file,
								Toast.LENGTH_LONG).show();
						return;

					} catch (IOException e) {
						Toast.makeText(this, R.string.error_opening_file,
								Toast.LENGTH_LONG).show();
						return;
					} finally {

						try {
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (fileSize >= FILE_SIZE_LIMIT) {
						Toast.makeText(MainActivity.this,
								R.string.error_file_size, Toast.LENGTH_LONG)
								.show();
						return;
					}
				}
			} else {
				Intent mediaScanIntent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);

			}
			
			Intent recipientsIntent = new Intent(this,RecipientsActivity.class);
			recipientsIntent.setData(mMediaUri);
			
			String fileType;
			if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST){
				fileType = ParseConstants.TYPE_IMAGE;
			}else{
				fileType = ParseConstants.TYPE_VIDEO;
			}
			
			recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
			startActivity(recipientsIntent);
			
			

		} else if (resultCode != RESULT_CANCELED) {
			Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		/*
		 * Allowing the new activity thats being started to be the First
		 * activity of the app and clear the old one from being the first
		 */
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_edit_friends:
			Intent intent = new Intent(this, EditFriendsActivity.class);
			/*
			 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			 */
			startActivity(intent);
			break;
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_camera:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(R.array.camera_choices, mDialogLister);
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		case R.id.action_chat:
			Intent startMessage = new Intent(this, ChatMessaging.class);
			startActivity(startMessage);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

}
