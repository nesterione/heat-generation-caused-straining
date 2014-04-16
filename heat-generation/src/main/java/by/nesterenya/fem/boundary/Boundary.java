package by.nesterenya.fem.boundary;

import java.util.List;

import by.nesterenya.fem.element.Node;

public class Boundary {
  private String name;
  private List<Node> nodes;
  private double square;
  
  public Boundary(String name, List<Node> nodes, double square) {
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

  public List<Node> getNodes() {
    return nodes;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
  }
  
  public double getSquare() {
    return square;
  }
}
