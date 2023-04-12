package multigui;

import java.awt.Color;
import ij.plugin.filter.Binary;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imageware.ImageWare;

public class Histogram {
	
	
	/** The ImagePlus this plugin operates on. */
	protected ImagePlus imp;

	/** The number of bins to create. */
	protected int nbins = 90;

	/**
	 * The first bin in degrees when displaying the histogram, so that we are
	 * not forced to start at -90
	 */
	private float bin_start = -90;

	/**
	 * The last bin in degrees when displaying the histogram, so that we are
	 * able to limit the range of angles to analyse
	 */
	private float bin_end = 90;


	/* STD FIELDS */

	/** Polar coordinates, stored as a FloatProcessor. */
	protected FloatProcessor window, r, theta;

	protected int width, height, small_side, long_side, npady, npadx, step, pad_size;

	/**
	 * The bin centers, in radians. Internally, they always range from -pi/2 to
	 * pi/2.
	 */
	protected float[] bins;

	/**
	 * The directionality histogram, one array per processor (3 in the case of a
	 * ColorProcessor).
	 */
	protected ArrayList< double[] > histograms;

	/** Store a String representing the fitting function. */
	protected String fit_string;

	/** This stack stores the orientation map. */
	ImageStack orientation_map;
	public boolean masking = false; 
	

	public void run(ImagePlus img, ImageWare hue, ImageWare sat, ImageWare bri, String prefix, double coherency, ImagePlus masked) {


		if ( img == null )
		{
			IJ.error( "Directionality", "No images are open." );
			return;
		}
		
		setMethod(img);
		
		if (masked == null) {
			getDefaultHistogram(hue, sat, prefix, coherency); 
		}
		else {
			getMaskedHistogram(hue, sat, prefix, masked, coherency);
		}
		
		
	    
	 
	}
	
	
	private void getMaskedHistogram(ImageWare hue, ImageWare sat, String prefix, ImagePlus masked, double coherency) {
		int nscale = hue.getSizeZ();
		
		masking = true; 
		ImagePlus flattened = masked.flatten();

		ImageProcessor ip = flattened.getProcessor();
		System.out.println(hue.getSizeZ());
		
		 for (int k = 0; k <nscale; k++) {
		    	
		    	
		    	ArrayList<Float> hueMap = new ArrayList<Float>();
		    	int mx = (int)(hue.getSizeX()/(Math.pow(2, k)));
				int my = (int)(hue.getSizeY()/(Math.pow(2, k)));

		    	  
				ImageProcessor ip_resized = ip.resize((int)(flattened.getWidth()/(Math.pow(2.0D, k))), (int)(flattened.getHeight()/(Math.pow(2.0D, k))), true);
		    	ImagePlus resizedImg = new ImagePlus("", ip_resized);
				
		    	for(int y=0; y< resizedImg.getHeight(); y++) {
		    		for (int x = 0; x <resizedImg.getWidth(); x++) {
		    			 if (resizedImg.getPixel(x,y)[0] != 0) {
		    				 float h = (float)hue.getPixel(x, y, k);  
								float s = (float)sat.getPixel(x, y, k);  
								if (s > coherency/100) {
									hueMap.add(h);}
		    			
		    		}
		    		}}
		    
				Collections.sort(hueMap);
			
				String scale = Integer.toString(k+1);

				setMethod();
				computeHistograms(hueMap);
			
				plotResults(scale, prefix, coherency).setVisible( true );
			
			
		}
		
	}
		
	public void getDefaultHistogram(ImageWare hue, ImageWare sat, String prefix, double coherency) {
		

	    for (int k = 0; k < hue.getSizeZ(); k++) {
	    	
	    	
	    	ArrayList<Float> hueMap = new ArrayList<Float>();
	    	int mx = (int)(hue.getSizeX()/(Math.pow(2, k)));
			int my = (int)(hue.getSizeY()/(Math.pow(2, k)));

	    	
			for (int y = 0; y < my; y++) {
				for (int x = 0; x < mx; x++) {
					float h = (float)hue.getPixel(x, y, k);  
					float s = (float)sat.getPixel(x, y, k);  
					if (s > coherency/100) {
						hueMap.add(h);}
				}
			}
	    
			Collections.sort(hueMap);
		
			String scale = Integer.toString(k+1);

			setMethod();
			computeHistograms(hueMap);
		
			plotResults(scale, prefix, coherency).setVisible( true );
	    
	    
	    }
		
		
	}
	
	public void setMethod() {
		histograms = null;
	}
	
	public void setMethod(ImagePlus img)
	{
		histograms = null;
		
		setImagePlus(img);
		
		final ImageProcessor ip = imp.getProcessor();
		ip.setLineWidth( 4 );
		ip.setColor( Color.WHITE );
		
		setImagePlus( imp );

		setBinNumber( 24 );
		setBinStart( 0 );
		
		
	}
	
	public void setImagePlus( final ImagePlus imp )
	{
		this.imp = imp;
	}

	public void setBinNumber( final int nbins )
	{
		this.nbins = nbins;
	}
	public void setBinStart( final float bin_start )
	{
		this.bin_start = bin_start;
		this.bin_end = bin_start + 180;

	}
	
	
	public void computeHistograms(ArrayList<Float> hueMap )
	{
		if ( null == imp )
			return;

		// Prepare helper fields
		bins = prepareBins( nbins, bin_start, bin_end );
		
		// Prepare result holder
		final int n_slices = imp.getStackSize();
		histograms = new ArrayList< double[] >( n_slices * imp.getNChannels() );
		
		// Loop over each slice
		ImageProcessor ip = null;
		double[] dir = null;
		for ( int i = 0; i < n_slices; i++ )
		{
			ip = imp.getStack().getProcessor( i + 1 );
			for ( int channel_number = 0; channel_number < ip.getNChannels(); channel_number++ )
			{

				dir = putHueInBins(hueMap);
				
				histograms.add( dir );
			}
		}
	}
	
