package steerabletools;

import additionaluserinterface.Chrono;
import additionaluserinterface.GridPanel;
import additionaluserinterface.Settings;
import additionaluserinterface.WalkBar;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GUI;
import imageware.Builder;
import imageware.ImageWare;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;

public abstract class TransformDialog extends JDialog implements ActionListener, WindowListener, Runnable {
  protected Settings settings = null;
  
  private WalkBar walk = null;
  
  private Thread thread = null;
  
  private JButton job = null;
  
  private JButton bnAnalysis = new JButton("Analysis");
  
  private JButton bnSynthesis = new JButton("Synthesis");
  
  private JButton bnShowFilter = new JButton("Show Filters");
  
  private JButton bnShowCoef = new JButton("Show Coef.");
  
  private JButton bnRecons = new JButton("Check Perfect Reconstruction");
  
  public TransformDialog(String title, Settings settings, WalkBar walk) {
    super(new Frame(), title);
    this.settings = settings;
    this.walk = walk;
    doDialog();
    recordParameters();
    settings.loadRecordedItems();
  }
  
  public abstract void recordParameters();
  
  public abstract GridPanel createPanelParameters();
  
  public abstract void analysisAndShowCoef(ImageWare paramImageWare);
  
  public abstract void analysis(ImageWare paramImageWare);
  
  public abstract void synthesis(ImageWare[] paramArrayOfImageWare);
  
  public abstract void showFilter(int paramInt1, int paramInt2);
  
  public abstract void checkPerfectReconstruction(ImageWare paramImageWare);
  
  private void doDialog() {
    GridPanel pn0 = new GridPanel(true);
    pn0.place(1, 0, 1, 1, this.bnAnalysis);
    pn0.place(1, 1, 1, 1, this.bnSynthesis);
    pn0.place(2, 0, 1, 1, this.bnShowFilter);
    pn0.place(2, 1, 1, 1, this.bnShowCoef);
    pn0.place(4, 0, 2, 1, this.bnRecons);
    GridPanel panel = new GridPanel(false, 5);
    panel.place(0, 0, (JComponent)createPanelParameters());
    panel.place(1, 0, (JComponent)pn0);
    panel.place(2, 0, (JComponent)this.walk);
    this.walk.getButtonClose().addActionListener(this);
    this.bnRecons.addActionListener(this);
    this.bnSynthesis.addActionListener(this);
    this.bnAnalysis.addActionListener(this);
    this.bnShowFilter.addActionListener(this);
    this.bnShowCoef.addActionListener(this);
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
    } else if (e.getSource() == this.bnAnalysis || e.getSource() == this.bnSynthesis || e.getSource() == this.bnShowFilter || e.getSource() == this.bnShowCoef || e.getSource() == this.bnRecons) {
      this.job = (JButton)e.getSource();
      if (this.thread == null) {
        this.thread = new Thread(this);
        this.thread.setPriority(1);
        this.thread.start();
      } 
    } 
    notify();
  }
  
  public void run() {
    Chrono.tic();
    if (this.job == this.bnAnalysis) {
      ImagePlus imp = getCurrentImage();
      if (imp != null) {
        ImageWare input = Builder.wrap(imp);
        analysis(input);
      } 
    } else if (this.job == this.bnSynthesis) {
      ImagePlus imp = getCurrentStack();
      if (imp != null) {
        int n = imp.getStack().getSize();
        ImageWare[] channels = new ImageWare[n];
        for (int s = 0; s < n; s++) {
          ImagePlus temp = new ImagePlus("", imp.getStack().getProcessor(s + 1));
          channels[s] = Builder.create(temp, 4);
        } 
        synthesis(channels);
      } 
    } else if (this.job == this.bnRecons) {
      ImagePlus imp = getCurrentImage();
      if (imp != null) {
        ImageWare input = Builder.wrap(imp);
        checkPerfectReconstruction(input);
      } 
    } else if (this.job == this.bnShowFilter) {
      ImagePlus imp = getCurrentImage();
      int nx = 256, ny = 256;
      if (imp != null) {
        nx = imp.getWidth();
        ny = imp.getHeight();
      } 
      showFilter(nx, ny);
    } else if (this.job == this.bnShowCoef) {
      ImagePlus imp = getCurrentImage();
      if (imp != null) {
        ImageWare input = Builder.wrap(imp);
        analysisAndShowCoef(input);
      } 
    } 
    this.walk.setMessage(Chrono.toc("End:"));
    this.thread = null;
  }
  
  private ImagePlus getCurrentImage() {
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
  
  private ImagePlus getCurrentStack() {
    ImagePlus imp = WindowManager.getCurrentImage();
    if (imp == null) {
      IJ.error("No open image.");
      return null;
    } 
    if (imp.getType() != 0 && imp.getType() != 1 && imp.getType() != 2) {
      IJ.error("Only processed 8-bits, 16-bits, or 32 bits images.");
      return null;
    } 
    if (imp.getStack().getSize() == 1) {
      IJ.error("Only process stack of images.");
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
}