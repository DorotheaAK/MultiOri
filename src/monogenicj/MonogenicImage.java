package monogenicj;

import additionaluserinterface.Chrono;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import imageware.Builder;
import imageware.ImageWare;
import multigui.ColorBar;
import multigui.Histogram;
import multigui.LogAbstract;
import multigui.OrientationalOrderParameter;
import multigui.PreprocessOrient;
import polyharmonicwavelets.ComplexImage;
import polyharmonicwavelets.DyadicFilters;
import polyharmonicwavelets.DyadicTransform;
import polyharmonicwavelets.Parameters;
import riesz.RieszTransform;
import steerabletools.DisplayPyramid;
import steerabletools.StructureTensor;

public class MonogenicImage implements Runnable{
  public ImageWare source;
  
  public ImageWare sourceColorChannel;
  
  public ImageWare rx;
  
  public ImageWare ry;
  
  public ImageWare laplace;
  
  public ImageWare energy;
  
  public ImageWare coherency;
  
  public ImageWare orientation;
  
  public ImageWare monogenicFrequency;
  
  public ImageWare monogenicPhase;
  
  public ImageWare monogenicModulus;
  
  public ImageWare directionalHilbert;
  
  public int nx;
  
  public int ny;
  
  public int scale;
  
  public boolean pyramid;
  
  public double sigma;
  
  public boolean  prefilter; 
  
  public boolean signedDir; 
  
  private LogAbstract log;
  

  public MonogenicImage(LogAbstract log, ImageProcessor ip, MonogenicParameters params) {
  
    this.log = log;
    this.source = Builder.create(new ImagePlus("", ip));
    this.nx = this.source.getWidth();
    this.ny = this.source.getHeight();
    this.scale = params.scale;
    this.pyramid = params.pyramid;
    this.sigma = params.sigma; 
    this.prefilter = params.ckPrefilter; 
    this.signedDir = params.ckSignedDir; 
    log.reset();
    this.monogenicFrequency = Builder.create(this.nx, this.ny, scale, 3);
    this.monogenicModulus = Builder.create(this.nx, this.ny, scale, 3);
    this.monogenicPhase = Builder.create(this.nx, this.ny, scale, 3);
    this.directionalHilbert = Builder.create(this.nx, this.ny, scale, 3);
    this.sourceColorChannel = computeSourceForColorChannel();
  }
  
	@Override
	public void run() {
		compute(sigma, 1.0E-7D, prefilter, signedDir);
	}

  
  private ImageWare computeSourceForColorChannel() {
    ImageWare out = Builder.create(this.nx, this.ny, this.scale, 3);
    ImageWare slice = Builder.create(this.nx, this.ny, 1, 3);
    this.source.getXY(0, 0, 0, slice);
    slice.rescale(0.0D, 1.0D);
    out.putXY(0, 0, 0, slice);
    if (this.pyramid) {
      for (int s = 1; s < this.scale; s++) {
        int div = (int)Math.round(Math.pow(2.0D, s));
        int mx = this.nx / div;
        int my = this.ny / div;
        for (int i = 0; i < mx; i++) {
          for (int j = 0; j < my; j++)
            out.putPixel(i, j, s, out.getPixel(i * div, j * div, 0)); 
        } 
      } 
    } else {
      for (int s = 1; s < this.scale; s++)
        out.putXY(0, 0, s, slice); 
    } 
    return out;
  }
  
