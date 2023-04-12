package additionaluserinterface;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SpinnerDouble extends JSpinner {
  private SpinnerNumberModel model;
  
  private double defValue;
  
  private double minValue;
  
  private double maxValue;
  
  private double incValue;

  public SpinnerDouble(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    this.defValue = paramDouble1;
    this.minValue = paramDouble2;
    this.maxValue = paramDouble3;
    this.incValue = paramDouble4;
    Double double_1 = new Double(paramDouble1);
    Double double_2 = new Double(paramDouble2);
    Double double_3 = new Double(paramDouble3);
    Double double_4 = new Double(paramDouble4);
    this.model = new SpinnerNumberModel(double_1, double_2, double_3, double_4);
    setModel(this.model);
  }
  
  public void setLimit(double paramDouble1, double paramDouble2) {
    this.minValue = paramDouble1;
    this.maxValue = paramDouble2;
    double d = get();
    Double double_1 = new Double(paramDouble1);
    Double double_2 = new Double(paramDouble2);
    Double double_3 = new Double(this.incValue);
    this.defValue = (d > paramDouble2) ? paramDouble2 : ((d < paramDouble1) ? paramDouble1 : d);
    Double double_4 = new Double(this.defValue);
    this.model = new SpinnerNumberModel(double_4, double_1, double_2, double_3);
    setModel(this.model);
  }
  
  public void setIncrement(double paramDouble) {
    this.incValue = paramDouble;
    Double double_1 = (Double)getModel().getValue();
    Double double_2 = new Double(this.minValue);
    Double double_3 = new Double(this.maxValue);
    Double double_4 = new Double(paramDouble);
    this.model = new SpinnerNumberModel(double_1, double_2, double_3, double_4);
    setModel(this.model);
  }
  
  public double getIncrement() {
    return this.incValue;
  }
  
  public void set(double paramDouble) {
    paramDouble = (paramDouble > this.maxValue) ? this.maxValue : ((paramDouble < this.minValue) ? this.minValue : paramDouble);
    this.model.setValue(Double.valueOf(paramDouble));
  }
  
  public double get() {
    if (this.model.getValue() instanceof Integer) {
      Integer integer = (Integer)this.model.getValue();
      return integer.intValue();
    } 
    if (this.model.getValue() instanceof Double) {
      Double double_ = (Double)this.model.getValue();
      return double_.doubleValue();
    } 
    if (this.model.getValue() instanceof Float) {
      Float float_ = (Float)this.model.getValue();
      return float_.floatValue();
    } 
    return 0.0D;
  }
}
