package com.koalabeast.tagpro;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	MediaPlayer mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make fullscreen, hide action bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game);

		// Play music loop
		// TODO make optional
		mPlayer = MediaPlayer.create(this, R.raw.levelloop);
		mPlayer.setLooping(true);
		mPlayer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPlayer.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPlayer.start();
	}

}
