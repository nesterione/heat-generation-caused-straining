package by.nesterenya.fem.element;

/**
 * 
 * @author igor Клас реализующий трехмерный узел с тремя размерностями: X, Y, Z
 */
public class Node3d implements Node {
  final private static int DEMENTION = 3;

  private double x;
  private double y;
  private double z;
  private int globalIndex;

  public Node3d(int globalIndex, double x, double y, double z) {
	
	this.globalIndex = globalIndex;
    this.setX(x);
    this.setY(y);
    this.setZ(z);
  }

  @Override
  public int getDemention() {
	  
    return DEMENTION;
  }

  public double getPosition(Axis axis) {
	switch (axis) {
      case X:
        return this.getX();
      case Y:
        return this.getY();
      case Z:
        return this.getZ();
    }
	
	return Double.NaN;
  }
 
  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getZ() {
    return z;
  }

  public void setZ(double z) {
    this.z = z;
  }

  @Override
  public int getGlobalIndex() {
    return globalIndex;
  }
}
