package by.nesterenya.fem.mesh;

import java.util.*;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.material.IMaterial;

public class Mesh implements IMesh {

	// Members
	private List<Node> nodes; 
	private List<Element> elements; 
	private Map<Integer, IMaterial> materials;
	private Map<String, Boundary> boundaries; 

	public Mesh(List<Node> nodes, List<Element> elements, Map<Integer,IMaterial> materials, Map<String, Boundary> boundaries) {
		//TODO добавить проверку параметров на null
		
		this.nodes = nodes;
		this.elements = elements;
		this.materials = materials;
		this.boundaries = boundaries;
	}
	
	@Override
	public Map<Integer, IMaterial> getMaterial() {
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
