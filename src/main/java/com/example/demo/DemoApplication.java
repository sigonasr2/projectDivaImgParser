package com.example.demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sig.utils.FileUtils;

@SpringBootApplication
public class DemoApplication {
	
	public static List<SongData> songs = new ArrayList<SongData>();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		/*File dir = new File("resources");
		for (String s : dir.list()) {
			StringBuilder sb = new StringBuilder(s.replace(".jpg","")).append(":");
			boolean first=true;
			try {
				BufferedImage img = ImageIO.read(new File(dir,s));
				for (int y=288;y<288+288;y++) {
					for (int x=352;x<352+8;x++) {
						if (!first) {
							sb.append(",");
						} else {
							first=false;
						}
						sb.append(img.getRGB(x, y));
					}
				}
				FileUtils.logToFile(sb.toString(), "colorData");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		String[] str = FileUtils.readFromFile("colorData");
		for (String s : str) {
			songs.add(new SongData(s));
		}
		//System.out.println(songs);
	}

}