  public void compute(double sigma, double epsilon, boolean prefilter, boolean signedDir) {
    this.log.progress("Polyharmonic", 20);
    this.laplace = computePolyharmonicWavelets(this.pyramid, this.source);
    this.log.progress("Riesz", 50);
    ImageWare pre = prefilter ? prefilter(this.source) : this.source;
    RieszTransform rt = new RieszTransform(this.nx, this.ny, 1, true);
    ImageWare[] rieszChannels = rt.analysis(pre);
    this.rx = computePolyharmonicWavelets(this.pyramid, rieszChannels[0]);
    this.ry = computePolyharmonicWavelets(this.pyramid, rieszChannels[1]);
    ImagePlus impSource = new ImagePlus("", this.source.buildImageStack());
    ComplexImage image = new ComplexImage(impSource);
    this.log.progress("Wavenumber", 60);
    Parameters param = new Parameters();
    param.J = this.scale;
    param.redundancy = this.pyramid ? 2 : 1;
    param.analysesonly = true;
    param.flavor = 7;
    param.prefilter = true;
    param.lattice = 1;
    param.order = 1.0D;
    param.rieszfreq = 1;
    param.N = 0;
    DyadicFilters filters1 = new DyadicFilters(param, image.nx, image.ny);
    filters1.compute();
    DyadicTransform transform = new DyadicTransform(filters1, param);
    ComplexImage[] q1xq2y = transform.analysis(image);
    Chrono.tic();
    Parameters param1 = new Parameters();
    param1.J = this.scale;
    param1.redundancy = this.pyramid ? 2 : 1;
    param1.analysesonly = true;
    param1.flavor = 7;
    param1.prefilter = true;
    param1.lattice = 1;
    param1.order = 1.0D;
    param1.rieszfreq = 1;
    param1.N = 1;
    DyadicFilters filters = new DyadicFilters(param1, image.nx, image.ny);
    filters.compute();
    DyadicTransform transform1 = new DyadicTransform(filters, param1);
    ComplexImage[] pxpy = transform1.analysis(image);
    this.log.progress("ST", 70);
    computeStructureTensor(sigma, epsilon, signedDir);
    this.log.progress("Monogenic", 70);
    computeMonogenic(pxpy, q1xq2y);
    this.log.finish();
  }
  
  private ImageWare prefilter(ImageWare in) {
    Parameters param = new Parameters();
    param.J = this.scale;
    param.analysesonly = true;
    param.flavor = 7;
    param.prefilter = true;
    param.lattice = 1;
    param.order = 2.0D;
    param.rieszfreq = 0;
    param.N = 0;
    DyadicFilters filters = new DyadicFilters(param, in.getWidth(), in.getHeight());
    ComplexImage P = filters.computePrefilter(2.0D);
    ComplexImage X = new ComplexImage(in);
    X.FFT2D();
    X.multiply(P);
    X.iFFT2D();
    return convertDoubleToImageWare(X.real, in.getWidth(), in.getHeight());
  }
  
  public ImageWare computePolyharmonicWavelets(boolean pyramid, ImageWare in) {
    Parameters param = new Parameters();
    param.J = this.scale;
    param.redundancy = pyramid ? 2 : 1;
    param.analysesonly = true;
    param.flavor = 7;
    param.prefilter = true;
    param.lattice = 1;
    param.order = 2.0D;
    param.rieszfreq = 0;
    param.N = 0;
    DyadicFilters filters = new DyadicFilters(param, in.getWidth(), in.getHeight());
    filters.compute();
    DyadicTransform transform = new DyadicTransform(filters, param);
    ImageWare out = transform.analysisImage(in);
    return out;
  }
  
  public ComplexImage[] computePolyharmonicWavelets(Parameters param, ComplexImage image) {
    param.order = 2.0D;
    param.rieszfreq = 0;
    param.N = 0;
    DyadicFilters filtersPHW = new DyadicFilters(param, image.nx, image.ny);
    filtersPHW.setParameters(param);
    filtersPHW.compute();
    DyadicTransform transform = new DyadicTransform(filtersPHW, param);
    ComplexImage[][] plp = transform.analysisLowpass(image);
    return plp[0];
  }
  
  public void computeStructureTensor(double sigma, double epsilon, boolean signedDir) {
    StructureTensor tensor = new StructureTensor(null);
    tensor.compute(this.rx, this.ry, sigma, epsilon);
    this.orientation = tensor.getOrientation();
    this.energy = tensor.getEnergy();
    this.coherency = tensor.getCoherency();
    if (signedDir)
      for (int k = 0; k < this.scale; k++) {
        for (int x = 0; x < this.nx; x++) {
          for (int y = 0; y < this.ny; y++) {
            if (this.ry.getPixel(x, y, k) < 0.0D)
              this.orientation.putPixel(x, y, k, -this.orientation.getPixel(x, y, k)); 
          } 
        } 
      }  
  }
  
