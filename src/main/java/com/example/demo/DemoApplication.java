package com.example.demo;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

import sig.TypeFace;
import sig.utils.FileUtils;
import sig.utils.ImageUtils;

@SpringBootApplication
public class DemoApplication {
	
	public static List<SongData> songs = new ArrayList<SongData>();
	public static TypeFace typeface1;
	public static TypeFace typeface2;
	static Integer totalConfidence = 0;
	static TestResult result = null;
	static Integer[]controls = new Integer[]{150, 255, 153, 255, 159, 255, 0, 159, 3, 171, 8, 155};
	static Integer[]lastControls = new Integer[]{150, 255, 153, 255, 159, 255, 0, 159, 3, 171, 8, 155};
	static Integer generation = 30; //Confidence level.

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		/*File dir = new File("resources");
		for (String s : dir.list()) {
			StringBuilder sb = new StringBuilder(s.replace(".jpg","")).append(":");
			boolean first=true;
			try {
				BufferedImage img = ImageIO.read(new File(dir,s));
		        if (img.getWidth()!=1200) {
		        	img = ImageUtils.toBufferedImage(img.getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
		        }
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
		/*File dir = new File("resources");
		StringBuilder sb = new StringBuilder();
		for (String s : dir.list()) {
			sb.append("and name!='").append(s.replace(".jpg","")).append("' ");
		}
		FileUtils.logToFile(sb.toString(), "command");*/
		BufferedImage img1 = null;
        BufferedImage img2 = null;
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
		
		String[] str = FileUtils.readFromFile("colorData");
		for (String s : str) {
			songs.add(new SongData(s));
		}
		
