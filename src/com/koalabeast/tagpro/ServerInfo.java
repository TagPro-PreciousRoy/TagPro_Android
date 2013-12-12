package com.koalabeast.tagpro;

public class ServerInfo {
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
	
}
