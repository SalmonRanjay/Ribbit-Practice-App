package com.ranjay.ribbit.adapters;

import java.util.Locale;

import com.ranjay.ribbit.R;
import com.ranjay.ribbit.R.drawable;
import com.ranjay.ribbit.R.string;
import com.ranjay.ribbit.ui.FriendsFragment;
import com.ranjay.ribbit.ui.InboxFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	protected Context mContext;

	public SectionsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class
		// below).
		//return PlaceholderFragment.newInstance(position + 1);
		switch(position){
		case 0:
			return new InboxFragment();
		case 1:
			return new FriendsFragment();
			
		}
		return null;
	}

	@Override
	public int getCount() {
		// Show the number of total pages.
		// set to two
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			// passing int the context using member variable
			return mContext.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return mContext.getString(R.string.title_section2).toUpperCase(l);
		
		}
		return null;
	}
	
	// method to return an image for the tabs instead of a text
	public int getIcon(int position){
		
		switch (position) {
		case 0:
			// passing int the context using member variable
			return R.drawable.ic_tab_inbox;
		case 1:
			return R.drawable.ic_tab_friends;
		
		}
		return R.drawable.ic_tab_inbox;
		
	}
}