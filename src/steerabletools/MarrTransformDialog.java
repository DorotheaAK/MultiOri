package steerabletools;

import additionaluserinterface.GridPanel;
import additionaluserinterface.Settings;
import additionaluserinterface.SpinnerInteger;
import additionaluserinterface.WalkBar;
import imageware.Builder;
import imageware.ImageWare;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import polyharmonicwavelets.ComplexImage;
import polyharmonicwavelets.DyadicFilters;
import polyharmonicwavelets.DyadicTransform;
import polyharmonicwavelets.Filters;
import polyharmonicwavelets.Parameters;
import polyharmonicwavelets.QuincunxFilters;
import polyharmonicwavelets.QuincunxTransform;

public class MarrTransformDialog extends TransformDialog {
  public static final int BSPLINE = 0;
  
  public static final int ORTHOGONAL = 1;
  
  public static final int DUAL = 2;
  
  public static final int OPERATOR = 3;
  
  public static final int MARR = 7;
  
  public static final int DUALOPERATOR = 8;
  
  public static final int BASIS = 0;
  
  public static final int REDUNDANT = 1;
  
  public static final int PYRAMID = 2;
  
  public static final int ISOTROPIC = 1;
  
  public static final int CHANGESIGMA = 4;
  
  public static final double s2 = 6.0D;
  
  public static final int QUINCUNX = 0;
  
  public static final int DYADIC = 1;
  
  public static final int ITERATIVE = 0;
  
  public static final int GAMMA = 1;
  
  private SpinnerInteger spnScale;
  
  private JRadioButton chkDyadic;
  
  private JRadioButton chkQuincunx;
  
  private Parameters params = new Parameters();
  
  public MarrTransformDialog(String title, Settings settings, WalkBar walk) {
    super(title, settings, walk);
    this.params.analysesonly = false;
    this.params.rieszfreq = 0;
    this.params.flavor = 7;
    this.params.type = 1;
    this.params.redundancy = 2;
    this.params.lattice = 1;
    this.params.order = 2.0D;
    this.params.N = 1;
    this.params.prefilter = false;
    this.params.J = this.spnScale.get();
  }
  
  public void recordParameters() {
    this.settings.record("MarrWavelet-spnScale", (JSpinner)this.spnScale, "3");
    this.settings.record("MarrWavelet-chkDyadic", this.chkDyadic, true);
    this.settings.record("MarrWavelet-chkQuincunx", this.chkQuincunx, false);
  }
  
  public GridPanel createPanelParameters() {
    this.spnScale = new SpinnerInteger(2, 1, 100, 1);
    this.chkDyadic = new JRadioButton("Dyadic", true);
    this.chkQuincunx = new JRadioButton("Quincunx", false);
    ButtonGroup lattice = new ButtonGroup();
    lattice.add(this.chkDyadic);
    lattice.add(this.chkQuincunx);
    GridPanel pn = new GridPanel("Parameters");
    pn.place(0, 0, new JLabel("Scale"));
    pn.place(0, 1, (JComponent)this.spnScale);
    pn.place(1, 0, this.chkDyadic);
    pn.place(1, 1, this.chkQuincunx);
    this.chkQuincunx.setEnabled(false);
    return pn;
  }
  
  public void analysisAndShowCoef(ImageWare input) {
    this.params.J = this.spnScale.get();
    ComplexImage in = new ComplexImage(input);
    ComplexImage[] coef = null;
    if (this.chkDyadic.isSelected()) {
      DyadicFilters filters = new DyadicFilters(this.params, in.nx, in.ny);
      filters.compute();
      DyadicTransform transform = new DyadicTransform(filters, this.params);
      coef = transform.analysis(in);
    } else {
      QuincunxFilters filters = new QuincunxFilters(this.params, in.nx, in.ny);
      filters.compute();
      QuincunxTransform transform = new QuincunxTransform(filters, this.params);
      coef = transform.analysis(in);
    } 
    ImageWare out = convertComplexToFlatten(coef, this.chkDyadic.isSelected());
    out.show("Marr Coefficients - Rescaled - [real | imag]");
  }
  
  private ImageWare convertComplexToFlatten(ComplexImage[] coef, boolean dyadic) {
    int ny = 0, posy = 0;
    int nx = (coef[0]).nx;
    int mx = nx;
    for (int s = 0; s < coef.length; s++)
      ny += (coef[s]).ny; 
    ImageWare module = Builder.create(nx * 2, ny, 1, 1);
    for (int i = 0; i < coef.length; i++) {
      nx = (coef[i]).nx;
      ny = (coef[i]).ny;
      ImageWare real = Builder.create(nx, ny, 1, 3);
      ImageWare imag = Builder.create(nx, ny, 1, 3);
      for (int j = 0; j < nx; j++) {
        for (int k = 0; k < ny; k++) {
          int index = k * nx + j;
          real.putPixel(j, k, 0, (coef[i]).real[index]);
          imag.putPixel(j, k, 0, (coef[i]).imag[index]);
        } 
      } 
      real.rescale();
      imag.rescale();
      module.putXY(0, posy, 0, real);
      module.putXY(mx, posy, 0, imag);
      posy += ny;
    } 
    module.show("Marr Coefficients - Rescaled - [real | imag]");
    return module;
  }
  
