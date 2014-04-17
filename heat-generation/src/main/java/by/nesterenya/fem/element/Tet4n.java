package by.nesterenya.fem.element;

import static by.nesterenya.fem.solver.MMath.DET;

import java.util.Map;

import by.nesterenya.fem.element.Node.Axis;
import by.nesterenya.fem.element.material.IMaterial;

public class Tet4n implements Element {

  private final static int NODE_COUNT = 4;

  //TODO хранить не номер материала а ссылку, может быть, подумать
  private int numberMaterial;
  private Map<Integer, IMaterial> materials;
  private Node nodes[];
  private int globalIndex;
  
  public Tet4n(int globalIndex, int materialId, Node nodes[], Map<Integer, IMaterial> materials, int numberMaterial) throws Exception {
    if(nodes.length!=NODE_COUNT)
      throw new Exception("При создании элемента, передано недостаточное количество узлов");
    
    this.globalIndex = globalIndex;
    this.nodes = nodes;
    this.materials = materials;
    this.numberMaterial = numberMaterial;
  }

  @Override
  public IMaterial getMatherial() {
    return materials.get(numberMaterial);
  }

  @Override
  public Node getNode(int number) throws Exception {
    if(number <0||number>(NODE_COUNT-1))
      throw new Exception("Недопустимый номер узла");
    return nodes[number];
  }

@Override
public double getVolume() {
	
	double[][] md = new double[4][4];
	
	for(int i = 0; i < NODE_COUNT; i++) {
		md[i][0] = 1;
		md[i][1] = nodes[i].getPosition(Axis.X);
		md[i][2] = nodes[i].getPosition(Axis.Y);
		md[i][3] = nodes[i].getPosition(Axis.Z);
	}
	
	double Ve = Math.abs(DET(md)) / 6.0;
	
	return Ve;
  }

  @Override
  public int getGlobalIndex() {
	return globalIndex;
  }
  
}
