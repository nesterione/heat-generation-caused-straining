package by.nesterenya.fem.element;

public interface Node {

  public enum Axis {

    X(0), Y(1), Z(2);

    private final int id;

    Axis(int id) {
      this.id = id;
    }

    public int getValue() {
      return id;
    }
  };

  /**
   * 
   * @param demention Размерность пространства, например x = 0, y = 1, z = 2
   * @return координата в пространстве
   */
  double getPosition(Axis axis);

  /**
   * Узнать размерность узла
   * 
   * @return разменость узла
   */
  int getDemention();
  
  int getGlobalIndex();
}
