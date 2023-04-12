package monogenicj;


import java.awt.Frame;

import ij.ImagePlus;
import orientation.OrientationParameters;


public class MonogenicResults {
	
	MonogenicImage mgim; 
	public static String prefix = "Multiscale Analysis -";

	public static void hide() {
		Frame frame[] = Frame.getFrames();
		for (int i=0; i<frame.length; i++) {
			if (frame[i].getTitle().startsWith(prefix))
				frame[i].dispose();
		}
	}

	public static void show(int feature, MonogenicImage mgim, MonogenicParameters params, int countRun, ImagePlus img) {
		
			boolean view[]= new boolean[params.view.length];
			view[feature] = true;
			show(view, mgim, params, countRun, img);
		
	}

	public static void show(MonogenicImage mgim, MonogenicParameters params, int countRun, ImagePlus img) {
		show(params.view, mgim, params, countRun, img);
	}
	
	public static void show(boolean view[], MonogenicImage mgim, MonogenicParameters params, int countRun, ImagePlus img) {
		int feature;

		feature = MonogenicParameters.RIESZ_HORIZONTAL;
		if (view[feature])
			display(feature, mgim, params, countRun, null, prefix);

		feature = MonogenicParameters.RIESZ_VERTICAL;
		if (view[feature])
			display(feature, mgim, params, countRun, null, prefix);

		feature = MonogenicParameters.ENERGY;
		if (view[feature])
			display(feature, mgim, params, countRun, null, prefix);

		feature = MonogenicParameters.ORIENTATION;
		if (view[feature])
			display(feature, mgim, params, countRun, null,prefix);
		
		feature = MonogenicParameters.LAPLACE;
		if (view[feature])
			display(feature, mgim, params, countRun, null, prefix);

		feature = MonogenicParameters.COHERENCY;
		if (view[feature])
			display(feature, mgim, params, countRun, null, prefix);

		feature = MonogenicParameters.SURVEY;
		if (view[feature]) {
		
			display(feature, mgim, params, countRun,img,prefix);
		}
		
		
		feature = MonogenicParameters.WAVE;
		if (view[feature]) {
		
			display(feature, mgim, params, countRun,img,prefix);
		}
		
		feature = MonogenicParameters.MODULUS;
		if (view[feature]) {
		
			display(feature, mgim, params, countRun,img,prefix);
		}
		
		feature = MonogenicParameters.PHASE;
		if (view[feature]) {
		
			display(feature, mgim, params, countRun,img,prefix);
		}
		
		feature = MonogenicParameters.HILBERT;
		if (view[feature]) {
		
			display(feature, mgim, params, countRun,img,prefix);
		}
		
		feature = MonogenicParameters.HISTOGRAM;
		if (view[feature]) {
		
			display(feature, mgim, params, countRun, img,prefix);
		}
		
		feature = MonogenicParameters.OOP;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun, img,prefix);
		}
		
		feature = MonogenicParameters.FILTERED;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun, img,prefix);
		}
		
		feature = MonogenicParameters.FILTERED_HISTO;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun,img,prefix);
		}
		
		feature = MonogenicParameters.FILTERED_OOP;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun,img,prefix);
		}
		

		feature = MonogenicParameters.MASKED_COLOR;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun, img, prefix);
		}
		
		
		feature = MonogenicParameters.MASKED_HISTO;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun, img, prefix);
		}
		
		feature = MonogenicParameters.MASKED_OOP;
		if (view[feature]) {
			
			display(feature, mgim, params, countRun, img, prefix);
		}
		
		
	}
	
	public static void display(int feature, MonogenicImage mgim, MonogenicParameters params, int countRun, ImagePlus img, String prefix) {
		mgim.showFeature(feature, params, img, prefix);
	
	}
	
	
	
	
	

}