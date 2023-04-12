package monogenicj;

import java.util.Arrays;

import multigui.Settings;


public class MonogenicParameters {
	
	
	
	
	final public static String scaled_options[] = {"Scaled values / bands", "True values"};
	
	final public static int		SCALED	= 0;
	final public static int		TRUE_VALUES	= 1;
	
	final public static String stacked_options[] = {"Stacked presentation", "Horizontal Flatten", "Vertical Flatten"};
	
	final public static int		STACKED	= 0;
	final public static int		HORIZONTAL_FLATTEN			= 1;
	final public static int		VERTICAL_FLATTEN		= 2;
	
	final public static String masked_options[] = {"Not Filtered", "Filtered"};
	
	final public static int NO_FILTER = 0;
	final public static int FILTER = 1; 
	
	
	final public static String oop_options[] = {"Without Director", "With Director"};
	
	final public static int		DIRECTOR					= Arrays.asList(oop_options).indexOf("With Director");
	final public static int		NO_DIRECTOR					= Arrays.asList(oop_options).indexOf("Without Director");
	
	final public static String director_options[] = { "Manual Entry", "Draw" };

	final public static int		MANUAL					= Arrays.asList(director_options).indexOf("Manual Entry");
	final public static int		DRAW					= Arrays.asList(director_options).indexOf("Draw");
	
	
	
	
	final static public String	monogenic_parameters[]					= { 
			"Riesz X", "Riesz Y", "Orientation", "Coherency", "Energy", "Laplace", "Color Survey",
			  "Wavenumber", "Modulus", "Phase", "Dir. Hilbert", "Filtered Color Survey", "Histogram", "Orientational Order Parameter", 
			  "Filtered Histogram", "Filtered Orientational Order Parameter",  "Masked Color Survey", "Masked Histogram", "Masked Orientational Order Parameter"};

	
	
	final public static int		RIESZ_HORIZONTAL		= Arrays.asList(monogenic_parameters).indexOf("Riesz X");
	final public static int		RIESZ_VERTICAL		= Arrays.asList(monogenic_parameters).indexOf("Riesz Y");
	final public static int		ENERGY			= Arrays.asList(monogenic_parameters).indexOf("Energy");
	final public static int		COHERENCY			= Arrays.asList(monogenic_parameters).indexOf("Coherency");
	final public static int		ORIENTATION		= Arrays.asList(monogenic_parameters).indexOf("Orientation");
	final public static int		LAPLACE			= Arrays.asList(monogenic_parameters).indexOf("Laplace");
	
	
	final public static int		SURVEY					= Arrays.asList(monogenic_parameters).indexOf("Color Survey");
	final public static int		HISTOGRAM			= Arrays.asList(monogenic_parameters).indexOf("Histogram");
	final public static int		OOP			= Arrays.asList(monogenic_parameters).indexOf("Orientational Order Parameter");
	final public static int		FILTERED			= Arrays.asList(monogenic_parameters).indexOf("Filtered Color Survey");
	final public static int		WAVE			= Arrays.asList(monogenic_parameters).indexOf("Wavenumber");
	final public static int		MODULUS			= Arrays.asList(monogenic_parameters).indexOf("Modulus");
	final public static int		PHASE			= Arrays.asList(monogenic_parameters).indexOf("Phase");
	final public static int		HILBERT			= Arrays.asList(monogenic_parameters).indexOf("Dir. Hilbert");
	final public static int		FILTERED_HISTO			= Arrays.asList(monogenic_parameters).indexOf("Filtered Histogram");
	final public static int		FILTERED_OOP			= Arrays.asList(monogenic_parameters).indexOf("Filtered Orientational Order Parameter");
	final public static int		MASKED_COLOR					= Arrays.asList(monogenic_parameters).indexOf("Masked Color Survey");
	final public static int		MASKED_HISTO					= Arrays.asList(monogenic_parameters).indexOf("Masked Histogram");
	final public static int		MASKED_OOP					= Arrays.asList(monogenic_parameters).indexOf("Masked Orientational Order Parameter");
	
	
	
	public double			sigma 					= 3;
	
	public boolean				pyramid					= true;
	public boolean ckPrefilter 						= true; 
	public boolean ckSignedDir 						= true; 
	
	public double				coherency 				= 0;
	public double 				saturated				= 0.2; 
	
	public int 					director					= NO_DIRECTOR;
	public int 					drawing					= MANUAL;
	public int 					mask						= NO_FILTER; 
	public int				scale					= TRUE_VALUES;
	public int 			stacked 				= STACKED;
	
	
	final public static int		MONOG_FEATURES				= monogenic_parameters.length;
	
	public boolean				view[]					= new boolean[MONOG_FEATURES];

	

	public String				featureHue				= "Orientation";
	public String				featureSat				= "Coherency";
	public String				featureBri				= "Image Original";

	
	
	public MonogenicParameters() {
		
		view[SURVEY] = true;
		System.out.println("Monogenic Parameters view");
		for (int i = 0; i < view.length; i++) {
			System.out.print("view: " + view[i] + " ");
		}
	}
	


	public void load(Settings settings) {
		scale = settings.loadValue("scale", scale);
		pyramid = settings.loadValue("pyramid", true);
		
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++) {
			view[k] = settings.loadValue("view_" + monogenic_parameters[k],
					(k == SURVEY ? true : false));
		}
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++) {
			System.out.print("view load settings mono : " + view[k] + " ");
		}

		
	}

	public void store(Settings settings) {
		settings.storeValue("scale", scale);
		settings.storeValue("pyramid", pyramid);

		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++) {
			settings.storeValue("view_" + monogenic_parameters[k], view[k]);
		}
		
	}
	
}
