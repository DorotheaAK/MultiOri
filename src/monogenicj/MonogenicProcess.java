package monogenicj;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ij.ImagePlus;

import multigui.LogAbstract;



public class MonogenicProcess extends Thread {
	
	MonogenicImage mgim;
	private MonogenicParameters	monoParams;
	private LogAbstract				log;
	private ImagePlus 				imp;

	public MonogenicProcess(LogAbstract log, ImagePlus imp, MonogenicParameters monoParams) {
		this.log = log;
		this.imp = imp; 
		this.monoParams = monoParams;
	}
	
	public MonogenicImage getMonogenicImage() {
		return mgim;
	}


	@Override
	public void run() {
		log.reset();

	    //mgim.compute(monoParams.sigma, 1.0E-7D, monoParams.ckPrefilter, monoParams.ckSignedDir);
	    
		mgim = new MonogenicImage(log, imp.getProcessor(), monoParams);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(mgim);
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	    
		log.finish();
	}


}