  public void computeMonogenic(ComplexImage[] pxpy, ComplexImage[] q1xq2y) {
    double correctionFreq = 1.0D;
    for (int j = 0; j < this.scale; j++) {
      ComplexImage w = pxpy[j];
      ComplexImage r = q1xq2y[j];
      for (int x = 0; x < w.nx; x++) {
        for (int y = 0; y < w.ny; y++) {
          int k = x + y * w.nx;
          double theta = this.orientation.getPixel(x, y, j);
          double p = this.laplace.getPixel(x, y, j);
          double r1 = this.rx.getPixel(x, y, j);
          double r2 = this.ry.getPixel(x, y, j);
          double cos = Math.cos(theta);
          double sin = Math.sin(theta);
          double q = r2 * cos + r1 * sin;
          double amp = Math.sqrt(p * p + q * q);
          double nu = q * (w.real[k] * cos + w.imag[k] * sin) + p * r.real[k];
          this.monogenicModulus.putPixel(x, y, j, amp);
          this.monogenicPhase.putPixel(x, y, j, Math.atan2(q, p));
          this.monogenicFrequency.putPixel(x, y, j, nu * correctionFreq / amp * amp);
          this.directionalHilbert.putPixel(x, y, j, q);
        } 
      } 
      correctionFreq /= 2.0D;
    } 
  }
  
  
  private ImageWare convertDoubleToImageWare(double[] array, int mx, int my) {
    ImageWare image = Builder.create(mx, my, 1, 4);
    int k = 0;
    for (int y = 0; y < my; y++) {
      for (int x = 0; x < mx; x++)
        image.putPixel(x, y, 0, array[k++]); 
    } 
    return image;
  }
  
  
  public void showFeature(int feature, MonogenicParameters params, ImagePlus imp, String prefix) {
	
	if (feature == MonogenicParameters.LAPLACE) {
		DisplayPyramid.show(laplace, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid, prefix); //else if checkboxName = Histogram, else if checkboxName = OOP etc.
	} else if (feature == MonogenicParameters.RIESZ_HORIZONTAL) {
		DisplayPyramid.show(rx, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid, prefix);
	} else if (feature == MonogenicParameters.RIESZ_VERTICAL) { 
		DisplayPyramid.show(ry, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid, prefix);
	} else if (feature == MonogenicParameters.ORIENTATION)  {
		DisplayPyramid.show(orientation, MonogenicParameters.monogenic_parameters[feature], DisplayPyramid.NORESCALE, params.stacked, pyramid,prefix);
	} else if (feature == MonogenicParameters.COHERENCY) {
		DisplayPyramid.show(coherency, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid,prefix);
	} else if (feature == MonogenicParameters.ENERGY) {
		DisplayPyramid.show(energy, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid,prefix);
	} else if (feature == MonogenicParameters.WAVE) {
		DisplayPyramid.show(monogenicFrequency, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid,prefix);
	} else if (feature == MonogenicParameters.MODULUS) {
		DisplayPyramid.show(monogenicModulus, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid,prefix);
	} else if (feature == MonogenicParameters.PHASE) {
		DisplayPyramid.show(monogenicPhase, MonogenicParameters.monogenic_parameters[feature], DisplayPyramid.NORESCALE, params.stacked, pyramid,prefix);
	} else if (feature == MonogenicParameters.HILBERT) {
		DisplayPyramid.show(directionalHilbert, MonogenicParameters.monogenic_parameters[feature], params.scale, params.stacked, pyramid,prefix);
	}
	else {
		ImageWare hue = selectColor(params.featureHue);
		ImageWare sat = selectColor(params.featureSat);
		ImageWare bri = selectColor(params.featureBri);
		    	
		if (feature == MonogenicParameters.HISTOGRAM){
			final Histogram da = new Histogram();
		    da.run(imp, hue, sat, bri,prefix, 0, null);
		    }
		else if (feature ==  MonogenicParameters.SURVEY) {
			ImagePlus img = DisplayPyramid.colorHSB(hue, sat, bri, params.stacked, pyramid);
			String title = "Color Survey";
			img.setTitle(prefix + title);
			img.show();
		    new ColorBar().getColorBar(img);
		    }
		else if (feature == MonogenicParameters.FILTERED){
			System.out.println("Saturation: " + params.featureSat);
			ImagePlus filteredImg = null;
			if (params.featureSat == "Coherency") {
				filteredImg = DisplayPyramid.filteredColorHSB(hue, sat, bri, params.stacked, pyramid, params.coherency);
			}
			else {
				filteredImg = DisplayPyramid.colorHSB(hue, sat, bri, params.stacked, pyramid);
			}
			String title = "Filtered Color Survey";
		    filteredImg.setTitle(prefix + title);
		    filteredImg.show();

		    }
		
		
		else if (feature == MonogenicParameters.FILTERED_HISTO) {
			
			final Histogram filterd_histo = new Histogram();
			System.out.println("Saturation: " + params.featureSat);
			if (params.featureSat == "Coherency") {
				filterd_histo.run(imp,  hue, sat, bri, prefix, params.coherency, null);
			}
			else {
				filterd_histo.run(imp, hue, sat, bri, prefix, 0, null);
			}
		}
		
		else if (feature == MonogenicParameters.FILTERED_OOP) {
			final OrientationalOrderParameter oop = new OrientationalOrderParameter();
    		
    		if (params.featureSat == "Coherency") {
    			System.out.println("Saturation: " + params.featureSat);
    			oop.run(imp, hue, sat, bri, params.director, params.drawing, prefix, params.coherency);
			}
			else {
				oop.run(imp, hue, sat, bri, params.director, params.drawing, prefix, 0);
			}
		
		}
		else if (feature == MonogenicParameters.OOP){
		    final OrientationalOrderParameter oop = new OrientationalOrderParameter();
		   
		    oop.run(imp, hue, sat, bri, params.director, params.drawing,prefix, 0);
		    }
		else if (feature == MonogenicParameters.MASKED_COLOR || feature == MonogenicParameters.MASKED_HISTO || feature == MonogenicParameters.MASKED_OOP) {
			
			PreprocessOrient preproc = new PreprocessOrient(); 
			ImagePlus maskedImg = preproc.filteringOrient(imp, params.saturated);
			
			if ((params.mask == MonogenicParameters.FILTER) && (params.featureSat == "Coherency") ) {
				

				if (feature ==  MonogenicParameters.MASKED_COLOR) {
					ImagePlus maskedFiltered = null;
					
					maskedFiltered = DisplayPyramid.maskedColorHSB(hue, sat, bri, maskedImg, params.coherency, params.stacked, params.pyramid);
					String title = "Masked Filtered Color Survey";
					maskedFiltered.setTitle(prefix + title);
					maskedFiltered.show();
				}
				
				else if (feature == MonogenicParameters.MASKED_HISTO) {
					final Histogram maskedHisto = new Histogram();
					
					maskedHisto.run(imp, hue, sat, bri, prefix, params.coherency, maskedImg);
					
				}
				
				else if (feature == MonogenicParameters.MASKED_OOP) {
					final OrientationalOrderParameter maskedOop = new OrientationalOrderParameter();
				
					maskedOop.getMaskedOop(maskedImg, hue, sat, prefix, params.coherency);
				}
				
				
				
			}
			
			else {
			
			if (feature == MonogenicParameters.MASKED_HISTO) {
				final Histogram maskedHisto = new Histogram();
				maskedHisto.run(imp, hue, sat, bri, prefix, 0, maskedImg);
			}
			
			else if (feature == MonogenicParameters.MASKED_COLOR) {
				
				ImagePlus maskedFiltered = null;
				System.out.println("Masked Color");
				System.out.println("implement filtered mask");
				maskedFiltered = DisplayPyramid.maskedColorHSB(hue, sat, bri, maskedImg, 0, params.stacked, params.pyramid);
				String title = "Masked Color Survey";
				maskedFiltered.setTitle(prefix + title);
				maskedFiltered.show();
			}
			
			else if (feature == MonogenicParameters.MASKED_OOP) {
				final OrientationalOrderParameter maskedOop = new OrientationalOrderParameter();

				maskedOop.getMaskedOop(maskedImg, hue, sat, prefix, 0);
			}
			}
		}
		}
	
	}
		
