package multigui;
import javax.swing.JComboBox;



public class OrientComboFeature extends JComboBox<String> {


	public OrientComboFeature(String init, boolean orient) {
		super();
		if (orient) {
			addItem("Gradient-X");
			addItem("Gradient-Y");
			addItem("Orientation");
			addItem("Coherency");
			addItem("Energy");
			addItem("Constant");
			addItem("Original-Image");
			setSelectedItem(init);
		
		}
		
		else {
			
			
			String[] colorSurveyMonogenic = new String[] { 
					 "Original-Image", "Laplace", "Riesz X", "Riesz Y", "Orientation", "Coherency", "Energy", "Wavenumber", "Modulus", "Phase", 
				      "Dir. Hilbert", "Maximum", "Constant" };
			for (int i = 0; i < colorSurveyMonogenic.length; i++) {
				addItem(colorSurveyMonogenic[i].toString());
		
			}
			
			setSelectedItem(init);
		}
		
		
	}
	
}
