package com.ranjay.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.ranjay.ribbit.ui.MainActivity;
import com.ranjay.ribbit.utils.ParseConstants;

public class RibbitApplication extends Application {
	@Override
	public void onCreate() {
		  super.onCreate();
		  Parse.initialize(this, "uM0mPEFke43aYXwsTFcKQaBM7Xracz3poChE4DgO", "lpHuBibZnLA9cdrPuuEwc9dkNTpU4AaZoXYRIZ4g");
		  
		  // change call back for pic
		  //PushService.setDefaultPushCallback(this, MainActivity.class);
		  PushService.setDefaultPushCallback(this, MainActivity.class,R.drawable.ic_stat_ic_launcher);
		  ParseInstallation.getCurrentInstallation().saveInBackground();
		}

	// code for push notifications
	public static void updateParseInstallation(ParseUser user){
		ParseInstallation installation= ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}
}
