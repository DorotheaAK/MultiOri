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

import java.util.Vector;

import ij.ImagePlus;
import imageware.Builder;
import imageware.ImageWare;
import multigui.ColorBar;
import multigui.Histogram;
import multigui.LogAbstract;
import multigui.OrientationalOrderParameter;
import multigui.PreprocessOrient;

public class GroupImage {

	final public static int SCALABLE_NO = 0;
	final public static int SCALABLE = 1;
	final public static int SCALABLE_RANGE_PI = 2;
	final public static int SCALABLE_RANGE_2PI = 3;
	
	public ImageWare source;
	
	public ImageWare gx;
	public ImageWare gy;

	public ImageWare hxx;
	public ImageWare hyy;
	public ImageWare hxy;
	
	public ImageWare energy;
	public ImageWare coherency;
	public ImageWare orientation;
	public ImageWare harris;
	
	public ImageWare selectedDistributionMask;
	public ImageWare selectedDistributionOrientation;

	public double minmaxHarris[] = new double[2];
	
	public int nx;
	public int ny;
	public int nt;
	
	public ImagePlus originalImage; 

	private static ImagePlus imp;
	
	private LogAbstract log;
	
	private String prefix = "Gardient Structure Tensor Analysis -";
	
	public GroupImage(LogAbstract log, ImagePlus imp, OrientationParameters params) {
		this.log = log;
		this.source = Builder.create(imp);
		originalImage = imp;
		create(params);
	}
	
	private void create( OrientationParameters params) {
		nx = source.getWidth();
		ny = source.getHeight();
		nt = source.getSizeZ();
		long kb = (nx*ny*nt*4) / 1024;
		log.progress("Alloction", 10);
		if (params.gradient == OrientationParameters.HESSIAN) {
			hxx = allocate("Hessian Horizontal", kb);
			log.progress("Alloc HXX", 30);
			hyy = allocate("Hessian Vertical", kb);
			log.progress("Alloc HYY", 40);
			hxy = allocate("Hessian Cross Term", kb);
			log.progress("Alloc HXY", 60);
		}
		else {
			gx = allocate("Gradient Horizontal", kb);
			log.progress("Alloc GX", 40);
			gy = allocate("Gradient Vertical", kb);
			log.progress("Alloc GY", 60);
		}
		energy	= allocate("Tensor Energy", kb);
		log.progress("Alloc E", 70);
		coherency 	= allocate("Coherency", kb);
		log.progress("Alloc Coh", 80);
		orientation = allocate("Orientation", kb);
		
		
		log.progress("Alloc Ori", 90);
	}
	
	private ImageWare allocate(String title, long kb) {
		return Builder.create(nx, ny, nt, ImageWare.FLOAT);
	}
	
	public ImagePlus showFeature(int feature, int countRun, boolean degrees, OrientationParameters params) {
		ImagePlus imp = createImageFeature(feature, countRun, degrees, params);
		if (imp != null)
			imp.show();
		return imp;
	}
	
