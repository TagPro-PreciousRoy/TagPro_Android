package com.koalabeast.tagpro;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.koalabeast.tagpro.infocontainers.LeaderInfo;
import com.koalabeast.tagpro.infocontainers.ProfileStats;
import com.koalabeast.tagpro.infocontainers.ServerInfo;
import com.koalabeast.tagpro.parsers.ProfileViewParser;

public class ProfileViewActivity extends Activity {
	private LeaderInfo player;
	private ServerInfo server;
	private ProfileStats profile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		this.player = b.getParcelable("player");
		this.server = b.getParcelable("server");
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(false);
		
		setContentView(R.layout.activity_profile_view);
		
		TextView helloWorld = (TextView) findViewById(R.id.helloworld);
		helloWorld.setText(player.getName() + "\n" + server.url + player.getHref());
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
	
	/*
	@Override
	public void onResume() {
		super.onResume();
		ProgressDialog pd = new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setTitle("Finding stats on " + player.getName());
		pd.setMessage("Please wait...");
		pd.show();
		
		try {
			profile = new ProfileViewParser().execute(this.server.url + this.player.getHref()).get();
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
		}
		
		pd.dismiss();
	}
	*/
}
