package steerabletools;

import additionaluserinterface.Chrono;
import additionaluserinterface.GridPanel;
import additionaluserinterface.GridToolbar;
import additionaluserinterface.Settings;
import additionaluserinterface.SpinnerInteger;
import additionaluserinterface.WalkBar;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imageware.Builder;
import imageware.ImageWare;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

public class HessianDialog extends JDialog implements ActionListener, WindowListener, Runnable {
  private Settings settings = null;
  
  private WalkBar walk = null;
  
  private Thread thread = null;
  
  private JButton job = null;
  
  private SpinnerInteger spnScale = new SpinnerInteger(3, 1, 100, 1);
  
  private JButton bnRun = new JButton("Run");
  
  private JCheckBox ckRiesz = new JCheckBox("Riesz transform channels", false);
  
  private JCheckBox ckWavelet = new JCheckBox("Wavelet coefficients", false);
  
  private JCheckBox ckLargest = new JCheckBox("Largest eigenvalue (Hessian)", true);
  
  private JCheckBox ckSmallest = new JCheckBox("Smallest eigenvalue", false);
  
  private JCheckBox ckCoherency = new JCheckBox("Coherency", false);
  
  private JCheckBox ckDirection = new JCheckBox("Direction", false);
  
  private JCheckBox ckColor = new JCheckBox("Hessian, color-coded dir.", false);
  
  private JCheckBox ckNMS = new JCheckBox("Non-maximum suppression", false);
  
  private JRadioButton rbPyramid = new JRadioButton("Pyramid", true);
  
  private JRadioButton rbRedundant = new JRadioButton("Redundant", true);
  
  private JButton showRiesz = new JButton(" Show ");
  
  private JButton showWavelet = new JButton(" Show ");
  
  private JButton showLargest = new JButton(" Show ");
  
  private JButton showSmallest = new JButton(" Show ");
  
  private JButton showCoherency = new JButton(" Show ");
  
  private JButton showDirection = new JButton(" Show ");
  
  private JButton showColor = new JButton(" Show ");
  
  private JButton showNMS = new JButton(" Show ");
  
  private Hessian hessian;
  
  public HessianDialog(String title, Settings settings, WalkBar walk) {
    super(new Frame(), title);
    this.settings = settings;
    this.walk = walk;
    doDialog();
    settings.record("MultiscaleHessian-scale", (JSpinner)this.spnScale, "3");
    settings.record("MultiscaleHessian-ckRiesz", this.ckRiesz, false);
    settings.record("MultiscaleHessian-ckWavelet", this.ckWavelet, false);
    settings.record("MultiscaleHessian-ckLargest", this.ckLargest, true);
    settings.record("MultiscaleHessian-ckSmallest", this.ckSmallest, false);
    settings.record("MultiscaleHessian-ckCoherency", this.ckCoherency, false);
    settings.record("MultiscaleHessian-ckDirection", this.ckDirection, false);
    settings.record("MultiscaleHessian-ckColor", this.ckColor, false);
    settings.record("MultiscaleHessian-ckNMS", this.ckNMS, false);
    settings.record("MultiscaleHessian-ckPyramid", this.rbPyramid, true);
    settings.record("MultiscaleHessian-rbRedundant", this.rbRedundant, false);
    settings.loadRecordedItems();
  }
  