	public ImagePlus createImageFeature(int feature, int countRun, boolean degrees, OrientationParameters params) {
		int scalability = 0;
		ImageWare image = null;
		
		if (feature == OrientationParameters.GRADIENT_HORIZONTAL) {
			image = (hxx != null ? hxx : gx);
			scalability = SCALABLE;
		}
		else if (feature == OrientationParameters.GRADIENT_VERTICAL) {
			image = (hyy != null ? hyy : gy);
			scalability = SCALABLE;
		}
		else if (feature == OrientationParameters.TENSOR_ORIENTATION) {
			image = orientation;
			scalability = SCALABLE_RANGE_PI;
		}
		else if (feature == OrientationParameters.TENSOR_COHERENCY) {
			image = coherency;
			scalability = SCALABLE_NO;
		}
		else if (feature == OrientationParameters.TENSOR_ENERGY) {
			image = energy;
			scalability = SCALABLE;
		}
	
				
		if (image != null) {
			String title = OrientationParameters.orientation_parameters[feature];
			ImageWare pim = prepare(image, scalability, degrees, false);
			return new ImagePlus(title + "-" + countRun, pim.buildImageStack());
		}

			
		else {
			
			ImageWare c1 = selectChannel(params.featureHue);
			ImageWare c2 = selectChannel(params.featureSat);
			ImageWare c3 = selectChannel(params.featureBri);
			
		if (feature == OrientationParameters.SURVEY) {
			
			ImagePlus imp = null;

			if (params.hsb)
				imp = ColorMapping.colorHSB(nt,  OrientationParameters.orientation_parameters[OrientationParameters.SURVEY],  c1, c2, c3);
			else
				imp = ColorMapping.colorRGB(nt,  OrientationParameters.orientation_parameters[OrientationParameters.SURVEY],  c1, c2, c3);
			
			if (params.featureHue == "Orientation") {
			ColorBar colorBar = new ColorBar();
			colorBar.getColorBar(imp);
			}
			return imp;
		}
		else if (feature == OrientationParameters.FILTERED) {
			ImagePlus filtered = null;
			
			if (params.featureSat == "Coherency") {
				filtered = ColorMapping.filteredColorHSB(nt, OrientationParameters.orientation_parameters[OrientationParameters.FILTERED],  c1, c2, c3, params.coherency);
			}
			else {
				filtered = ColorMapping.colorHSB(nt, OrientationParameters.orientation_parameters[OrientationParameters.FILTERED],  c1, c2, c3);
				
			}
			if (params.featureHue == "Orientation") {
				ColorBar colorBar = new ColorBar();
				colorBar.getColorBar(filtered);
				}
			
			return filtered;
		}
		else if (feature == OrientationParameters.FILTERED_HISTO) {
			final Histogram filterd_histo = new Histogram();
			if (params.featureSat == "Coherency") {
				filterd_histo.run(originalImage, c1, c2, c3, prefix, params.coherency, null);
			}
			else {
				filterd_histo.run(originalImage, c1, c2, c3, prefix, 0, null);
			}
			
        	return null;
		}
		
			
		else if (feature == OrientationParameters.HISTOGRAM) {
			final Histogram da = new Histogram();
        	da.run(originalImage, c1, c2, c3, prefix, 0, null);
        	return null;
		}
		else if (feature == OrientationParameters.FILTERED_OOP){
			final OrientationalOrderParameter oop = new OrientationalOrderParameter();
    	
    		if (params.featureSat == "Coherency") {
    			oop.run(originalImage,c1, c2, c3, params.director, params.drawing, prefix, params.coherency);
			}
			else {
				oop.run(originalImage,c1, c2, c3, params.director, params.drawing, prefix, 0);
			}
    		
			return null;
		}
		
		else if (feature == OrientationParameters.OOP){
			final OrientationalOrderParameter oop = new OrientationalOrderParameter();
    	
			oop.run(originalImage,c1, c2, c3, params.director, params.drawing, prefix, 0);	
			
			return null;
		
		
		}
			
		else if (feature == OrientationParameters.MASKED_COLOR || feature == OrientationParameters.MASKED_HISTO || feature == OrientationParameters.MASKED_OOP) {
			PreprocessOrient preproc = new PreprocessOrient(); 
			ImagePlus maskedImg = preproc.filteringOrient(originalImage, params.saturated);
			
			if ((params.mask == OrientationParameters.FILTER) && (params.featureSat == "Coherency") ) {
				

				if (feature ==  OrientationParameters.MASKED_COLOR) {
					ImagePlus maskedFiltered = null;
					
					maskedFiltered = ColorMapping.maskedColorHSB(nt, OrientationParameters.orientation_parameters[OrientationParameters.FILTERED],  c1, c2, c3, maskedImg, params.coherency);
					if (params.featureHue == "Orientation") {
						ColorBar colorBar = new ColorBar();
						colorBar.getColorBar(maskedFiltered);
						}
					return maskedFiltered;
				}
				
				else if (feature == OrientationParameters.MASKED_HISTO) {
					final Histogram maskedHisto = new Histogram();
					
					maskedHisto.run(originalImage, c1, c2, c3, prefix, params.coherency, maskedImg);
					
				}
				
				else if (feature == OrientationParameters.MASKED_OOP) {
					final OrientationalOrderParameter maskedOop = new OrientationalOrderParameter();
					
					maskedOop.getMaskedOop(maskedImg, c1, c2, prefix, params.coherency);
				}
				
				
				
			}
			
			else {
			if (feature == OrientationParameters.MASKED_HISTO) {
				final Histogram maskedHisto = new Histogram();
				maskedHisto.run(originalImage, c1, c2, c3, prefix, 0, maskedImg);
			}
			
			else if (feature == OrientationParameters.MASKED_COLOR) {
				System.out.println("Masked Color");
				ImagePlus maskedSurvey = ColorMapping.maskedColorHSB(nt, OrientationParameters.orientation_parameters[OrientationParameters.SURVEY],  c1, c2, c3, maskedImg, 0);
				if (params.featureHue == "Orientation") {
					ColorBar colorBar = new ColorBar();
					colorBar.getColorBar(maskedSurvey);
					}
		
				return maskedSurvey; 
			}
			
			else if (feature == OrientationParameters.MASKED_OOP) {
				final OrientationalOrderParameter maskedOop = new OrientationalOrderParameter();
			
				maskedOop.getMaskedOop(maskedImg, c1, c2, prefix, 0);
			}
			}

		}
		}
		

		return null;
	}
	
