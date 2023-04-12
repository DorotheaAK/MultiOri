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
// educational purposes. In addition, we expect you to include MEasu
// citations and acknowledgments whenever you present or publish results that
// are based on it.
//
//=============================================================================================================

package multigui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import additionaluserinterface.GridPanel;
import additionaluserinterface.GridToolbar;
import additionaluserinterface.SpinnerDouble;
import additionaluserinterface.SpinnerInteger;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import ij.process.ImageProcessor;
import orientation.GroupImage;
import orientation.OrientationParameters;
import orientation.OrientationProcess;
import orientation.OrientationResults;
import orientation.OrientationService;
import monogenicj.MonogenicImage;
import monogenicj.MonogenicParameters;
import monogenicj.MonogenicProcess;
import monogenicj.MonogenicResults;

public class AnalysisDialog extends JDialog implements ActionListener, ChangeListener, WindowListener, Runnable {

	private Settings					settings				= new Settings("MULTIGUI", IJ.getDirectory("plugins") + "MultiGUI.txt");
	private Thread					thread				= null;
	protected int					countRun				= 0;

	private String[]				gradientsOperators		= new String[] {
			"Cubic Spline", "Finite Difference", "Fourier",
			"Riesz Filters", "Gaussian", "Hessian" };


	protected OrientationParameters	orientParams;

	protected MonogenicParameters monoParams; 
	
	
	protected GroupImage				gim;
	
	protected WalkBar		walk						= new WalkBar();
	
	protected JButton				bnRun					= new JButton("Run");
	
	private SpinnerDouble			spnST					= new SpinnerDouble(1, 0.01, 100, 1);
	
	private JComboBox<String>		cmbColorHSB				= new JComboBox<String>(new String[] {"HSB", "RGB"});
	private JComboBox<String>		cmbUnitOrientation		= new JComboBox<String>(new String[] {"rad", "deg"});
	
	
	private SpinnerInteger			spnNbClasses				= new SpinnerInteger(3, 1, 10000, 1);
	
	private OrientComboFeature				cmbHue					= new OrientComboFeature("Orientation", true);
	private OrientComboFeature				cmbSaturation			= new OrientComboFeature("Coherency", true);
	private OrientComboFeature				cmbBrightness			= new OrientComboFeature("Original-Image", true);

	private JLabel					lblHue					= new JLabel("Hue");
	private JLabel					lblSaturation			= new JLabel("Saturation");
	private JLabel					lblBrightness			= new JLabel("Brightness");

	private JCheckBox[]				chkOrient				= new JCheckBox[OrientationParameters.ORIENT_FEATURES];
	protected JButton[]				bnOrient					= new JButton[OrientationParameters.ORIENT_FEATURES];
	
	private JCheckBox[]				chkMonogenic				= new JCheckBox[MonogenicParameters.MONOG_FEATURES];
	protected JButton[]				bnMonogenic					= new JButton[MonogenicParameters.MONOG_FEATURES];
	
	
	protected JCheckBox monogenicAnalysis = new JCheckBox("Monogenic Analysis"); 
	protected JCheckBox orientationAnalysis = new JCheckBox("Orientation Analysis Structure Tensor"); 

	
	//newly added for MonogenicJ 
	
	private SpinnerDouble spnSigma = new SpinnerDouble(3.0D, 0.0D, 100.0D, 1.0D);
	  
	private SpinnerInteger spnScale = new SpinnerInteger(3, 1, 16, 1);
	  
	private SpinnerDouble spnOrientCoherency = new SpinnerDouble(0.0D, 0.0D, 100.0D, 1.0D);

	private SpinnerDouble spnMonoCoherency = new SpinnerDouble(0.0D, 0.0D, 100.0D, 1.0D);
	
	private SpinnerDouble spnOrientSaturated = new SpinnerDouble(0.0D, 0.0D, 100.0D, 0.1D);
	private SpinnerDouble spnMonoSaturated = new SpinnerDouble(0.0D, 0.0D, 100.0D, 0.1D);
	  
	private SpinnerDouble spnEpsilon = new SpinnerDouble(1.0D, 0.0D, 32.0D, 1.0D);
	
	private JCheckBox ckSignedDir = new JCheckBox("Signed Dir.", false);
	  
