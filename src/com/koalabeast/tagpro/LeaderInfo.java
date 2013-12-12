package com.koalabeast.tagpro;

import java.net.URL;
import java.net.MalformedURLException;

public class LeaderInfo {
	private int rank;
	private String name;
	private int points;
	private URL profile;
	
	public LeaderInfo(int rank, String name, int points, String profileUrl) {
		this.rank = rank;
		this.name = name;
		this.points = points;
		try {
			this.profile = new URL(profileUrl);
		}
		catch (MalformedURLException e) {
			this.profile = null;
		}
	}

	public int getRank() {
		return this.rank;
	}

	public String getName() {
		return this.name;
	}
	
	public int getPoints() {
		return this.points;
	}
	
	public URL getProfileLink() {
		return this.profile;
	}
}
