package com.example.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CharacterEncodingFilter;

import sig.TypeFace;
import sig.utils.FileUtils;
import sig.utils.ImageUtils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.imageio.ImageIO;

@RestController
public class Controller {    
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");
        registrationBean.setFilter(characterEncodingFilter);
        return registrationBean;
    }

	public static boolean textFailPixel(BufferedImage img) {
		Color failPixel = new Color(img.getRGB(0, 0));
		//System.out.println(failPixel);
		//r=128,g=5,b=232
		return failPixel.getRed()>=50 && failPixel.getRed()<=150 && failPixel.getGreen()>=50 && failPixel.getGreen()<=150 && failPixel.getBlue()>=50 && failPixel.getBlue()<=150;
	}

    @GetMapping("/image")
    public HashMap<String,String> helloWorld(@RequestParam("url") String url){
		HashMap<String,String> data = new HashMap<>();
		//System.out.println(new File(".").getAbsolutePath());
		try {
			System.out.println(url);
			downloadFileFromUrl(url,"temp");
			//BufferedImage img = ImageUtils.toBufferedImage(ImageIO.read(new File("temp")).getScaledInstance(1227, 690, Image.SCALE_SMOOTH));
	        BufferedImage img = ImageIO.read(new File("temp"));
	        if (img.getWidth()!=1200) {
	        	//Resize.
	        	img = ImageUtils.toBufferedImage(ImageIO.read(new File("temp")).getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
	        }
			Point offset = new Point(418,204);
			File tmp = new File("tmp");
			if (tmp.exists()) {
				FileUtils.deleteFile(tmp);
			} else {
				tmp.mkdir();
			}
			String song = getSongTitle(img);
			int cool = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(790,242,118,26)),new File(tmp,"cool"));
			int fine = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,274,118,26)),new File(tmp,"fine"));
			int safe = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,308,118,26)),new File(tmp,"safe"));
			int sad = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,341,118,26)),new File(tmp,"sad"));
			int worst = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,372,118,26)),new File(tmp,"worst"));
			float percent = DemoApplication.typeface2.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(986,154,122,31)),new File(tmp,"percent"))/100f;
			boolean fail = textFailPixel(ImageUtils.cropImage(img, new Rectangle(514,171,1,1)));

			ImageIO.write(img,"png",new File("tmp/image.png"));
			data.put("songname",song);
			data.put("cool",Integer.toString(cool));
			data.put("fine",Integer.toString(fine));
			data.put("safe",Integer.toString(safe));
			data.put("sad",Integer.toString(sad));
			data.put("worst",Integer.toString(worst));
			data.put("percent",Float.toString(percent));
			data.put("fail",Boolean.toString(fail));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
    }
	
	public static String getSongTitle(BufferedImage img) {
		final int THRESHOLD=1;
		float lowestMatching = Integer.MAX_VALUE;
		SongData matchingSong = null;
		//There are 2304 pixels total. Once 2188 match, we'll call it good.
		for (SongData song : DemoApplication.songs) {
			float matching = 0;
			for (int y=0;y<288;y++) {
				for (int x=0;x<8;x++) {
					Color p2 = song.data[(y*8)+x];
					Color p1 = new Color(img.getRGB(x+352, y+288));
					matching+=Math.sqrt(Math.pow(p2.getRed()-p1.getRed(), 2)+Math.pow(p2.getGreen()-p1.getGreen(), 2)+Math.pow(p2.getBlue()-p1.getBlue(), 2));
				}
			}
			if (matching<lowestMatching) {
				lowestMatching=matching;
				matchingSong = song;
			}

            /*try {
				PrintStream out = new PrintStream(System.out, true, "UTF-8");
				out.println("Comparing to "+song.song+": "+matching);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}*/
            
		}
		//System.out.println("Lowest: "+lowestMatching);
		return matchingSong.song;
	}

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
		    } catch (ConnectException | FileNotFoundException e) {
		    	System.out.println("Failed to connect, moving on...");
		    }
	  }

}