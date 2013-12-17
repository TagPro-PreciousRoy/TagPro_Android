package com.koalabeast.tagpro.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			List<List<List<String>>> tables = new ArrayList<List<List<String>>>();
			
			Elements tbl = doc.select("table");
			
			for (Element tb : tbl) {
				List<List<String>> table = new ArrayList<List<String>>();
				for (Element row : tb.select("tr")) {
					List<String> elems = new ArrayList<String>();
					for (Element elem : row.children()) {
						elems.add(elem.text());
					}
					table.add(elems);
				}
				tables.add(table);
			}
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
		}
		
		return profile;
	}
}
