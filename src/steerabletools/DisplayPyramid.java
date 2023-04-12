package steerabletools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imageware.Builder;
import imageware.ImageWare;
import orientation.OrientationParameters;

import java.awt.Color;



public class DisplayPyramid {
  public static int RESCALE = 0;
  
  public static int NORESCALE = 1;
  
  public static void show(ImageWare image, String title, int scaled, int stacked, boolean pyramid, String prefix) {
    ImageWare disp = create(image, scaled, stacked, pyramid);
    disp.show(prefix+title);
  }
  
  public static ImageWare create(ImageWare image, int scaled, int stacked, boolean pyramid) {
    ImageWare out;
    int py = 0;
    int px = 0;
    int mx = image.getWidth();
    int my = image.getHeight();
    int nx = mx;
    int ny = my;
    int nscale = image.getSizeZ();
    int factSize = nscale;
    if (pyramid)
      factSize = (nscale > 1) ? 2 : 1; 
    if (stacked == 0) {
      out = Builder.create(nx, ny, nscale, (scaled == RESCALE) ? 1 : 3);
    } else if (stacked == 1) {
      out = Builder.create(nx * factSize, ny, 1, (scaled == RESCALE) ? 1 : 3);
    } else {
      out = Builder.create(nx, ny * factSize, 1, (scaled == RESCALE) ? 1 : 3);
    } 
    for (int k = 0; k < nscale; k++) {
      ImageWare band = Builder.create(mx, my, 1, 3);
      image.getXY(0, 0, k, band);
      if (scaled == RESCALE)
        band.rescale(); 
      if (stacked == 0) {
        out.putXY(0, 0, k, band);
      } else if (stacked == 1) {
        out.putXY(px, 0, 0, band);
      } else {
        out.putXY(0, py, 0, band);
      } 
      if (pyramid) {
        mx /= 2;
        my /= 2;
        if (stacked == 1)
          px = (int)(px + nx / Math.pow(2.0D, k)); 
        if (stacked == 2)
          py = (int)(py + ny / Math.pow(2.0D, k)); 
      } else {
        if (stacked == 1)
          px += nx; 
        if (stacked == 2)
          py += ny; 
      } 
    } 
    return out;
  }
  
