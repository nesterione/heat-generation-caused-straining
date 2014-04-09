package by.nesterenya.fem.mesh;

import java.util.List;
import java.util.Map;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.IElement;
import by.nesterenya.fem.element.INode;
import by.nesterenya.fem.element.material.IMaterial;

public interface IMesh {

	public Map<Integer, IMaterial> getMaterial();

	//TODO продумать интерфейс для Boundary
	public Map<String, Boundary> getBoundaries();

	public List<INode> getNodes();

	public List<IElement> getElements();
}
