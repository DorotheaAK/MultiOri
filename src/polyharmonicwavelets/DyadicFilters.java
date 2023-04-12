package polyharmonicwavelets;

public class DyadicFilters extends Filters {
  public DyadicFilters(Parameters param, int nx, int ny) {
    super(param, nx, ny);
    this.FA[0] = new ComplexImage(nx, ny, (param.N == 0));
    this.FA[1] = new ComplexImage(nx, ny, (param.redundancy != 0 && param.N == 0));
    if (param.redundancy == 0) {
      this.FA[2] = new ComplexImage(nx, ny);
      this.FA[3] = new ComplexImage(nx, ny);
    } 
    if (!param.analysesonly) {
      this.FS = new ComplexImage[4];
      this.FS[0] = new ComplexImage(nx, ny, (param.N == 0));
      int i;
      for (i = 1; i < 4; i++)
        this.FS[i] = new ComplexImage(nx, ny); 
      if (param.redundancy == 2) {
        this.FP = new ComplexImage[3];
        for (i = 0; i < 3; i++)
          this.FP[i] = new ComplexImage(nx, ny); 
      } 
    } 
  }
  
  public ComplexImage computePrefilter(double order) {
    ComplexImage P = multiplicator(this.nx, this.ny, -3.141592653589793D, -3.141592653589793D, Math.PI, Math.PI, order, 0);
    ComplexImage d = denominator(this.nx, this.ny, -3.141592653589793D, -3.141592653589793D, Math.PI, Math.PI, 0);
    P.divide(d, 1.0D, 0.0D);
    P.shift();
    return P;
  }
  
  public void compute() {
    double k = 1.0D / Math.pow(2.0D, this.param.order);
    ComplexImage HH = null;
    ComplexImage L1 = null;
    ComplexImage L = multiplicator(this.nx, this.ny, 0.0D, 0.0D, 12.566370614359172D, 12.566370614359172D, this.param.order, this.param.N);
    L1 = multiplicator(this.nx, this.ny, 0.0D, 0.0D, 6.283185307179586D, 6.283185307179586D, this.param.order, this.param.N);
    HH = L;
    HH.multiply(k);
    HH.divide(L1, 1.0D, 0.0D);
    if (!this.param.analysesonly || this.param.flavor != 7) {
      ComplexImage simpleloc = multiplicator(this.nx, this.ny, 0.0D, 0.0D, 6.283185307179586D, 6.283185307179586D, 2.0D * this.param.order, 0);
      Autocorrelation autocorrelation = new Autocorrelation(simpleloc.nx, simpleloc.ny, (int)this.param.order);
      this.ac = autocorrelation.computeGamma(false);
    } 
    calculatePrefilter();
    if (this.param.flavor == 3 || this.param.flavor == 8 || this.param.flavor == 7) {
      this.FA[0].copyImageContent(HH);
      this.FA[0].multiply(2.0D);
      ComplexImage G = this.FA[1];
      G.copyImageContent(L1);
      if (this.param.flavor != 7)
        G.divide(this.ac); 
      G.multiply(2.0D);
      if (this.param.rieszfreq == 1) {
        ComplexImage V2 = multiplicator(this.nx, this.ny, 0.0D, 0.0D, 6.283185307179586D, 6.283185307179586D, 2.0D, 0);
        G.multiply(V2);
      } 
      G.conj();
      ComplexImage R = null;
      if (this.param.redundancy == 0) {
        this.FA[2].copyImageContent(G);
        this.FA[3].copyImageContent(G);
      } 
      this.FA[1] = G;
      if (!this.param.analysesonly) {
        ComplexImage L1conj = L1.copyImage();
        L1conj.conj();
        ComplexImage H = this.FS[0];
        H.copyImageContent(HH);
        H.conj();
        double k1 = k * 4.0D;
        H.multiply(k1);
        ComplexImage acd = this.ac.copyImage();
        acd.decimate();
        ComplexImage Gs = this.ac.copyImage();
        Gs.divide(acd);
        Gs.multiply(0.25D);
        H.multiply(Gs);
        this.FS[0] = H;
        this.FS[0].multiply(2.0D / k);
        Gs.divide(L1conj, 0.0D, 0.0D);
        ComplexImage D = HH.copyImage();
        D.squareModulus();
        D.multiply(1.0D / k);
        D.multiply(this.ac);
        D.multiply(k1);
        ComplexImage D1 = D.copyImage();
        ComplexImage D2 = D.copyImage();
        ComplexImage D12 = D.copyImage();
        D1.shiftX();
        D2.shiftY();
        D12.shift();
        D1.multiply(Gs);
        D2.multiply(Gs);
        D12.multiply(Gs);
        this.FS[1] = D1.copyImage();
        this.FS[1].add(D12);
        this.FS[2] = D2.copyImage();
        this.FS[2].add(D12);
        this.FS[3] = D1;
        this.FS[3].add(D2);
      } 
      if (this.param.flavor == 8) {
        ComplexImage[] Ftmp = this.FA;
        this.FA = this.FS;
        this.FS = Ftmp;
        this.FA[0].conj();
        this.FA[1].conj();
        this.FA[2].conj();
        this.FA[3].conj();
        this.FS[0].conj();
        this.FS[1].conj();
        this.FS[2].conj();
        this.FS[3].conj();
      } 
    } 
    if (this.param.redundancy == 0) {
      this.FA[1].modulateMinusX();
      this.FA[2].modulateMinusY();
      this.FA[3].modulateMinusQuincunx();
    } 
    if (!this.param.analysesonly) {
      this.FS[1].modulatePlusX();
      this.FS[2].modulatePlusY();
      this.FS[3].modulatePlusQuincunx();
    } 
    if (this.param.redundancy == 2 && !this.param.analysesonly)
      pyramidSynthesisFilters(); 
  }
  