  public static ImagePlus colorHSB(ImageWare hue, ImageWare sat, ImageWare bri, int stacked, boolean pyramid) {
    int py = 0;
    int px = 0;
    int nx = hue.getSizeX();
    int ny = hue.getSizeY();
    int mx = nx;
    int my = ny;
    int nscale = hue.getSizeZ();
    int fx = nx;
    int fy = ny;
    
    System.out.println("stacked?? " + stacked);
   
    if (pyramid) {
      if (stacked == 1)
        fx = nx * ((nscale > 1) ? 2 : 1); 
      if (stacked == 2)
        fy = ny * ((nscale > 1) ? 2 : 1); 
    } else {
      if (stacked == 1)
        fx = nx * nscale; 
      if (stacked == 2)
        fy = ny * nscale; 
    } 
    int size = fx * fy;
    ImageStack stack = new ImageStack(fx, fy);
    int[] cpixels = new int[size];
    

    
    for (int k = 0; k < nscale; k++) {
      IJ.showStatus("Show Color Image " + (k + 1) + "/" + nscale);
      if (stacked == 0) {
    	  ColorProcessor cp = new ColorProcessor(fx, fy);
    	 
    	  for (int y = 0; y < my; y++) {
    		  for (int x = 0; x < mx; x++) {
    			  float h = (float)hue.getPixel(x, y, k);   
    			  float s = (float)sat.getPixel(x, y, k);
    			  float b = (float)bri.getPixel(x, y, k);

    			  cp.putPixel(x, y, Color.HSBtoRGB(h, s, b) + -16777216);
            
            } 
    	} 
    	  stack.addSlice("", (ImageProcessor)cp);
    	  
      } else {
        int[] pixels = new int[size];
        for (int y = 0; y < my; y++) {
          for (int x = 0; x < mx; x++) {
        	
            float h = (float)hue.getPixel(x, y, k);
            float s = (float)sat.getPixel(x, y, k);
            float b = (float)bri.getPixel(x, y, k);
            cpixels[(py + y) * fx + x + px] = Color.HSBtoRGB(h, s, b) + -16777216;
          } 
        } 
      } 
      if (pyramid) {
        mx /= 2;
        my /= 2;
        if (stacked == 1)
          px = (int)(px + nx / Math.pow(2.0D, k)); 
        if (stacked == 2)
          py = (int)(py + ny / Math.pow(2.0D, k)); 
      } else {
        if (stacked == 1)
          px += nx; 
        if (stacked == 2)
          py += ny; 
      } 
    } 
    if (stacked != 0)
      stack.addSlice("", (ImageProcessor)new ColorProcessor(fx, fy, cpixels)); 
    ImagePlus imp = new ImagePlus("", stack);
    
    return imp;
  }
  
  
  public static ImagePlus maskedColorHSB(ImageWare hue, ImageWare sat, ImageWare bri, ImagePlus masked, double coherency, int stacked, boolean pyramid) {
	  	int py = 0;
	    int px = 0;
	    int nx = hue.getSizeX();
	    int ny = hue.getSizeY();
	    int mx = nx;
	    int my = ny;
	    int nscale = hue.getSizeZ();
	    int fx = nx;
	    int fy = ny;
	   
	    if (pyramid) {
	      if (stacked == 1)
	        fx = nx * ((nscale > 1) ? 2 : 1); 
	      if (stacked == 2)
	        fy = ny * ((nscale > 1) ? 2 : 1); 
	    } else {
	      if (stacked == 1)
	        fx = nx * nscale; 
	      if (stacked == 2)
	        fy = ny * nscale; 
	    } 
	    int size = fx * fy;
	    ImageStack stack = new ImageStack(fx, fy);
	    int[] cpixels = new int[size];
	    System.out.println("stacked=?? " + stacked);
	    
        ImagePlus flattened = masked.flatten();
        
       
	    for (int k = 0; k < nscale; k++) {
	      IJ.showStatus("Show Color Image " + (k + 1) + "/" + nscale);
	      if (stacked == 0) {
	    	  ColorProcessor cp = new ColorProcessor(fx, fy);
	    	  ImageProcessor ip = flattened.getProcessor();
	    	  
	    	  ImageProcessor ip_resized = ip.resize((int)(flattened.getWidth()/(Math.pow(2.0D, k))), (int)(flattened.getHeight()/(Math.pow(2.0D, k))), true); 
	    	  ImagePlus resizedImg = new ImagePlus("img " + k, ip_resized);
	    	  System.out.println("height and width of img" + resizedImg.getHeight() + " " + resizedImg.getWidth());
	    	  
	    	  for (int y = 0; y < resizedImg.getHeight(); y++) {
	    		  for (int x = 0; x < resizedImg.getWidth(); x++) {
	    			//System.out.println(x + " " +  y); 
	    			if (resizedImg.getPixel(x, y)[0] != 0) {
	    				  float h = (float)hue.getPixel(x, y, k);   
	    				  float s = (float)sat.getPixel(x, y, k);
	    				  float b = (float)bri.getPixel(x, y, k);

	    				  cp.putPixel(x, y, Color.HSBtoRGB(h, s, b) + -16777216);
	    			}
	    			} 
	    		  }
	    	  stack.addSlice("", (ImageProcessor)cp);
	    	  
	      } else {
	        int[] pixels = new int[size];
	       
	        for (int y = 0; y < my; y++) {
	          for (int x = 0; x < mx; x++) {
	            float h = (float)hue.getPixel(x, y, k);
	            float s = (float)sat.getPixel(x, y, k);
	            float b = (float)bri.getPixel(x, y, k);
	            cpixels[(py + y) * fx + x + px] = Color.HSBtoRGB(h, s, b) + -16777216;
	          } 
	        } 
	      } 
	      if (pyramid) {
	        mx /= 2;
	        my /= 2;
	        if (stacked == 1)
	          px = (int)(px + nx / Math.pow(2.0D, k)); 
	        if (stacked == 2)
	          py = (int)(py + ny / Math.pow(2.0D, k)); 
	      } else {
	        if (stacked == 1)
	          px += nx; 
	        if (stacked == 2)
	          py += ny; 
	      } 
	    } 
	    if (stacked != 0)
	      stack.addSlice("", (ImageProcessor)new ColorProcessor(fx, fy, cpixels)); 
	    ImagePlus imp = new ImagePlus("", stack);
	    
	    return imp;
	  }
  
