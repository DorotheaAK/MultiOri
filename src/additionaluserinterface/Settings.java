package additionaluserinterface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Timer;

public class Settings {
  private String filename;
  
  private String project;
  
  private Vector items;
  
  private Properties props;
  
  public Settings(String paramString1, String paramString2) {
    this.filename = paramString2;
    this.project = paramString1;
    this.items = new Vector();
    this.props = new Properties();
  }
  
  public void record(String paramString1, JTextField paramJTextField, String paramString2) {
    Item item = new Item(paramString1, paramJTextField, paramString2);
    this.items.add(item);
  }
  
  public void record(String paramString1, JComboBox paramJComboBox, String paramString2) {
    Item item = new Item(paramString1, paramJComboBox, paramString2);
    this.items.add(item);
  }
  
  public void record(String paramString1, JSpinner paramJSpinner, String paramString2) {
    Item item = new Item(paramString1, paramJSpinner, paramString2);
    this.items.add(item);
  }
  
  public void record(String paramString, JToggleButton paramJToggleButton, boolean paramBoolean) {
    Item item = new Item(paramString, paramJToggleButton, paramBoolean ? "on" : "off");
    this.items.add(item);
  }
  
  public void record(String paramString, JCheckBox paramJCheckBox, boolean paramBoolean) {
    Item item = new Item(paramString, paramJCheckBox, paramBoolean ? "on" : "off");
    this.items.add(item);
  }
  
  public void record(String paramString1, JSlider paramJSlider, String paramString2) {
    Item item = new Item(paramString1, paramJSlider, paramString2);
    this.items.add(item);
  }
  
  public String loadValue(String paramString1, String paramString2) {
    String str = "";
    try {
      FileInputStream fileInputStream = new FileInputStream(this.filename);
      this.props.load(fileInputStream);
      str = this.props.getProperty(paramString1, "" + paramString2);
    } catch (Exception exception) {
      str = paramString2;
    } 
    return str;
  }
  
  public double loadValue(String paramString, double paramDouble) {
    double d = 0.0D;
    try {
      FileInputStream fileInputStream = new FileInputStream(this.filename);
      this.props.load(fileInputStream);
      String str = this.props.getProperty(paramString, "" + paramDouble);
      d = (new Double(str)).doubleValue();
    } catch (Exception exception) {
      d = paramDouble;
    } 
    return d;
  }
  
  public int loadValue(String paramString, int paramInt) {
    int i = 0;
    try {
      FileInputStream fileInputStream = new FileInputStream(this.filename);
      this.props.load(fileInputStream);
      String str = this.props.getProperty(paramString, "" + paramInt);
      i = (new Integer(str)).intValue();
    } catch (Exception exception) {
      i = paramInt;
    } 
    return i;
  }
  
  public boolean loadValue(String paramString, boolean paramBoolean) {
    boolean bool = false;
    try {
      FileInputStream fileInputStream = new FileInputStream(this.filename);
      this.props.load(fileInputStream);
      String str = this.props.getProperty(paramString, "" + paramBoolean);
      bool = (new Boolean(str)).booleanValue();
    } catch (Exception exception) {
      bool = paramBoolean;
    } 
    return bool;
  }
  
