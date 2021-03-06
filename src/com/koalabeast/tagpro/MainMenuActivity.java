package com.koalabeast.tagpro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koalabeast.tagpro.infocontainers.ServerInfo;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainMenuActivity extends Activity implements OnNavigationListener {
	private ArrayAdapter<String> serverListAdapter;
	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	private ServerInfo pickedServer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		ActionBar bar = getActionBar();
		serverListAdapter = new ArrayAdapter<String>(bar.getThemedContext(),
				android.R.layout.simple_spinner_item); // Create adapter

		// Set up server spinner
		serverListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(serverListAdapter, this);
		
		// Request the server list
		Ion.with(this, "http://tagpro.koalabeast.com/servers").asJsonArray()
				.setCallback(new FutureCallback<JsonArray>() {
					
					@Override
					public void onCompleted(Exception e, JsonArray result) {
						// Called when the server list has been fetched

						// Hide the loading spinner
						findViewById(R.id.loadingSpinnerLayout).setVisibility(View.GONE);
						if (e != null) {
							showNetworkError();
						} else {
							// Add maptest server - not for final release!
							servers.add(new ServerInfo("Maptest", "unknown", "http://tagpro-maptest.koalabeast.com", 0, 0));
							

							// Loop through servers
							for (JsonElement serverElement : result) {
								JsonObject obj = serverElement.getAsJsonObject();
								
								// Convert JsonObjects to ServerInfo objects
								ServerInfo server = new ServerInfo(
									obj.get("name").getAsString(),
									obj.get("location").getAsString(),
									obj.get("url").getAsString(),
									obj.get("games").getAsInt(),
									obj.get("players").getAsInt()
								);
								
								// Add to servers ArrayList
								servers.add(server);
							}
							
							// Put the servers in Alpha order.
							Collections.sort(servers, new Comparator<ServerInfo>() {
								public int compare(ServerInfo s1, ServerInfo s2) {
									return s1.name.compareToIgnoreCase(s2.name);
								}
							});
							
							for (ServerInfo s : servers) {
								// Loop through servers, adding them to the dropdown
								serverListAdapter.add(s.name);
							}
							
							// Show the main layout
							findViewById(R.id.serverInfoLayout).setVisibility(View.VISIBLE);
						}
					}

				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar
		// TODO Add things to menu!
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Select server
		
		// Find info for picked server
		ServerInfo server = servers.get(itemPosition);
		
		// Make it available from showJoinDialog
		pickedServer = server;
		
		// Show server info on the menu
		((TextView) findViewById(R.id.serverName)).setText(server.name);
		((TextView) findViewById(R.id.serverLocation)).setText(server.location);
		((TextView) findViewById(R.id.serverPlayers)).setText("Number of players: " + server.players);
		((TextView) findViewById(R.id.serverGames)).setText("Active games: " + server.games);

		return false;
	}

	// Save the instance state when we go to other activities.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelable("server", this.pickedServer);
	}
	
	// TODO - Restore the instance state when we get back from other activities.
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//this.server = savedInstanceState.getParcelable("server");
	}
	
	public void showJoinDialog(View button) {
		if (pickedServer != null) { // Shouldn't fail, but doing nothing is better than a crash
			// Create and show join dialog
			JoinGameDialogFragment
				.newInstance(pickedServer.name, pickedServer.url, pickedServer.location)
				.show(getFragmentManager(), "JoinGameDialogFragment");
		}
	}

	public void switchToPlay() {
		Intent in = new Intent(this, GameActivity.class);
		startActivity(in);
	}
	
	public void switchToLeaders(View button) {
		Intent in = new Intent(this, LeaderActivity.class);
		// Pass in the server info to query from.
		Bundle b = new Bundle();
		b.putParcelable("server", pickedServer);
		in.putExtras(b);
		startActivity(in);
	}

	public void showNetworkError() {
		new NetworkErrorDialogFragment().show(getFragmentManager(),
				"NetworkErrorDialogFragment");
	}
}
