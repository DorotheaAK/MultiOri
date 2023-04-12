package steerabletools;

import additionaluserinterface.Settings;
import ij.IJ;

public class SettingsMDA extends Settings {
  public SettingsMDA() {
    super("MultiscaleDirectionAnalysis", IJ.getDirectory("plugins") + "MultiscaleDirectionAnalysis.txt");
  }
}
