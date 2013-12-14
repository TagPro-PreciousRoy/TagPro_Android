package com.koalabeast.tagpro;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class ProfileViewActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(false);
		
		setContentView(R.layout.activity_profile_view);
	}
	
	/*
	 * Provide a way to navigate back from the profile view.  Does not
	 * use the normal parent type to ensure that we do not always go back 
	 * to the main screen and simply go back a single level.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		finish();
		return true;
	}
}
