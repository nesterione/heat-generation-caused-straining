package by.nesterenya.fem.primitives;

public class Box {
  private double lenght;
  private double width;
  private double height;
  
  /**
   * 
   * @param lenght длина вдоль оси OX
   * @param width длина вдоль оси OY
   * @param height длина вдоль оси OZ
   */
  public Box(double lenght, double width, double height ) {
    this.setLenght(lenght);
    this.setWidth(width);
    this.setHeight(height);
  }

  /**
   * 
   * @return длина вдоль оси OX
   */
  public double getLenght() {
    return lenght;
  }

  public void setLenght(double lenght) {
    this.lenght = lenght;
  }

  /**
   * 
   * @return длина вдоль оси OY
   */
  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  /**
   * 
   * @param height длина вдоль оси OZ
   */
  public void setHeight(double height) {
    this.height = height;
  }
}
