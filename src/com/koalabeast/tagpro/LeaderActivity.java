package com.koalabeast.tagpro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.koalabeast.tagpro.parsers.BoardParser;

public class LeaderActivity extends Activity implements OnItemSelectedListener, OnClickListener {
	private ServerInfo server;
	private List<List<LeaderInfo>> leaderboards = new ArrayList<List<LeaderInfo>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the server we are to query the leaderboard from.
		Bundle b = getIntent().getExtras();
		this.server = b.getParcelable("server");
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(false);
		
		setContentView(R.layout.activity_leader_board);
		
		// Populate the text fields with the server info
		TextView serverName = (TextView) findViewById(R.id.leadersServerName);
		serverName.setText(server.name);
		
		TextView serverLocation = (TextView) findViewById(R.id.leadersServerLocation);
		serverLocation.setText(server.location);
		
		// Create the spinner for the leaderboard filter
		Spinner spinLeaderFilter = (Spinner) findViewById(R.id.leader_filter);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.leader_queries, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinLeaderFilter.setAdapter(adapter);
		spinLeaderFilter.setOnItemSelectedListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ProgressDialog pd = new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setTitle("Querying " + server.name);
		pd.setMessage("Please wait...");
		pd.show();
		
		try {
			leaderboards = new BoardParser().execute(this.server.url).get();
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
		}
		
		pd.dismiss();
		
		if (leaderboards == null) {
			finish();
		}
		if (leaderboards.size() < 3) {
			Toast.makeText(this, "Unable to retrieve leaderboards.", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onClick(View v) {
		TextView tv = (TextView) v;
		Spinner filterSpinner = (Spinner) findViewById(R.id.leader_filter);
		String filterCategory = filterSpinner.getSelectedItem().toString();
		int idx = Arrays.asList(BoardParser.divs).indexOf(filterCategory);
		TableRow parent = (TableRow) tv.getParent();
		int rank = Integer.parseInt(((TextView) parent.getChildAt(0)).getText().toString());
		String href = leaderboards.get(idx).get(rank).getHref();
		
		Intent in = new Intent(this, ProfileViewActivity.class);
		startActivity(in);
		
		Toast.makeText(this, href, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		clearTable();
		populateTable(leaderboards.get(pos));
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Doesn't matter, leave things alone.
	}
	
	/*
	 * Clear out the current leader board list
	 */
	private void clearTable() {
		TableLayout tl = (TableLayout) findViewById(R.id.leader_table);
		if (tl.getChildCount() > 1) {
			for (int i = 0; i < 100; i++) {
				tl.removeView(tl.getChildAt(1));
			}
		}
	}
	
	/*
	 * Populate the table with all leader info.
	 */
	private void populateTable(List<LeaderInfo> board) {
		TableLayout tl = (TableLayout) findViewById(R.id.leader_table);
		
		for (int i = 0; i < board.size(); i++) {
			TableRow row = new TableRow(this);
			LeaderInfo leader = board.get(i);
			
			// Create the rank column
			TextView rank = new TextView(this);
			rank.setText(Integer.toString(leader.getRank()));
			rank.setTextAppearance(this, R.style.tableOuter);
			rank.setWidth(findViewById(R.id.table_head_col1).getWidth()); // Hacky way to mimic the "weight" programmatically.
			rank.setGravity(Gravity.CENTER);
			
			// Create the name column
			TextView name = new TextView(this);
			name.setText(leader.getName());
			name.setTextAppearance(this, R.style.tableInner);
			name.setWidth(findViewById(R.id.table_head_col2).getWidth());
			name.setGravity(Gravity.CENTER);
			name.setOnClickListener(this);
			
			// Create points column
			TextView points = new TextView(this);
			points.setText(Integer.toString(leader.getPoints()));
			points.setTextAppearance(this, R.style.tableOuter);
			points.setWidth(findViewById(R.id.table_head_col3).getWidth());
			points.setGravity(Gravity.CENTER);
			
			if ((i + 1) % 2 == 0) {
				row.setBackgroundColor(getResources().getColor(R.color.bg_lightgrey));
			}
			else {
				row.setBackgroundColor(getResources().getColor(R.color.bg_darkgrey));
			}
			
			row.addView(rank);
			row.addView(name);
			row.addView(points);
			tl.addView(row);
		}
	}
}