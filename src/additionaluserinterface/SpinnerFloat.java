package additionaluserinterface;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SpinnerFloat extends JSpinner {
  private SpinnerNumberModel model;
  
  private float defValue;
  
  private float minValue;
  
  private float maxValue;
  
  private float incValue;
  
  public SpinnerFloat(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    this.defValue = paramFloat1;
    this.minValue = paramFloat2;
    this.maxValue = paramFloat3;
    this.incValue = paramFloat4;
    Float float_1 = new Float(paramFloat1);
    Float float_2 = new Float(paramFloat2);
    Float float_3 = new Float(paramFloat3);
    Float float_4 = new Float(paramFloat4);
    this.model = new SpinnerNumberModel(float_1, float_2, float_3, float_4);
    setModel(this.model);
  }
  
  public void setLimit(float paramFloat1, float paramFloat2) {
    this.minValue = paramFloat1;
    this.maxValue = paramFloat2;
    float f = get();
    Float float_1 = new Float(paramFloat1);
    Float float_2 = new Float(paramFloat2);
    Float float_3 = new Float(this.incValue);
    this.defValue = (f > paramFloat2) ? paramFloat2 : ((f < paramFloat1) ? paramFloat1 : f);
    Float float_4 = new Float(this.defValue);
    this.model = new SpinnerNumberModel(float_4, float_1, float_2, float_3);
    setModel(this.model);
  }
  
  public void setIncrement(float paramFloat) {
    this.incValue = paramFloat;
    Float float_1 = (Float)getModel().getValue();
    Float float_2 = new Float(this.minValue);
    Float float_3 = new Float(this.maxValue);
    Float float_4 = new Float(paramFloat);
    this.model = new SpinnerNumberModel(float_1, float_2, float_3, float_4);
    setModel(this.model);
  }
  
  public float getIncrement() {
    return this.incValue;
  }
  
  public void set(float paramFloat) {
    paramFloat = (paramFloat > this.maxValue) ? this.maxValue : ((paramFloat < this.minValue) ? this.minValue : paramFloat);
    this.model.setValue(Float.valueOf(paramFloat));
  }
  
  public float get() {
    if (this.model.getValue() instanceof Integer) {
      Integer integer = (Integer)this.model.getValue();
      return integer.intValue();
    } 
    if (this.model.getValue() instanceof Double) {
      Double double_ = (Double)this.model.getValue();
      return (float)double_.doubleValue();
    } 
    if (this.model.getValue() instanceof Float) {
      Float float_ = (Float)this.model.getValue();
      return float_.floatValue();
    } 
    return 0.0F;
  }
}
