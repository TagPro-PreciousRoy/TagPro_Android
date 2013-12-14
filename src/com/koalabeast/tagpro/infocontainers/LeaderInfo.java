package com.koalabeast.tagpro.infocontainers;

import android.os.Parcel;
import android.os.Parcelable;

public class LeaderInfo implements Parcelable {
	private int rank;		// Rank on leader board
	private String name;	// User name
	private String href;	// Link to Profile
	private int points;		// Number of points
	
	public LeaderInfo (String rank, String name, String href, String points) {
		this.rank = Integer.parseInt(rank);
		this.name = name;
		this.href = href;
		this.points = Integer.parseInt(points);
	}
	
	public LeaderInfo (Parcel in) {
		String[] data = new String[4];
		in.readStringArray(data);
		this.rank = Integer.parseInt(data[0]);
		this.name = data[1];
		this.href = data[2].substring(1);
		this.points = Integer.parseInt(data[3]);
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
			Integer.toString(points)
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
}
