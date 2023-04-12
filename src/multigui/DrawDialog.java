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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.text.TextWindow;


public class DrawDialog extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int LINE_WIDTH = 8;

    private MouseHandler mouseHandler = new MouseHandler();
    private BufferedImage img;
    private Point p1 = null;
    private Point p2 = null;
    private Point imgPoint = null;
    private boolean drawing;
    private Point draggingPoint = null;
 	
    private Double director = null;
    private static final DecimalFormat df = new DecimalFormat("00.00");  
    private boolean isEnabled = false; 
    private JDialog f;

    // Store lines in an arraylist
    private ArrayList<Line> lines = new ArrayList<>();

    
    public DrawDialog(ImagePlus img2) {
    	ImagePlus imp = img2;
		img = imp.getBufferedImage();

		imgPoint = new Point(0,0);
		
        this.setPreferredSize(new Dimension(img.getHeight(), img.getWidth()));
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }
    

    @Override
    protected void paintComponent(Graphics g) {
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
        // draw all previous lines
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

    private void deSelectAll() {
        for (Line line : lines) {
            line.isSelected = false;
        }
    }
    

    private class MouseHandler extends MouseAdapter {


        @Override
        public void mousePressed(MouseEvent e) {

        	System.out.println(isEnabled);
        	if (!isEnabled) {
        		return;
        	}
            if (!e.isControlDown()) {
                drawing = true;
                p1 = e.getPoint();
                p2 = p1;
                repaint();
            } else {
                for (Line line : lines) {
                    line.isSelected = false;
                    if (isInside(line, e.getPoint())) {
                        line.isSelected = true;
                    }
                }

                draggingPoint = e.getPoint();

                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        	System.out.println(isEnabled);
        	if (!isEnabled) {
        		return;
        	}

            if (!e.isControlDown()) {
                drawing = false;
                p2 = e.getPoint();
                repaint();
                deSelectAll();
                lines.add(new Line(p1, p2));
              
                p1 = null;
                p2 = null;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        	//System.out.println(isEnabled);
        	if (!isEnabled) {
        		return;
        	}

            if (!e.isControlDown()) {
                if (drawing) {
                    p2 = e.getPoint();
                    repaint();
                }
            } else if (draggingPoint != null) {

                int XDiff = e.getPoint().x - draggingPoint.x;
                int YDiff = e.getPoint().y - draggingPoint.y;

                draggingPoint = e.getPoint();

                for (Line line : lines) {
                    if (line.isSelected) {
                        line.p1.x = line.p1.x + XDiff;
                        line.p1.y = line.p1.y + YDiff;
                        line.p2.x = line.p2.x + XDiff;
                        line.p2.y = line.p2.y + YDiff;
                    }
                }

                repaint();
            }
        }

		/**
         * Returns true if the given point is inside the given line.
         */
        private boolean isInside(Line line, Point p) {
            return new Line2D.Double(line.p1, line.p2).ptSegDist(p) < (LINE_WIDTH / 2);
        }
    }

    private void display() {
    	
        f = new JDialog();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.add(this);
        
        JPanel subPanel = new JPanel(); 
        
        subPanel.add(new JButton(new DrawLineAction()));
        subPanel.add(new JButton(new DoneLineAction()));
        subPanel.add(new JButton(new DeleteLineAction()));
        subPanel.add(new JButton(new CloseLineAction()));
       
        
        f.add(subPanel, BorderLayout.SOUTH);
        
        f.pack();
        f.setModal(true);
        f.setLocationRelativeTo(null);
        f.setVisible(true); 
    }

    private class DeleteLineAction extends AbstractAction {

        public DeleteLineAction() {
            super("Reset");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Iterator<Line> iterator = lines.iterator();

            while (iterator.hasNext()) {
                Line line = iterator.next();

                if (line.isSelected) {
                    iterator.remove();
                }
            }

            repaint();
            isEnabled = true;
        }

		
    }
    
    private class DoneLineAction extends AbstractAction {

        public DoneLineAction() {
            super("Done");
            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
        	System.out.println("Disable the Draw Functionality");
        	isEnabled = false;
        	
    		Line line = lines.get(0); // fix thtat 
        	
            double directorAngle = (Math.toDegrees(Math.atan2(line.p2.y-line.p1.y, line.p2.x-line.p1.x)));
            
            if (directorAngle < 0.0) {
            	System.out.println("angle smaller 0: " + directorAngle);
            	directorAngle = directorAngle+180;
            }
            System.out.println(directorAngle);
            
            director = directorAngle;
            
            
            Object[] options = {"YES", "NO"};
            
            
            String directorString = df.format(director);
    		
    		directorString = directorString.replace(',', '.');
            
        	int chosen = JOptionPane.showOptionDialog(null, 
        			"The angle of the director is: " + directorString + "°. Use this director for calculating the "
        			+ "Orientational Order Parameter?", 
        			"Select an Option for the Director of the Orientational Order Parameter", 
        			JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        	
        	if ( chosen == JOptionPane.YES_OPTION) {
        		f.setModal(false);
            	f.dispose();   
        	}
        	
        	
        	
        }

		
    }
    
    private class DrawLineAction extends AbstractAction {

        public DrawLineAction() {
            super("Draw");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
           System.out.println("Enable the DRAW functionality");
           isEnabled = true;
           
        }

		
    }
    
    
    
    private class CloseLineAction extends AbstractAction { // change to close 
    	public CloseLineAction() {
            super("Close");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
        	System.out.println("Close the window");
        	//close window and setmodal(false)
        	f.setModal(false);
        	f.dispose();        	
        }

    	
    }
    


    public double drawDirector() {
    	
    	display();
    	
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
