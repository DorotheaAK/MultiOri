package additionaluserinterface;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.text.DefaultCaret;

public class WalkBar extends JToolBar implements ActionListener {
  private JProgressBar progress = new JProgressBar();
  
  private JButton bnHelp = new JButton("Help");
  
  private JButton bnAbout = new JButton("About");
  
  private JButton bnClose = new JButton("Close");
  
  private String[] about = new String[] { "About", "Version", "Description", "Author", "Biomedical Image Group", "2008", "http://bigwww.epfl.ch" };
  
  private String help;
  
  private double chrono;
  
  public WalkBar(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    super("Walk Bar");
    build(paramString, paramBoolean1, paramBoolean2, paramBoolean3, 100);
  }
  
  public WalkBar(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt) {
    super("Walk Bar");
    build(paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
  }
  
  private void build(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt) {
    if (paramBoolean1)
      add(this.bnAbout); 
    if (paramBoolean2)
      add(this.bnHelp); 
    addSeparator();
    add(this.progress);
    addSeparator();
    if (paramBoolean3)
      add(this.bnClose); 
    this.progress.setStringPainted(true);
    this.progress.setString(paramString);
    this.progress.setFont(new Font("Arial", 0, 10));
    this.progress.setMinimum(0);
    this.progress.setMaximum(100);
    this.progress.setPreferredSize(new Dimension(paramInt, 20));
    this.bnAbout.addActionListener(this);
    this.bnHelp.addActionListener(this);
    setFloatable(false);
    setRollover(true);
    setBorderPainted(false);
    this.chrono = System.currentTimeMillis();
  }
  
  public synchronized void actionPerformed(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (paramActionEvent.getSource() == this.bnHelp) {
      showHelp();
    } else if (paramActionEvent.getSource() == this.bnAbout) {
      showAbout();
    } else if (paramActionEvent.getSource() == this.bnClose) {
    
    } 
  }
  
  public JButton getButtonClose() {
    return this.bnClose;
  }
  
  public void setValue(int paramInt) {
    this.progress.setValue(paramInt);
  }
  
  public void setMessage(String paramString) {
    this.progress.setString(paramString);
  }
  
  public void progress(String paramString, int paramInt) {
    this.progress.setValue(paramInt);
    double d = System.currentTimeMillis() - this.chrono;
    String str = " [" + ((d > 3000.0D) ? ((Math.round(d / 10.0D) / 100.0D) + "s.") : (d + "ms")) + "]";
    this.progress.setString(paramString + str);
  }
  
  public void progress(String paramString, double paramDouble) {
    progress(paramString, (int)Math.round(paramDouble));
  }
  
  public void reset() {
    this.chrono = System.currentTimeMillis();
    this.progress.setValue(0);
    this.progress.setString("Starting ...");
  }
  
  public void finish() {
    progress("Terminated", 100);
  }
  
  public void finish(String paramString) {
    progress(paramString, 100);
  }
  
  public void fillAbout(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) {
    this.about[0] = paramString1;
    this.about[1] = paramString2;
    this.about[2] = paramString3;
    this.about[3] = paramString4;
    this.about[4] = paramString5;
    this.about[5] = paramString6;
    this.about[6] = paramString7;
  }
  
  public void fillHelp(String paramString) {
    this.help = paramString;
  }
  
  public void showAbout() {
    final JFrame frame = new JFrame("About " + this.about[0]);
    JEditorPane jEditorPane = new JEditorPane();
    jEditorPane.setEditable(false);
    jEditorPane.setContentType("text/html; charset=ISO-8859-1");
    jEditorPane.setText("<html><head><title>" + this.about[0] + "</title>" + getStyle() + "</head><body>" + ((this.about[0] == "") ? "" : ("<p class=\"name\">" + this.about[0] + "</p>")) + ((this.about[1] == "") ? "" : ("<p class=\"vers\">" + this.about[1] + "</p>")) + ((this.about[2] == "") ? "" : ("<p class=\"desc\">" + this.about[2] + "</p><hr>")) + ((this.about[3] == "") ? "" : ("<p class=\"auth\">" + this.about[3] + "</p>")) + ((this.about[4] == "") ? "" : ("<p class=\"orga\">" + this.about[4] + "</p>")) + ((this.about[5] == "") ? "" : ("<p class=\"date\">" + this.about[5] + "</p>")) + ((this.about[6] == "") ? "" : ("<p class=\"more\">" + this.about[6] + "</p>")) + "</html>");
    JButton jButton = new JButton("Close");
    jButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            frame.dispose();
          }
        });
    jEditorPane.setCaret(new DefaultCaret());
    JScrollPane jScrollPane = new JScrollPane(jEditorPane);
    jScrollPane.setPreferredSize(new Dimension(400, 400));
    frame.getContentPane().add(jScrollPane, "North"); //jFrame
    frame.getContentPane().add(jButton, "Center"); //jFrame
    frame.pack(); //jFrame
    frame.setResizable(false); //jFrame
    frame.setVisible(true); // jFrame
    center(frame); //jFrame
  }
  
  public void showHelp() {
    JFrame jFrame = new JFrame("Help " + this.about[0]);
    JEditorPane jEditorPane = new JEditorPane();
    jEditorPane.setEditable(false);
    jEditorPane.setContentType("text/html; charset=ISO-8859-1");
    jEditorPane.setText("<html><head><title>" + this.about[0] + "</title>" + getStyle() + "</head><body>" + ((this.about[0] == "") ? "" : ("<p class=\"name\">" + this.about[0] + "</p>")) + ((this.about[1] == "") ? "" : ("<p class=\"vers\">" + this.about[1] + "</p>")) + ((this.about[2] == "") ? "" : ("<p class=\"desc\">" + this.about[2] + "</p><hr>")) + ((this.about[3] == "") ? "" : ("<p class=\"auth\">" + this.about[3] + "</p>")) + ((this.about[4] == "") ? "" : ("<p class=\"orga\">" + this.about[4] + "</p>")) + ((this.about[5] == "") ? "" : ("<p class=\"date\">" + this.about[5] + "</p>")) + ((this.about[6] == "") ? "" : ("<p class=\"more\">" + this.about[6] + "</p>")) + "<hr><p class=\"help\">" + this.help + "</p>" + "</html>");
    jEditorPane.setCaret(new DefaultCaret());
    JScrollPane jScrollPane = new JScrollPane(jEditorPane);
    jScrollPane.setVerticalScrollBarPolicy(22);
    jScrollPane.setPreferredSize(new Dimension(400, 600));
    jFrame.setPreferredSize(new Dimension(400, 600));
    jFrame.getContentPane().add(jScrollPane, "Center");
    jFrame.setVisible(true);
    jFrame.pack();
    center(jFrame);
  }
  
  private void center(Window paramWindow) {
    Dimension dimension1 = new Dimension(0, 0);
    boolean bool = System.getProperty("os.name").startsWith("Windows");
    if (bool)
      dimension1 = Toolkit.getDefaultToolkit().getScreenSize(); 
    if (GraphicsEnvironment.isHeadless()) {
      dimension1 = new Dimension(0, 0);
    } else {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
      GraphicsConfiguration[] arrayOfGraphicsConfiguration = arrayOfGraphicsDevice[0].getConfigurations();
      Rectangle rectangle = arrayOfGraphicsConfiguration[0].getBounds();
      if (rectangle.x == 0 && rectangle.y == 0) {
        dimension1 = new Dimension(rectangle.width, rectangle.height);
      } else {
        dimension1 = Toolkit.getDefaultToolkit().getScreenSize();
      } 
    } 
    Dimension dimension2 = paramWindow.getSize();
    if (dimension2.width == 0)
      return; 
    int i = dimension1.width / 2 - dimension2.width / 2;
    int j = (dimension1.height - dimension2.height) / 4;
    if (j < 0)
      j = 0; 
    paramWindow.setLocation(i, j);
  }
  
  private String getStyle() {
    return "<style type=text/css>body {backgroud-color:#222277}hr {width:80% color:#333366; padding-top:7px }p, li {margin-left:10px;margin-right:10px; color:#000000; font-size:1em; font-family:Verdana,Helvetica,Arial,Geneva,Swiss,SunSans-Regular,sans-serif}p.name {color:#ffffff; font-size:1.2em; font-weight: bold; background-color: #333366; text-align:center;}p.vers {color:#333333; text-align:center;}p.desc {color:#333333; font-weight: bold; text-align:center;}p.auth {color:#333333; font-style: italic; text-align:center;}p.orga {color:#333333; text-align:center;}p.date {color:#333333; text-align:center;}p.more {color:#333333; text-align:center;}p.help {color:#000000; text-align:left;}</style>";
  }
}
