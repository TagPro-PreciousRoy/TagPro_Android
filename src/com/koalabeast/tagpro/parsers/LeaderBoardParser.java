package com.koalabeast.tagpro.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

import com.koalabeast.tagpro.infocontainers.LeaderInfo;

public class LeaderBoardParser extends AsyncTask<String, Void, List<List<LeaderInfo>>> {
	public static final String[] divs = {"Day", "Week", "Month"};
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
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
				
				// Ugly way to handle broken/empty leaderboards (maptest)
				if (rows.size() == 0 ) {
					this.cancel(true);
				}
				
				for (Element row : rows) {
					Elements cols = row.select("td");
					board.add(new LeaderInfo(cols.get(0).text(), cols.get(1).text(), cols.get(1).select("a").attr("href"), cols.get(2).text()));
				}
				leaderBoards.add(board);
			}
		}
		catch (Exception e) {
			Log.e("[HTML-PARSE]", Log.getStackTraceString(e));
			return null;
		}
		
		return leaderBoards;
	}
}
