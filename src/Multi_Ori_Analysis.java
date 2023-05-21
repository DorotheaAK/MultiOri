import multigui.AnalysisDialog;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import orientation.OrientationService;

public class Multi_Ori_Analysis implements PlugIn {
	public static void main(String arg[]) {

		new Multi_Ori_Analysis().run("");
	}
	public void run(String arg) {
		
		//ImagePlus img = new ImagePlus("Chirp", TestImage.chirp(512, 512)); //256,256
		//img.show();
		
		
		//
		//String url = "C:/Users/dorot/Downloads/Source.jpg";
		
		//String url = "C:/Users/dorot/Downloads/chirp";
		
		//String url = "http://bigwww.epfl.ch/demo/monogenicj/viewer/Dendrochronology/Source.jpg";
		
		//String url = "http://bigwww.epfl.ch/demo/monogenicj/viewer/Zoneplate/Source.jpg";
		
		//String url = "http://bigwww.epfl.ch/demo/orientation/chirp.gif";
		//String url = "http://bigwww.epfl.ch/demo/orientation/artificial-fibers.jpg";

		//ImagePlus imp = IJ.openImage(url);
		//imp.show();
		
		//dialog.run();
		//IJ.save(imp, "C:\\Users\\Dorothea Kraft\\Downloads\\this.tiff");
		
		//String url = "C:/Users/dorot/Downloads/chip.png";
		String url = "C:/Users/dorot/Downloads/TestImages_Orientation/Control_chip01-05.tif";
		//String url = "C:/Users/dorot/Downloads/Diseased_IL13_chip01-03.tif";
		//String url =  "C:/Users/dorot/Downloads/STD_H47_BG2_FOV7_WGA_Series037_60fps.tif";
		ImagePlus imp = IJ.openImage(url);
		imp.show();
		
		//PreprocessOrient orient = new PreprocessOrient(); 
		//ImagePlus filteredImg = orient.filteringOrient(imp);
		
		
		//filteredImg.show();
		AnalysisDialog orientation = new AnalysisDialog(OrientationService.ANALYSIS);
		orientation.showDialog();

	}
	
	
}