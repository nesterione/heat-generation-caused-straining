package by.nesterenya.fem.element;

import static by.nesterenya.fem.solver.MMath.DET;

import java.util.Map;

import by.nesterenya.fem.element.Node.Axis;
import by.nesterenya.fem.element.material.Material;

public class Tet4n implements Element {

  private final static int NODE_COUNT = 4;

  private int numberMaterial;
  private Map<Integer, Material> materials;
  private Node nodes[];
  private int globalIndex;
  
  public Tet4n(int globalIndex, int materialId, Node nodes[], Map<Integer, Material> materials, int numberMaterial) throws Exception {
    if(nodes.length!=NODE_COUNT)
      throw new Exception("При создании элемента, передано недостаточное количество узлов");
    
    this.globalIndex = globalIndex;
    this.nodes = nodes;
    this.materials = materials;
    this.numberMaterial = numberMaterial;
  }

  @Override
  public Material getMatherial() {
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
		
	double x1 = nodes[0].getPosition(Axis.X);
	double x2 = nodes[1].getPosition(Axis.X);
	double x3 = nodes[2].getPosition(Axis.X);
	double x4 = nodes[3].getPosition(Axis.X);
	
	double y1 = nodes[0].getPosition(Axis.Y);
	double y2 = nodes[1].getPosition(Axis.Y);
	double y3 = nodes[2].getPosition(Axis.Y);
	double y4 = nodes[3].getPosition(Axis.Y);
	
	double z1 = nodes[0].getPosition(Axis.Z);
	double z2 = nodes[1].getPosition(Axis.Z);
	double z3 = nodes[2].getPosition(Axis.Z);
	double z4 = nodes[3].getPosition(Axis.Z);
	
	double[][] md = {
			{x2-x1, y2-y1, z2-z1},
			{x3-x1, y3-y1, z3-z1},
			{x4-x1, y4-y1, z4-z1}
	};
	
	double det = DET(md);
	double Ve = Math.abs(det) / 6.0;
	return Ve;
  }

  @Override
  public int getGlobalIndex() {
	return globalIndex;
  }
  
}