  private ComplexImage multiplicator(int sizex, int sizey, double minx, double miny, double maxx, double maxy, double gama, int N) {
    ComplexImage result = new ComplexImage(sizex, sizey, (N == 0));
    double gama2 = gama / 2.0D;
    double d83 = 2.6666666666666665D;
    double d23 = 0.6666666666666666D;
    double epsx = (maxx - minx) / 4.0D * sizex;
    double epsy = (maxy - miny) / 4.0D * sizey;
    double rx = (maxx - minx) / sizex;
    double ry = (maxy - miny) / sizey;
    double[] sxarr = new double[sizex];
    double[] x1arr = new double[sizex];
    double x = minx;
    for (int kx = 0; kx < sizex; kx++, x += rx) {
      sxarr[kx] = Math.sin(x / 2.0D) * Math.sin(x / 2.0D);
      double x1 = x;
      for (; x1 >= Math.PI - epsx; x1 -= 6.283185307179586D);
      for (; x1 < -3.141592653589793D - epsx; x1 += 6.283185307179586D);
      x1arr[kx] = x1;
    } 
    double y = miny;
    for (int ky = 0; ky < sizey; ky++, y += ry) {
      int kxy = ky * sizex;
      double sy = Math.sin(y / 2.0D);
      sy *= sy;
      double y1 = y;
      for (; y1 >= Math.PI - epsy; y1 -= 6.283185307179586D);
      for (; y1 < -3.141592653589793D - epsy; y1 += 6.283185307179586D);
      double y11 = y1;
      for (int i = 0, index = kxy; i < sizex; i++, index++) {
        y1 = y11;
        double x1 = x1arr[i];
        double sx = sxarr[i];
        double a = 1.0D;
        if (this.param.type == 1)
          a = 4.0D * (sx + sy) - 2.6666666666666665D * sx * sy; 
        if (this.param.type == 4) {
          double sigma2 = this.param.s2;
          double b = -16.0D / sigma2;
          double c = 24.0D / sigma2 * sigma2 - 16.0D / 3.0D * sigma2;
          double d = 8.0D / sigma2 * sigma2 + 0.7111111111111111D - 16.0D / 3.0D * sigma2;
          double e = 1.3333333333333333D - 8.0D / sigma2;
          a = 4.0D * (sx + sy) + b * sx * sy + c * (sx * sx * sy + sy * sy * sx) + d * (sx * sx * sx + sy * sy * sy) + e * (sx * sx + sy * sy);
        } 
        double re = Math.pow(a, gama2);
        double im = 0.0D;
        if (N > 0) {
          boolean xpi = (x1 < -3.141592653589793D + epsx && x1 > -3.141592653589793D - epsx);
          boolean ypi = (y1 < -3.141592653589793D + epsy && y1 > -3.141592653589793D - epsy);
          boolean x0 = (x1 < epsx && x1 > -epsx);
          boolean y0 = (y1 < epsy && y1 > -epsy);
          if (!x0 || !y0) {
            double x1p = x1;
            double y1p = y1;
            if (xpi && !y0 && !ypi)
              x1p = 0.0D; 
            if (ypi && !x0 && !xpi)
              y1p = 0.0D; 
            x1 = x1p;
            y1 = y1p;
          } 
          for (int j = 0; j < N; j++) {
            double re1 = re * x1 - im * y1;
            double im1 = re * y1 + im * x1;
            re = re1;
            im = im1;
          } 
          double t = Math.pow(x1 * x1 + y1 * y1, N / 2.0D);
          if (t == 0.0D) {
            result.real[index] = 0.0D;
            result.imag[index] = 0.0D;
          } else {
            result.real[index] = re / t;
            result.imag[index] = im / t;
          } 
        } else {
          result.real[index] = re;
        } 
      } 
    } 
    return result;
  }
  
