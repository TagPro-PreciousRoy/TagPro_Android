package com.koalabeast.tagpro;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class LeaderActivity extends Activity implements OnItemSelectedListener {
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
		
		Spinner spinLeaderFilter = (Spinner) findViewById(R.id.leader_filter);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.leader_queries, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinLeaderFilter.setAdapter(adapter);
		spinLeaderFilter.setOnItemSelectedListener(this);
		
		try {
			leaderboards = new BoardParser(LeaderActivity.this).execute(this.server.url).get();
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
		}
		
		if (leaderboards.size() < 3) {
			Toast.makeText(this, "Unable to retrieve leaderboards.", Toast.LENGTH_SHORT).show();
		}
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
		
	public void onNothingSelected(AdapterView<?> parent) {
		// Doesn't matter, leave things alone.
	}
	
	private void clearTable() {
		TableLayout tl = (TableLayout) findViewById(R.id.leader_table);
		tl.removeAllViews();
	}
	
	private void populateTable(List<LeaderInfo> board) {
		TableLayout tl = (TableLayout) findViewById(R.id.leader_table);
		
		for (int i = 0; i < board.size(); i++) {
			TableRow row = new TableRow(this);
			
			TextView rank = new TextView(this);
			rank.setText(Integer.toString(board.get(i).getRank()));
			TextView name = new TextView(this);
			name.setText(board.get(i).getName());
			TextView points = new TextView(this);
			points.setText(Integer.toString(board.get(i).getPoints()));
			
			row.addView(rank);
			row.addView(name);
			row.addView(points);
			tl.addView(row);
		}
	}
	
	private class BoardParser extends AsyncTask<String, Void, List<List<LeaderInfo>>> {
		private final String[] divs = {"Day", "Week", "Month"};
		private ProgressDialog progressDialog;
		private Context context;
		
		public BoardParser (Context context) {
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setCancelable(true);
			progressDialog.setTitle("Querying server");
			progressDialog.setMessage("Please wait...");
			progressDialog.show();
		}
		
		@Override
		protected List<List<LeaderInfo>> doInBackground(String... urls) {
			List<List<LeaderInfo>> leaderBoards = new ArrayList<List<LeaderInfo>>(divs.length);
			
			try {
				Document doc = Jsoup.connect(urls[0] + "boards").get();
				for (String div : divs) {
					List<LeaderInfo> board = new ArrayList<LeaderInfo>();
					Elements elems = doc.select("#" + div);
					//prevWinners[Arrays.asList(divs).indexOf(div)] = elems.select("h3").text().replace("Previous Winner: ", "");
					Elements rows = elems.select("tr");
					rows.remove(0); // Remove the header line.
					for (Element row : rows) {
						Elements cols = row.select("td");
						board.add(new LeaderInfo(cols.get(0).text(), cols.get(1).text(), cols.get(1).select("a").attr("href"), cols.get(2).text()));
					}
					leaderBoards.add(board);
				}
			}
			catch (Exception e) {
				Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
			}
			finally {
				progressDialog.dismiss();
			}
			return leaderBoards;
		}
	}
}