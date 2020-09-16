package com.example.demo;

import java.awt.Color;

public class SongData {
	String song;
	Color[] data;
	
	public SongData(String parseStr) {
		String[] split = parseStr.split(":");
		song = split[0];
		song = DemoApplication.getCorrectSongName(song); 
		String[] colors = split[1].split(",");
		data = new Color[colors.length];
		for (int i=0;i<colors.length;i++) {
			data[i] = new Color(Integer.parseInt(colors[i]),true);
		}
	}
	
	public String toString() {
		return new StringBuilder(song).append(" - Data: ["+data.length+"]").toString();
	}
}
