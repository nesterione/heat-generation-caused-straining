package by.nesterenya.fem.element;

import java.util.Map;

import by.nesterenya.fem.element.material.IMaterial;

public class Tet4n implements IElement {

  private final static int NODE_COUNT = 4;

  private int numberMaterial;
  private Map<Integer, IMaterial> materials;
  
  private INode nodesNumber[];

  public Tet4n(int materialId, INode nodesNumber[], Map<Integer, IMaterial> materials, int numberMaterial) throws Exception {
    if(nodesNumber.length!=NODE_COUNT)
      throw new Exception("При создании элемента, передано недостаточное количество узлов");
    
    //TODO перейменовать
    this.nodesNumber = nodesNumber;
    this.materials = materials;
    this.numberMaterial = numberMaterial;
  }

  /*public INode getNode(int numberNode) {
    if(numberNode <0||numberNode>(NODE_COUNT-1))
      throw new Exception("Недопустимый номер узла");
    
    //return 
  }
  */
  @Override
  public IMaterial getMatherial() {
    return materials.get(numberMaterial);
  }

  @Override
  public INode getNode(int number) throws Exception {
    if(number <0||number>(NODE_COUNT-1))
      throw new Exception("Недопустимый номер узла");
    return nodesNumber[number];
  }

  //TODO ищменить название numberNode
}
