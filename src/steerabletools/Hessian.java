package steerabletools;

import additionaluserinterface.WalkBar;
import ij.IJ;
import ij.ImagePlus;
import imageware.Builder;
import imageware.ImageWare;
import polyharmonicwavelets.ComplexImage;
import polyharmonicwavelets.DyadicFilters;
import polyharmonicwavelets.DyadicTransform;
import polyharmonicwavelets.Parameters;
import riesz.RieszTransform;

public class Hessian {
  private ImageWare[] channels;
  
  private ImageWare wavelets;
  
  private ImageWare largest;
  
  private ImageWare smallest;
  
  private ImageWare coherency;
  
  private ImageWare direction;
  
  public Hessian(WalkBar walk, ImageWare input, int scales, boolean pyramid) {
    walk.reset();
    int order = 2;
    int nx = input.getWidth();
    int ny = input.getHeight();
    int fy = pyramid ? ((scales <= 1) ? 1 : 2) : scales;
    walk.progress("Riesz", 10);
    RieszTransform riesz = new RieszTransform(nx, ny, order, false);
    this.channels = riesz.analysis(input);
    Parameters params = new Parameters();
    params.J = scales;
    params.redundancy = pyramid ? 2 : 1;
    params.flavor = 7;
    params.N = 2;
    walk.progress("Filter", 20);
    DyadicFilters filter = new DyadicFilters(params, nx, ny);
    filter.compute();
    walk.progress("Transform", 30);
    DyadicTransform transform = new DyadicTransform(filter, params);
    this.wavelets = Builder.create(nx, ny * fy, order + 1, 3);
    for (int k = 0; k < order + 1; k++) {
      walk.progress("Transform", 30 + k * 10);
      ImagePlus impc = new ImagePlus("", this.channels[k].buildImageStack());
      ComplexImage image = new ComplexImage(impc);
      ComplexImage[] array = transform.analysis(image);
      int posy = 0;
      int sizy = ny;
      for (int s = 0; s < array.length - 1; s++) {
        ImageWare real = convertComplexModule(array[s]);
        this.wavelets.putXY(0, posy, k, real);
        posy += sizy;
        if (pyramid)
          sizy /= 2; 
      } 
    } 
    this.largest = Builder.create(nx, ny * fy, 1, 3);
    this.smallest = Builder.create(nx, ny * fy, 1, 3);
    this.coherency = Builder.create(nx, ny * fy, 1, 3);
    this.direction = Builder.create(nx, ny * fy, 1, 3);
    int mx = nx;
    int my = ny;
    int py = 0;
    double sqrt2 = Math.sqrt(2.0D);
    for (int i = 0; i < scales; i++) {
      IJ.showStatus("Hessian scale " + (i + 1));
      walk.progress("Transform", 60 + i * 10);
      for (int x = 0; x < mx; x++) {
        for (int y = 0; y < my; y++) {
          double vxx = this.wavelets.getPixel(x, py + y, 0);
          double vxy = this.wavelets.getPixel(x, py + y, 1) / sqrt2;
          double vyy = this.wavelets.getPixel(x, py + y, 2);
          double delta = (vxx - vyy) * (vxx - vyy) + 4.0D * vxy * vxy;
          delta = Math.sqrt(delta);
          double lmax = 0.5D * (vxx + vyy + delta);
          double lmin = 0.5D * (vxx + vyy - delta);
          this.largest.putPixel(x, py + y, 0, lmax);
          this.smallest.putPixel(x, py + y, 0, lmin);
          this.coherency.putPixel(x, py + y, 0, (lmax - lmin) / (lmax + lmin + 1.0E-5D));
          double ex = Math.sqrt(vxy * vxy / ((vxx - lmax) * (vxx - lmax) + vxy * vxy));
          double ey = Math.sqrt(1.0D - ex * ex);
          double sx = Math.sqrt(vxy * vxy / ((vxx - lmin) * (vxx - lmin) + vxy * vxy));
          double sy = Math.sqrt(1.0D - ex * ex);
          this.direction.putPixel(x, py + y, 0, Math.atan2(ex, ey));
        } 
      } 
      py += my;
      if (pyramid) {
        mx /= 2;
        my /= 2;
      } 
    } 
    walk.finish();
  }
  
  public ImageWare[] getRieszChannels() {
    return this.channels;
  }
  
  public ImageWare getWaveletCoefficients() {
    return this.wavelets;
  }
  
  public ImageWare getLargestEigenvalues() {
    return this.largest;
  }
  
  public ImageWare getSmallestEigenvalues() {
    return this.smallest;
  }
  
  public ImageWare getCoherency() {
    return this.coherency;
  }
  
  public ImageWare getDirection() {
    return this.direction;
  }
  
  private ImageWare convertComplexModule(ComplexImage c) {
    int nx = c.nx;
    int ny = c.ny;
    ImageWare out = Builder.create(nx, ny, 1, 4);
    double[] r = out.getSliceDouble(0);
    for (int k = 0; k < nx * ny; k++)
      r[k] = Math.sqrt(c.real[k] * c.real[k] + c.imag[k] * c.imag[k]); 
    return out;
  }
}
