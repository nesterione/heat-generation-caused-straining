package by.nesterenya.fem.element;

/**
 * 
 * @author igor Клас реализующий трехмерный узел с тремя размерностями: X, Y, Z
 */
public class Node3d implements INode {
  final private static int DEMENTION = 3;

  private double x;
  private double y;
  private double z;

  /**
   * Конструктор для создания трехмерного узла, по значениям координат в узлах
   * 
   * @param x - Координата x
   * @param y - Кооридната y
   * @param z - Кооридната z
   */
  public Node3d(double x, double y, double z) {
    this.setX(x);
    this.setY(y);
    this.setZ(z);
  }



  @Override
  public int getDemention() {

    return DEMENTION;
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



  public double getValueOfDemention(Dim demention) throws Exception {
    switch (demention) {
      case X:
        return this.getX();
      case Y:
        return this.getY();
      case Z:
        return this.getZ();
      default:
        throw new Exception("Недопустимая размерность для трехменого элемента");
    }

  }

}
