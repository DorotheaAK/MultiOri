package multigui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import imageware.ImageWare;


public class OrientationalOrderParameter{
	
	private Double director = null; 
	
	private static final DecimalFormat df = new DecimalFormat("00.00");  
	ResultsTable resultsTable;
	int counter;  
	boolean masking = false; 
	
	public void run(ImagePlus img, ImageWare hue, ImageWare sat, ImageWare bri,  int directorUsed, int drawingDirector, String prefix, double coherency) 
	{
		
		if (directorUsed == 1) {
			
			director = getDirector(drawingDirector, img);
			
		}
		
		
		
		ResultsTable rt = ResultsTable.getResultsTable( prefix + " Orientational Order Parameter" );
		

		double[] arr_s = new double[(int)(hue.getSizeZ())];
		double[] arr_angle = new double[(int)(hue.getSizeZ())];
		
	    for (int k = 0; k < hue.getSizeZ(); k++) {
	    	
	    	ArrayList<Float> hueMap = new ArrayList<Float>();
	    	
	    	int mx = (int)(hue.getSizeX()/(Math.pow(2, k)));
			int my = (int)(hue.getSizeY()/(Math.pow(2, k)));
			    
			for (int y = 0; y < my; y++) {
				for (int x = 0; x < mx; x++) {
					float h = (float)hue.getPixel(x, y, k);  
					float s = (float)sat.getPixel(x, y, k);  
					if (s > coherency/100) { //only colors, not gray, black, white values
						hueMap.add(h);}
				}
			}
	    
			Collections.sort(hueMap);
			
			arr_s[k] = computeOrientationalOrderParameter(directorUsed, hueMap);
			arr_angle[k] = computeMeanAngle(hueMap);
	    }
	    
	    rt = updateResultsTable(rt, arr_s, arr_angle, img, directorUsed, (int)(hue.getSizeZ()), coherency, masking); 
	    
	    rt.show( prefix + " Orientational Order Parameter" );
		
	}
	
	
	
	public Double getDirector(int drawingDirector, ImagePlus img) {
		if (drawingDirector == 1) {
			
			final DrawDialog draw = new DrawDialog(img);
			director = draw.drawDirector();
    	}
    	else {
    		    		
    		final ManualEntry manualEntry = new ManualEntry(img);
    		director = manualEntry.displayManualEntry();
    		
    	}
		return director;
		
	}
	
	public void getMaskedOop(ImagePlus masked, ImageWare hue, ImageWare sat, String prefix, double coherency) {
		
		int nscale = hue.getSizeZ();
		
		ResultsTable rt = ResultsTable.getResultsTable( prefix + " Orientational Order Parameter" );
		

		double[] arr_s = new double[(int)(hue.getSizeZ())];
		double[] arr_angle = new double[(int)(hue.getSizeZ())];
		
		
		ImagePlus flattened = masked.flatten();
		ArrayList<Float> hueMap = new ArrayList<Float>();
		ImageProcessor ip = flattened.getProcessor();
  	  
		
		for (int k = 0; k < nscale; k++) {
			
	    	  
			ImageProcessor ip_resized = ip.resize((int)(flattened.getWidth()/(Math.pow(2.0D, k))), (int)(flattened.getHeight()/(Math.pow(2.0D, k))), true);
	    	 ImagePlus resizedImg = new ImagePlus("", ip_resized);
	    	  for(int i=0; i< resizedImg.getWidth(); i++) {
	    		  for (int j = 0; j <resizedImg.getHeight(); j++) {
				
	    			  if (resizedImg.getPixel(i,j)[0] != 0) {
	    				  float h = (float)hue.getPixel(i, j, k);  
	    				  float s = (float)sat.getPixel(i, j, k);  
	    				  if (s > coherency/100) {
	    					  hueMap.add(h);}
	    			  }
	    		  }}
		
			Collections.sort(hueMap);
			
		arr_s[k]= computeOrientationalOrderParameter(0, hueMap);
		arr_angle[k] = computeMeanAngle(hueMap);
	    
		masking = true; 
		}
		rt = updateResultsTable(rt, arr_s, arr_angle, masked, 0, (int)(hue.getSizeZ()), coherency, masking); 
	    
		rt.show( prefix + " Orientational Order Parameter" );
		
		
		
	}
	
