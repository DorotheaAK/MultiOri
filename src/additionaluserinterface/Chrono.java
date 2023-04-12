package additionaluserinterface;

import java.text.DecimalFormat;

public class Chrono {
  private static double chrono = 0.0D;
  
  public static void tic() {
    chrono = System.currentTimeMillis();
  }
  
  public static String toc() {
    return toc("");
  }
  
  public static String toc(String paramString) {
    double d = System.currentTimeMillis() - chrono;
    String str = paramString + " ";
    DecimalFormat decimalFormat = new DecimalFormat("####.##");
    if (d < 3000.0D)
      return str + decimalFormat.format(d) + " ms"; 
    d /= 1000.0D;
    if (d < 600.1D)
      return str + decimalFormat.format(d) + " s"; 
    d /= 60.0D;
    if (d < 240.1D)
      return str + decimalFormat.format(d) + " min."; 
    d /= 24.0D;
    return str + decimalFormat.format(d) + " h.";
  }
}
