package com.koalabeast.tagpro.infocontainers;

import android.os.Parcel;
import android.os.Parcelable;

public class LeaderInfo implements Parcelable {
	public static enum Flairs {LeaderOfDay, LeaderOfWeek, LeaderOfMonth, Donor, Developer, LuckySpammer};
	private int rank;		// Rank on leader board
	private String name;	// User name
	private String href;	// Link to Profile
	private int points;		// Number of points
	private boolean[] flair = new boolean[Flairs.values().length]; // They either have it, or they don't.
	
	public LeaderInfo (String rank, String name, String href, String points, boolean[] flair) {
		this.rank = Integer.parseInt(rank);
		this.name = name;
		this.href = href;
		this.points = Integer.parseInt(points);
		this.flair = flair.clone();
	}
	
	public LeaderInfo (Parcel in) {
		String[] data = new String[4 + Flairs.values().length]; // The other data fields are unlikely to change, but the flair may.
		in.readStringArray(data);
		this.rank = Integer.parseInt(data[0]);
		this.name = data[1];
		this.href = data[2].substring(1);
		this.points = Integer.parseInt(data[3]);
		for (int i = 0; i < Flairs.values().length; i++) {
			this.flair[i] = Boolean.valueOf(data[i + 4]);
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {
			Integer.toString(rank),
			name,
			href,
			Integer.toString(points),
			String.valueOf(flair[0]),
			String.valueOf(flair[1]),
			String.valueOf(flair[2]),
			String.valueOf(flair[3]),
			String.valueOf(flair[4]),
			String.valueOf(flair[5])
		});
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public LeaderInfo createFromParcel(Parcel in) {
			return new LeaderInfo(in);
		}
		
		public LeaderInfo[] newArray(int size) {
			return new LeaderInfo[size];
		}
	};
	
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
	
	public boolean hasFlair(Flairs flair) {
		return this.flair[flair.ordinal()];
	}
	public boolean[] getFlair() {
		return flair;
	}
}