	private final double[] putHueInBins(ArrayList<Float> hueMap) {
		final double[] norm_dir = new double[ nbins ]; 
		
		for (int j = 0; j < hueMap.size(); j++) {
			for (int i=0; i< nbins-1; i++) {
				
				float value = hueMap.get(j);
				
				if ((value < bins[i+1]) & (value >= bins[i])) {
					
					norm_dir[i] += 1;
			
					break;
					
					
				}
			}
			
			
		}
		
		return norm_dir;
		
	}
	
	
	
	public JFrame plotResults(String scale, String prefix, double coherency)
	{
		final XYSeriesCollection histogram_plots = new XYSeriesCollection();
		final LookupPaintScale lut = createLUT( histograms.size() );
		String name = imp.getShortTitle();
		
		System.out.println("coherency in HIST " + coherency);
		if (masking) {
			name = name + " masked";
		}
		
		if (coherency > 0) {
			name = name + " filtered"; 
		}

		System.out.println(name);
		XYSeries series;

		final double[] degrees_bins = new double[ nbins ];
		for ( int i = 0; i < degrees_bins.length; i++ )
		{
			degrees_bins[ i ] = bins[ i ] * 180;
		}

		// This is where we shift histograms
		double[] dir;
		for ( int i = 0; i < histograms.size(); i++ )
		{
			dir = histograms.get( i );
			series = new XYSeries( name );
			for ( int j = 0; j < nbins; j++ )
			{
				series.add( degrees_bins[ j ], dir[ j ] );
				
			}
			histogram_plots.addSeries( series );
		}
		histogram_plots.setIntervalWidth( bins[ 1 ]*180 - bins[ 0 ]*180 );


		// Create chart with histograms
		final JFreeChart chart; 
		if (prefix == "Gradient Structure Tensor Analysis -") {
			chart = ChartFactory.createHistogram(
					prefix + " Histogram",
					"Orientation (°)",
					"Amount",
					histogram_plots,
					PlotOrientation.VERTICAL,
					true,
					true,
					false );
		}
		else {
		chart = ChartFactory.createHistogram(
				prefix + " Histogram of scale " + scale,
				"Orientation (°)",
				"Amount",
				histogram_plots,
				PlotOrientation.VERTICAL,
				true,
				true,
				false );
		}
		// Set the look of histograms
		final XYPlot plot = ( XYPlot ) chart.getPlot();
		final ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer( 0.3, false );
		float color_index;
		for ( int i = 0; i < histograms.size(); i++ )
		{
			color_index = ( float ) i / ( float ) ( histograms.size() - 1 );
			renderer.setSeriesPaint( i, lut.getPaint( color_index ) );
		}
		plot.setRenderer( 0, renderer );

		
		plot.getDomainAxis().setRange( degrees_bins[ 0 ], degrees_bins[ nbins - 1 ] );

		final ChartPanel chartPanel = new ChartPanel( chart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 500, 270 ) );
		final JFrame window = new JFrame( "Orientation " + imp.getShortTitle());
		window.add( chartPanel );
		window.validate();
		window.setSize( new java.awt.Dimension( 500, 270 ) );
		return window;
	}
	
	
	protected final static float[] prepareBins( final int n, final float first, final float last )
	{
		final float[] bins = new float[ n ];
		for ( int i = 0; i < n; i++ )
		{
			bins[ i ] = ( first + i * ( last - first) / ( n - 1) ) / 180 ;
		}
		return bins;
	}

	protected static final LookupPaintScale createLUT( final int ncol )
	{
		final float[][] colors = new float[][] {
				{ 0, 75 / 255f, 150 / 255f },
				{ 0.1f, 0.8f, 0.1f },
				{ 150 / 255f, 75 / 255f, 0 }
		};
		final float[] limits = new float[] { 0, 0.5f, 1 };
		final LookupPaintScale lut = new LookupPaintScale( 0, 1, Color.BLACK );
		float val;
		float r, g, b;
		for ( int j = 0; j < ncol; j++ )
		{
			val = j / ( ncol - 0.99f );
			int i = 0;
			for ( i = 0; i < limits.length; i++ )
			{
				if ( val < limits[ i ] )
				{
					break;
				}
			}
			i = i - 1;
			r = colors[ i ][ 0 ] + ( val - limits[ i ] ) / ( limits[ i + 1 ] - limits[ i ] ) * ( colors[ i + 1 ][ 0 ] - colors[ i ][ 0 ] );
			g = colors[ i ][ 1 ] + ( val - limits[ i ] ) / ( limits[ i + 1 ] - limits[ i ] ) * ( colors[ i + 1 ][ 1 ] - colors[ i ][ 1 ] );
			b = colors[ i ][ 2 ] + ( val - limits[ i ] ) / ( limits[ i + 1 ] - limits[ i ] ) * ( colors[ i + 1 ][ 2 ] - colors[ i ][ 2 ] );
			lut.add( val, new Color( r, g, b ) );
		}
		return lut;
	}

	
	
}
