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

import java.util.Arrays;

import multigui.Settings;

public class OrientationParameters {

	final public static int		MODE_ANALYSIS			= 0;

	private OrientationService	service					= OrientationService.ANALYSIS;

	final public static int		GRADIENT_CUBIC_SPLINE	= 0;
	final public static int		GRADIENT_FINITE_DIFF	= 1;
	final public static int		GRADIENT_FOURIER_DOMAIN	= 2;
	final public static int		GRADIENT_RIESZ			= 3;
	final public static int		GRADIENT_GAUSSIAN		= 4;
	final public static int		HESSIAN					= 5;
	
	final public static String director_options[] = { "Manual Entry", "Draw" };

	final public static int		MANUAL					= Arrays.asList(director_options).indexOf("Manual Entry");
	final public static int		DRAW					= Arrays.asList(director_options).indexOf("Draw");
	
	
	final public static String masked_options[] = {"Not Filtered", "Filtered"};
	
	final public static int NO_FILTER = 0;
	final public static int FILTER = 1; 
	
	
	final public static String oop_options[] = {"Without Director", "With Director"};

	final public static int		DIRECTOR					= Arrays.asList(oop_options).indexOf("With Director");
	final public static int		NO_DIRECTOR					= Arrays.asList(oop_options).indexOf("Without Director");
	
	final static public String	orientation_parameters[]					= { 
			"Gradient-X", "Gradient-Y", "Orientation",
			"Coherency", "Energy", "Color Survey", "Histogram", "Orientational Order Parameter", "Filtered Color Survey", "Filtered Histogram",
			"Filtered Orientational Order Parameter", "Masked Color Survey", "Masked Histogram", "Masked Orientational Order Parameter"};
	
	final public static int		GRADIENT_HORIZONTAL		= Arrays.asList(orientation_parameters).indexOf("Gradient-X");
	final public static int		GRADIENT_VERTICAL		= Arrays.asList(orientation_parameters).indexOf("Gradient-Y");
	final public static int		TENSOR_ENERGY			= Arrays.asList(orientation_parameters).indexOf("Energy");
	final public static int		TENSOR_ORIENTATION		= Arrays.asList(orientation_parameters).indexOf("Orientation");
	final public static int		TENSOR_COHERENCY			= Arrays.asList(orientation_parameters).indexOf("Coherency");

	final public static int		SURVEY					= Arrays.asList(orientation_parameters).indexOf("Color Survey");
	
	final public static int		HISTOGRAM					= Arrays.asList(orientation_parameters).indexOf("Histogram");
	final public static int		OOP					= Arrays.asList(orientation_parameters).indexOf("Orientational Order Parameter");
	final public static int		FILTERED					= Arrays.asList(orientation_parameters).indexOf("Filtered Color Survey");
	final public static int		FILTERED_HISTO					= Arrays.asList(orientation_parameters).indexOf("Filtered Histogram");
	final public static int		FILTERED_OOP					= Arrays.asList(orientation_parameters).indexOf("Filtered Orientational Order Parameter");
	final public static int		MASKED_COLOR					= Arrays.asList(orientation_parameters).indexOf("Masked Color Survey");
	final public static int		MASKED_HISTO					= Arrays.asList(orientation_parameters).indexOf("Masked Histogram");
	final public static int		MASKED_OOP					= Arrays.asList(orientation_parameters).indexOf("Masked Orientational Order Parameter");
	
	
	public int					gradient					= GRADIENT_CUBIC_SPLINE;
	
	public int 					director					= NO_DIRECTOR;
	
	public int 					drawing 					= MANUAL; 
	
	public int 					mask						= NO_FILTER; 
	
	public double				sigmaLoG					= 0;
	public double				sigmaST					= 3;
	public double				epsilon					= 0.001;
	public boolean				radian					= true;
	public double				coherency 				= 0;
	
	public double 				saturated				= 0.2; 
	
	final public static int		ORIENT_FEATURES				= orientation_parameters.length;
	
	public boolean				hsb						= true;
	public boolean				view[]					= new boolean[ORIENT_FEATURES];
	
	
	

	public String				featureHue				= "Orientation";
	public String				featureSat				= "Coherency";
	public String				featureBri				= "Image Original";

	public OrientationParameters(OrientationService service) {
		this.service = service;
		view[SURVEY] = true;
		System.out.println("Orientation Parameters view");
		for (int i = 0; i < view.length; i++) {
			System.out.print("view: " + view[i] + " ");
		}
		
	}
	
	public boolean isServiceAnalysis() {
		return service == OrientationService.ANALYSIS;
	}

	public void load(Settings settings) {
		epsilon = settings.loadValue("epsilon", epsilon);
		radian = settings.loadValue("radian", true);
		hsb = settings.loadValue("hsb", hsb);
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++) {
			view[k] = settings.loadValue("view_" + orientation_parameters[k],
					(k == SURVEY ? true : false));
		}
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++) {
			System.out.print("view load settings : " + view[k] + " ");
		}
		
	}

	public void store(Settings settings) {
		settings.storeValue("epsilon", epsilon);
		settings.storeValue("radian", radian);
		settings.storeValue("hsb", hsb);
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++) {
			settings.storeValue("view_" + orientation_parameters[k], view[k]);
		}
		
	}

	

}