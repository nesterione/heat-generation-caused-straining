package by.nesterenya.fem.mesh;

import java.util.List;
import java.util.Map;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.material.Material;

public interface Mesh {

	public Map<Integer, Material> getMaterial();
	public Map<String, Boundary> getBoundaries();
	public List<Node> getNodes();
	public List<Element> getElements();
}
