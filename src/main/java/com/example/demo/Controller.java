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

import sig.MyRobot;
import sig.Result;
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
				/*downloadFileFromUrl(url,"temp");
				//BufferedImage img = ImageUtils.toBufferedImage(ImageIO.read(new File("temp")).getScaledInstance(1227, 690, Image.SCALE_SMOOTH));
				f = new File("temp");*/
		        img = ImageIO.read(new URL(url+":orig"));
		        img = ImageIO.read(new URL(url+":orig"));
		        f = new File("temp.png");
		        ImageIO.write(img,"png",f);
			}
			
			img = CropFutureToneImage(img);
			//String song,String diff,int cool,int fine,int safe,int sad,int worst,float percent
			Result r=new Result("","",-1,-1,-1,-1,-1,0f);
			try {
				r = DemoApplication.typeface1.getAllData(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	        if (img.getWidth()!=1200) {
	        	//Resize.
	        	img = ImageUtils.toBufferedImage(img.getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
				//ImageIO.write(img,"png",new File("testresize.png"));
	        }
			Point offset = new Point(418,204);
			File tmp = new File("tmp");
			if (tmp.exists()) {
				FileUtils.deleteFile(tmp);
			}
			tmp.mkdir();
			String song = getSongTitle(img);
			
			ImageIO.write(img,"png",new File("tmp/image.png"));
			data.put("songname",song);
			data.put("cool",Integer.toString(r.cool));
			data.put("fine",Integer.toString(r.fine));
			data.put("safe",Integer.toString(r.safe));
			data.put("sad",Integer.toString(r.sad));
			data.put("worst",Integer.toString(r.worst));
			data.put("percent",Float.toString(r.percent));
			data.put("fail",Boolean.toString(r.fail));
			data.put("difficulty",r.difficulty);
			data.put("combo",Integer.toString(r.combo));
			data.put("gameScore",Integer.toString(r.score));
			data.put("mod",r.mod);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static BufferedImage CropFutureToneImage(BufferedImage img) throws IOException {
		Point crop1 = null;
		Point crop2 = null;
		
		Color col = new Color(img.getRGB(0, 0));
		BufferedImage img2 = ImageUtils.toBufferedImage(img.getScaledInstance(1280 , 720, Image.SCALE_SMOOTH));
		ColorRegion ft_results = new ColorRegion(img2,new Rectangle(81,35,80,37));
		if ((col.getRed()<=5&&col.getGreen()<=5&&col.getBlue()<=5)) {
			MyRobot.FUTURETONE=true;
			boolean done=false;
			for (int x=img.getWidth()-1;x>=img.getWidth()*(7f/8);x--) {
				for (int y=0;y<img.getHeight()/8;y++) {
					col = new Color(img.getRGB(x, y));
					if (col.getRed()>=100&&col.getGreen()>=100&&col.getBlue()>=100) {
						crop1 = new Point(x,y);
						done=true;
						break;
					}
				}
				if (done) {
					break;
				}
			}
			done=false;
			for (int x=0;x<img.getWidth()/8;x++) {
				for (int y=img.getHeight()-1;y>=img.getHeight()*(7f/8);y--) {
					col = new Color(img.getRGB(x, y));
					if (col.getRed()>=100&&col.getGreen()>=100&&col.getBlue()>=100) {
						crop2 = new Point(x,y);
						done=true;
						break;
					}
				}
				if (done) {
					break;
				}
			}
			img = img.getSubimage(crop2.x, crop1.y, crop1.x-crop2.x, crop2.y-crop1.y);
		} else {
			if (ft_results.getAllRange(30,150,60,180,60,180)) {
				MyRobot.FUTURETONE=true;
			} else {
				MyRobot.FUTURETONE=false;
			}
		}
		//System.out.println("Future Tone? "+MyRobot.FUTURETONE);
		return img;
	}
	
	public static String getSongTitle(BufferedImage img) throws IOException {
		return getSongTitle(img,false);
	}
	
	public static String getSongTitle(BufferedImage img,boolean debugging) throws IOException {
		final int THRESHOLD=1;
		float lowestMatching = Integer.MAX_VALUE;
		SongData matchingSong = null;
		//There are 2304 pixels total. Once 2188 match, we'll call it good.
		if (MyRobot.FUTURETONE) {
			File f = new File("debugcol");
			FileUtils.deleteFile(f);
			f.mkdirs();
			for (SongData song : DemoApplication.FTsongs) {
				BufferedImage debug = new BufferedImage(16,288,BufferedImage.TYPE_INT_ARGB);
				float matching = 0;
				for (int y=0;y<288;y++) {
					for (int x=0;x<8;x++) {
						Color p2 = song.data[(y*8)+x];
						Color p1 = new Color(img.getRGB(x+352, y+288));
						matching+=ImageUtils.distanceToColor(p2,p1);
						if (debugging) {
							debug.setRGB(x,y,p2.getRGB());
							debug.setRGB(x+8,y,p1.getRGB());
						}
					}
				}
				if (matching<lowestMatching) {
					lowestMatching=matching;
					matchingSong = song;
				}
				if (debugging) {
		            try {
						PrintStream out = new PrintStream(System.out, true, "UTF-8");
						out.println("Comparing to "+song.song.replaceAll("/","").replaceAll("\\*","").replaceAll(":","")+": "+matching);
						//ImageIO.write(debug,"png",new File("debugcol",song.song.replaceAll("/","").replaceAll("\\*","").replaceAll(":","")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			for (SongData song : DemoApplication.songs) {
				BufferedImage debug = new BufferedImage(16,288,BufferedImage.TYPE_INT_ARGB);
				float matching = 0;
				for (int y=0;y<288;y++) {
					for (int x=0;x<8;x++) {
						Color p2 = song.data[(y*8)+x];
						Color p1 = new Color(img.getRGB(x+352, y+288));
						matching+=ImageUtils.distanceToColor(p2,p1);
						if (debugging) {
							debug.setRGB(x,y,p2.getRGB());
							debug.setRGB(x+8,y,p1.getRGB());
						}
					}
				}
				if (matching<lowestMatching) {
					lowestMatching=matching;
					matchingSong = song;
				}

				if (debugging) {
					try {
						PrintStream out = new PrintStream(System.out, true, "UTF-8");
						out.println("Comparing to "+song.song+": "+matching);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
	            
			}
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