	private JCheckBox ckPrefilter = new JCheckBox("Prefilter", false);
	  
	private JRadioButton rbPyramid = new JRadioButton("Pyramid", true);
	  
	private JRadioButton rbRedundant = new JRadioButton("Redundant", false);
	
	private OrientComboFeature				cmbMonoHue					= new OrientComboFeature("Orientation", false);
	private OrientComboFeature				cmbMonoSat			= new OrientComboFeature("Coherency", false);
	private OrientComboFeature				cmbMonoBri			= new OrientComboFeature("Original-Image", false);
	  
	private JComboBox<String> cmbScaled = new JComboBox<String>(MonogenicParameters.scaled_options);
	  
	private JComboBox<String> cmbStacked = new JComboBox<String>(MonogenicParameters.stacked_options);
	  
	private JComboBox<String> cmbMonoMask = new JComboBox<String>(MonogenicParameters.masked_options);
	
	private JComboBox<String> cmbOrientMask = new JComboBox<String>(OrientationParameters.masked_options);
	
	private JComboBox<String> cmbMonogenicOop = new JComboBox<String>(MonogenicParameters.oop_options);
	private JComboBox<String> cmbOrientOop				= new JComboBox<String>(OrientationParameters.oop_options);
	
	private JComboBox<String> cmbOrientDirector = new JComboBox<String>(OrientationParameters.director_options);
	private JComboBox<String> cmbMonogenicDirector = new JComboBox<String>(MonogenicParameters.director_options);
	
	private JComboBox<String>		cmbGradient				= new JComboBox<String>(gradientsOperators);

	private enum Job {NONE, RUN};
	private Job job = Job.NONE;
	
	private MonogenicImage mgim;

	protected ImagePlus imp;
	
	public AnalysisDialog(OrientationService service) {
		super(new JFrame(), "OrientationJ ");
		String title = "Orientation Analysis MultiGUI ";
		this.orientParams = new OrientationParameters(service);
		this.monoParams = new MonogenicParameters();
		setTitle(title);
	}

