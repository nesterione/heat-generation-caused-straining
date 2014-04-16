package by.nesterenya.fem.analysis.init;

public class InitDataStaticThermal implements InitData {
 
  private double initialThemperature;
  
  public InitDataStaticThermal(double initialThemperature) {
    setInitialThemperature(initialThemperature);
  }
  
  public double getInitialThemperature() {
    return initialThemperature;
  }
  
  public void setInitialThemperature(double initialThemperature) {
    this.initialThemperature = initialThemperature;
  }
}
