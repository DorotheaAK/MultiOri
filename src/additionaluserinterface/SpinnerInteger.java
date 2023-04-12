package additionaluserinterface;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SpinnerInteger extends JSpinner {
  private SpinnerNumberModel model;
  
  private int defValue;
  
  private int minValue;
  
  private int maxValue;
  
  private int incValue;
  
  public SpinnerInteger(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.defValue = paramInt1;
    this.minValue = paramInt2;
    this.maxValue = paramInt3;
    this.incValue = paramInt4;
    Integer integer1 = new Integer(paramInt1);
    Integer integer2 = new Integer(paramInt2);
    Integer integer3 = new Integer(paramInt3);
    Integer integer4 = new Integer(paramInt4);
    this.model = new SpinnerNumberModel(integer1, integer2, integer3, integer4);
    setModel(this.model);
  }
  
  public void setLimit(int paramInt1, int paramInt2) {
    this.minValue = paramInt1;
    this.maxValue = paramInt2;
    int i = get();
    Integer integer1 = new Integer(paramInt1);
    Integer integer2 = new Integer(paramInt2);
    Integer integer3 = new Integer(this.incValue);
    this.defValue = (i > paramInt2) ? paramInt2 : ((i < paramInt1) ? paramInt1 : i);
    Integer integer4 = new Integer(this.defValue);
    this.model = new SpinnerNumberModel(integer4, integer1, integer2, integer3);
    setModel(this.model);
  }
  
  public void setIncrement(int paramInt) {
    this.incValue = paramInt;
    Integer integer1 = (Integer)getModel().getValue();
    Integer integer2 = new Integer(this.minValue);
    Integer integer3 = new Integer(this.maxValue);
    Integer integer4 = new Integer(paramInt);
    this.model = new SpinnerNumberModel(integer1, integer2, integer3, integer4);
    setModel(this.model);
  }
  
  public int getIncrement() {
    return this.incValue;
  }
  
  public void set(int paramInt) {
    paramInt = (paramInt > this.maxValue) ? this.maxValue : ((paramInt < this.minValue) ? this.minValue : paramInt);
    this.model.setValue(Integer.valueOf(paramInt));
  }
  
  public int get() {
    if (this.model.getValue() instanceof Integer) {
      Integer integer = (Integer)this.model.getValue();
      return integer.intValue();
    } 
    if (this.model.getValue() instanceof Double) {
      Double double_ = (Double)this.model.getValue();
      return (int)double_.doubleValue();
    } 
    if (this.model.getValue() instanceof Float) {
      Float float_ = (Float)this.model.getValue();
      return (int)float_.floatValue();
    } 
    return 0;
  }
}
