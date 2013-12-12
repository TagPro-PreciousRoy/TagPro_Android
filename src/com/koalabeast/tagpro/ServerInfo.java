package com.koalabeast.tagpro;

import android.os.Parcelable;
import android.os.Parcel;

public class ServerInfo implements Parcelable {
	public String name;
	public String location;
	public String url;
	public int games;
	public int players;
	
	ServerInfo(String name, String location, String url, int games, int players) {
		this.name = name;
		this.location = location;
		this.url = url;
		this.games = games;
		this.players = players;
	}
	
	public ServerInfo(Parcel in) {
		String[] data = new String[5];
		in.readStringArray(data);
		this.name = data[0];
		this.location = data[1];
		this.url = data[2];
		this.games = Integer.parseInt(data[3]);
		this.players = Integer.parseInt(data[4]);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {
			this.name, 
			this.location, 
			this.url,
			Integer.toString(this.games), 
			Integer.toString(this.players)
		});
	}
	
	@SuppressWarnings("rawtypes") // Bad to do, I know.
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public ServerInfo createFromParcel(Parcel in) {
			return new ServerInfo(in);
		}
		
		public ServerInfo[] newArray(int size) {
			return new ServerInfo[size];
		}
	};
}
