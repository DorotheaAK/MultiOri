package multigui;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.filter.RankFilters;
import ij.process.ImageProcessor;
import ij.plugin.ContrastEnhancer;

public class PreprocessOrient {
	
	
	public ImagePlus filteringOrient(ImagePlus img, double saturated) {
		
		
		ImagePlus filteredImg = (ImagePlus) img.duplicate();
		
		filteredImg.setTitle(img.getShortTitle() + " masked");
		
		ImageProcessor ip = filteredImg.getProcessor();
		RankFilters rankFilters = new RankFilters();
		
		rankFilters.rank(ip, 2, 4); //Median Filtering 4 = MEDIAN, 2 = RADIUS 
		
		
		//rankFilters.run(ip); 
		
		//img.show(); 
		
		Clahe.run(filteredImg, (int)(img.getWidth()/20), 256, 3);
		
		System.out.println("CLAHE " +  (int)(img.getWidth()/20));
		
		ContrastEnhancer contrastEnhancer = new ContrastEnhancer();
		contrastEnhancer.stretchHistogram(filteredImg, saturated/100); 
		
		ip.setAutoThreshold("Default", false, 1);
		ip.erode();
		ip.dilate();
		//ip.dilate();
		//ip.erode();
		//ip.erode();
		
		int[] wList = WindowManager.getIDList();
		//System.out.println("ID LIST: " + wList);
		boolean maskPrinted = false; 
		if (wList!=null) {
			for (int i=0; i<wList.length; i++) {
				
				if (WindowManager.getImage(wList[i])!=null && WindowManager.getImage(wList[i]).getTitle().equals(filteredImg.getTitle())) {
					maskPrinted = true;
				}
				 
			}
				 
				
		}
		if (!maskPrinted) {
			filteredImg.show();
		}
		 
		
		//System.out.println(filteredImg.getType());


	return filteredImg; 
	}
	
}
