package com.example.demo;

import java.awt.Color;
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

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

import sig.MyRobot;
import sig.Result;
import sig.TypeFace2;
import sig.utils.FileUtils;
import sig.utils.ImageUtils;

@SpringBootApplication
public class DemoApplication {
	
	public static List<SongData> songs = new ArrayList<SongData>();
	public static List<SongData> FTsongs = new ArrayList<SongData>();
	public static TypeFace2 typeface1;
	public static TypeFace2 typeface2;
	public static TypeFace2 typeface3;
	static String testdir="resources";
	static Integer totalConfidence = 0;
	static TestResult result = null;
	static Integer generation = 30; //Confidence level.
	
	final static boolean REDO_COLOR_DATA = false; 

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DemoApplication.class, args);
		
		File debugFolder = new File("debug");
		FileUtils.deleteFile(debugFolder);
		
		if (REDO_COLOR_DATA) {
			File colordat = new File("colorData");
			colordat.delete();
			colordat = new File("FTcolorData");
			colordat.delete();
			SearchSongs("resources","colorData");
			SearchSongs("resourcesFT","FTcolorData");
		}
		/*File dir = new File("resources");
		StringBuilder sb = new StringBuilder();
		for (String s : dir.list()) {
			sb.append("and name!='").append(s.replace(".jpg","")).append("' ");
		}
		FileUtils.logToFile(sb.toString(), "command");*/
		typeface1 = new TypeFace2(ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface.png"))),
					 ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface2.png"))),
					 ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface3.png"))),
					 ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface4.png"))),
					 ImageUtils.toCompatibleImage(ImageIO.read(new File("typeface5.png"))));
		
		String[] str = FileUtils.readFromFile("colorData");
		for (String s : str) {
			songs.add(new SongData(s));
		}
		str = FileUtils.readFromFile("FTcolorData");
		for (String s : str) {
			FTsongs.add(new SongData(s));
		}
		RunTest("test12.jpg","アゲアゲアゲイン",612,32,0,0,0,104.78f,false,"H","",644,734316,false);
		RunTest("test10.jpg","どりーみんチュチュ",456,207,31,11,18,71.06f,false,"EX","",96,544610,false);
		RunTest("test9.jpg","アゲアゲアゲイン",436,217,24,3,38,72.49f,false,"EX","",82,552330,false);
		RunTest("test8.png","メテオ",478,57,2,0,1,100.19f,false,"EX","",229,695618,false);
		RunTest("test6.png","ありふれたせかいせいふく",586,161,6,5,9,85.75f,false,"EX","",425,672452,false);
		RunTest("test5.png","ZIGG-ZAGG",88,18,1,2,13,11.87f,true,"EXEX","HS",28,88935,false);
		RunTest("test3.png","メルト",39,9,0,0,9,7.44f,true,"EXEX","HS",48,40040,false);
		RunTest("16 -out of the gravity-.jpg",554,45,1,0,1,101.36f,false,"EX","HS",339,606780);
		RunTest("＊ハロー、プラネット。 (I.M.PLSE-EDIT).jpg",336,128,24,6,93,58.85f,true,"EX","",52,308760);
		
		RunTest("39.jpg",531,71,2,0,2,97.82f,false,"EXEX","HS",324,832390);
		RunTest("39みゅーじっく！.jpg",573,175,5,0,18,91.22f,false,"EX","HS",354,754140);
		RunTest("1925.jpg",510,115,14,7,22,77.79f,false,"EX","HS",85,564860);
		RunTest("ACUTE.jpg",478,64,1,1,5,95.76f,false,"EX","HS",197,505210);
		RunTest("AFTER BURNER.jpg",370,113,25,16,30,68.76f,true,"EX","HS",94,386390);
		RunTest("Blackjack.jpg",415,123,15,7,50,71.22f,false,"EX","HS",79,443260);
		RunTest("Catch the Wave.jpg",603,72,0,0,1,100.65f,false,"EX","HS",559,773570);
		RunTest("Dear.jpg",402,64,0,0,1,100.90f,false,"EXEX","HS",394,587740);
		RunTest("DECORATOR.jpg",436,100,1,0,6,93.52f,false,"EX","HS",217,560180);
		RunTest("DYE.jpg",530,106,7,2,13,84.77f,false,"EX","HS",143,486360);
		RunTest("erase or zero.jpg",442,70,0,0,2,100.12f,false,"EX","HS",265,731120);
		RunTest("FREELY TOMORROW.jpg",367,57,0,0,0,102.84f,false,"EX","HS",424,498640);
		RunTest("from Y to Y.jpg",350,49,6,1,8,86.35f,false,"EXEX","HS",139,427650);
		RunTest("Glory 3usi9.jpg",468,43,0,0,2,101.62f,false,"EX","HS",382,549780);
		RunTest("Hand in Hand.jpg",401,54,1,0,3,97.58f,false,"EX","HS",176,610040);
		RunTest("Hello, Worker.jpg",439,118,7,1,14,89.93f,false,"EXEX","HS",147,930290);
		RunTest("Just Be Friends.jpg",510,107,6,0,12,89.38f,false,"EXEX","HS",203,602080);
		RunTest("Knife.jpg",327,85,14,9,27,51.96f,true,"EX","HS",124,395170);
		RunTest("LIKE THE WIND.jpg",330,144,20,9,20,72.06f,false,"EX","HS",65,425970);
		RunTest("LOL -lots of laugh-.jpg",489,59,1,2,2,96.36f,false,"EX","HS",183,641920);
		RunTest("magnet.jpg",435,101,18,4,35,76.98f,false,"EXEX","HS",115,480540);
		RunTest("No Logic.jpg",491,101,11,5,15,86.32f,false,"EX","HS",186,476910);
		RunTest("Nostalogic.jpg",346,70,15,7,16,83.61f,false,"EX","HS",94,486030);
		RunTest("WORLD'S END UMBRELLA.jpg",437,136,6,1,3,90.59f,false,"H","",215,475120);
		RunTest("ぽっぴっぽー.jpg",350,46,7,6,3,80.39f,false,"N","",175,263630);
		RunTest("サマーアイドル.jpg",245,19,4,0,2,87.04f,false,"E","",103,179360);
		RunTest("アゲアゲアゲイン_alt.jpg","アゲアゲアゲイン",452,201,20,11,34,71.89f,false,"EX","",115,543240,false);
		RunTest("test.jpg","1/6 -out of the gravity-",575,26,0,0,0,104.51f,false,"EX","HS",601,698951,false);
		RunTest("test2.jpg","エンヴィキャットウォーク",26,6,2,3,129,4.96f,true,"EX","HS",3,47575,false);
		RunTest("EiMmwvJUMAEVmaT.jpg","アゲアゲアゲイン",0,0,0,0,89,0.00f,true,"E","",0,0,false);
		//RunTest("test38.jpg","サマーアイドル",345,49,6,2,2,94.53f,false,"H","",232,347990,false);
		//RunTest("ジターバグ_2.jpg","ジターバグ",0,0,0,0,159,0.00f,true,"EX","SD",0,0);
		//RunTest("大江戸ジュリアナイト_2.jpg","大江戸ジュリアナイト",0,0,0,0,79,0.08f,true,"EX","HD",0,580);
		/*RunRemoteTest("http://projectdivar.com/files/DECORATOR_EXplay_436_100_1_0_6_93.52.png","DECORATOR",436,100,1,0,6,93.52f,false,"EX","HS",217,560180);
		RunRemoteTest("http://projectdivar.com/files/img2.png","SING&SMILE",551,168,7,2,15,87.24f,false,"EX","HS",138,733310);
		RunRemoteTest("http://projectdivar.com/files/img3.png","忘却心中",361,89,31,9,28,79.20f,false,"EXEX","HS",55,693650);
		RunRemoteTest("http://projectdivar.com/files/img4.png","ロミオとシンデレラ",612,70,7,0,12,88.05f,false,"EX","HS",339,522350);
		RunRemoteTest("http://projectdivar.com/files/img5.png","巨大少女",441,33,0,1,3,102.11f,false,"EXEX","HS",244,673260);*/
		//RunRemoteTest("http://projectdivar.com/files/146.jpg","アンハッピーリフレイン",942,71,1,0,3,97.02f,false,"EXEX","",714,951020);
		System.out.println("All Tests passed! - Test Update");
	}

	private static void SearchSongs(String folderName,String colorDataFile) {
		File dir = new File(folderName);
		for (String s : dir.list()) {
			StringBuilder sb = new StringBuilder(s.replace(".jpg","").replace("_alt","")).append(":");
			boolean first=true;
			try {
				BufferedImage img = ImageIO.read(new File(dir,s));
				
				img = Controller.CropFutureToneImage(img);
				
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
				FileUtils.logToFile(sb.toString(), colorDataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getCorrectSongName(String song) {
		switch (song.toLowerCase()) { 
		/***************
		 * 
		 * 
		 * THIS IS USING TO LOWER CASE!
		 * USE LOWERCASE!!!
		 * 
		 * 
		 *****************/
			case "pianogirl":{
				return "PIANO*GIRL";
			}
			case "16 -out of the gravity-":{
				return "1/6 -out of the gravity-";
			}
			case "ファインダー (dslr remix - reedit)":{
				return "ファインダー (DSLR remix - re:edit)";
			}
			case "恋ノート":{
				return "恋ノート////";
			}
			case "equation+":{
				return "Equation+**";
			}
			case "雨のちsweetdrops":{
				return "雨のちSweet*Drops";
			}
				
			default:
				return song;
		}
	}
	
	static void RunTest(String _img,String _song,int _cool,int _fine, int _safe, int _sad, int _worst, float _percent,boolean _fail,String _difficulty,String _mod
			,int _combo,int _score,boolean debug) throws IOException {
		if (!_img.equalsIgnoreCase("image.png")) {
			System.out.println("Running test "+_img);
		}
		long startTime = System.currentTimeMillis();
		Point offset = new Point(418,204);
		File tmp = new File("tmp");
		if (tmp.exists()) {
			FileUtils.deleteFile(tmp);
		}
		tmp.mkdir();
		BufferedImage img=null;
		File f = new File(testdir,_img);
		if (!f.exists()) {
			f = new File("testsuite",_img);
		}
		Assert.isTrue(f.exists(),"Expected file to exist: "+f.getAbsoluteFile());
		img = ImageIO.read(f);
		img = Controller.CropFutureToneImage(img);
		ImageIO.write(img,"png",new File("test.png"));
		Result r = typeface1.getAllData(img,debug);
		if (img.getWidth()!=1200) {
        	//Resize.
        	img = ImageUtils.toBufferedImage(img.getScaledInstance(1200, 675, Image.SCALE_SMOOTH));
        }
		ImageIO.write(img,"png",new File("image.png"));
		String song = Controller.getSongTitle(img,debug);
		
		
		song = getCorrectSongName(song);
		_song = getCorrectSongName(_song);
		try {
			Assert.isTrue(song.equalsIgnoreCase(_song),"Expected song name to be "+_song+", got "+song);
			Assert.isTrue(r.cool == _cool,"Expected cool count to be "+_cool+", got "+r.cool);
			Assert.isTrue(r.fine == _fine,"Expected fine count to be "+_fine+", got "+r.fine);
			Assert.isTrue(r.safe == _safe,"Expected safe count to be "+_safe+", got "+r.safe);
			Assert.isTrue(r.sad == _sad,"Expected sad count to be "+_sad+", got "+r.sad);
			Assert.isTrue(r.worst == _worst,"Expected worst count to be "+_worst+", got "+r.worst);
			Assert.isTrue(r.percent == _percent,"Expected percent to be "+_percent+", got "+r.percent);
			Assert.isTrue(r.combo == _combo,"Expected combo to be "+_combo+", got "+r.combo);
			Assert.isTrue(r.score == _score,"Expected score to be "+_score+", got "+r.score);
			Assert.isTrue(r.fail == _fail,"Expected fail to be "+_fail+", got "+r.fail);
			Assert.isTrue(r.difficulty.equalsIgnoreCase(_difficulty),"Expected difficulty to be "+_difficulty+", got "+r.difficulty);
			Assert.isTrue(_mod.equalsIgnoreCase(r.mod),"Expected mod to be "+_mod+", got "+r.mod);
			System.out.println(" Passed ("+(System.currentTimeMillis()-startTime)+"ms)!");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			if (!debug) {
				System.err.println("Running in debug mode...");
				RunTest(_img,_song,_cool,_fine,_safe,_sad,_worst,_percent,_fail,_difficulty,_mod,_combo,_score,true);
				System.exit(0);
			}
		}
	}

	static void RunTest(String _img,int _cool,int _fine, int _safe, int _sad, int _worst, float _percent,boolean _fail,String _difficulty,String _mod
			,int _combo, int _score) throws IOException {
		RunTest(_img,_img.replace(".jpg",""),_cool,_fine,_safe,_sad,_worst,_percent,_fail,_difficulty,_mod,_combo,_score,false);
	}

	static void RunRemoteTest(String url,String song,int _cool,int _fine, int _safe, int _sad, int _worst, float _percent,boolean _fail,String _difficulty,String _mod
			,int _combo, int _score) throws IOException {
		System.out.println("Running remote test "+url);
		File file = new File(testdir,"image.png");
		file.delete();
		try {
			FileUtils.downloadFileFromUrl(url, file.getAbsolutePath());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		RunTest("image.png",song,_cool,_fine,_safe,_sad,_worst,_percent,_fail,_difficulty,_mod,_combo,_score,false);
	}

	static void RunRemoteTest(String url,String song,int _cool,int _fine, int _safe, int _sad, int _worst, float _percent,boolean _fail,String _difficulty,String _mod
			,int _combo, int _score,boolean debug) throws IOException {
		System.out.println("Running remote test "+url);
		File file = new File(testdir,"image.png");
		file.delete();
		try {
			FileUtils.downloadFileFromUrl(url, file.getAbsolutePath());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		RunTest("image.png",song,_cool,_fine,_safe,_sad,_worst,_percent,_fail,_difficulty,_mod,_combo,_score,debug);
	}

	static void RunTest(String _img,int _cool,int _fine, int _safe, int _sad, int _worst, float _percent,boolean _fail,String _difficulty,String _mod
			,int _combo, int _score,boolean debug) throws IOException {
		RunTest(_img,_img.replace(".jpg",""),_cool,_fine,_safe,_sad,_worst,_percent,_fail,_difficulty,_mod,_combo,_score,debug);
	}

}
