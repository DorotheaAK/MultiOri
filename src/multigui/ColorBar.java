package multigui;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.measure.Calibration;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.lang.Math;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.util.Random;
import java.util.TreeMap;


public class ColorBar{
  
  static int nBins = 256;
  
  static final String[] colors = new String[] { "White", "Black", "None" };
  
  private static String sFillColor = colors[0];
  
  private static String sTextColor = colors[1];
  
  private static double sZoom = 1.0D;
  
  private static int sNumLabels = 5;
  
  private static int sFontSize = 12;
  
  private static int sDecimalPlaces = 0;
  
  private static boolean sBoldText;
  
  private String fillColor = sFillColor;
  
  private String textColor = sTextColor;
  
  private double zoom = sZoom;
  
  private int numLabels = sNumLabels;
  
  private int fontSize = sFontSize;
  
  private int decimalPlaces = sDecimalPlaces;
  
  private boolean boldText = sBoldText;
  
  ImagePlus imp;
  
  ImageStatistics stats;
  
  Calibration cal;
  
  int[] histogram;
  
  Image img;
  
  int win_width;
  
  int fontHeight = 0;
  
  String boxOutlineColor = colors[2];
  
  String barOutlineColor = colors[1];
  
  ImageProcessor ip;
  
  int insetPad;
  
  boolean decimalPlacesChanged;
  
  
  public void getColorBar(ImagePlus img) {
	
	  
	  int[] wList = WindowManager.getIDList();
	  boolean cBarPrinted = false; 
	  if (wList!=null) {
		for (int i=0; i<wList.length; i++) {
				
			if (WindowManager.getImage(wList[i])!=null && WindowManager.getImage(wList[i]).getTitle().equals("ColorBar")) {
				cBarPrinted = true;
			}
				 
		}
				 
				
		}
	if (!cBarPrinted) {
    Random r = new Random();
    int[] angleValues = new int[1000];
    for (int i = 0; i < angleValues.length; i++) {
    	int high = 181; 
    	int low = 0;
    	angleValues[i] = r.nextInt(high-low) + low;
    }
    
    TreeMap<Float, Integer> map = new TreeMap<Float, Integer>();
    for (int i = 0; i < angleValues.length; i++) {
    	int angleVal = angleValues[i];
    	float hueVal = (float)(angleVal/180.0);
    	int rgb = Color.HSBtoRGB(hueVal, 1, 1)+ -16777216; 
    	map.put(hueVal*180, rgb);
    }
  
    TreeMap<Float, Integer> sorted = new TreeMap<Float, Integer>();
    
    sorted.putAll(map); //sorted the hashTreeMap so that the key value/hue value is increasing 
    
    setImage(img);
    updateColorBar(sorted); }

  }
  
  private void setImage(ImagePlus imp) {
	  ImageCanvas ic = imp.getCanvas();
	  double mag = (ic != null) ? ic.getMagnification() : 1.0D; //WHY???
	  //if (this.zoom <= 1.0D && mag < 1.0D)
	  //   this.zoom = 1.0D / mag; 
	  this.zoom = 1.0;
	  System.out.println("zoom: " + zoom);
	  this.insetPad = (imp.getWidth() + imp.getHeight()) / 100;
	  if (this.insetPad < 4)
	      this.insetPad = 4; 
	  this.imp = imp;
	  //this.imp.show();
  }
  
  private void updateColorBar(TreeMap<Float, Integer> sorted) {
	//calculateWidth();
	//drawBarAsOverlay(this.imp, null, -1, -1);
    drawBarAsOverlay(sorted);
    //this.imp.updateAndDraw();
  }
  
  
  private void drawBarAsOverlay(TreeMap<Float, Integer> sorted) {

	int x = this.imp.getWidth() - this.insetPad - this.win_width;
	int y = this.insetPad; 
	  
	//System.out.println("x: " + x + " y: " + y);
    //this.stats = imp.getStatistics(16, nBins);

    //this.histogram = this.stats.histogram;
   
    this.cal = this.imp.getCalibration();
    
    Overlay overlay = imp.getOverlay();
 
    if (overlay == null) {
      overlay = new Overlay();
    } else {
      overlay.remove("|CB|");
    } 
    
    int maxTextWidth = addText(null, null, 0, 0);

    this.win_width = (int)(10.0D * this.zoom) + 5 + (int)(12.0D * this.zoom) + maxTextWidth + (int)(5.0D * this.zoom);
    
    if (x == -1 && y == -1)
      return; 
    
    Color c = getColor(this.fillColor);
    if (c != null) {
      Roi r = new Roi(x, y, this.win_width, (int)(128.0D * this.zoom + (2 * (int)(10.0D * this.zoom))));
      r.setFillColor(c);
      overlay.add(r, "|CB|");
    } 
    int xOffset = x;
    int yOffset = y;
    if (this.decimalPlaces == -1)
      this.decimalPlaces = Analyzer.getPrecision(); 
    x = (int)(10.0D * this.zoom) + xOffset;
    y = (int)(10.0D * this.zoom) + yOffset;
    
    addVerticalColorBar(overlay, sorted, x, y, (int)(12.0D * this.zoom), (int)(128.0D * this.zoom));
    addText(overlay, sorted, x + (int)(12.0D * this.zoom), y);
    
    c = getColor(this.boxOutlineColor);
    
    overlay.setIsCalibrationBar(false);
    
    if (this.imp.getCompositeMode() > 0)
      for (int i = 0; i < overlay.size(); i++)
        overlay.get(i).setPosition(this.imp.getC(), 0, 0);  
    
    Rectangle r = overlay.get(0).getBounds();
    overlay.translate(-r.x, -r.y);
    ImagePlus impSep = IJ.createImage("CBar", "RGB", r.width, r.height, 1);
    impSep.setOverlay(overlay);
    impSep = impSep.flatten();
    impSep.setTitle("ColorBar");
    impSep.show();
  }
  
