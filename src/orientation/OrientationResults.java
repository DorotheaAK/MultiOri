//=============================================================================================================
//
// Project: Directional Image Analysis - OrientationJ plugins
// 
// Author: Daniel Sage
// 
// Organization: Biomedical Imaging Group (BIG)
// Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
//
// Information: 
// OrientationJ: http://bigwww.epfl.ch/demo/orientation/
// MonogenicJ: http://bigwww.epfl.ch/demo/monogenic/
//  
// Reference on methods and plugins
// Z. Püspöki, M. Storath, D. Sage, M. Unser
// Transforms and Operators for Directional Bioimage Analysis: A Survey 
// Advances in Anatomy, Embryology and Cell Biology, vol. 219, Focus on Bio-Image Informatics 
// Springer International Publishing, ch. 33, 2016.
//
//
// Reference the application measure of coherency
// R. Rezakhaniha, A. Agianniotis, J.T.C. Schrauwen, A. Griffa, D. Sage, 
// C.V.C. Bouten, F.N. van de Vosse, M. Unser, N. Stergiopulos
// Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia 
// Using Confocal Laser Scanning Microscopy
// Biomechanics and Modeling in Mechanobiology, vol. 11, no. 3-4, 2012.

// Reference the application direction of orientation
// E. Fonck, G.G. Feigl, J. Fasel, D. Sage, M. Unser, D.A. Ruefenacht, N. Stergiopulos 
// Effect of Aging on Elastin Functionality in Human Cerebral Arteries
// Stroke, vol. 40, no. 7, 2009.
//
// Conditions of use: You are free to use this software for research or
// educational purposes. In addition, we expect you to include adequate
// citations and acknowledgments whenever you present or publish results that
// are based on it.
//
//=============================================================================================================

package orientation;


import java.awt.Frame;

import ij.ImagePlus;


public class OrientationResults {
	public static String prefix =  "Gradient Structure Tensor Analysis -";

	public static void hide() {
		Frame frame[] = Frame.getFrames();
		for (int i=0; i<frame.length; i++) {
			if (frame[i].getTitle().startsWith(prefix))
				frame[i].dispose();
		}
	}

	public static void show(int feature, GroupImage gim, OrientationParameters params, int countRun) {
		
			boolean view[]= new boolean[params.view.length];
			view[feature] = true;
			show(view, gim, params, countRun);
		
	}

	public static void show(GroupImage gim, OrientationParameters params, int countRun) {
		show(params.view, gim, params, countRun);
	}
	
	public static void show(boolean view[], GroupImage gim, OrientationParameters params, int countRun) {
		int feature;

		feature = OrientationParameters.GRADIENT_HORIZONTAL;
		if (view[feature] && params.isServiceAnalysis())
			display(feature, gim, params, countRun);

		feature = OrientationParameters.GRADIENT_VERTICAL;
		if (view[feature] && params.isServiceAnalysis())
			display(feature, gim, params, countRun);

		feature = OrientationParameters.TENSOR_ENERGY;
		if (view[feature])
			display(feature, gim, params, countRun);

		feature = OrientationParameters.TENSOR_ORIENTATION;
		if (view[feature])
			display(feature, gim, params, countRun);

		feature = OrientationParameters.TENSOR_COHERENCY;
		if (view[feature])
			display(feature, gim, params, countRun);

		feature = OrientationParameters.SURVEY;
		if (view[feature]) {
		
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.HISTOGRAM;
		if (view[feature]) {
		
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.OOP;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.FILTERED;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.FILTERED_HISTO;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.FILTERED_OOP;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		
		feature = OrientationParameters.MASKED_COLOR;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		
		feature = OrientationParameters.MASKED_HISTO;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		feature = OrientationParameters.MASKED_OOP;
		if (view[feature]) {
			
			display(feature, gim, params, countRun);
		}
		
		
	}
	
	public static void display(int feature, GroupImage gim, OrientationParameters params, int countRun) {
		ImagePlus imp = gim.showFeature(feature, countRun, !params.radian, params);
		if (imp == null) {}
		else {
			imp.setTitle(prefix + imp.getTitle());
			imp.show();
		}
	}
	
	
	
	
	

}