	public void hideFeature(String title, Vector<ImagePlus> listImage, int countRun) {			
		Vector<ImagePlus> updatedList = new Vector<ImagePlus>();
		for(int i=0; i<listImage.size(); i++) {
			ImagePlus imp = (ImagePlus)listImage.get(i);
			boolean closed = false;
			if (imp != null) {
				for (int k=0; k<=countRun; k++) {
					if (imp.getTitle().equals(title + "-" + k)) {
						imp.close();
						closed = true;
					}
				}
			}
			if (closed == false) {
				updatedList.add(imp);
			}
		}
		listImage.removeAllElements();
		for(int i=0; i<updatedList.size(); i++) {
			ImagePlus imp = (ImagePlus)updatedList.get(i);
			listImage.add(imp);
		}
	}
				
	public ImageWare prepare(ImageWare image, int scalability, boolean degrees, boolean forColor) {
		if (image == null) {
			return null;
		}
		return createStacked(image, scalability, degrees, forColor);
	}
		
	private ImageWare createStacked(ImageWare image, int scalability, boolean degrees, boolean forColor) {
		ImageWare stack = image.duplicate();
		System.out.println("forColor: " + forColor);
		System.out.println("scalability: " + scalability);
		if (forColor) 
			rescaleColor(stack, scalability);
		else
			rescaleMono(stack, scalability, degrees);
		return stack;
	} 

	private void rescaleColor(ImageWare stack, int scalability) {
		System.out.println("scalability: " + scalability + " SCALABLE " + SCALABLE);
		if (scalability == SCALABLE) {
			System.out.println("stack: " + stack);
			stack.rescale(0, 1);
			System.out.println("stack: " + stack);
			
		}
		else if (scalability == SCALABLE_RANGE_PI ) {
			stack.add(Math.PI/2.0);
			stack.multiply(1.0/(Math.PI));
		}
		else if (scalability == SCALABLE_RANGE_2PI ) {
			stack.add(Math.PI);
			stack.multiply(1.0/(Math.PI*2));
		}
	}
	
	private void rescaleMono(ImageWare stack, int scalability, boolean degrees) {
		if (scalability == SCALABLE) {
			stack.rescale(0, 1);
		}
		else if (scalability == SCALABLE_RANGE_PI) {
			if (degrees)				
				stack.multiply(180.0/Math.PI);

		}
		else if (scalability == SCALABLE_RANGE_2PI) {
			stack.add(Math.PI);
			if (degrees)
				stack.multiply(180.0/Math.PI);
		}
	}
		
	public ImageWare selectChannel(String name) {
	
		if (name.equals("Gradient-X") && gy != null) {
			return prepare(gy, SCALABLE, false, true);
		}
		else if (name.equals("Gradient-Y") && gy != null) {
			return prepare(gx, SCALABLE, false, true);
		}
		else if (name.equals("Orientation") && orientation != null) {
			System.out.println("orientation");
			return prepare(orientation, SCALABLE_RANGE_PI, false, true);
		}
		else if (name.equals("Coherency") && coherency != null) {
			return prepare(coherency, SCALABLE_NO, false, true);
		}
		else if (name.equals("Energy") && energy != null) {
			return prepare(energy, SCALABLE, false, true);
		}
		else if (name.equals("Constant")) {
			ImageWare max = Builder.create(nx, ny, nt, ImageWare.FLOAT);
			max.fillConstant(1);
			return prepare(max, SCALABLE_NO, false, true);
		}
		
		ImageWare ori = source.convert(ImageWare.FLOAT);
		return prepare(ori, SCALABLE, false, true);
	}
		
}