  private void doDialog() {
    ButtonGroup group = new ButtonGroup();
    group.add(this.rbPyramid);
    group.add(this.rbRedundant);
    GridToolbar pn1 = new GridToolbar("Features");
    pn1.place(0, 0, this.ckRiesz);
    pn1.place(1, 0, this.ckWavelet);
    pn1.place(2, 0, this.ckLargest);
    pn1.place(3, 0, this.ckSmallest);
    pn1.place(4, 0, this.ckCoherency);
    pn1.place(5, 0, this.ckDirection);
    pn1.place(6, 0, this.ckColor);
    pn1.place(7, 0, this.ckNMS);
    pn1.place(0, 1, this.showRiesz);
    pn1.place(1, 1, this.showWavelet);
    pn1.place(2, 1, this.showLargest);
    pn1.place(3, 1, this.showSmallest);
    pn1.place(4, 1, this.showCoherency);
    pn1.place(5, 1, this.showDirection);
    pn1.place(6, 1, this.showColor);
    pn1.place(7, 1, this.showNMS);
    GridPanel pn0 = new GridPanel("Parameters", 6);
    pn0.place(0, 0, this.rbPyramid);
    pn0.place(0, 1, this.rbRedundant);
    pn0.place(1, 0, new JLabel("Scale"));
    pn0.place(1, 1, (JComponent)this.spnScale);
    GridPanel pnRun = new GridPanel(false, 0);
    pnRun.place(2, 2, 1, 1, this.bnRun);
    GridPanel panel = new GridPanel(false, 5);
    panel.place(0, 0, (JComponent)pn1);
    panel.place(1, 0, (JComponent)pn0);
    panel.place(2, 0, (JComponent)pnRun);
    panel.place(3, 0, (JComponent)this.walk);
    this.walk.getButtonClose().addActionListener(this);
    this.bnRun.addActionListener(this);
    this.showRiesz.addActionListener(this);
    this.showWavelet.addActionListener(this);
    this.showLargest.addActionListener(this);
    this.showSmallest.addActionListener(this);
    this.showCoherency.addActionListener(this);
    this.showDirection.addActionListener(this);
    this.showColor.addActionListener(this);
    this.showNMS.addActionListener(this);
    addWindowListener(this);
    add((Component)panel);
    setResizable(true);
    pack();
    GUI.center(this);
    setVisible(true);
  }
  
