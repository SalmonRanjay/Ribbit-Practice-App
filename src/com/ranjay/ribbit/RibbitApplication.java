package com.ranjay.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class RibbitApplication extends Application {
	@Override
	public void onCreate() {
		  super.onCreate();
		  Parse.initialize(this, "uM0mPEFke43aYXwsTFcKQaBM7Xracz3poChE4DgO", "lpHuBibZnLA9cdrPuuEwc9dkNTpU4AaZoXYRIZ4g");
		  
		  /*PushService.setDefaultPushCallback(Ribthis, RibbitApplication.class);
		  ParseInstallation.getCurrentInstallation().saveInBackground();
		  */
		  
		 /* // Testing the Parse backend
		  ParseObject testObject = new ParseObject("TestObject"); // creates the parse object
		  testObject.put("Ranjeee", "Test Data"); //creates the values to set
		  testObject.saveInBackground(); // save them in the background
		  */
		}

}
