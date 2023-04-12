package additionaluserinterface;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JToolBar;

public class GridToolbar extends JToolBar {
  private GridBagLayout layout = new GridBagLayout();
  
  private GridBagConstraints constraint = new GridBagConstraints();
  
  private int defaultSpace = 3;
  
  public GridToolbar() {
    super("Control");
    setLayout(this.layout);
    setBorder(BorderFactory.createEtchedBorder());
    setFloatable(false);
  }
  
  public GridToolbar(boolean paramBoolean) {
    super("Control");
    setLayout(this.layout);
    if (paramBoolean)
      setBorder(BorderFactory.createEtchedBorder()); 
    setFloatable(false);
  }
  
  public GridToolbar(String paramString) {
    super(paramString);
    setLayout(this.layout);
    setBorder(BorderFactory.createTitledBorder(paramString));
    setFloatable(false);
  }
  
  public GridToolbar(int paramInt) {
    super("Control");
    setLayout(this.layout);
    this.defaultSpace = paramInt;
    setBorder(BorderFactory.createEtchedBorder());
    setFloatable(false);
  }
  
  public GridToolbar(boolean paramBoolean, int paramInt) {
    super("Control");
    setLayout(this.layout);
    this.defaultSpace = paramInt;
    if (paramBoolean)
      setBorder(BorderFactory.createEtchedBorder()); 
    setFloatable(false);
  }
  
  public GridToolbar(boolean paramBoolean1, int paramInt, boolean paramBoolean2) {
    super("Control");
    setLayout(this.layout);
    this.defaultSpace = paramInt;
    if (paramBoolean1)
      setBorder(BorderFactory.createEtchedBorder()); 
    setFloatable(paramBoolean2);
  }
  
  public GridToolbar(boolean paramBoolean1, boolean paramBoolean2) {
    super("Control");
    setLayout(this.layout);
    if (paramBoolean1)
      setBorder(BorderFactory.createEtchedBorder()); 
    setFloatable(paramBoolean2);
  }
  
  public GridToolbar(String paramString, boolean paramBoolean) {
    super(paramString);
    setLayout(this.layout);
    setBorder(BorderFactory.createTitledBorder(paramString));
    setFloatable(paramBoolean);
  }
  
  public GridToolbar(int paramInt, boolean paramBoolean) {
    super("Control");
    setLayout(this.layout);
    this.defaultSpace = paramInt;
    setBorder(BorderFactory.createEtchedBorder());
    setFloatable(paramBoolean);
  }
  
  public GridToolbar(String paramString, int paramInt) {
    super(paramString);
    setLayout(this.layout);
    this.defaultSpace = paramInt;
    setBorder(BorderFactory.createTitledBorder(paramString));
    setFloatable(false);
  }
  
  public GridToolbar(String paramString, int paramInt, boolean paramBoolean) {
    super(paramString);
    setLayout(this.layout);
    this.defaultSpace = paramInt;
    setBorder(BorderFactory.createTitledBorder(paramString));
    setFloatable(paramBoolean);
  }
  
  public void setSpace(int paramInt) {
    this.defaultSpace = paramInt;
  }
  
  public void place(int paramInt1, int paramInt2, JComponent paramJComponent) {
    place(paramInt1, paramInt2, 1, 1, this.defaultSpace, paramJComponent);
  }
  
  public void place(int paramInt1, int paramInt2, int paramInt3, JComponent paramJComponent) {
    place(paramInt1, paramInt2, 1, 1, paramInt3, paramJComponent);
  }
  
  public void place(int paramInt1, int paramInt2, int paramInt3, int paramInt4, JComponent paramJComponent) {
    place(paramInt1, paramInt2, paramInt3, paramInt4, this.defaultSpace, paramJComponent);
  }
  
  public void place(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, JComponent paramJComponent) {
    this.constraint.gridx = paramInt2;
    this.constraint.gridy = paramInt1;
    this.constraint.gridwidth = paramInt3;
    this.constraint.gridheight = paramInt4;
    this.constraint.anchor = 18;
    this.constraint.insets = new Insets(paramInt5, paramInt5, paramInt5, paramInt5);
    this.constraint.fill = 2;
    this.layout.setConstraints(paramJComponent, this.constraint);
    add(paramJComponent);
  }
}