  public void analysis(ImageWare input) {
    this.params.J = this.spnScale.get();
    ComplexImage in = new ComplexImage(input);
    ComplexImage[] coef = null;
    if (this.chkDyadic.isSelected()) {
      DyadicFilters filters = new DyadicFilters(this.params, in.nx, in.ny);
      filters.compute();
      DyadicTransform transform = new DyadicTransform(filters, this.params);
      coef = transform.analysis(in);
    } else {
      QuincunxFilters filters = new QuincunxFilters(this.params, in.nx, in.ny);
      filters.compute();
      QuincunxTransform transform = new QuincunxTransform(filters, this.params);
      coef = transform.analysis(in);
    } 
    int nx = (coef[0]).nx;
    int ny = (coef[0]).ny;
    int mx = nx;
    ImageWare out = Builder.create(nx * 2, ny, coef.length, 3);
    for (int s = 0; s < coef.length; s++) {
      nx = (coef[s]).nx;
      ny = (coef[s]).ny;
      if ((coef[s]).imag == null) {
        for (int i = 0; i < nx; i++) {
          for (int j = 0; j < ny; j++)
            out.putPixel(i, j, s, (coef[s]).real[j * nx + i]); 
        } 
      } else {
        for (int i = 0; i < nx; i++) {
          for (int j = 0; j < ny; j++) {
            int index = j * nx + i;
            out.putPixel(i, j, s, (coef[s]).real[index]);
            out.putPixel(mx + i, j, s, (coef[s]).imag[index]);
          } 
        } 
      } 
    } 
    out.show("Marr Wavelets Bands [real | imag]");
  }
  
  public void synthesis(ImageWare[] pyramid) {
    ComplexImage out;
    this.params.J = this.spnScale.get();
    int nx = pyramid[0].getWidth() / 2;
    int ny = pyramid[0].getHeight();
    int n = pyramid.length;
    int mx = nx;
    int my = ny;
    ComplexImage[] coef = new ComplexImage[n];
    for (int s = 0; s < n; s++) {
      ImageWare real = Builder.create(nx, ny, 1, 3);
      ImageWare imag = Builder.create(nx, ny, 1, 3);
      pyramid[s].getXY(0, 0, 0, real);
      pyramid[s].getXY(mx, 0, 0, imag);
      coef[s] = new ComplexImage(real, imag);
      nx /= 2;
      ny /= 2;
    } 
    nx = pyramid[0].getWidth();
    ny = pyramid[0].getHeight();
    if (this.chkDyadic.isSelected()) {
      DyadicFilters filters = new DyadicFilters(this.params, mx, my);
      filters.compute();
      DyadicTransform transform = new DyadicTransform(filters, this.params);
      out = transform.synthesis(coef);
    } else {
      QuincunxFilters filters = new QuincunxFilters(this.params, mx, my);
      filters.compute();
      QuincunxTransform transform = new QuincunxTransform(filters, this.params);
      out = transform.synthesis(coef);
    } 
    out.showModulus("Inverse Marr Wavelets");
  }
  
  public void showFilter(int nx, int ny) {
    QuincunxFilters quincunxFilters = null; //QuincunxFilters quincunxFilters ;
    this.params.J = this.spnScale.get();
    String title = "";
    if (this.chkDyadic.isSelected()) {
      DyadicFilters dyadicFilters = new DyadicFilters(this.params, nx, ny);
      title = "Dyadic";
    } else {
      quincunxFilters = new QuincunxFilters(this.params, nx, ny);
      title = "Quincunx";
    } 
    quincunxFilters.compute();
    ((Filters)quincunxFilters).FA[0].shift();
    ((Filters)quincunxFilters).FA[0].showModulus(title + " Analysis Lowpass");
    ((Filters)quincunxFilters).FA[0].shift();
    ((Filters)quincunxFilters).FA[1].shift();
    ((Filters)quincunxFilters).FA[1].showModulus(title + " Analysis highpass");
    ((Filters)quincunxFilters).FA[1].shift();
  }
  
  public void checkPerfectReconstruction(ImageWare input) {
    this.params.J = this.spnScale.get();
    ComplexImage in = new ComplexImage(input);
    if (this.chkDyadic.isSelected()) {
      DyadicFilters dfilters = new DyadicFilters(this.params, in.nx, in.ny);
      dfilters.compute();
      DyadicTransform dtransform = new DyadicTransform(dfilters, this.params);
      ComplexImage[] dcoef = dtransform.analysis(in);
      ComplexImage dout = dtransform.synthesis(dcoef);
      dout.showModulus("Perfect Reconstruction");
      dout.subtract(in);
      dout.showModulus("Error");
    } 
    if (this.chkQuincunx.isSelected()) {
      QuincunxFilters qfilters = new QuincunxFilters(this.params, in.nx, in.ny);
      qfilters.compute();
      QuincunxTransform qtransform = new QuincunxTransform(qfilters, this.params);
      ComplexImage[] qcoef = qtransform.analysis(in);
      ComplexImage qout = qtransform.synthesis(qcoef);
      qout.showModulus("Q Perfect Reconstruction");
      qout.subtract(in);
      qout.showModulus("Q Error");
    } 
  }
}