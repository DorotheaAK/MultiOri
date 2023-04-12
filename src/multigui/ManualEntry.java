package multigui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ij.IJ;
import ij.ImagePlus;
import multigui.DrawDialog.Line;


public class ManualEntry extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private BufferedImage img;
    private Point p1 = null;
    private Point p2 = null;
    private Point imgPoint = null;
    private double angle;

    private Double director = null;
	private static final float LINE_WIDTH = 8;  
    private JDialog f;
    
    // Store lines in an arraylist
    private ArrayList<Line> lines = new ArrayList<>();
    
    
    Locale locale = new Locale("en","US");
    
    public ManualEntry(ImagePlus img2) {
    	ImagePlus imp = img2;
		img = imp.getBufferedImage();

		imgPoint = new Point(0,0);
		
		p1 = new Point(img.getHeight()/2, img.getWidth()/2);
		
        this.setPreferredSize(new Dimension(img.getHeight(), img.getWidth()));
       
      
    }
    

    @Override
	public void paint(Graphics g) {
        super.paintComponent(g);

        if (img != null && imgPoint != null) {
            g.drawImage(img, imgPoint.x, imgPoint.y, this);
        }
     

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.red);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(LINE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        
        if (p1 != null && p2 != null) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            System.out.println("line: " + line);
        }
        
        if (lines.size()>1) {
        	
        	lines.remove(0);
        	
        }
        
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            
            if (line.isSelected) {
                g2d.setColor(Color.red);
            } else {
            	lines.remove(i);
            }
            g.drawLine(lines.get(i).p1.x, lines.get(i).p1.y, lines.get(i).p2.x, lines.get(i).p2.y);
        }
    
    }
    
    private void display() {
        f = new JDialog();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.add(this);
        
        JPanel subPanel = new JPanel(); 
        
        subPanel.add(new JButton(new CloseLineAction()));
        subPanel.add(new JButton(new ResetLineAction()));
       
        
        f.add(subPanel, BorderLayout.SOUTH);
        
        f.pack();
        f.setModal(true);
        f.setLocationRelativeTo(null);
        f.setVisible(true); 
    }
    
    
    private class CloseLineAction extends AbstractAction { // change to close 
    	public CloseLineAction() {
            super("Done");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        	
        	Object[] options = {"YES", "NO"};
              
        	director = angle; 
        	Locale.setDefault(Locale.ENGLISH);
            int chosen = JOptionPane.showOptionDialog(null, 
        			"Use the director angle " + director + "° for calculating the Orientational Order Parameter?", 
        			"Select an Option for the Director of the Orientational Order Parameter", 
        			JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        	
        	if ( chosen == JOptionPane.YES_OPTION) {
        		f.setModal(false);
            	f.dispose();   
        	}
   	
        }

    	
    }
    
    private class ResetLineAction extends AbstractAction { // change to close 
    	public ResetLineAction() {
            super("Re-draw");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        	Locale.setDefault(Locale.ENGLISH);
        	String directorString = JOptionPane.showInputDialog("Type in the angle of the director (in °)");
        	
        	calculatePoint(directorString);
        	
        	repaint();
        }	

    	
    }
    
    public void calculatePoint(String directorString) {
    	
    	
    	angle=Double.parseDouble(directorString);  
    	//System.out.println("Angle: " + angle);
    	//System.out.println("Radians: " + Math.toRadians(angle));
    	
        double p2_x = Math.cos(Math.toRadians(angle));
    	double p2_y = Math.sin(Math.toRadians(angle));
    	
    	
    	System.out.println("p2_x: " + p2_x + " p2_y: " + p2_y);
    	director = angle;
    	
    	
    	p2 = new Point((int)(img.getWidth()/2+img.getWidth()/2*p2_x), (int)(img.getHeight()/2+img.getWidth()/2*p2_y));
    	
    	//System.out.println("p2_x: " + (img.getWidth()/2+img.getWidth()/2*p2_x)+ " p2_y: " + (int)(img.getHeight()/2+img.getHeight()*p2_y));
    	
    	lines.add(new Line(p1, p2));
    	
    	p2 = null;
    	
    	
    }



    public double displayManualEntry() {

    	Locale.setDefault(Locale.ENGLISH);
    	JOptionPane.setDefaultLocale(locale);
    	
    	String directorString = JOptionPane.showInputDialog(null, "Which director angle would you like to choose?");
    	System.out.println("directorSrting" + directorString);
    	if (directorString != null) {
    		calculatePoint(directorString);
    		display();
    	}
    	

    	
    	double directorAngle; 
    	if(director != null) {
    	
    		directorAngle = director.doubleValue();
    	
    		System.out.println("directorAngle: " + directorAngle); }

    	else {
    		 directorAngle = 0;
    		 IJ.error("not possible to draw director");
    	}
    
    	
        return directorAngle;
    	
    }

    class Line {
        Point p1;
        Point p2;

        boolean isSelected;

        public Line(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;

            isSelected = true;
        }
    }

    
}