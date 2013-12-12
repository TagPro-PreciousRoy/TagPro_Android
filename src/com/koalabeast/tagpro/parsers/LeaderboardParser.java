package com.koalabeast.tagpro.parsers;
import com.koalabeast.tagpro.LeaderInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Xml;

public class LeaderboardParser {
	private String url;
	private ArrayList<LeaderInfo> dailyLeaderboard = null;
	private ArrayList<LeaderInfo> weeklyLeaderboard = null;
	private ArrayList<LeaderInfo> monthlyLeaderboard = null;
	
	public LeaderboardParser(String url) {
		this.url = url;
		parseLeaderboards();
	}
	
	public ArrayList<LeaderInfo> getDaily() {
		return this.dailyLeaderboard;
	}
	
	public ArrayList<LeaderInfo> getWeekly() {
		return this.weeklyLeaderboard;
	}
	
	public ArrayList<LeaderInfo> getMonthly() {
		return this.monthlyLeaderboard;
	}
	
	private void parseLeaderboards() {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setValidating(false);
			factory.setFeature(Xml.FEATURE_RELAXED, true);
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(getRawHtml(url), "UTF-8");
			
			int eventType = xpp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("div")) {
					if (xpp.getAttributeValue(null, "id") == "Day") {
						while (eventType != XmlPullParser.END_TAG && !xpp.getName().equalsIgnoreCase("div")) {
							if (xpp.getName().equalsIgnoreCase("table")) {
								
							}
							eventType = xpp.next();
						}
					}
					else if (xpp.getAttributeValue(null, "id") == "Week") {
						
					}
					else if (xpp.getAttributeValue(null, "id") == "Month") {
						
					}
				}
				eventType = xpp.next();
			}
		}
		catch (XmlPullParserException e) {
			
		}
		catch (IOException e) {
			
		}
	}
	
	private InputStream getRawHtml(String url) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			InputStream in = response.getEntity().getContent();
			
			return in;
		}
		catch (ClientProtocolException e) {
			// Should probably alert.
		}
		catch (IOException e) {
			// Should probably alert.
		}
		
		return null;
	}
}
