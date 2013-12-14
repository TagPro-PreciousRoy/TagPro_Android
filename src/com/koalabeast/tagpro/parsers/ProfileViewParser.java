package com.koalabeast.tagpro.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.util.Log;

import com.koalabeast.tagpro.infocontainers.ProfileStats;

public class ProfileViewParser extends AsyncTask<String, Void, ProfileStats> {
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected ProfileStats doInBackground(String... urls) {
		ProfileStats profile = new ProfileStats();
		
		try {
			Document doc = Jsoup.connect(urls[0] + urls[1]).get();
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
		}
		
		return profile;
	}
}