  private ComplexImage denominator(int sizex, int sizey, double minx, double miny, double maxx, double maxy, int N) {
    ComplexImage result = new ComplexImage(sizex, sizey);
    double gamaN2 = (this.param.order - N) / 2.0D;
    for (int ky = 0; ky < sizey; ky++) {
      int kxy = ky * sizex;
      double y = miny + ky * (maxy - miny) / sizey;
      for (int kx = 0, index = kxy; kx < sizex; kx++, index++) {
        double x = minx + kx * (maxx - minx) / sizex;
        double re = Math.pow(x * x + y * y, gamaN2);
        double im = 0.0D;
        if (N > 0) {
          for (int i = 0; i < N; i++) {
            double re1 = re * x - im * y;
            double im1 = re * y + im * x;
            re = re1;
            im = im1;
          } 
          result.real[index] = re;
          result.imag[index] = im;
        } else {
          result.real[index] = re;
        } 
      } 
    } 
    return result;
  }
  
  private void calculatePrefilter() {
    this.P = multiplicator(this.nx, this.ny, -3.141592653589793D, -3.141592653589793D, Math.PI, Math.PI, this.param.order, 0);
    ComplexImage d = denominator(this.nx, this.ny, -3.141592653589793D, -3.141592653589793D, Math.PI, Math.PI, 0);
    this.P.divide(d, 1.0D, 0.0D);
    this.P.shift();
    if (this.param.flavor == 8)
      this.P.divide(this.ac); 
  }
  