  public synchronized void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Close")) {
      this.settings.storeRecordedItems();
      dispose();
    } else if (e.getSource() == this.bnRun) {
      this.job = (JButton)e.getSource();
      if (this.thread == null) {
        this.thread = new Thread(this);
        this.thread.setPriority(1);
        this.thread.start();
      } 
      return;
    } 
    if (this.hessian == null) {
      IJ.showMessage("Run first the Hessian to show this feature");
      return;
    } 
    if (e.getSource() == this.showRiesz) {
      showRieszChannels();
    } else if (e.getSource() == this.showWavelet) {
      this.hessian.getWaveletCoefficients().show("Wavelet Coefficients");
    } else if (e.getSource() == this.showLargest) {
      this.hessian.getLargestEigenvalues().show("Largest Eigenvalues");
    } else if (e.getSource() == this.showSmallest) {
      this.hessian.getSmallestEigenvalues().show("Smallest Eigenvalues");
    } else if (e.getSource() == this.showCoherency) {
      this.hessian.getCoherency().show("Coherency");
    } else if (e.getSource() == this.showDirection) {
      this.hessian.getDirection().show("Direction");
    } else if (e.getSource() == this.showColor) {
      showColor();
    } else if (e.getSource() == this.showNMS) {
      ImageWare nms = suppressNonMaximum(this.hessian.getLargestEigenvalues(), this.hessian.getDirection());
      nms.show("Non-maximum suppression");
    } 
    notify();
  }
  
  public void run() {
    Chrono.tic();
    if (this.job == this.bnRun) {
      ImagePlus imp = getInputImage();
      if (imp != null) {
        ImageWare input = Builder.wrap(imp);
        this.hessian = new Hessian(this.walk, input, this.spnScale.get(), this.rbPyramid.isSelected());
        if (this.ckRiesz.isSelected())
          showRieszChannels(); 
        if (this.ckWavelet.isSelected())
          this.hessian.getWaveletCoefficients().show("Wavelet Coefficients"); 
        if (this.ckLargest.isSelected())
          this.hessian.getLargestEigenvalues().show("Largest Eigenvalues"); 
        if (this.ckSmallest.isSelected())
          this.hessian.getSmallestEigenvalues().show("Smallest Eigenvalues"); 
        if (this.ckCoherency.isSelected())
          this.hessian.getCoherency().show("Coherency"); 
        if (this.ckDirection.isSelected())
          this.hessian.getDirection().show("Direction"); 
        if (this.ckColor.isSelected())
          showColor(); 
        if (this.ckNMS.isSelected()) {
          ImageWare nms = suppressNonMaximum(this.hessian.getLargestEigenvalues(), this.hessian.getDirection());
          nms.show("Non-maximum suppression");
        } 
      } 
    } 
    this.walk.setMessage(Chrono.toc("End:"));
    this.thread = null;
  }
  
  private void showRieszChannels() {
    ImageWare[] channels = this.hessian.getRieszChannels();
    int n = channels.length;
    ImageWare out = Builder.create(channels[0].getWidth(), channels[0].getHeight(), n, 3);
    for (int k = 0; k < n; k++)
      out.putXY(0, 0, k, channels[k]); 
    out.show("Riesz Channels");
  }
  
  private void showColor() {
    ImageWare bri = rescaleSubband(this.hessian.getLargestEigenvalues(), 0.0D, 1.0D, this.spnScale.get());
    ImageWare hue = this.hessian.getDirection().duplicate();
    hue.divide(1.5707963267948966D);
    ImageWare sat = this.hessian.getCoherency();
    int nx = hue.getWidth();
    int ny = hue.getHeight();
    int[] pixels = new int[nx * ny];
    float[] h = hue.getSliceFloat(0);
    float[] b = bri.getSliceFloat(0);
    float[] s = sat.getSliceFloat(0);
    for (int index = 0; index < nx * ny; index++)
      pixels[index] = Color.HSBtoRGB(h[index], 1.0F, 1.0F) + -16777216; 
    (new ImagePlus("Colored Hessian", (ImageProcessor)new ColorProcessor(nx, ny, pixels))).show();
  }
  
  private ImagePlus getInputImage() {
    ImagePlus imp = WindowManager.getCurrentImage();
    if (imp == null) {
      IJ.error("No open image.");
      return null;
    } 
    if (imp.getType() != 0 && imp.getType() != 1 && imp.getType() != 2) {
      IJ.error("Only processed 8-bits, 16-bits, or 32 bits images.");
      return null;
    } 
    if (imp.getStack().getSize() > 1) {
      IJ.error("Do not process stack of images.");
      return null;
    } 
    return imp;
  }
  
  public void windowActivated(WindowEvent e) {}
  
  public void windowClosed(WindowEvent e) {}
  
  public void windowDeactivated(WindowEvent e) {}
  
  public void windowDeiconified(WindowEvent e) {}
  
  public void windowIconified(WindowEvent e) {}
  
  public void windowOpened(WindowEvent e) {}
  
  public void windowClosing(WindowEvent e) {
    dispose();
  }
  
  private ImageWare rescaleSubband(ImageWare in, double min, double max, int scales) {
    ImageWare out = in.replicate();
    int nx = in.getWidth();
    int ny = in.getHeight();
    int mx = nx;
    int my = ny / 2;
    int posy = 0;
    for (int k = 0; k < scales; k++) {
      ImageWare sub = Builder.create(mx, my, 1, in.getType());
      in.getXY(0, posy, 0, sub);
      sub.rescale(min, max);
      out.putXY(0, posy, 0, sub);
      posy += my;
      mx /= 2;
      my /= 2;
    } 
    return out;
  }
  
  private ImageWare suppressNonMaximum(ImageWare inModule, ImageWare inOrientation) {
    int nx = inModule.getWidth();
    int ny = inModule.getHeight();
    ImageWare out = Builder.create(nx, ny, 1, 3);
    for (int y = 1; y < ny - 1; y++) {
      for (int x = 1; x < nx - 1; x++) {
        double g = inModule.getPixel(x, y, 0);
        if (g != 0.0D) {
          double dx = Math.cos(inOrientation.getPixel(x, y, 0));
          double dy = Math.sin(inOrientation.getPixel(x, y, 0));
          double g1 = inModule.getInterpolatedPixel(x - dx, y - dy, 0.0D);
          if (g >= g1) {
            double g2 = inModule.getInterpolatedPixel(x + dx, y + dy, 0.0D);
            if (g >= g2)
              out.putPixel(x, y, 0, g); 
          } 
        } 
      } 
    } 
    return out;
  }
}