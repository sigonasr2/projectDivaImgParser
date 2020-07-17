package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import javax.imageio.ImageIO;

@RestController
public class Controller {
	
	public static void downloadFileFromUrl(String url, String file) throws IOException{
		  File filer = new File(file);
		  filer.createNewFile();
		  
		  URL website = new URL(url);
		  HttpURLConnection connection = (HttpURLConnection) website.openConnection();
		    /*for (String s : connection.getHeaderFields().keySet()) {
		    	System.out.println(s+": "+connection.getHeaderFields().get(s));
		    }*/
		    connection.setRequestMethod("GET");
		    //connection.setRequestProperty("Content-Type", "application/json");
		    try {
			  ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
			  FileOutputStream fos = new FileOutputStream(file);
			  fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			  fos.close();
		    } catch (ConnectException e) {
		    	System.out.println("Failed to connect, moving on...");
		    }
	  }

    @GetMapping("/image")
    public HashMap<String,String> helloWorld(@RequestParam("url") String url){
		try {
			downloadFileFromUrl("http://pbs.twimg.com/media/EdKE8xzVcCEf1qd.jpg","temp");
			BufferedImage img = ImageIO.read(new File("temp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

}