package com.koalabeast.tagpro;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;

/*
 // Proceed to play screen
 MainMenuActivity menu = (MainMenuActivity) getActivity();
 dismiss();
 menu.switchToPlay();
 */

public class JoinGameDialogFragment extends DialogFragment implements OnClickListener {
	private String serverName;
	private String serverURL;
	private String serverLocation;

	private TextView stateText;
	private TextView serverText;

	static JoinGameDialogFragment newInstance(String name, String url, String location) {
		JoinGameDialogFragment frag = new JoinGameDialogFragment();

		// Remove slash from URL, if it has one
		if (url.charAt(url.length() - 1) == '/') {
			url = url.substring(0, url.length() - 1);
		}

		// Set arguments for the fragment
		Bundle args = new Bundle();
		args.putString("serverName", name);
		args.putString("serverURL", url);
		args.putString("serverLocation", location);
		frag.setArguments(args);

		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Initialize Builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get arguments passed to newInstance()
		serverName = getArguments().getString("serverName");
		serverURL = getArguments().getString("serverURL");
		serverLocation = getArguments().getString("serverLocation");

		// Set title and button action
		builder.setTitle(R.string.join_game_title);
		builder.setNegativeButton(android.R.string.cancel, this);

		// Inflate the view that shows the spinner and status text
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View loadingView = inflater.inflate(R.layout.dialog_join_game, null);

		// Get the 2 text views
		stateText = (TextView) loadingView.findViewById(R.id.join_game_state);
		serverText = (TextView) loadingView.findViewById(R.id.join_game_server);

		stateText.setText(R.string.join_game_state_connecting);
		serverText.setText("On server: " + serverName + " (" + serverLocation + ")");

		// Add view to dialog
		builder.setView(loadingView);

		// Connect to the server
		SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), serverURL + ":81",
				new ConnectCallback() {

					@Override
					public void onConnectCompleted(Exception e, SocketIOClient client) {
						if (e != null) {
							e.printStackTrace();
							return;
						}

						client.setStringCallback(new StringCallback() {

							@Override
							public void onString(String str, Acknowledge ack) {
								System.out.println();

							}
						});
						
						client.setJSONCallback(new JSONCallback() {
							
							@Override
							public void onJSON(JSONObject obj, Acknowledge ack) {
								System.out.println();
							}
						});
					}
				});

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO cancel
	}
}
