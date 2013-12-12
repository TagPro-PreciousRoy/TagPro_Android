package com.koalabeast.tagpro;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class LeaderActivity extends Activity implements OnItemSelectedListener {
	private ServerInfo server;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the server we are to query the leaderboard from.
		Bundle b = getIntent().getExtras();
		this.server = b.getParcelable("server");
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_leader_board);
		
		Spinner spinLeaderFilter = (Spinner) findViewById(R.id.leader_filter);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.leader_queries, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinLeaderFilter.setAdapter(adapter);
		spinLeaderFilter.setOnItemSelectedListener(this);
		
		// TODO - Use the Leaderboard Parser to get the data from the leaderboards.  Have a loader while this is underway.
		
		// Just checking to make sure that the server passed in correctly.
		Toast.makeText(getApplicationContext(), "Server: " + this.server.name, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		//
	}
		
	public void onNothingSelected(AdapterView<?> parent) {
		// Doesn't matter.
	}
}