		RunTest("＊ハロー、プラネット。 (I.M.PLSE-EDIT).jpg",336,128,24,6,93,58.85f,true);
		RunTest("16 -out of the gravity-.jpg",554,45,1,0,1,101.36f,false);
		RunTest("39.jpg",531,71,2,0,2,97.82f,false);
		RunTest("39みゅーじっく！.jpg",573,175,5,0,18,91.22f,false);
		RunTest("1925.jpg",510,115,14,7,22,77.79f,false);
		RunTest("ACUTE.jpg",478,64,1,1,5,95.76f,false);
		RunTest("AFTER BURNER.jpg",370,113,25,16,30,68.76f,true);
		RunTest("Blackjack.jpg",415,123,15,7,50,71.22f,false);
		RunTest("Catch the Wave.jpg",603,72,0,0,1,100.65f,false);
		RunTest("Dear.jpg",402,64,0,0,1,100.90f,false);
		RunTest("DECORATOR.jpg",436,100,1,0,6,93.52f,false);
		RunTest("Fire◎Flower.jpg",86.79f,false);
		RunTest("DYE.jpg",530,106,7,2,13,84.77f,false);
		RunTest("erase or zero.jpg",442,70,0,0,2,100.12f,false);
		RunTest("FREELY TOMORROW.jpg",367,57,0,0,0,102.84f,false);
		RunTest("from Y to Y.jpg",350,49,6,1,8,86.35f,false);
		RunTest("Glory 3usi9.jpg",468,43,0,0,2,101.62f,false);
		RunTest("Hand in Hand.jpg",401,54,1,0,3,97.58f,false);
		RunTest("Hello, Worker.jpg",439,118,7,1,14,89.93f,false);
		RunTest("Just Be Friends.jpg",510,107,6,0,12,89.38f,false);
		RunTest("Knife.jpg",327,85,14,9,27,51.96f,true);
		RunTest("LIKE THE WIND.jpg",330,144,20,9,20,72.06f,false);
		RunTest("LOL -lots of laugh-.jpg",489,59,1,2,2,96.36f,false);
		RunTest("magnet.jpg",435,101,18,4,35,76.98f,false);
		RunTest("No Logic.jpg",491,101,11,5,15,86.32f,false);
		System.out.println("All Tests passed!");
	}
	
	private static void ModifyResults() {
		//generation+=result.count-1;
		if (!result.passed) {
			if (result.count-1>totalConfidence) {
				lastControls = controls.clone();
				totalConfidence = Math.max(result.count-1,totalConfidence);
				System.out.println("Passed "+(result.count-1)+" test! Confidence:"+totalConfidence+" Storing lastControls: "+Arrays.toString(lastControls));
			}
			if (result.count-1<totalConfidence) {
				//System.out.println("Restoring previous trial...");
				controls = lastControls.clone();
			}
			if (generation.equals(0)) {
				for (int i=0;i<controls.length;i+=2) {
					controls[i]=(int)(Math.random()*255);
					controls[i+1]=controls[i]+(int)(Math.random()*(255-controls[i]));
				}
			} else {
				for (int i=0;i<controls.length;i++) {
					if (Math.random()*generation<1) {
						controls[i]=lastControls[i].intValue();
						controls[i]+=(int)((Math.random()*(255*(1f/(generation))))-(255*(1f/(generation*2f))));
						controls[i]=Math.max(controls[i], 0);
						controls[i]=Math.min(255, controls[i]);
					}
				}
			}
			System.out.println(Arrays.toString(controls));
		} else {
			lastControls = controls.clone();
			System.out.println("Passed with "+Arrays.toString(controls));
		}
	}
	
	static void RunTest(String _img,float _percent,boolean _fail) {
		long startTime = System.currentTimeMillis();
		String testdir="resources";
		Point offset = new Point(418,204);
		File tmp = new File("tmp");
		if (tmp.exists()) {
			FileUtils.deleteFile(tmp);
		}
		tmp.mkdir();
		BufferedImage img=null;
		try {
			img = ImageIO.read(new File(testdir,_img));
			if (img.getWidth()!=1200) {
	        	//Resize.
	        	img = ImageUtils.toBufferedImage(ImageIO.read(new File(testdir,_img)).getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
	        }
			ImageIO.write(img,"png",new File("image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String song = Controller.getSongTitle(img);
		float percent = DemoApplication.typeface2.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(986,154,122,31)),new File(tmp,"percent"),false)/100f;
		boolean fail = Controller.textFailPixel(ImageUtils.cropImage(img, new Rectangle(514,171,1,1)));
		
		String _name = _img.replace(".jpg","");
		song = (song.equalsIgnoreCase("PIANOGIRL"))?"PIANO*GIRL":(song.equalsIgnoreCase("16 -out of the gravity-"))?"1/6 -out of the gravity-":song;
		_name = (_name.equalsIgnoreCase("PIANOGIRL"))?"PIANO*GIRL":(_name.equalsIgnoreCase("16 -out of the gravity-"))?"1/6 -out of the gravity-":_name;
		
		Assert.isTrue(song.equalsIgnoreCase(_name),"Expected song name to be "+_name+", got "+song);
		Assert.isTrue(percent == _percent,"Expected percent to be "+_percent+", got "+percent);
		Assert.isTrue(fail == _fail,"Expected fail to be "+_fail+", got "+fail);
		System.out.println(" Passed ("+(System.currentTimeMillis()-startTime)+"ms)!");
	}

	static void RunTest(String _img,int _cool,int _fine, int _safe, int _sad, int _worst, float _percent,boolean _fail) {
		//System.out.println("Running test "+_img);
		long startTime = System.currentTimeMillis();
		String testdir="resources";
		Point offset = new Point(418,204);
		File tmp = new File("tmp");
		if (tmp.exists()) {
			FileUtils.deleteFile(tmp);
		}
		tmp.mkdir();
		BufferedImage img=null;
		try {
			img = ImageIO.read(new File(testdir,_img));
			if (img.getWidth()!=1200) {
	        	//Resize.
	        	img = ImageUtils.toBufferedImage(ImageIO.read(new File(testdir,_img)).getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
	        }
			ImageIO.write(img,"png",new File("image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String song = Controller.getSongTitle(img);
		int cool = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(790,242,118,26)),new File(tmp,"cool"),false);
		int fine = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,274,118,26)),new File(tmp,"fine"),false);
		int safe = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,308,118,26)),new File(tmp,"safe"),false);
		int sad = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,341,118,26)),new File(tmp,"sad"),false);
		int worst = DemoApplication.typeface1.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(792,372,118,26)),new File(tmp,"worst"),false);
		float percent = DemoApplication.typeface2.extractNumbersFromImage(ImageUtils.cropImage(img,new Rectangle(986,154,122,31)),new File(tmp,"percent"),false)/100f;
		boolean fail = Controller.textFailPixel(ImageUtils.cropImage(img, new Rectangle(514,171,1,1)));
		
		String _name = _img.replace(".jpg","");
		song = (song.equalsIgnoreCase("PIANOGIRL"))?"PIANO*GIRL":(song.equalsIgnoreCase("16 -out of the gravity-"))?"1/6 -out of the gravity-":song;
		_name = (_name.equalsIgnoreCase("PIANOGIRL"))?"PIANO*GIRL":(_name.equalsIgnoreCase("16 -out of the gravity-"))?"1/6 -out of the gravity-":_name;
		
		Assert.isTrue(song.equalsIgnoreCase(_name),"Expected song name to be "+_name+", got "+song);
		Assert.isTrue(cool == _cool,"Expected cool count to be "+_cool+", got "+cool);
		Assert.isTrue(fine == _fine,"Expected fine count to be "+_fine+", got "+fine);
		Assert.isTrue(safe == _safe,"Expected safe count to be "+_safe+", got "+safe);
		Assert.isTrue(sad == _sad,"Expected sad count to be "+_sad+", got "+sad);
		Assert.isTrue(worst == _worst,"Expected worst count to be "+_worst+", got "+worst);
		Assert.isTrue(percent == _percent,"Expected percent to be "+_percent+", got "+percent);
		Assert.isTrue(fail == _fail,"Expected fail to be "+_fail+", got "+fail);
		System.out.println(" Passed ("+(System.currentTimeMillis()-startTime)+"ms)!");
	}

}
