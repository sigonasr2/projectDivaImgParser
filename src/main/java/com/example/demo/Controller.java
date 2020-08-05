package com.example.demo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.annotation.JsonFormat;

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
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
	
    @PostMapping("/image")
    public String postImage(@RequestBody Map<String,String> body){
		HashMap<String,String> data = new HashMap<>();
    	if (body.containsKey("url") && body.containsKey("user") && body.containsKey("auth")) {
    		HashMap<String,String> imageData = GetImageData(body.get("url"));
    		data.putAll(imageData);
    		data.put("user",body.get("user"));
    		data.put("auth",body.get("auth"));
    		//System.out.println("In here.");
    		HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("http://projectdivar.com/submit");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("song", data.get("songname")));
			params.add(new BasicNameValuePair("username", data.get("user")));
			params.add(new BasicNameValuePair("authentication_token", data.get("auth")));
			params.add(new BasicNameValuePair("difficulty", data.get("difficulty")));
			params.add(new BasicNameValuePair("cool", data.get("cool")));
			params.add(new BasicNameValuePair("fine", data.get("fine")));
			params.add(new BasicNameValuePair("safe", data.get("safe")));
			params.add(new BasicNameValuePair("sad", data.get("sad")));
			params.add(new BasicNameValuePair("worst", data.get("worst")));
			params.add(new BasicNameValuePair("percent", data.get("percent")));
			params.add(new BasicNameValuePair("fail", data.get("fail")));
			params.add(new BasicNameValuePair("combo", data.get("combo")));
			params.add(new BasicNameValuePair("mod", data.get("mod")));
			params.add(new BasicNameValuePair("gameScore", data.get("gameScore")));
			params.add(new BasicNameValuePair("src", body.get("url")));
			try {
				httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			//Execute and get the response.
			HttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (IOException e) {
				e.printStackTrace();
			}
			HttpEntity entity = response.getEntity();
			String result = "";
			if (entity != null) {
			    try (InputStream instream = entity.getContent()) {
			    	Scanner s = new Scanner(instream).useDelimiter("\\A");
			    	result = s.hasNext() ? s.next() : "";
			    	instream.close();
			    } catch (UnsupportedOperationException | IOException e) {
					e.printStackTrace();
				}
			}
	    	return result;
    	}
		return "Invalid parameters!";
    }

    @GetMapping("/image")
    public HashMap<String,String> getImage(@RequestParam("url") String url){
		return GetImageData(url);
    }

	private HashMap<String, String> GetImageData(String url) {
		HashMap<String,String> data = new HashMap<>();
		//System.out.println(new File(".").getAbsolutePath());
		//System.out.println(body);
		try {
			System.out.println(url);
			File f;
	        BufferedImage img = null;
			if (url.contains("http://projectdivar.com/")) {
				//System.out.println("Locally available. "+"./"+url.replace("http://projectdivar.com/", ""));
				f = new File("../../server/"+url.replace("http://projectdivar.com/", ""));
				img = ImageIO.read(f);
			} else {
				downloadFileFromUrl(url,"temp");
				//BufferedImage img = ImageUtils.toBufferedImage(ImageIO.read(new File("temp")).getScaledInstance(1227, 690, Image.SCALE_SMOOTH));
				f = new File("temp");
		        img = ImageIO.read(f);
			}
	        if (img.getWidth()!=1200) {
	        	//Resize.
	        	img = ImageUtils.toBufferedImage(ImageIO.read(f).getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
	        }
			Point offset = new Point(418,204);
			File tmp = new File("tmp");
			if (tmp.exists()) {
				FileUtils.deleteFile(tmp);
			} else {
				tmp.mkdir();
			}
			String song = getSongTitle(img);
			int cool = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(790,242,123,26)),new File(tmp,"cool"),false);
			int fine = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,274,123,26)),new File(tmp,"fine"),false);
			int safe = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,308,123,26)),new File(tmp,"safe"),false);
			int sad = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,341,123,26)),new File(tmp,"sad"),false);
			int worst = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,372,123,26)),new File(tmp,"worst"),false);
			int combo = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(926,405,127,24)),new File(tmp,"combo"),false);
			int score = DemoApplication.typeface3.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(808,537,242,36)),new File(tmp,"score"),false);
			float percent = DemoApplication.typeface2.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(986,151,134,37)),new File(tmp,"percent"),false)/100f;
			boolean fail = Controller.textFailPixel(ImageUtils.cropImage(img, new Rectangle(514,171,1,1)));
			String difficulty = Controller.GetDifficulty(ImageUtils.cropImage(img,new Rectangle(580,94,1,1)));
			String getMod = Controller.GetMod(ImageUtils.cropImage(img,new Rectangle(993,70,105,54))); //"","HS","HD","SD"
			
			ImageIO.write(img,"png",new File("tmp/image.png"));
			data.put("songname",song);
			data.put("cool",Integer.toString(cool));
			data.put("fine",Integer.toString(fine));
			data.put("safe",Integer.toString(safe));
			data.put("sad",Integer.toString(sad));
			data.put("worst",Integer.toString(worst));
			data.put("percent",Float.toString(percent));
			data.put("fail",Boolean.toString(fail));
			data.put("difficulty",difficulty);
			data.put("combo",Integer.toString(combo));
			data.put("gameScore",Integer.toString(score));
			data.put("mod",getMod);
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
					matching+=ImageUtils.distanceToColor(p2,p1);
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

	public static String GetDifficulty(BufferedImage img) {
		final int TOLERANCE = 30;
		Color p1 = new Color(169,13,236);
		Color p2 = new Color(img.getRGB(0, 0));
		if (ImageUtils.distanceToColor(p1, p2)<TOLERANCE) {
			return "EXEX";
		}
		p1 = new Color(253,14,81);
		if (ImageUtils.distanceToColor(p1, p2)<TOLERANCE) {
			return "EX";
		}
		p1 = new Color(251,191,0);
		if (ImageUtils.distanceToColor(p1, p2)<TOLERANCE) {
			return "H";
		}
		p1 = new Color(20,234,0);
		if (ImageUtils.distanceToColor(p1, p2)<TOLERANCE) {
			return "N";
		}
		p1 = new Color(95,243,255);
		if (ImageUtils.distanceToColor(p1, p2)<TOLERANCE) {
			return "E";
		}
		return null;
	}

	public static String GetMod(BufferedImage img) {
		final int TOLERANCE = 30;
		Color hs = new Color(176,51,55);
		Color hd = new Color(201,179,31);
		Color sd = new Color(67,144,174);
		Color p_hs = new Color(img.getRGB(22, 8));
		Color p_hd = new Color(img.getRGB(51, 28));
		Color p_sd = new Color(img.getRGB(81, 8));
		if (ImageUtils.distanceToColor(p_hs, hs)<TOLERANCE) {
			return "HS";
		}
		if (ImageUtils.distanceToColor(p_hd, hd)<TOLERANCE) {
			return "HD";
		}
		if (ImageUtils.distanceToColor(p_sd, sd)<TOLERANCE) {
			return "SD";
		}
		return "";
	}

}