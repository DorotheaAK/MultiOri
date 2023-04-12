package polyharmonicwavelets;

public class Autocorrelation {
  private int nx;
  
  private int ny;
  
  private int order;
  
  public Autocorrelation(int nx, int ny, int order) {
    this.nx = nx;
    this.ny = ny;
    this.order = order;
  }
  
  public ComplexImage computeGamma(boolean showLocalisation) {
    double PI2 = 6.283185307179586D;
    int N = 0;
    ComplexImage localisationFunc = multiplicator(this.nx, this.ny, 0.0D, 0.0D, PI2, PI2, (2 * this.order), N);
    if (showLocalisation)
      localisationFunc.showReal("LocalisationFunc Gamma"); 
    return autocorrGamma(localisationFunc, this.order);
  }
  
  public ComplexImage computeIterative(boolean showLocalisation) {
    double PI2 = 6.283185307179586D;
    int N = 0;
    ComplexImage HH = multiplicator(2 * this.nx, 2 * this.ny, 0.0D, 0.0D, 2.0D * PI2, 2.0D * PI2, this.order, N);
    ComplexImage L1 = multiplicator(2 * this.nx, 2 * this.ny, 0.0D, 0.0D, PI2, PI2, this.order, N);
    if (showLocalisation)
      L1.showReal("LocalisationFunc Iterative"); 
    double k = 1.0D / Math.pow(2.0D, this.order);
    HH.multiply(k);
    HH.divide(L1, 1.0D, 0.0D);
    HH.squareModulus();
    return autocorrIterative(HH);
  }
  
  private ComplexImage autocorrIterative(ComplexImage HH) {
    double improvement;
    int nx = HH.nx;
    int ny = HH.ny;
    int lx = nx / 2;
    int ly = ny / 2;
    int lyx = ly * lx;
    ComplexImage A0 = new ComplexImage(lx, ly, true);
    ComplexImage Af = new ComplexImage(lx, ly);
    ComplexImage Afe = new ComplexImage(lx + 1, ly + 1);
    ComplexImage Ad = new ComplexImage(lx, ly, true);
    ComplexImage Aq = new ComplexImage(lx, ly, true);
    ComplexImage A1 = new ComplexImage(lx, ly, true);
    ComplexImage At = new ComplexImage(nx, ny, true);
    ComplexImage Ai = new ComplexImage(nx, ny);
    for (int i = 0; i < lx * ly; i++)
      A0.real[i] = 1.0D; 
    double crit = 1.0E-8D;
    int lx2 = lx / 2;
    int lx231 = 3 * lx2 - 1;
    int ly2 = ly / 2;
    int ly231 = 3 * ly2 - 1;
    int ly32 = 3 * ly / 2;
    int k1 = nx * 3 * ly / 2 + lx / 2;
    int k3 = nx * ly / 2 + 3 * lx / 2;
    int k2 = k3 - 1;
    int k4 = nx * (ly32 - 1) + lx / 2;
    int count = 0;
    int maxit = 100;
    do {
      count++;
      Af.copyImageContent(A0);
      Af.iFFT2D();
      Af.shift();
      for (int x = 0; x < lx; x++) {
        Af.real[x] = Af.real[x] / 2.0D;
        Af.imag[x] = Af.imag[x] / 2.0D;
      } 
      int y;
      for (y = 0; y < lyx; y += lx) {
        Af.real[y] = Af.real[y] / 2.0D;
        Af.imag[y] = Af.imag[y] / 2.0D;
      } 
      Afe.extend(Af);
      Ai.putZeros();
      Ai.putSubimage(lx2, ly2, Afe);
      Ai.shift();
      Ai.FFT2D();
      A1.putZeros();
      At.copyImageContent(HH);
      At.multiply(Ai);
      Aq.getSubimageContent(0, Aq.nx - 1, 0, Aq.ny - 1, At);
      A1.add(Aq);
      Aq.getSubimageContent(0, Aq.nx - 1, ly, ly + Aq.ny - 1, At);
      A1.add(Aq);
      Aq.getSubimageContent(lx, lx + Aq.nx - 1, 0, Aq.ny - 1, At);
      A1.add(Aq);
      Aq.getSubimageContent(lx, lx + Aq.nx - 1, ly, ly + Aq.ny - 1, At);
      A1.add(Aq);
      Ad.copyImageContent(A1);
      Ad.subtract(A0);
      improvement = Ad.meanModulus();
      A0 = A1.copyImage();
    } while (improvement > 1.0E-8D && count < maxit);
    System.out.println("The autocoorelation has been computed in " + count + " iterations.");
    if (count == maxit)
      System.out.println("The autocorrelation does not converge!"); 
    return A0;
  }
  
