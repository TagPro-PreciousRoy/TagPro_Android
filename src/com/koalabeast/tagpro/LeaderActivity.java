package com.koalabeast.tagpro;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class LeaderActivity extends Activity implements OnItemSelectedListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leader_board);
		
		Spinner spinLeaderFilter = (Spinner) findViewById(R.id.leader_filter);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.leader_queries, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinLeaderFilter.setAdapter(adapter);
		spinLeaderFilter.setOnItemSelectedListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		//parent.getItemAtPosition(pos);
		Context context = getApplicationContext();
		CharSequence text = "Item Selected: " + Integer.toString(pos);
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}
		
	public void onNothingSelected(AdapterView<?> parent) {
			
	}
}