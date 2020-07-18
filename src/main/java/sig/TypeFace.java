package sig;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import sig.utils.FileUtils;
import sig.utils.ImageUtils;

public class TypeFace {
	final static int WIDTH = 32;
	final static int HEIGHT = 32;
	final static int NUMBER_COUNT = 10;
	public int blue_minthreshold = 75;
	public int green_minthreshold = 0;
	public int red_minthreshold = 0;
	public int blue_maxthreshold = 255;
	public int green_maxthreshold = 150;
	public int red_maxthreshold = 126;
	public int blue_fillminthreshold = 75;
	public int green_fillminthreshold = 0;
	public int red_fillminthreshold = 0;
	public int blue_fillmaxthreshold = 255;
	public int green_fillmaxthreshold = 150;
	public int red_fillmaxthreshold = 107;
	public boolean darkFillCheck = true;
	Color[][] numbers = new Color[WIDTH*HEIGHT][NUMBER_COUNT];
	BufferedImage baseImg;
	public TypeFace(BufferedImage img) {
		this.baseImg = img;
		for (int k=0;k<NUMBER_COUNT;k++) {
			for (int i=0;i<WIDTH;i++) {
				for (int j=0;j<HEIGHT;j++) {
					numbers[i*HEIGHT+j][k]=new Color(baseImg.getRGB(i+k*WIDTH, j),true);
				}
			}
		}
	}
	
