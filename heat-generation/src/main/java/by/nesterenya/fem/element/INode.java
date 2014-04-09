package by.nesterenya.fem.element;

/**
 * 
 * @author igor <br>
 *         Базовый интерфейс для узлов элемента
 */
public interface INode {

  public enum Dim {

    X(0), Y(1), Z(2);

    private final int id;

    Dim(int id) {
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
  double getValueOfDemention(Dim demention) throws Exception;

  /**
   * Узнать размерность узла
   * 
   * @return разменость узла
   */
  int getDemention();
}
