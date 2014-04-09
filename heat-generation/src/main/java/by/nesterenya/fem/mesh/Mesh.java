package by.nesterenya.fem.mesh;

import java.util.*;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.IElement;
import by.nesterenya.fem.element.INode;
import by.nesterenya.fem.element.material.IMaterial;

public class Mesh implements IMesh {

	// Members
	private List<INode> nodes; 
	private List<IElement> elements; 
	private Map<Integer, IMaterial> materials;
	private Map<String, Boundary> boundaries; 

	public Mesh(List<INode> nodes, List<IElement> elements, Map<Integer,IMaterial> materials, Map<String, Boundary> boundaries) {
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
	public List<INode> getNodes() {
		return nodes;
	}

	@Override
	public List<IElement> getElements() {
		return elements;
	}
}
