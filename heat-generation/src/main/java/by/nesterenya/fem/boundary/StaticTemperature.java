package by.nesterenya.fem.boundary;

public class StaticTemperature implements Load {
  private double temperature;
  private Boundary boundary;
  
  public StaticTemperature(double tempereture, Boundary boundary) {
    this.setTemperature(tempereture);
    this.setBoundary(boundary); 
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public Boundary getBoundary() {
    return boundary;
  }

  public void setBoundary(Boundary boundary) {
    this.boundary = boundary;
  }
  
  @Override
  public String toString() {
    return boundary.getName()+ ": температура " + temperature + " К";
  }
}