	public int extractNumbersFromImage(BufferedImage img,File saveLoc) {
		if (!saveLoc.exists()) {
			saveLoc.mkdirs();
		}
		
		int midY = img.getHeight()/2;
		int X = 0;
		
		String extractedNumbers = "";
		
		int state = 0;
		
		BufferedImage numberImg = null;
		
		final boolean DEBUG_IMG = false;
		

		int iterations=0;
		while (X<img.getWidth()) {
			boolean success=true;
			Color p = new Color(img.getRGB(X, midY),true);
			switch (state) { 
				case 0:{
					//Search for a dark pixel.
					if (p.getBlue()>blue_minthreshold && p.getBlue()<blue_maxthreshold &&
							p.getGreen()>green_minthreshold && p.getGreen()<green_maxthreshold &&
							p.getRed()>red_minthreshold && p.getRed()<red_maxthreshold) {
						//We found a dark pixel.
						state=1;
						if (DEBUG_IMG) {
							try {
								BufferedImage img2 = ImageUtils.toCompatibleImage(ImageUtils.copyBufferedImage(img));
								img2.setRGB(X, midY, Color.RED.getRGB());
								ImageIO.write(img2,"png",new File("stage1_"+System.currentTimeMillis()+".png"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (darkFillCheck) {
							success = fillDark(img,X,midY,0,0);
							if (!success) {
								//We're done.
								X=img.getWidth();
								break;
							}
						}
					}
				}break;
				case 1:{
					//Move right until light pixel.
					if (p.getBlue()>200 && p.getGreen()>200 && p.getRed()>200) {
						state=2;
						if (DEBUG_IMG) {
							try {
								BufferedImage img2 = ImageUtils.toCompatibleImage(ImageUtils.copyBufferedImage(img));
								img2.setRGB(X, midY, Color.RED.getRGB());
								ImageIO.write(img2,"png",new File("stage2_"+System.currentTimeMillis()+".png"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}break;
				case 2:{
					//Fill algorithm until entire number is filled.
					ImgRectangle i = new ImgRectangle();
					success = fill(img,X,midY,0,0,i);
					if (!success) {
						//We're done.
						X=img.getWidth();
						break;
					} else {
						X+=i.maxX-1;
						numberImg = i.createImage();
						//X+=numberImg.getWidth();
						state=3;
						if (DEBUG_IMG) {
							try {
								BufferedImage img2 = ImageUtils.toCompatibleImage(ImageUtils.copyBufferedImage(img));
								img2.setRGB(X, midY, Color.RED.getRGB());
								ImageIO.write(img2,"png",new File("stage3_"+System.currentTimeMillis()+".png"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}break;
				case 3:{
					//Figure out which number in the typeface it best represents.
					numberImg = ImageUtils.toCompatibleImage(ImageUtils.toBufferedImage(numberImg.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_FAST)));
					//System.out.println(numberImg.getWidth()+"x"+numberImg.getHeight());
					int[] hits = new int[NUMBER_COUNT];
					double highestRatio = 0;
					int highest = 0;
					for (int k=0;k<NUMBER_COUNT;k++) {
						BufferedImage img2 = ImageUtils.toCompatibleImage(ImageUtils.copyBufferedImage(numberImg));
						for (int i=0;i<WIDTH;i++) {
							for (int j=0;j<HEIGHT;j++) {
								if (i<numberImg.getWidth() &&
										j<numberImg.getHeight()) {
									Color pixel = new Color(numberImg.getRGB(i, j));
									if (numbers[i*HEIGHT+j][k].getRed()==pixel.getRed() && numbers[i*HEIGHT+j][k].getGreen()==pixel.getGreen() && numbers[i*HEIGHT+j][k].getBlue()==pixel.getBlue()) {
										hits[k]++;
										//System.out.println("Hit for "+((k+1)%NUMBER_COUNT)+"! "+hits[k] + "/"+numbers[i*HEIGHT+j][k]+"/"+pixel);
										if ((double)hits[k]/(WIDTH*HEIGHT)>highestRatio) {
											highestRatio= (double)(hits[k])/(WIDTH*HEIGHT);
											highest=k;
										}
									} else {
										if (pixel.equals(Color.WHITE)) {
											img2.setRGB(i, j, Color.BLUE.getRGB());
										} else {
											img2.setRGB(i, j, Color.RED.getRGB());
										}
										//FileUtils.logToFile("Pixel difference: "+numbers[i*HEIGHT+j][k]+"/"+pixel, new File(saveLoc,(iterations)+".txt").getPath());
									}
								}
							}
						}
						FileUtils.logToFile(((k+1)%NUMBER_COUNT)+":"+((double)(hits[k])/(WIDTH*HEIGHT)), new File(saveLoc,(iterations)+".txt").getPath());
						try {
							ImageIO.write(img2,"png",new File(saveLoc,(iterations)+"_"+((k+1)%NUMBER_COUNT)+".png"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//System.out.println("Matches closest to "+((highest+1)%NUMBER_COUNT)+" with "+highestRatio);
					iterations++;
					extractedNumbers+=Integer.toString((highest+1)%NUMBER_COUNT);
					state=4;
				}break;
				case 4:{
					//Find dark pixels again.
					if (p.getBlue()>blue_minthreshold && p.getBlue()<blue_maxthreshold &&
							p.getGreen()>green_minthreshold && p.getGreen()<green_maxthreshold &&
							p.getRed()>red_minthreshold && p.getRed()<red_maxthreshold) {
						//We found a dark pixel. Back to the start.
						state=0;
						if (DEBUG_IMG) {
							try {
								BufferedImage img2 = ImageUtils.toCompatibleImage(ImageUtils.copyBufferedImage(img));
								img2.setRGB(X, midY, Color.RED.getRGB());
								ImageIO.write(img2,"png",new File("stage4_"+System.currentTimeMillis()+".png"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}break;
			}
			X++;
		}
		//System.out.println("Got "+extractedNumbers);
		if (extractedNumbers.length()==0) {
			return -1;
		} else {
			return Integer.parseInt(extractedNumbers);
		}
	}

	public boolean fillDark(BufferedImage img,int startX,int startY,int x,int y) {
		//rect.AddPixel(new Point(x,y), Color.BLACK);
		img.setRGB(startX+x, startY+y, Color.BLACK.getRGB());
		Color p = null;
		if (startX+x+1<img.getWidth()) {
			p = new Color(img.getRGB(startX+x+1, startY+y));
			if (p.getBlue()>blue_fillminthreshold && p.getBlue()<blue_fillmaxthreshold &&
					p.getGreen()>green_fillminthreshold && p.getGreen()<green_fillmaxthreshold &&
					p.getRed()>red_fillminthreshold && p.getRed()<red_fillmaxthreshold) {
				fillDark(img,startX,startY,x+1,y);
			}
		} else {
			return false;
		}
		if (startX+x-1>0) {
			p = new Color(img.getRGB(startX+x-1, startY+y));
			if (p.getBlue()>blue_fillminthreshold && p.getBlue()<blue_fillmaxthreshold &&
					p.getGreen()>green_fillminthreshold && p.getGreen()<green_fillmaxthreshold &&
					p.getRed()>red_fillminthreshold && p.getRed()<red_fillmaxthreshold) {
				fillDark(img,startX,startY,x-1,y);
			}
		} else {
			return false;
		}
		if (startY+y+1<img.getHeight()) {
			p = new Color(img.getRGB(startX+x, startY+y+1));
			if (p.getBlue()>blue_fillminthreshold && p.getBlue()<blue_fillmaxthreshold &&
					p.getGreen()>green_fillminthreshold && p.getGreen()<green_fillmaxthreshold &&
					p.getRed()>red_fillminthreshold && p.getRed()<red_fillmaxthreshold) {
				fillDark(img,startX,startY,x,y+1);
			}
		} else {
			return false;
		}
		if (startY+y-1>0) {
			p = new Color(img.getRGB(startX+x, startY+y-1));
			if (p.getBlue()>blue_fillminthreshold && p.getBlue()<blue_fillmaxthreshold &&
					p.getGreen()>green_fillminthreshold && p.getGreen()<green_fillmaxthreshold &&
					p.getRed()>red_fillminthreshold && p.getRed()<red_fillmaxthreshold) {
				fillDark(img,startX,startY,x,y-1);
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean fill(BufferedImage img,int startX,int startY,int x,int y,ImgRectangle rect) {
		rect.AddPixel(new Point(x,y), Color.BLACK);
		img.setRGB(startX+x, startY+y, Color.BLACK.getRGB());
		Color p = null;
		if (startX+x+1<img.getWidth()) {
			p = new Color(img.getRGB(startX+x+1, startY+y));
			if (p.getBlue()>200 && p.getGreen()>200 && p.getRed()>200) {
				fill(img,startX,startY,x+1,y,rect);
			}
		} else {
			return false;
		}
		if (startX+x-1>0) {
			p = new Color(img.getRGB(startX+x-1, startY+y));
			if (p.getBlue()>200 && p.getGreen()>200 && p.getRed()>200) {
				fill(img,startX,startY,x-1,y,rect);
			}
		} else {
			return false;
		}
		if (startY+y+1<img.getHeight()) {
			p = new Color(img.getRGB(startX+x, startY+y+1));
			if (p.getBlue()>200 && p.getGreen()>200 && p.getRed()>200) {
				fill(img,startX,startY,x,y+1,rect);
			}
		} else {
			return false;
		}
		if (startY+y-1>0) {
			p = new Color(img.getRGB(startX+x, startY+y-1));
			if (p.getBlue()>200 && p.getGreen()>200 && p.getRed()>200) {
				fill(img,startX,startY,x,y-1,rect);
			}
		} else {
			return false;
		}
		return true;
	}
	
	
	class ImgRectangle{
		int minX=0,maxX=0,minY=0,maxY=0;
		List<Pixel> p = new ArrayList<Pixel>();
		void AddPixel(Point p, Color c) {
			this.p.add(new Pixel(p,c));
			if (p.x<minX) {
				minX=p.x;
			}
			if (p.x>maxX) {
				maxX=p.x;
			}
			if (p.y<minY) {
				minY=p.y;
			}
			if (p.y>maxY) {
				maxY=p.y;
			}
		}
		BufferedImage createImage() {
			int finalWidth = maxX-minX+1;
			int offsetX = 0;
			int finalHeight = maxY-minY+1;
			int offsetY = 0;
			/*if (finalWidth > finalHeight) {
				//Add padding for height.
				offsetY += (finalWidth-finalHeight)/2;
				finalHeight+= offsetY*2;
			}
			if (finalHeight > finalWidth) {
				//Add padding for width.
				offsetX += (finalHeight-finalWidth)/2;
				finalWidth+= offsetX*2;
			}*/
			
			BufferedImage bufferedImage = ImageUtils.toCompatibleImage(new BufferedImage(finalWidth, finalHeight,
		            BufferedImage.TYPE_INT_RGB));
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setPaint ( new Color ( 255, 255, 255 ) );
			graphics.fillRect ( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() );
			for (Pixel pixel : p) {
				bufferedImage.setRGB(pixel.p.x-minX+offsetX, pixel.p.y-minY+offsetY, pixel.c.getRGB());
			}
			return bufferedImage;
		}
	}
	
	
	class Pixel{
		Point p;
		Color c;
		Pixel(Point p,Color c) {
			this.p=p;
			this.c=c;
		}
	}
}