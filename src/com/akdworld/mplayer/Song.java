package com.akdworld.mplayer;

public class Song {
	private long id;
	private String title;
	private String artist;
	private String path;
	private String year;
	private String duration;
	private String album;

	public Song(long songID, String songTitle, String songArtist,
			String songPath, String songAlbum, String songYear,
			String songDuration) {
		id = songID;
		title = songTitle;
		artist = songArtist;
		path = songPath;
		year = songYear;
		duration = songDuration;
		album = songAlbum;
	}

	public long getID() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getPath() {
		return path;
	}

	public String getYear() {
		return year;
	}

	public String getDuration() {
		return duration;
	}

	public String getAlbum() {
		return album;
	}

}