	public void showDialog() {
		
		// ORIENTATION_ANALYSIS
		
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++) {
			chkOrient[k] = new JCheckBox(OrientationParameters.orientation_parameters[k]);
			bnOrient[k] = new JButton("Show");
			bnOrient[k].addActionListener(this);
			chkOrient[k].addActionListener(this);
		}
		
		
		// MONOGENIC ANALYSIS
		
		
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++) {
			chkMonogenic[k] = new JCheckBox(MonogenicParameters.monogenic_parameters[k]);
			bnMonogenic[k] = new JButton("Show");
			chkMonogenic[k].addActionListener(this);
			bnMonogenic[k].addActionListener(this);
		}
		
		
		// PANEL ORIENTATION ANALYSIS
		


		// Panel Features
		GridToolbar pnOrientFeatures = new GridToolbar(false, 2);
		
		for (int k = 0; k < OrientationParameters.SURVEY+1 ; k++) {
				
			pnOrientFeatures.place(k, 1, chkOrient[k]);
			pnOrientFeatures.place(k, 3, bnOrient[k]);
				
			if (k == OrientationParameters.TENSOR_ORIENTATION) {

					
				pnOrientFeatures.place(k, 4, 2, 1, cmbUnitOrientation);
			}
				
			if (k == OrientationParameters.SURVEY) {
				pnOrientFeatures.place(OrientationParameters.SURVEY, 4, 2, 1, cmbColorHSB);
				cmbColorHSB.addActionListener(this);
			}
				
			
		}
		
		// Panel Color
		GridToolbar pnColorSurvey = new GridToolbar("Color Survey");
		pnColorSurvey.place(1, 0, lblHue);
		pnColorSurvey.place(1, 1, cmbHue);
		pnColorSurvey.place(2, 0, lblSaturation);
		pnColorSurvey.place(2, 1, cmbSaturation);
		pnColorSurvey.place(3, 0, lblBrightness);
		pnColorSurvey.place(3, 1, cmbBrightness);

		GridToolbar pnColor = new GridToolbar();
		pnColor.place(0,0, pnColorSurvey);
		pnColor.place(1, 0, chkOrient[OrientationParameters.FILTERED]);
		pnColor.place(1, 1, bnOrient[OrientationParameters.FILTERED]);
		pnColor.place(2, 0, new JLabel("Min. Coherency (%)"));
		pnColor.place(2, 1, (JComponent)this.spnOrientCoherency);
		pnColor.place(3, 0, chkOrient[OrientationParameters.FILTERED_HISTO]);
		pnColor.place(3, 1, bnOrient[OrientationParameters.FILTERED_HISTO]);
		pnColor.place(4, 0, chkOrient[OrientationParameters.FILTERED_OOP]);
		pnColor.place(4, 1, bnOrient[OrientationParameters.FILTERED_OOP]);
		
		
		// MONOGENIC PANEL 
		
		// Panel Features
		GridToolbar pnMonogComp = new GridToolbar(false, 2);
		
		for (int k = 0; k < 7 ; k++) {
				
			pnMonogComp.place(k, 1, chkMonogenic[k]);
			pnMonogComp.place(k, 3, bnMonogenic[k]);
				
			
		}
		
	
		// Panel Parameters 
		GridPanel pnCustomizeMonog = new GridPanel(false);
	    ButtonGroup group = new ButtonGroup();
	    group.add(this.rbPyramid);
	    group.add(this.rbRedundant);
		pnCustomizeMonog.place(0, 0, this.rbPyramid);
		pnCustomizeMonog.place(0, 1, this.rbRedundant);
		pnCustomizeMonog.place(1, 0, new JLabel("Nb of Scale"));
		pnCustomizeMonog.place(1, 1, (JComponent)this.spnScale);
		pnCustomizeMonog.place(2, 0, new JLabel("Sigma \u03C3 [Tensor]"));
		pnCustomizeMonog.place(2, 1, (JComponent)this.spnSigma);
		
		
		pnCustomizeMonog.place(3, 0, this.cmbScaled);
		pnCustomizeMonog.place(4, 0, this.cmbStacked);

		pnCustomizeMonog.place(5, 0, chkMonogenic[MonogenicParameters.HISTOGRAM]);
		pnCustomizeMonog.place(5, 1, bnMonogenic[MonogenicParameters.HISTOGRAM]);
	    
	    //pnParam.place(8, 0, 2, 1, lblStatistics);
		pnCustomizeMonog.place(6, 0, chkMonogenic[MonogenicParameters.OOP]);
		pnCustomizeMonog.place(6, 1, bnMonogenic[MonogenicParameters.OOP]);
		pnCustomizeMonog.place(7, 0, cmbMonogenicOop);
		pnCustomizeMonog.place(7, 1, cmbMonogenicDirector);
	    
	   
	    // Panel Color Monogenic J 
	    GridToolbar pnColorMap = new GridToolbar("Color Survey");
	    pnColorMap.place(1, 0, new JLabel("Hue"));
	    pnColorMap.place(2, 0, new JLabel("Saturation"));
	    pnColorMap.place(3, 0, new JLabel("Brightness"));
	    pnColorMap.place(1, 1, this.cmbMonoHue);
	    pnColorMap.place(2, 1, this.cmbMonoSat);
	    pnColorMap.place(3, 1, this.cmbMonoBri);
	    
	    
	    GridToolbar pnColorMon = new GridToolbar();
	    pnColorMon.place(0,0, pnColorMap);
	    pnColorMon.place(1, 0, chkMonogenic[MonogenicParameters.FILTERED]);
	    pnColorMon.place(1, 1, bnMonogenic[MonogenicParameters.FILTERED]);
	    pnColorMon.place(2, 0, new JLabel("Min. Coherency (%)"));
	    pnColorMon.place(2, 1, (JComponent)this.spnMonoCoherency);
	    pnColorMon.place(3, 0, chkMonogenic[MonogenicParameters.FILTERED_HISTO]);
	    pnColorMon.place(3, 1, bnMonogenic[MonogenicParameters.FILTERED_HISTO]);
	    pnColorMon.place(4, 0, chkMonogenic[MonogenicParameters.FILTERED_OOP]);
	    pnColorMon.place(4, 1, bnMonogenic[MonogenicParameters.FILTERED_OOP]);
	   
	    
	    // Panel Advanced MonogenicJ 
	    
	    GridToolbar pnAdvanced = new GridToolbar("Advanced MonogenicJ",2);
	    for (int k=7; k <MonogenicParameters.FILTERED; k++) {
	    	pnAdvanced.place(k-4, 0, chkMonogenic[k]);
	    	pnAdvanced.place(k-4, 1, bnMonogenic[k]);
	    }
	    

		
		GridPanel pnMaskMono = new GridPanel(false);
	
		pnMaskMono.place(0,  0, this.cmbMonoMask); 
		pnMaskMono.place(1, 0, chkMonogenic[MonogenicParameters.MASKED_COLOR]);
		pnMaskMono.place(1, 1, bnMonogenic[MonogenicParameters.MASKED_COLOR]);
	 	pnMaskMono.place(2, 0, chkMonogenic[MonogenicParameters.MASKED_HISTO]);
	 	pnMaskMono.place(2, 1, bnMonogenic[MonogenicParameters.MASKED_HISTO]);
	 	pnMaskMono.place(3, 0,  chkMonogenic[MonogenicParameters.MASKED_OOP]);
	 	pnMaskMono.place(3, 1,  bnMonogenic[MonogenicParameters.MASKED_OOP]);
	   

		JTabbedPane tabbedPaneMonog = new JTabbedPane();
		tabbedPaneMonog.addTab("Customize", (Component)pnCustomizeMonog);
	    tabbedPaneMonog.addTab("Color Map", (Component)pnColorMon);
	    tabbedPaneMonog.addTab("Mask", (Component)pnMaskMono);
	    tabbedPaneMonog.addTab("Advanced", (Component)pnAdvanced);
	    	
	    
	    GridPanel pnMonogenic = new GridPanel(2); 
	    pnMonogenic.place(0, 0, monogenicAnalysis);
	    pnMonogenic.place(1, 0, pnMonogComp);
	    pnMonogenic.place(2, 0, tabbedPaneMonog);
		
	    
	    // Panel Tensor
	 	GridToolbar pnOrientTensor = new GridToolbar(false, 2);
	 	pnOrientTensor.place(0, 0, new JLabel("Local window \u03C3"));
	 	pnOrientTensor.place(0, 2, spnST);
	 	pnOrientTensor.place(0, 3, new JLabel("pixel"));
	 	pnOrientTensor.place(2, 0, new JLabel("Gradient"));
	 	pnOrientTensor.place(2, 2, 3, 1, cmbGradient);
	 	
	 	
	 	GridPanel pnCustomizeOrient = new GridPanel(false);
	 	pnCustomizeOrient.place(0,  0, pnOrientTensor); // add Histogram and OOP 
	 	pnCustomizeOrient.place(2, 0, chkOrient[OrientationParameters.HISTOGRAM]);
	 	pnCustomizeOrient.place(2, 1, bnOrient[OrientationParameters.HISTOGRAM]);
	 	pnCustomizeOrient.place(3, 0, chkOrient[OrientationParameters.OOP]);
	 	pnCustomizeOrient.place(3, 1, bnOrient[OrientationParameters.OOP]);
	 	pnCustomizeOrient.place(4, 0, cmbOrientOop);
		pnCustomizeOrient.place(4, 1, cmbOrientDirector);
		
	
		
		GridPanel pnMaskOrient = new GridPanel(false);
		pnMaskOrient.place(0,  0, this.cmbOrientMask); 
	 	pnMaskOrient.place(1, 0, chkOrient[OrientationParameters.MASKED_COLOR]);
	 	pnMaskOrient.place(1, 1, bnOrient[OrientationParameters.MASKED_COLOR]);
	 	pnMaskOrient.place(2, 0, chkOrient[OrientationParameters.MASKED_HISTO]);
	 	pnMaskOrient.place(2, 1, bnOrient[OrientationParameters.MASKED_HISTO]);
	 	pnMaskOrient.place(3, 0,  chkOrient[OrientationParameters.MASKED_OOP]);
	 	pnMaskOrient.place(3, 1,  bnOrient[OrientationParameters.MASKED_OOP]);
		
		
	    JTabbedPane tabbedPaneOrient = new JTabbedPane(); 
	    tabbedPaneOrient.addTab("Customize", (Component)pnCustomizeOrient);
	    tabbedPaneOrient.addTab("Color Map", (Component)pnColor);
	    tabbedPaneOrient.addTab("Mask", (Component)pnMaskOrient);
	    
	    
	    GridPanel pnMainOrient = new GridPanel(2);
	    pnMainOrient.place(0,  0, orientationAnalysis);
	    pnMainOrient.place(1, 0, pnOrientFeatures);
		pnMainOrient.place(2, 0, tabbedPaneOrient);
		
		

	    GridPanel pnMain = new GridPanel(false, 2);
		pnMain.place(0, 0, pnMainOrient); 
		pnMain.place(0, 2,  pnMonogenic);

		pnMain.place(1, 0, 4, 1, bnRun);
	    

	    pnMain.place(2, 0, 4, 1, (JComponent)this.walk);
	    
		// Listener
		walk.getButtonClose().addActionListener(this);
		bnRun.addActionListener(this);
		
		// Finalize
		addWindowListener(this);
		getContentPane().add(pnMain);     //add((Component)panel);
		pack();
		setResizable(false);
		GUI.center(this);
		setVisible(true);
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++)
			settings.record("feature-Orient-"+OrientationParameters.orientation_parameters[k], chkOrient[k], false);
	
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++)
			settings.record("feature-Monogenic-"+MonogenicParameters.monogenic_parameters[k], chkMonogenic[k], false);
	
		settings.record("cmbColorHSB", cmbColorHSB, cmbColorHSB.getItemAt(0));
		settings.record("cmbUnitOrientation", cmbUnitOrientation, cmbUnitOrientation.getItemAt(0));
		settings.record("spnTensor", spnST, "1");
		settings.record("spnSaturared", (JSpinner)this.spnOrientSaturated, "0.02");
		settings.record("Color_Hue", cmbHue, "Orientation");
		settings.record("Color_Staturation", cmbSaturation, "Coherency");
		settings.record("Color_Brigthness", cmbBrightness, "Original-Image");
		settings.record("spnNbClasses", spnNbClasses, "3");
		settings.record("cmbGradient", cmbGradient, gradientsOperators[OrientationParameters.GRADIENT_CUBIC_SPLINE]);
		settings.record("cmbOop", this.cmbOrientOop,OrientationParameters.oop_options[OrientationParameters.NO_DIRECTOR]);
		settings.record("cmbMask", this.cmbOrientMask, OrientationParameters.masked_options[OrientationParameters.NO_FILTER]);
	    settings.record("spnCoherency", (JSpinner)this.spnOrientCoherency, "0");
		
		settings.record("MonogenicJ-cmbHue", this.cmbMonoHue, "Orientation");
		settings.record("MonogenicJ-cmbSat", this.cmbMonoSat, "Coherency");
		settings.record("MonogenicJ-cmbBri", this.cmbMonoBri, "Input");
		settings.record("MonogenicJ-spnSigma", (JSpinner)this.spnSigma, "3");
		settings.record("MonogenicJ-spnScale", (JSpinner)this.spnScale, "3"); //1
		settings.record("MonogenicJ-spnCoherency", (JSpinner)this.spnMonoCoherency, "0.0"); // displayed as , in dialog!!!_
		settings.record("MonogenicJ-spnSaturared", (JSpinner)this.spnMonoSaturated, "0.02");
		settings.record("MonogenicJ-spnEpsilon", (JSpinner)this.spnEpsilon, "1");
		settings.record("MonogenicJ-ckPyramid", this.rbPyramid, true);
		settings.record("MonogenicJ-rbRedundant", this.rbRedundant, false);
		settings.record("MonogenicJ-cmbScaled", this.cmbScaled, MonogenicParameters.scaled_options[MonogenicParameters.TRUE_VALUES]);
		settings.record("MonogenicJ-cmbStacked", this.cmbStacked, MonogenicParameters.stacked_options[MonogenicParameters.STACKED]);
		settings.record("MonogenicJ-cmbMask", this.cmbMonoMask, MonogenicParameters.masked_options[MonogenicParameters.NO_FILTER]);
		settings.record("MonogenicJ-cmbOop", this.cmbMonogenicOop,MonogenicParameters.oop_options[MonogenicParameters.NO_DIRECTOR]);
		settings.record("MonogenicJ-ckPrefilter", this.ckPrefilter, false);
		settings.record("MonogenicJ-ckSignedDir", this.ckSignedDir, false);
		
		
		settings.loadRecordedItems();
		orientParams.load(settings);
		monoParams.load(settings);
		setParameters();
		updateInterface();
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {

		getParameters();
		Object source = e.getSource();
		if (e.getSource() == walk.getButtonClose()) {
			settings.storeRecordedItems();
			orientParams.store(settings);
			monoParams.store(settings);
			dispose();
		}
		
		
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++)
			if (source == bnOrient[k] && gim != null)
				OrientationResults.show(k, gim, orientParams, ++countRun);
		
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++)
			if (source == bnMonogenic[k] && mgim != null)
				MonogenicResults.show(k, mgim, monoParams, ++countRun, imp);
				
		
		if (e.getSource() == cmbColorHSB) 
			orientParams.hsb = cmbColorHSB.getSelectedIndex() == 0;
		
		else if (e.getSource() == bnRun) 
			start(Job.RUN);
		
		updateInterface();
	}

	private void start(Job job) {
		if (thread != null)
			return;
		this.job = job;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();		
	}
	
	
	private ImagePlus getInputImage() {
	    ImagePlus imp = WindowManager.getCurrentImage();
	    //System.out.println(imp2);
	    if (imp == null) {
	      IJ.error("No open image.");
	      return null;
	    } 
	    if (imp.getType() != 0 && imp.getType() != 1 && imp.getType() != 2) {
	      IJ.error("Only processed 8-bits, 16-bits, or 32 bits images.");
	      return null;
	    } 
	    if (imp.getStack().getSize() > 1) {
	      IJ.error("Do not processed stack of images.");
	      return null;
	    } 
	    int m = 1;
	    for (int s = 0; s < this.spnScale.get(); s++)
	      m *= 2; 
	    int nx = imp.getWidth();
	    int ny = imp.getHeight();
	    if ((nx % m != 0) | (ny % m != 0)){
	    	
	    	IJ.showMessage("Input image is not a multiple of 2^scale [\" + scale + \"]. For analysis, image is cropped.");
	    	
	    	ImagePlus img = cropImage(imp, nx, ny, m);
	    	this.imp = img;
	    	return img;
	    }
	    
		this.imp = imp;
	    return imp;
	  }
	
	
	private static ImagePlus cropImage(ImagePlus imp, int nx, int ny, int scale) {
	    ImageProcessor ip = imp.getProcessor();
	    
	    int newWidth = 0;
	    int newHeight = 0;
	    
	    if (nx % scale != 0) {
    		
    		double modulo = nx % scale;
    		
    		newWidth = (int)(nx - modulo);
    		
    		
    		if (newWidth% scale != 0) {
    			 IJ.error("The width [" + newWidth + "] of the input image is not a multiple of 2^scale [" + scale + "].");
    			 return null;
    		}	
    	}
    	else {
    		newWidth = nx;
    	}
    	if (ny % scale != 0) {
    		double modulo = ny % scale;
    		
    		newHeight = (int)(ny - modulo);
    		
    		if (newHeight% scale != 0) {
    			  IJ.error("The height  [" + newHeight + "] of the input image is not a multiple of 2^scale [" + scale + "].");
    			  return null;
    		}
    		
    	}
    	else {
    		
    		newHeight = ny;
    		}
    	
    	int cropX = (nx - newWidth)/2;
    	int cropY = (ny - newHeight)/2;
    	    
    	    
    	ip.setRoi(cropX, cropY, newWidth, newHeight);
    	ImageProcessor cropped = ip.crop();
    	    
    	BufferedImage croppedImage = cropped.getBufferedImage();

    	ImagePlus img = new ImagePlus(imp.getShortTitle() + " cropped", croppedImage);
    	
    	img.show();
    	
    	return img;
	    
	    
  }
    
	
	@Override
	public void stateChanged(ChangeEvent e) {
		
		updateInterface();
	}

	public void getParameters() {
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++) {
			orientParams.view[k] = chkOrient[k].isSelected();
			System.out.println("orientParams: "+ orientParams.view[k]);
		}
		
		orientParams.featureHue = (String) cmbHue.getSelectedItem();
		orientParams.featureSat = (String) cmbSaturation.getSelectedItem();
		orientParams.featureBri = (String) cmbBrightness.getSelectedItem();
		
		monoParams.featureHue = (String) cmbMonoHue.getSelectedItem();
		monoParams.featureSat = (String) cmbMonoSat.getSelectedItem();
		monoParams.featureBri = (String) cmbMonoBri.getSelectedItem();
		
		
		orientParams.sigmaST = spnST.get();
		
		orientParams.saturated = spnOrientSaturated.get(); 
		orientParams.coherency = spnOrientCoherency.get();
		
		orientParams.gradient = cmbGradient.getSelectedIndex();
		
		orientParams.radian = cmbUnitOrientation.getSelectedIndex() == 0;
		orientParams.hsb = cmbColorHSB.getSelectedIndex() == 0;
		orientParams.drawing = cmbOrientDirector.getSelectedIndex();
		orientParams.director = cmbOrientOop.getSelectedIndex();
		orientParams.mask = cmbOrientMask.getSelectedIndex();
		
		monoParams.saturated = spnMonoSaturated.get(); 
		
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++) {
			monoParams.view[k] = chkMonogenic[k].isSelected();
			System.out.println("monoParams get: "+ monoParams.view[k]);
		}
		monoParams.pyramid = rbPyramid.isSelected();
		monoParams.scale = spnScale.get();

		monoParams.ckPrefilter = ckPrefilter.isSelected();
		monoParams.ckSignedDir = ckSignedDir.isSelected();
		monoParams.sigma = spnSigma.get();
		monoParams.coherency = spnMonoCoherency.get();
		monoParams.drawing = cmbMonogenicDirector.getSelectedIndex();
		monoParams.director = cmbMonogenicOop.getSelectedIndex();
		monoParams.mask = cmbMonoMask.getSelectedIndex();
	
	}
	
	public void setParameters() {
		for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++) { 
			chkMonogenic[k].setSelected(monoParams.view[k]);
			System.out.println("monoParams set : "+ monoParams.view[k]);
		}
		for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++) { 
			 chkOrient[k].setSelected(orientParams.view[k]);
			 System.out.println("orientParams set : "+ orientParams.view[k]);
		}
		cmbHue.setSelectedItem(orientParams.featureHue);
		cmbSaturation.setSelectedItem(orientParams.featureSat);
		cmbBrightness.setSelectedItem(orientParams.featureBri);
		
		cmbMonoHue.setSelectedItem(monoParams.featureHue);
		cmbMonoSat.setSelectedItem(monoParams.featureSat);
		cmbMonoBri.setSelectedItem(monoParams.featureBri);
		
		
		spnST.set(orientParams.sigmaST);
		spnOrientCoherency.set(orientParams.coherency);
		spnOrientSaturated.set(orientParams.saturated);
		
		cmbGradient.setSelectedIndex(orientParams.gradient);
		
		cmbUnitOrientation.setSelectedIndex(orientParams.radian ? 0 : 1);
		cmbColorHSB.setSelectedIndex(orientParams.hsb ? 0 : 1);

		spnScale.set(monoParams.scale);
		spnMonoCoherency.set(monoParams.coherency);
		spnMonoSaturated.set(monoParams.saturated);
		
		cmbMonogenicDirector.setSelectedIndex(monoParams.drawing);
		cmbMonogenicOop.setSelectedIndex(monoParams.director);
		cmbMonoMask.setSelectedIndex(monoParams.mask);
		
		cmbOrientDirector.setSelectedIndex(orientParams.drawing);
		cmbOrientDirector.setSelectedIndex(orientParams.director);
		cmbOrientMask.setSelectedIndex(orientParams.mask);
	
	}

	@Override
	public void run() {		
		getParameters();
		walk.reset();
		
		if (job == Job.RUN) {
			
			
			ImagePlus imp = getInputImage();
			
			if (imp == null) {
				thread = null;
				return;
			}
			
			int list_of_open_things[] = WindowManager.getIDList();
			
			for (int i = 0; i < list_of_open_things.length; i++) {
				System.out.println(list_of_open_things[i]);
			}
			
			//recordMacroParameters();
			Cursor cursor = getCursor();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if (monogenicAnalysis.isSelected()) {
				
				MonogenicProcess monoProcess = new MonogenicProcess(walk, imp, monoParams);

				monoProcess.start();
				while (monoProcess.isAlive()) {
				}
				mgim = monoProcess.getMonogenicImage();
				
				MonogenicResults.show(monoParams.view, mgim, monoParams, ++countRun, imp);
				walk.reset();
				
			}
			
			if (orientationAnalysis.isSelected()) {
				
				//ImageWare source = Builder.create(imp);
				OrientationProcess process = new OrientationProcess(walk, imp, orientParams);
				process.start();
				while (process.isAlive()) {
				}
				gim = process.getGroupImage();
				
				OrientationResults.show(orientParams.view, gim, orientParams, ++countRun);
				walk.reset();
				
			}
			setCursor(cursor);
			if (!orientationAnalysis.isSelected() && !monogenicAnalysis.isSelected()) {
				IJ.showMessage("Select one of the two methods on the top of the dialog window.");
				
			}
		}

		walk.finish();
		updateInterface();
		thread = null;
	}

	public void updateInterface() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
	
				// Enable the show button
				if (gim != null && orientationAnalysis.isSelected()) {
					for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++)
						bnOrient[k].setEnabled(true);
					
					//bnOrient[OrientationParameters.GRADIENT_HORIZONTAL].setEnabled(gim.gx != null || gim.hxx != null);
					//bnOrient[OrientationParameters.GRADIENT_VERTICAL].setEnabled(gim.gy != null || gim.hyy != null);
					//bnOrient[OrientationParameters.TENSOR_ORIENTATION].setEnabled(gim.orientation != null);
					//bnOrient[OrientationParameters.TENSOR_COHERENCY].setEnabled(gim.coherency != null);
					//bnOrient[OrientationParameters.TENSOR_ENERGY].setEnabled(gim.energy != null);
					
					//bnOrient[OrientationParameters.SURVEY].setEnabled(gim != null);
				
				}
				else {
					
					for (int k = 0; k < OrientationParameters.ORIENT_FEATURES; k++)
						bnOrient[k].setEnabled(false);
				}
				
				if (mgim != null && monogenicAnalysis.isSelected()) {
					for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++)
						bnMonogenic[k].setEnabled(true);
				
				}
				else {
					
					for (int k = 0; k < MonogenicParameters.MONOG_FEATURES; k++)
						bnMonogenic[k].setEnabled(false);
				}
				
				
				
				if (cmbMonogenicOop.getSelectedIndex() == MonogenicParameters.DIRECTOR) {
					cmbMonogenicDirector.setEnabled(true);
				
				}
				else {
					cmbMonogenicDirector.setEnabled(false);
					
				}
				if (cmbOrientOop.getSelectedIndex() == OrientationParameters.DIRECTOR) {
					cmbOrientDirector.setEnabled(true);
				
				}
				else {
					cmbOrientDirector.setEnabled(false);
					
				}
				
				

				// Enabled the color survey channels
				if (cmbColorHSB.getSelectedIndex() != 0) {
					lblHue.setText("Red");
					lblSaturation.setText("Green");
					lblBrightness.setText("Blue");
				}
				else {
					lblHue.setText("Hue");
					lblSaturation.setText("Saturation");
					lblBrightness.setText("Brightness");
				}
				if (orientParams.gradient == OrientationParameters.HESSIAN) {
					if (chkOrient[0] != null)
						chkOrient[0].setText("Hessian-XX");
					if (chkOrient[1] != null)
						chkOrient[1].setText("Hessian-YY");
				}
				else {
					if (chkOrient[0] != null)
						chkOrient[0].setText("Gradient-X");
					if (chkOrient[1] != null)
						chkOrient[1].setText("Gradient-Y");
				}
			}
		});

	}


	public OrientationParameters getSettingParameters() {
		return orientParams;
	}
	
	public MonogenicParameters getSettingParameters1() {
		return monoParams;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}

}