  private ComplexImage autocorrGamma(ComplexImage loc, double order) {
    double PI = Math.PI;
    double PI2 = 6.283185307179586D;
    double[][] d = { 
        { 0.0D, 1.0D }, { 0.0D, 2.0D }, { 1.0D, 0.0D }, { 1.0D, 1.0D }, { 1.0D, 2.0D }, { 2.0D, 0.0D }, { 2.0D, 1.0D }, { -1.0D, 0.0D }, { -1.0D, 1.0D }, { -1.0D, 2.0D }, 
        { -2.0D, 0.0D }, { -2.0D, 1.0D }, { 0.0D, -1.0D }, { 0.0D, -2.0D }, { 1.0D, -1.0D }, { 1.0D, -2.0D }, { 2.0D, -1.0D }, { -1.0D, -1.0D }, { -1.0D, -2.0D }, { -2.0D, -1.0D }, 
        { 0.0D, 0.0D } };
    int nx = loc.nx;
    int ny = loc.ny;
    int nxy = nx * ny;
    ComplexImage ac = new ComplexImage(nx, ny, true);
    GammaFunction gm = new GammaFunction();
    double gammanorm = Math.exp(GammaFunction.lnGamma(order));
    for (int kx = 0, nx2 = nx / 2; kx <= nx2; kx++) {
      for (int ky = 0, ny2 = ny / 2; ky <= ny2; ky++) {
        int kynx = ky * nx;
        if (ac.real[kynx + kx] == 0.0D) {
          int kxny = kx * ny;
          double x = kx / nx;
          double y = ky / ny;
          double res = 1.0D / (order - 1.0D);
          for (int i = 0; i < 21; i++) {
            double sqn = Math.PI * ((x - d[i][0]) * (x - d[i][0]) + (y - d[i][1]) * (y - d[i][1]));
            res += GammaFunction.incompleteGammaQ(order, sqn) * gammanorm / Math.pow(sqn, order);
            sqn = Math.PI * (d[i][0] * d[i][0] + d[i][1] * d[i][1]);
            if (sqn > 0.0D)
              res += incompleteGammaGeneral(sqn, 1.0D - order) * Math.cos(6.283185307179586D * (d[i][0] * x + d[i][1] * y)) / Math.pow(sqn, 1.0D - order); 
          } 
          ac.real[kynx + kx] = res;
          if (kx > 0)
            ac.real[kynx + nx - kx] = res; 
          if (ky > 0)
            ac.real[nxy - kynx + kx] = res; 
          if (kx > 0 && ky > 0)
            ac.real[nxy - kynx + nx - kx] = res; 
          if (kynx / ny * ny == kynx && kxny / nx * nx == kxny) {
            int kx1 = ky * nx / ny;
            int ky1 = kx * ny / nx;
            kynx = ky1 * nx;
            kxny = kx1 * ny;
            ac.real[kynx + kx1] = res;
            if (kx1 > 0)
              ac.real[kynx + nx - kx1] = res; 
            if (ky1 > 0)
              ac.real[nxy - kynx + kx1] = res; 
            if (kx1 > 0 && ky1 > 0)
              ac.real[nxy - kynx + nx - kx1] = res; 
          } 
        } 
      } 
    } 
    ac.multiply(Math.pow(Math.PI, order) / gammanorm * Math.pow(6.283185307179586D, 2.0D * order));
    ac.multiply(loc);
    ac.real[0] = 1.0D;
    return ac;
  }
  