	public double computeMeanAngle(ArrayList<Float> hueMap)
	{
		double mean_angle = 0; 
		for ( int i = 0; i < hueMap.size(); i++ ) {
			float value = hueMap.get(i);
			
			mean_angle += value * 180; 
			
			
		}
		
		mean_angle = mean_angle/((double)hueMap.size());
		
		System.out.println("mean angle: " + mean_angle);
		return mean_angle; 
	
	}
	
	
	public double computeOrientationalOrderParameter( int directorUsed, ArrayList<Float> hueMap)
	{	
		double S;
		
		if ((directorUsed == 1) && (director != null))
		{
			
			double sum = 0; 
			
			for ( int i = 0; i < hueMap.size(); i++ ) {
				
				sum += Math.cos(Math.toRadians(hueMap.get(i)*180-director)); // compute OOP with director 
				
			}
				S = 2*(sum/(double)hueMap.size())-1;
			
			
		}
		
		else {
			
			double sum_uii = 0; 
			double sum_uij = 0;
			
			for ( int i = 0; i < hueMap.size(); i++ ) {
				float value = hueMap.get(i);
				double u_ii = Math.cos(Math.toRadians(value*180));
				double u_ij = Math.sin(Math.toRadians(value*180));
				
			
				sum_uii += Math.pow(u_ii, 2); 
				sum_uij += u_ii * u_ij;
				
			}
			double q11 = 2*(sum_uii/(double)hueMap.size()) - 1;
					
			double q12 = 2*((sum_uij/(double)hueMap.size()));
			
			 S = Math.sqrt(Math.pow(q11, 2) + Math.pow(q12, 2)); 

		}
		
		return S;
		
	}
	
	

	public ResultsTable updateResultsTable(ResultsTable resultsTable, double[] arr_s, double[] arr_angle, ImagePlus img, int directorUsed, float scale, double coherency, boolean masking)
	{
		System.out.println("coherency in OOP " + coherency);

		if (resultsTable == null) {
			
			resultsTable = new ResultsTable();
			System.out.println("new resultstable");
			resultsTable.setPrecision( 9 );
			resultsTable.incrementCounter();
			counter = 1;
			
		}
		else {
			resultsTable.incrementCounter();
			
			
		}
		if (coherency > 0) {
			if (masking) {
				resultsTable.addLabel(img.getShortTitle() + " masked: with min. Coherency: " + coherency + "%");
			}
			else {
				resultsTable.addLabel(img.getShortTitle() + " with min. Coherency: " + coherency + "%");
			}
			
		}
		else {
			if (masking) {
				resultsTable.addLabel(img.getShortTitle()+ " masked");
			}
			else {
				resultsTable.addLabel(img.getShortTitle());
			}
			//table.addValue("Image", imp.getShortTitle());
		}
		
		if ((directorUsed == 1) && director != null) {
			
			double directorAngle = director.doubleValue();
	    	
			String directorString = df.format(directorAngle);
			
			directorString = directorString.replace(',', '.');
			
			resultsTable.addValue("Director Angle", directorAngle);
			counter += 1; 
			
		}
		 else {
			 	
			 	
			 resultsTable.addValue("Type", "without Director");
			 resultsTable.addValue("Mean Angle", arr_angle[0]);
				//counter += 1; 
			 //
			//	counter += 1;
					
		 }
		for (int i = 0;  i<arr_s.length; i++) {
			resultsTable.addValue("Scale " + (i+1), arr_s[i] );
		}
		
		

		return resultsTable;
	}

}