  public void storeValue(String paramString1, String paramString2) {
    this.props.setProperty(paramString1, paramString2);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(this.filename);
      this.props.store(fileOutputStream, this.project);
    } catch (Exception exception) {
      new Msg(this.project, "Impossible to store settings in (" + this.filename + ")");
    } 
  }
  
  public void storeValue(String paramString, double paramDouble) {
    this.props.setProperty(paramString, "" + paramDouble);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(this.filename);
      this.props.store(fileOutputStream, this.project);
    } catch (Exception exception) {
      new Msg(this.project, "Impossible to store settings in (" + this.filename + ")");
    } 
  }
  
  public void storeValue(String paramString, int paramInt) {
    this.props.setProperty(paramString, "" + paramInt);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(this.filename);
      this.props.store(fileOutputStream, this.project);
    } catch (Exception exception) {
      new Msg(this.project, "Impossible to store settings in (" + this.filename + ")");
    } 
  }
  
  public void storeValue(String paramString, boolean paramBoolean) {
    this.props.setProperty(paramString, "" + paramBoolean);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(this.filename);
      this.props.store(fileOutputStream, this.project);
    } catch (Exception exception) {
      new Msg(this.project, "Impossible to store settings in (" + this.filename + ")");
    } 
  }
  
  public void loadRecordedItems() {
    try {
      FileInputStream fileInputStream = new FileInputStream(this.filename);
      this.props.load(fileInputStream);
    } catch (Exception exception) {
      new Msg(this.project, "Loading default value. No settings file (" + this.filename + ")");
    } 
    for (byte b = 0; b < this.items.size(); b++) {
      Item item = (Item) this.items.get(b);
      String str = this.props.getProperty(item.key, item.defaultValue);
      if (item.component instanceof JTextField) {
        ((JTextField)item.component).setText(str);
      } else if (item.component instanceof JComboBox) {
        ((JComboBox)item.component).setSelectedItem(str);
      } else if (item.component instanceof JCheckBox) {
        ((JCheckBox)item.component).setSelected(str.equals("on"));
      } else if (item.component instanceof JToggleButton) {
        ((JToggleButton)item.component).setSelected(str.equals("on"));
      } else if (item.component instanceof JSpinner) {
        ((JSpinner)item.component).setValue(Double.valueOf((new Double(str)).doubleValue()));
      } else if (item.component instanceof JSlider) {
        ((JSlider)item.component).setValue((new Integer(str)).intValue());
      } 
    } 
  }
  
  public void storeRecordedItems() {
    for (byte b = 0; b < this.items.size(); b++) {
      Item item = (Item) this.items.get(b);
      if (item.component instanceof JTextField) {
        String str = ((JTextField)item.component).getText();
        this.props.setProperty(item.key, str);
      } else if (item.component instanceof JComboBox) {
        String str = (String)((JComboBox)item.component).getSelectedItem();
        this.props.setProperty(item.key, str);
      } else if (item.component instanceof JCheckBox) {
        String str = ((JCheckBox)item.component).isSelected() ? "on" : "off";
        this.props.setProperty(item.key, str);
      } else if (item.component instanceof JToggleButton) {
        String str = ((JToggleButton)item.component).isSelected() ? "on" : "off";
        this.props.setProperty(item.key, str);
      } else if (item.component instanceof JSpinner) {
        String str = "" + ((JSpinner)item.component).getValue();
        this.props.setProperty(item.key, str);
      } else if (item.component instanceof JSlider) {
        String str = "" + ((JSlider)item.component).getValue();
        this.props.setProperty(item.key, str);
      } 
    } 
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(this.filename);
      this.props.store(fileOutputStream, this.project);
    } catch (Exception exception) {
      new Msg(this.project, "Impossible to store settings in (" + this.filename + ")");
    } 
  }
  
  private class DelayListener implements ActionListener {
    private Settings.Msg msg;
    
    public DelayListener(Settings.Msg param1Msg) {
      this.msg = param1Msg;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      this.msg.dispose();
    }
  }
  
  private class Msg extends JFrame {
    public Msg(String param1String1, String param1String2) {
      super(param1String1);
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      Container container = getContentPane();
      container.setLayout(gridBagLayout);
      gridBagConstraints.weightx = 0.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.gridheight = 1;
      gridBagConstraints.insets = new Insets(10, 10, 10, 10);
      gridBagConstraints.anchor = 10;
      JLabel jLabel = new JLabel(param1String2);
      gridBagLayout.setConstraints(jLabel, gridBagConstraints);
      container.add(jLabel);
      setResizable(false);
      pack();
      setVisible(true);
      Dimension dimension = getToolkit().getScreenSize();
      Rectangle rectangle = getBounds();
      setLocation((dimension.width - rectangle.width) / 2, (dimension.height - rectangle.height) / 2);
      Timer timer = new Timer(1000, new Settings.DelayListener(this));
      timer.start();
    }
  }
  
  private class Item {
    public Object component;
    
    public String defaultValue;
    
    public String key;
    
    public Item(String param1String1, Object param1Object, String param1String2) {
      this.component = param1Object;
      this.defaultValue = param1String2;
      this.key = param1String1;
    }
  }
}