  private static double incompleteGammaGeneral(double x, double a) {
    double res = 0.0D;
    GammaFunction gm = new GammaFunction();
    if (a < 0.0D) {
      double a0 = a;
      int iter = 0;
      while (a < 0.0D) {
        a++;
        iter++;
      } 
      if (a == 0.0D) {
        res = expInt(x);
      } else {
        res = GammaFunction.incompleteGammaQ(a, x) * Math.exp(GammaFunction.lnGamma(a));
      } 
      res *= Math.exp(x - a * Math.log(x));
      for (int k = 1; k <= iter; k++)
        res = (x * res - 1.0D) / (a - k); 
      res *= Math.exp(a0 * Math.log(x) - x);
    } else if (a == 0.0D) {
      res = expInt(x);
    } else {
      res = GammaFunction.incompleteGammaQ(a, x) * Math.exp(GammaFunction.lnGamma(a));
    } 
    return res;
  }
  
  private static double expInt(double x) {
    double[] p = { -3.602693626336023E-9D, -4.81953845214096E-7D, -2.569498322115933E-5D, -6.97379085953419E-4D, -0.01019573529845792D, -0.07811863559248197D, -0.3012432892762715D, -0.7773807325735529D, 8.267661952366478D };
    double d = 0.0D;
    double y = 0.0D;
    for (int j = 0; j < 9; j++) {
      d *= x;
      d += p[j];
    } 
    if (d > 0.0D) {
      double egamma = 0.5772156649015329D;
      y = -egamma - Math.log(x);
      double term = x;
      double pterm = x;
      double eps = 1.0E-29D;
      double d1;
      for (d1 = 2.0D; Math.abs(term) > eps; d1++) {
        y += term;
        pterm = -x * pterm / d1;
        term = pterm / d1;
      } 
    } else {
      double n = 1.0D;
      double am2 = 0.0D;
      double bm2 = 1.0D;
      double am1 = 1.0D;
      double bm1 = x;
      double f = am1 / bm1;
      double oldf = f + 100.0D;
      double d1 = 2.0D;
      double eps = 1.0E-27D;
      while (Math.abs(f - oldf) > eps) {
        double alpha = n - 1.0D + d1 / 2.0D;
        double a = am1 + alpha * am2;
        double b = bm1 + alpha * bm2;
        am2 = am1 / b;
        bm2 = bm1 / b;
        am1 = a / b;
        bm1 = 1.0D;
        oldf = f;
        f = am1;
        d1++;
        alpha = (d1 - 1.0D) / 2.0D;
        double beta = x;
        a = beta * am1 + alpha * am2;
        b = beta * bm1 + alpha * bm2;
        am2 = am1 / b;
        bm2 = bm1 / b;
        am1 = a / b;
        bm1 = 1.0D;
        oldf = f;
        f = am1;
        d1++;
      } 
      y = Math.exp(-x) * f;
    } 
    return y;
  }
  
  public ComplexImage multiplicator(int sizex, int sizey, double minx, double miny, double maxx, double maxy, double gama, int N) {
    double PI2 = 6.283185307179586D;
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
      for (; x1 >= Math.PI - epsx; x1 -= PI2);
      for (; x1 < -3.141592653589793D - epsx; x1 += PI2);
      x1arr[kx] = x1;
    } 
    double y = miny;
    for (int ky = 0; ky < sizey; ky++, y += ry) {
      int kxy = ky * sizex;
      double sy = Math.sin(y / 2.0D);
      sy *= sy;
      double y1 = y;
      for (; y1 >= Math.PI - epsy; y1 -= PI2);
      for (; y1 < -3.141592653589793D - epsy; y1 += PI2);
      double y11 = y1;
      for (int i = 0, index = kxy; i < sizex; i++, index++) {
        y1 = y11;
        double x1 = x1arr[i];
        double sx = sxarr[i];
        double a = 1.0D;
        a = 4.0D * (sx + sy) - 2.6666666666666665D * sx * sy;
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
  
  public ComplexImage denominator(int sizex, int sizey, double minx, double miny, double maxx, double maxy, int order, int N) {
    ComplexImage result = new ComplexImage(sizex, sizey);
    double gamaN2 = (order - N) / 2.0D;
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
}
