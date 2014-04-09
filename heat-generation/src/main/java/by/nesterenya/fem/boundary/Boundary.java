package by.nesterenya.fem.boundary;

import java.util.List;

import by.nesterenya.fem.element.INode;

public class Boundary {
  private String name;
  private List<INode> nodes;
  private double square;
  
  public Boundary(String name, List<INode> nodes, double square) {
    this.setName(name);
    this.setNodes(nodes);
    this.square = square;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<INode> getNodes() {
    return nodes;
  }

  public void setNodes(List<INode> nodes) {
    this.nodes = nodes;
  }
  
  public double getSquare() {
    return square;
  }
}
