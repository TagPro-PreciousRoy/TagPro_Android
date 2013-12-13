package com.koalabeast.tagpro;

public class LeaderInfo {
	private int rank;		// Rank on leaderboard
	private String name;	// Username
	private String href;	// Link to Profile
	private int points;		// Number of points
	
	public LeaderInfo (String rank, String name, String href, String points) {
		this.rank = Integer.parseInt(rank);
		this.name = name;
		this.href = href;
		this.points = Integer.parseInt(points);
	}
	
	public int getRank() {
		return rank;
	}
	
	public String getName() {
		return name;
	}
	
	public String getHref() {
		return href;
	}
	
	public int getPoints() {
		return points;
	}
}
