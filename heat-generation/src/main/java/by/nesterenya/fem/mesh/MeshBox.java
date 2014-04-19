package by.nesterenya.fem.mesh;

import java.util.*;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.material.Material;

public class MeshBox implements Mesh {

	private List<Node> nodes; 
	private List<Element> elements; 
	private Map<Integer, Material> materials;
	private Map<String, Boundary> boundaries; 

	public MeshBox(List<Node> nodes, List<Element> elements, Map<Integer,Material> materials, Map<String, Boundary> boundaries) {
		
		this.nodes = nodes;
		this.elements = elements;
		this.materials = materials;
		this.boundaries = boundaries;
	}
	
	@Override
	public Map<Integer, Material> getMaterial() {
		return materials;
	}

	@Override
	public Map<String, Boundary> getBoundaries() {
		return boundaries;
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public List<Element> getElements() {
		return elements;
	}
}
