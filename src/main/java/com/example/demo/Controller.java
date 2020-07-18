package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import javax.imageio.ImageIO;

@RestController
public class Controller {
    static TypeFace typeface1,typeface2; 

	boolean textFailPixel(BufferedImage img) {
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
			downloadFileFromUrl(url,"temp");
			BufferedImage img1 = null;
	        BufferedImage img2 = null;
	        typeface1 = null;
	        typeface2=null;
			//BufferedImage img = ImageUtils.toBufferedImage(ImageIO.read(new File("temp")).getScaledInstance(1227, 690, Image.SCALE_SMOOTH));
	        BufferedImage img = ImageIO.read(new File("temp"));
			try {
				 img1 = ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface1.png")));
				 img2 = ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface2.png")));
				 typeface1 = new TypeFace(img1);
				 typeface2 = new TypeFace(img2);
				 typeface2.green_minthreshold=typeface2.blue_minthreshold=100;
				 typeface2.green_maxthreshold=typeface2.blue_maxthreshold=200;
				 typeface2.darkFillCheck=false;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Point offset = new Point(418,204);
			File tmp = new File("tmp");
			if (tmp.exists()) {
				FileUtils.deleteFile(tmp);
			} else {
				tmp.mkdir();
			}
			int cool = typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(790,242,118,26)),new File(tmp,"cool"));
			int fine = typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,274,118,26)),new File(tmp,"fine"));
			int safe = typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,308,118,26)),new File(tmp,"safe"));
			int sad = typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,341,118,26)),new File(tmp,"sad"));
			int worst = typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,372,118,26)),new File(tmp,"worst"));
			float percent = (float)typeface2.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(986,154,122,31)),new File(tmp,"percent"))/100f;
			boolean fail = textFailPixel(ImageUtils.cropImage(img, new Rectangle(492,181,1,1)));

			ImageIO.write(img,"png",new File("tmp/image.png"));
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