  private void addVerticalColorBar(Overlay overlay, TreeMap<Float, Integer> sorted, int x, int y, int thickness, int length) {
    byte[] rLUT, gLUT, bLUT;
    int width = thickness;
    int height = length;
    int mapSize = 0;
    ColorModel cm = this.imp.getProcessor().getCurrentColorModel();
    if(cm instanceof DirectColorModel & sorted != null) {
    		mapSize = sorted.size();
    	    rLUT = new byte[mapSize];
    	    gLUT = new byte[mapSize];
    	    bLUT = new byte[mapSize];
    	    int j = 0;
    	    for (int rgb : sorted.values()) {
    	        rLUT[j] = (byte)((rgb >> 16) & 0xFF);
    	        gLUT[j] = (byte)((rgb >> 8) & 0xFF);
    	        bLUT[j] = (byte)(rgb & 0xFF);
    	        j++;	
    	    }
    	
    }
    else {
      mapSize = 256;
      rLUT = new byte[mapSize];
      gLUT = new byte[mapSize];
      bLUT = new byte[mapSize];
      for (int j = 0; j < mapSize; j++) {
        rLUT[j] = (byte)j;
        gLUT[j] = (byte)j;
        bLUT[j] = (byte)j;
      } 
    } 
    double colors = mapSize;
    int start = 0;
    
    for (int i = 0; i < (int)(128.0D * this.zoom); i++) {
      int iTreeMap = start + (int)Math.round(i * colors / 128.0D * this.zoom);
      if (iTreeMap >= mapSize)
        iTreeMap = mapSize - 1; 
      int j = (int)(128.0D * this.zoom) - i - 1;
      Line line = new Line(x, j + y, thickness + x, j + y);
      line.setStrokeColor(new Color(rLUT[iTreeMap] & 0xFF, gLUT[iTreeMap] & 0xFF, bLUT[iTreeMap] & 0xFF));
      line.setStrokeWidth(1.0001D);
      overlay.add((Roi)line, "|CB|");
    } 
    Color c = getColor(this.barOutlineColor);
    if (c != null) {
      Roi r = new Roi(x, y, width, height);
      r.setStrokeColor(c);
      r.setStrokeWidth(1.0D);
      overlay.add(r, "|CB|");
    } 
  }
  
  private int addText(Overlay overlay, TreeMap<Float, Integer> sorted, int x, int y) {
    Color c = getColor(this.textColor);
    if (c == null)
      return 0; 
  
    double barStep = 128.0D * this.zoom;
    if (this.numLabels > 2)
      barStep /= (this.numLabels - 1); 
    int fontType = this.boldText ? 1 : 0;
    Font font = new Font("SansSerif", fontType, (int)(this.fontSize * this.zoom));;
 
    int maxLength = 0;
    FontMetrics metrics = getFontMetrics(font);
    this.fontHeight = metrics.getHeight();
    int hashMapSize = 0;
    Object[] hueValues = null;
    if (sorted!=null) {
    	hueValues = sorted.keySet().toArray();
        
        hashMapSize = sorted.size()-1;
    }
    
    for (int i = 0; i < this.numLabels; i++) {
      int yLabel = (int)Math.round(y + 128.0D * this.zoom - i * barStep - 1.0D);
      
      String s = "";
   
      ImageProcessor ipOrig = this.imp.getProcessor();
  
      double min = ipOrig.getMin();
      double max = ipOrig.getMax();
     
      String todisplay = "";

      if (sorted != null) {
    	  int valueToDisplay = (int) (min + (hashMapSize - min) / (this.numLabels - 1) * i);
        
    	  float grayLabel = (float) hueValues[valueToDisplay];
    	  grayLabel = (float) (Math.round(grayLabel * 10.0) / 10.0);
    	  
    	  todisplay = Float.toString((grayLabel)) + " " + s;
    	  
      }
      else {
    	  double grayLabel = min + (max - min) / (this.numLabels - 1) * i;
    	  todisplay = d2s(grayLabel) + " " + s;
      }
  
      if (overlay != null) {
        TextRoi label = new TextRoi(todisplay, (x + 5), (yLabel + this.fontHeight / 2), font);
        label.setStrokeColor(c);
        overlay.add((Roi)label, "|CB|");
      } 
      int iLength = metrics.stringWidth(todisplay);
      if (iLength > maxLength)
        maxLength = iLength + 5; 
    } 
    return maxLength;
  }
  
  String d2s(double d) {
    return IJ.d2s(d, this.decimalPlaces);
  }
  
  int getFontHeight() {
    int fontType = this.boldText ? 1 : 0;
    Font font = new Font("SansSerif", fontType, (int)(this.fontSize * this.zoom));
    FontMetrics metrics = getFontMetrics(font);
    return metrics.getHeight();
  }
  
  Color getColor(String color) {
	Color c = Color.white;
    if (color.equals(colors[1])) {
    	c = Color.black;
    } else if (color.equals(colors[2])) {
      c = null;
    } 
    return c;
  }
  
  private FontMetrics getFontMetrics(Font font) {
    BufferedImage bi = new BufferedImage(1, 1, 1);
    Graphics g = bi.getGraphics();
    g.setFont(font);
    return g.getFontMetrics(font);
  }
  
   
}
