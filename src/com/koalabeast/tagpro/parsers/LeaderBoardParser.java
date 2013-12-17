package com.koalabeast.tagpro.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

import com.koalabeast.tagpro.LeaderActivity.LeaderBoardFragment;
import com.koalabeast.tagpro.infocontainers.LeaderInfo;

public class LeaderBoardParser extends AsyncTask<String, Void, List<LeaderInfo>> {
	private String previousWinner = "";
	private LeaderBoardFragment activity; // Provided to run a callback when the background task completes.
	private String targetBoard = "";
	
	/**
	 * 
	 * @param activity - The leaderboard activity that the async task is called from, used to run a callback when
	 * the background task is complete on the fragment.
	 * @param targetBoard - Pass in a string for the targeted board desired, as of current - "Day", "Week", "Month" are
	 * valid (not determined by the app, determined by http://tagpro-*.koalabeast.com/boards)
	 */
	public LeaderBoardParser(LeaderBoardFragment activity, String targetBoard) {
		this.activity = activity;
		this.targetBoard = targetBoard;
	}
	
	@Override
	protected List<LeaderInfo> doInBackground(String... urls) {
		List<LeaderInfo> leaderBoard = new ArrayList<LeaderInfo>(1);
		
		// Sanity check for required data.
		if (targetBoard.equals("")) {
			return null;
		}
		
		try {
			Document doc = Jsoup.connect(urls[0] + "boards").get();
			
			Elements elems = doc.select("#" + targetBoard);
			previousWinner = elems.select("h3").text().replace("Previous Winner: ", "");
			
			Elements rows = elems.select("tr");
			rows.remove(0); // Remove the header of the table
			
			// Sanity check on the rows to handle nothing existing well.
			if (rows.size() == 0) {
				return null;
			}
			
			for (Element row : rows) {
				Elements cols = row.select("td");
				String rank = cols.get(0).text();
				String name = cols.get(1).text();
				// Remove leading '/'.  TODO - Although unlikely to change, this should be regex to verify.
				String href = cols.get(1).select("a").attr("href").substring(1);
				String points = cols.get(2).text();
				boolean[] flair = new boolean[6];
				Arrays.fill(flair, false);//= findFlair(urls[0] + href);
				leaderBoard.add(new LeaderInfo(rank, name, href, points, flair));
			}
			
			return leaderBoard;
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<LeaderInfo> result) {
		super.onPostExecute(result);
		
		if (result != null) {
			this.activity.onParserComplete(result, previousWinner);
		}
		else {
			this.activity.onParserError();
		}
	}
	
	/**
	 * In master: this is unused currently because it will thrash the server through iterative parsing of profiles.
	 * We will need an API to allow for this functionality in production.
	 */
	private boolean[] findFlair(String url) {
		try {
			boolean[] flair = new boolean[LeaderInfo.Flairs.values().length];
			Document doc = Jsoup.connect(url).get();
			for (Element row : doc.select("tbody").get(1).select("tr")) {
				for (Element cell : row.select("td")) {
					if (cell.text().equals("Won Daily Leader Board")) {
						flair[LeaderInfo.Flairs.LeaderOfDay.ordinal()] = true;
					}
					else if (cell.text().equals("Thank You for your Support!")) {
						flair[LeaderInfo.Flairs.Donor.ordinal()] = true;
					}
					else if (cell.text().equals("Won Weekly Leader Board")) {
						flair[LeaderInfo.Flairs.LeaderOfWeek.ordinal()] = true;
					}
					else if (cell.text().equals("Won Monthly Leader Board")) {
						flair[LeaderInfo.Flairs.LeaderOfMonth.ordinal()] = true;
					}
					//else if () { // LuckySpammer, get on a leaderboard ;)
					//	flair[LeaderInfo.Flairs.LuckySpammer.ordinal()] = true;
					//}
				}
			}
			return flair;
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
			return null;
		}
	}
}
