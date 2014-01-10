package com.koalabeast.tagpro;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.opengl.*;


public class GameActivity extends Activity {
	MediaPlayer mPlayer;
    private GLSurfaceView gameGLSview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gameGLSview = new GameGLSurfaceView(this);
        // Set the Renderer for drawing on the GLSurfaceView
        gameGLSview.setRenderer(new GameRenderer());
        setContentView(gameGLSview);

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

class GameGLSurfaceView extends GLSurfaceView {

    public GameGLSurfaceView(Context context){
        super(context);


        
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
 