package additionaluserinterface;

import java.awt.Dimension;
import java.awt.Point;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class NumericTable extends JFrame {
  private JTable table;
  
  private DefaultTableModel model;
  
  public NumericTable(String paramString, String[] paramArrayOfString, Dimension paramDimension) {
    super(paramString);
    setMinimumSize(paramDimension);
    setSize(paramDimension);
    setPreferredSize(paramDimension);
    JScrollPane jScrollPane = new JScrollPane(22, 30);
    this.model = new DefaultTableModel();
    this.table = new JTable(this.model);
    for (byte b = 0; b < paramArrayOfString.length; b++)
      this.model.addColumn(paramArrayOfString[b]); 
    this.table.setAutoResizeMode(0);
    jScrollPane.getViewport().add(this.table, (Object)null);
    add(jScrollPane);
  }
  
  public void setData(double[][] paramArrayOfdouble) {
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    String[] arrayOfString = new String[j];
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++)
        arrayOfString[b1] = "" + paramArrayOfdouble[b][b1]; 
      this.model.addRow((Object[])arrayOfString);
    } 
  }
  
  public void setData(double[][] paramArrayOfdouble, String[] paramArrayOfString) {
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    String[] arrayOfString = new String[j];
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++)
        arrayOfString[b1] = (new DecimalFormat(paramArrayOfString[b1])).format(paramArrayOfdouble[b][b1]); 
      this.model.addRow((Object[])arrayOfString);
    } 
  }
  
  public void setColumnSize(int[] paramArrayOfint) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      TableColumn tableColumn = this.table.getColumnModel().getColumn(b);
      tableColumn.setPreferredWidth(paramArrayOfint[b]);
    } 
  }
  
  public void show(int paramInt1, int paramInt2) {
    pack();
    setLocation(new Point(paramInt1, paramInt2));
    setVisible(true);
  }
}