	private ImageWare selectColor(String name) {
			    ImageWare out = null;
			 
			    if (name.equals("Laplace")) {
			      out = laplace.convert(3);
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Riesz X")) {
			      out = rx.duplicate();
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Riesz Y")) {
			      out = ry.duplicate();
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Orientation")) {
			      out = DisplayPyramid.rescaleAngle(orientation, pyramid);
			    } else if (name.equals("Coherency")) {
			      out = coherency.duplicate();
			    } else if (name.equals("Energy")) {
			      out = energy.duplicate();
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Wavenumber")) {
			      out = monogenicFrequency.convert(3);
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Modulus")) {
			      out = monogenicModulus.convert(3);
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Phase")) {
			      out = DisplayPyramid.rescaleAngle(monogenicPhase, pyramid);
			    } else if (name.equals("Dir. Hilbert")) {
			      out = directionalHilbert.convert(3);
			      out.rescale(0.0D, 1.0D);
			    } else if (name.equals("Maximum")) {
			      out = Builder.create(nx, ny, scale, 3);
			      out.fillConstant(1.0D);
			    }
			    else if (name.equals("Constant")) {
			    	out = Builder.create(nx, ny, scale, 3);
					out.fillConstant(1);
			    } else {
			      return this.sourceColorChannel;
			    } 
			    return out;
			  }

}
