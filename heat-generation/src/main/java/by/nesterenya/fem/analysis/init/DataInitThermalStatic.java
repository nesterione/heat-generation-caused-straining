package by.nesterenya.fem.analysis.init;

public class DataInitThermalStatic implements IDataInit {
 
  private double initialThemperature;
  
  public DataInitThermalStatic(double initialThemperature) {
    setInitialThemperature(initialThemperature);
  }
  
  public double getInitialThemperature() {
    return initialThemperature;
  }
  
  public void setInitialThemperature(double initialThemperature) {
    this.initialThemperature = initialThemperature;
  }
}