  public static ImagePlus filteredColorHSB(ImageWare hue, ImageWare sat, ImageWare bri, int stacked, boolean pyramid,  double coherency) {
	  
	  int px = 0;
	  int py = 0;
	  int nx = hue.getSizeX();
	  int ny = hue.getSizeY();
	  int mx = nx;
	  int my = ny;
	  int nscale = hue.getSizeZ();
	  int fx = nx;
	  int fy = ny;
	   
	  if (pyramid) {
	    if (stacked == 1)
	        fx = nx * ((nscale > 1) ? 2 : 1); 
	     if (stacked == 2)
	        fy = ny * ((nscale > 1) ? 2 : 1); 
	   } else {
	     if (stacked == 1)
	        fx = nx * nscale; 
	      if (stacked == 2)
	        fy = ny * nscale; 
	    } 
	    int size = fx * fy;
	    ImageStack stack = new ImageStack(fx, fy);
	    int[] cpixels = new int[size];
	    

	    
	    for (int k = 0; k < nscale; k++) {
	      IJ.showStatus("Show Color Image " + (k + 1) + "/" + nscale);
	      if (stacked == 0) {
	    	  ColorProcessor cp = new ColorProcessor(fx, fy);
	    	  for (int y = 0; y < my; y++) {
	              for (int x = 0; x < mx; x++) {
	            	float h = (float)hue.getPixel(x, y, k);   
	                float s = (float)sat.getPixel(x, y, k);
	                float b = (float)bri.getPixel(x, y, k);
	                
	                if (s > coherency/100) { 
	                	cp.putPixel(x, y, Color.HSBtoRGB(h, s, b) + -16777216);
	                }
	                
	              } 
	            } 
	            
	            stack.addSlice("", (ImageProcessor)cp);
	      }
	      else {
	          int[] pixels = new int[size];
	          for (int y = 0; y < my; y++) {
	            for (int x = 0; x < mx; x++) {
	              float h = (float)hue.getPixel(x, y, k);
	              float s = (float)sat.getPixel(x, y, k);
	              float b = (float)bri.getPixel(x, y, k);
	              cpixels[(py + y) * fx + x + px] = Color.HSBtoRGB(h, s, b) + -16777216;
	            } 
	          } 
	        } 
	        if (pyramid) {
	          mx /= 2;
	          my /= 2;
	          if (stacked == 1)
	            px = (int)(px + nx / Math.pow(2.0D, k)); 
	          if (stacked == 2)
	            py = (int)(py + ny / Math.pow(2.0D, k)); 
	        } else {
	          if (stacked == 1)
	            px += nx; 
	          if (stacked == 2)
	            py += ny; 
	        } 
	      } 
	      if (stacked != 0)
	        stack.addSlice("", (ImageProcessor)new ColorProcessor(fx, fy, cpixels)); 
	      ImagePlus imp = new ImagePlus("", stack);
	      
	      return imp;
	    }
	    	 

	  
  
  public static ImagePlus colorRGB(String name, ImageWare red, ImageWare green, ImageWare blue) {
    int nx = red.getSizeX();
    int ny = red.getSizeY();
    int nz = red.getSizeZ();
    ImageStack stack = new ImageStack(nx, ny);
    int size = nx * ny;
    for (int k = 0; k < nz; k++) {
      IJ.showStatus("Show Color Image " + (k + 1) + "/" + nz);
      int[] pixels = new int[size];
      float[] r = red.getSliceFloat((k < red.getSizeZ()) ? k : 0);
      float[] g = green.getSliceFloat((k < green.getSizeZ()) ? k : 0);
      float[] b = blue.getSliceFloat((k < blue.getSizeZ()) ? k : 0);
      for (int index = 0; index < size; index++) {
        int ri = (int)(r[index] * 255.0F);
        int gi = (int)(g[index] * 255.0F);
        int bi = (int)(b[index] * 255.0F);
        pixels[index] = bi + (gi << 8) + (ri << 16) + -16777216;
      } 
      stack.addSlice("", (ImageProcessor)new ColorProcessor(nx, ny, pixels));
      IJ.showProgress(k / nz);
    } 
    ImagePlus imp = new ImagePlus(name, stack);
    
    imp.show();
    return imp;
  }
  
  public static ImageWare rescaleAngle(ImageWare in, boolean pyramid) {
    int nx = in.getSizeX();
    int ny = in.getSizeY();
    int nz = in.getSizeZ();
    int mx = nx;
    int my = ny;
    float PI = 3.1415927F; // maxValue in hue is PI, minValue in hue is -PI
    float PI2 = 1.5707964F;
    ImageWare out = in.replicate();
    for (int k = 0; k < nz; k++) {
      ImageWare band = Builder.create(mx, my, 1, 3);
      in.getXY(0, 0, k, band);
      float[] pix = band.getSliceFloat(0);
      float[] opix = out.getSliceFloat(k);
      for (int x = 0; x < mx; x++) {
        for (int y = 0; y < my; y++)
          opix[x + y * nx] = (pix[x + y * mx] + PI2) / PI; // (x-minValue)/(maxValue-minValue)
      } 
      if (pyramid) {
        mx /= 2;
        my /= 2;
      } 
    } 
    return out;
  }
}