  private void pyramidSynthesisFilters() {
    this.FA[1].multiply(1.0D / Constants.SQRT2);
    ComplexImage[] Ge = this.FP;
    Ge[0].copyImageContent(this.FA[1]);
    Ge[1].copyImageContent(this.FA[1]);
    Ge[2].copyImageContent(this.FA[1]);
    Ge[0].multiply(this.FS[1]);
    Ge[1].multiply(this.FS[2]);
    Ge[2].multiply(this.FS[3]);
    Ge[0].multiply(0.5D);
    Ge[1].multiply(0.5D);
    Ge[2].multiply(0.5D);
    ComplexImage[] Geconj = Ge;
    Geconj[0].conj();
    Geconj[1].conj();
    Geconj[2].conj();
    int nx2 = (this.FA[1]).nx / 2;
    int ny2 = (this.FA[1]).ny / 2;
    int nxy2 = (this.FA[1]).nxy / 2;
    int[] d = { 0, nx2, nxy2, nx2 + nxy2 };
    double[] mr = new double[9];
    double[] mi = new double[9];
    int ky, km;
    for (ky = 0, km = 0; ky < nxy2; ky += (this.FA[1]).nx) {
      for (int kx = ky, end = ky + nx2; kx < end; kx++, km++) {
        for (int i = 0; i < 9; i++) {
          mi[i] = 0.0D;
          mr[i] = 0.0D;
        } 
        double inr0 = 0.0D, inr1 = 0.0D, inr2 = 0.0D, inr4 = 0.0D, inr5 = 0.0D, inr8 = 0.0D;
        double inri = 0.0D, ini1 = 0.0D, ini2 = 0.0D, ini4 = 0.0D, ini5 = 0.0D, ini8 = 0.0D;
        for (int l = 0; l < 4; l++) {
          int k = kx + d[l];
          inr0 += (Geconj[0]).real[k] * (Geconj[0]).real[k] + (Geconj[0]).imag[k] * (Geconj[0]).imag[k];
          inr4 += (Geconj[1]).real[k] * (Geconj[1]).real[k] + (Geconj[1]).imag[k] * (Geconj[1]).imag[k];
          inr8 += (Geconj[2]).real[k] * (Geconj[2]).real[k] + (Geconj[2]).imag[k] * (Geconj[2]).imag[k];
          inr1 += (Geconj[0]).real[k] * (Geconj[1]).real[k] + (Geconj[0]).imag[k] * (Geconj[1]).imag[k];
          ini1 += -(Geconj[0]).real[k] * (Geconj[1]).imag[k] + (Geconj[0]).imag[k] * (Geconj[1]).real[k];
          inr2 += (Geconj[0]).real[k] * (Geconj[2]).real[k] + (Geconj[0]).imag[k] * (Geconj[2]).imag[k];
          ini2 += -(Geconj[0]).real[k] * (Geconj[2]).imag[k] + (Geconj[0]).imag[k] * (Geconj[2]).real[k];
          inr5 += (Geconj[1]).real[k] * (Geconj[2]).real[k] + (Geconj[1]).imag[k] * (Geconj[2]).imag[k];
          ini5 += -(Geconj[1]).real[k] * (Geconj[2]).imag[k] + (Geconj[1]).imag[k] * (Geconj[2]).real[k];
        } 
        mr[0] = inr4 * inr8 - inr5 * inr5 + ini5 * ini5;
        mr[1] = inr2 * inr5 + ini2 * ini5 - inr1 * inr8;
        mi[1] = -inr2 * ini5 + ini2 * inr5 - ini1 * inr8;
        mr[2] = inr1 * inr5 - ini1 * ini5 - inr2 * inr4;
        mi[2] = inr1 * ini5 + ini1 * inr5 - ini2 * inr4;
        double dr = mr[0] * inr0 + mr[1] * inr1 + mi[1] * ini1 + mr[2] * inr2 + mi[2] * ini2;
        mr[3] = (inr2 * inr5 + ini2 * ini5 - inr1 * inr8) / dr;
        mi[3] = (inr2 * ini5 - ini2 * inr5 + ini1 * inr8) / dr;
        mr[4] = (inr0 * inr8 - inr2 * inr2 + ini2 * ini2) / dr;
        mr[5] = (inr1 * inr2 + ini1 * ini2 - inr0 * inr5) / dr;
        mi[5] = (inr1 * ini2 - ini1 * inr2 - inr0 * ini5) / dr;
        mr[6] = (inr1 * inr5 - ini1 * ini5 - inr2 * inr4) / dr;
        mi[6] = (-inr1 * ini5 - ini1 * inr5 + ini2 * inr4) / dr;
        mr[7] = (inr2 * inr1 + ini2 * ini1 - inr0 * inr5) / dr;
        mi[7] = (inr2 * ini1 - ini2 * inr1 + inr0 * ini5) / dr;
        mr[8] = (inr0 * inr4 - inr1 * inr1 + ini1 * ini1) / dr;
        mr[0] = mr[0] / dr;
        mr[1] = mr[1] / dr;
        mi[1] = mi[1] / dr;
        mr[2] = mr[2] / dr;
        mi[2] = mi[2] / dr;
        for (int j = 0; j < 4; j++) {
          int k = kx + d[j];
          double[] ger = new double[3];
          double[] gei = new double[3];
          int m;
          for (m = 0; m < 3; m++) {
            ger[m] = (Geconj[m]).real[k];
            gei[m] = (Geconj[m]).imag[k];
          } 
          for (m = 0; m < 3; m++) {
            double gr = 0.0D;
            double gi = 0.0D;
            for (int n = 0; n < 3; n++) {
              gr += ger[n] * mr[3 * m + n] - gei[n] * mi[3 * m + n];
              gi += ger[n] * mi[3 * m + n] + gei[n] * mr[3 * m + n];
            } 
            (Geconj[m]).real[k] = gr;
            (Geconj[m]).imag[k] = gi;
          } 
        } 
      } 
    } 
    this.FP = Geconj;
    this.FA[1].multiply(Constants.SQRT2);
  }
}