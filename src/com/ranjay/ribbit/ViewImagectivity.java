package com.ranjay.ribbit;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewImagectivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_imagectivity);
		
		ImageView imageView = (ImageView)findViewById(R.id.imageView);
		Uri imageUri = getIntent().getData();
		// using picasso image library
		Picasso.with(this).load(imageUri.toString()).into(imageView);
		
		// creating a timer
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				finish();
				
			}
		}, 10*1000);
	}

	
}
