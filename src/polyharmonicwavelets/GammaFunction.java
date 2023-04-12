package polyharmonicwavelets;

public class GammaFunction {
  public static double lnGamma(double alpha) {
    double x = alpha, f = 0.0D;
    if (x < 7.0D) {
      f = 1.0D;
      double d = x - 1.0D;
      while (++d < 7.0D)
        f *= d; 
      x = d;
      f = -Math.log(f);
    } 
    double z = 1.0D / x * x;
    return f + (x - 0.5D) * Math.log(x) - x + 0.918938533204673D + (((-5.95238095238E-4D * z + 7.93650793651E-4D) * z - 0.002777777777778D) * z + 0.083333333333333D) / x;
  }
  
  public static double incompleteGammaQ(double a, double x) {
    return 1.0D - incompleteGamma(x, a, lnGamma(a));
  }
  
  public static double incompleteGammaP(double a, double x) {
    return incompleteGamma(x, a, lnGamma(a));
  }
  
  public static double incompleteGammaP(double a, double x, double lnGammaA) {
    return incompleteGamma(x, a, lnGammaA);
  }
  
  private static double incompleteGamma(double x, double alpha, double ln_gamma_alpha) {
    double gin, accurate = 1.0E-8D, overflow = 1.0E30D;
    if (x == 0.0D)
      return 0.0D; 
    if (x < 0.0D || alpha <= 0.0D)
      throw new IllegalArgumentException("Arguments out of bounds"); 
    double factor = Math.exp(alpha * Math.log(x) - x - ln_gamma_alpha);
    if (x > 1.0D && x >= alpha) {
      double a = 1.0D - alpha;
      double b = a + x + 1.0D;
      double term = 0.0D;
      double pn0 = 1.0D;
      double pn1 = x;
      double pn2 = x + 1.0D;
      double pn3 = x * b;
      gin = pn2 / pn3;
      while (true) {
        a++;
        b += 2.0D;
        term++;
        double an = a * term;
        double pn4 = b * pn2 - an * pn0;
        double pn5 = b * pn3 - an * pn1;
        if (pn5 != 0.0D) {
          double rn = pn4 / pn5;
          double dif = Math.abs(gin - rn);
          if (dif <= accurate && 
            dif <= accurate * rn)
            break; 
          gin = rn;
        } 
        pn0 = pn2;
        pn1 = pn3;
        pn2 = pn4;
        pn3 = pn5;
        if (Math.abs(pn4) >= overflow) {
          pn0 /= overflow;
          pn1 /= overflow;
          pn2 /= overflow;
          pn3 /= overflow;
        } 
      } 
      gin = 1.0D - factor * gin;
    } else {
      gin = 1.0D;
      double term = 1.0D;
      double rn = alpha;
      while (true) {
        rn++;
        term *= x / rn;
        gin += term;
        if (term <= accurate) {
          gin *= factor / alpha;
          return gin;
        } 
      } 
    } 
    return gin;
  }
}
