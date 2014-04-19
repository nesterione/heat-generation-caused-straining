package by.nesterenya.fem.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.Node3d;
import by.nesterenya.fem.element.Tet4n;
import by.nesterenya.fem.element.material.Material;
import by.nesterenya.fem.primitives.Box;
//TODO refactor
public class BoxMesher implements Mesher {

	private Box box;
	private int nodeCountOX;
	private int nodeCountOY;
	private int nodeCountOZ;

	public Box getBox() {
		return box;
	}

	protected void setBox(Box box) {
		this.box = box;
	}

	public int getNodeCountOX() {
		return nodeCountOX;
	}

	protected void setNodeCountOX(int nodeCountOX) {
		this.nodeCountOX = nodeCountOX;
	}

	public int getNodeCountOY() {
		return nodeCountOY;
	}

	protected void setNodeCountOY(int nodeCountOY) {
		this.nodeCountOY = nodeCountOY;
	}

	public int getNodeCountOZ() {
		return nodeCountOZ;
	}

	protected void setNodeCountOZ(int nodeCountOZ) {
		this.nodeCountOZ = nodeCountOZ;
	}

	public BoxMesher(Box box, int nodeCountOX, int nodeCountOY, int nodeCountOZ) {
		setBox(box);
		setNodeCountOX(nodeCountOX);
		setNodeCountOY(nodeCountOY);
		setNodeCountOZ(nodeCountOZ);
	}

	@Override
	public Mesh formMesh() throws Exception {
		Map<String, Boundary> boundaries = initBoundariesMap(getBox());
		List<Node> nodes = new ArrayList<>();
		List<Element> elements = new ArrayList<>();
		Map<Integer, Material> materials = new HashMap<>();

		int nCntOX = getNodeCountOX();
		int nCntOY = getNodeCountOY();
		int nCntOZ = getNodeCountOZ();
		/* формирование сетки для квадратной пластинки */
		// количество узлов на слое
		int nodesOnLayerCount = nCntOX * nCntOY;
		// общее количество узлов модели
		// int allNodesCount = nodesOnLayerCount * nCntOZ;

		// количество КВАДРАТНЫХ элементов на слое
		// int QuadElementOnLayerCount = (nCntOX - 1) * (nCntOY - 1);
		// общее количество ТЕТРАИДАЛЬНЫХ элементов модели
		// int countElem = QuadElementOnLayerCount * (nCntOZ - 1) * 6;

		// TODE граничные условия
		// bondary = new Bondary();

		double stepOX; // шаг по OX
		double stepOY; // шаг по OY
		double stepOZ; // шаг по OZ
		// double[] stepOZ = new double[6]; // шаг по ОZ

		// int[] mater = new int[6];
		// mater[0] = 0;
		// mater[1] = 1;
		// mater[2] = 1;
		// mater[3] = 1;
		// mater[4] = 0;
		// mater[5] = 0;

		stepOX = box.getLenght() / (nCntOX - 1);
		stepOY = box.getWidth() / (nCntOY - 1);
		stepOZ = box.getHeight() / (nCntOZ - 1);

		// double dtSrZ = (box.getHeight() - 2.0 * heightOwner) / 3.0;
		// stepOZ[0] = heightOwner;
		// stepOZ[1] = dtSrZ;
		// stepOZ[2] = dtSrZ;
		// stepOZ[3] = dtSrZ;
		// stepOZ[4] = heightOwner;

		double ox = 0, oy = 0, oz = 0;

		int counter = 0;
		// цикл по OZ
		for (int k = 0; k < nCntOZ; k++, oz += stepOZ) {
			oy = 0;
			for (int j = 0; j < nCntOY; j++, oy += stepOY) {
				ox = 0;
				for (int i = 0; i < nCntOX; i++, ox += stepOX) {
					
					Node tempNode = new Node3d(counter++,ox, oy, oz);

					nodes.add(tempNode);
					if (i == 0)
						boundaries.get(left).getNodes().add(tempNode);
					if (i == nCntOX - 1)
						boundaries.get(right).getNodes().add(tempNode);
					if (j == 0)
						boundaries.get(back).getNodes().add(tempNode);
					if (j == nCntOY - 1)
						boundaries.get(front).getNodes().add(tempNode);
					if (k == 0)
						boundaries.get(bottom).getNodes().add(tempNode);
					if (k == nCntOZ - 1)
						boundaries.get(top).getNodes().add(tempNode);
				}
			}
		}

		// i: cntSloi * iz + iy * nCntOX + ix
		// r: cntSloi * iz + iy * nCntOX + (ix+1)
		// p: cntSloi * iz + (iy+1) * nCntOX + (ix+1)
		// n: cntSloi * iz + (iy+1) * nCntOX + ix
		// j: cntSloi * (iz+1) + iy * nCntOX + ix
		// s: cntSloi * (iz+1) + iy * nCntOX + (ix+1)
		// m: cntSloi * (iz+1) + (iy+1) * nCntOX + (ix+1)
		// k: cntSloi * (iz+1) + (iy+1) * nCntOX + ix
        
		boolean isEven = true;
		boolean isEvenZ = true;
		boolean isEvenY = true;
		
		counter = 0;
		
		for (int iz = 0; iz < nCntOZ - 1; iz++) {
			for (int iy = 0; iy < nCntOY - 1; iy++) {
				for (int ix = 0; ix < nCntOX - 1; ix++) {

					int j = nodesOnLayerCount * iz + (iy    ) * nCntOX + (ix    );
					int i = nodesOnLayerCount * iz + (iy    ) * nCntOX + (ix + 1);
					int k = nodesOnLayerCount * iz + (iy + 1) * nCntOX + (ix    );
					int p = nodesOnLayerCount * iz + (iy + 1) * nCntOX + (ix + 1);
					
					int m = nodesOnLayerCount * (iz + 1) + (iy    ) * nCntOX + (ix    );
					int n = nodesOnLayerCount * (iz + 1) + (iy    ) * nCntOX + (ix + 1);
					int r = nodesOnLayerCount * (iz + 1) + (iy + 1) * nCntOX + (ix    );
					int s = nodesOnLayerCount * (iz + 1) + (iy + 1) * nCntOX + (ix + 1);
				
					if(isEven) {
						Node[] mkij =  new Node[] { nodes.get(m),nodes.get(k), nodes.get(i), nodes.get(j) };
						Node[] kmsr =  new Node[] { nodes.get(k),nodes.get(m), nodes.get(s), nodes.get(r) };
						Node[] sikp =  new Node[] { nodes.get(s),nodes.get(i), nodes.get(k), nodes.get(p) };
						Node[] ismn =  new Node[] { nodes.get(i),nodes.get(s), nodes.get(m), nodes.get(n) };
						Node[] smki =  new Node[] { nodes.get(s),nodes.get(m), nodes.get(k), nodes.get(i) };
					
						elements.add(new Tet4n(counter++,0, mkij, materials, 0));
						elements.add(new Tet4n(counter++,0, kmsr, materials, 0));
						elements.add(new Tet4n(counter++,0, sikp, materials, 0));
						elements.add(new Tet4n(counter++,0, ismn, materials, 0));
						elements.add(new Tet4n(counter++,0, smki, materials, 0));
					} else {
						Node[] rpjk =  new Node[] { nodes.get(r),nodes.get(p), nodes.get(j), nodes.get(k) };
						Node[] prns =  new Node[] { nodes.get(p),nodes.get(r), nodes.get(n), nodes.get(s) };
						Node[] njpi =  new Node[] { nodes.get(n),nodes.get(j), nodes.get(p), nodes.get(i) };
						Node[] jnrm =  new Node[] { nodes.get(j),nodes.get(n), nodes.get(r), nodes.get(m) };
						Node[] nrpj =  new Node[] { nodes.get(n),nodes.get(r), nodes.get(p), nodes.get(j) };
					
						elements.add(new Tet4n(counter++,0, rpjk, materials, 0));
						elements.add(new Tet4n(counter++,0, prns, materials, 0));
						elements.add(new Tet4n(counter++,0, njpi, materials, 0));
						elements.add(new Tet4n(counter++,0, jnrm, materials, 0));
						elements.add(new Tet4n(counter++,0, nrpj, materials, 0));
					}
					
					isEven = !isEven;
				}
				isEvenY = !isEvenY;
				isEven = isEvenY;
			}
			isEvenZ=!isEvenZ;
			isEvenY = isEvenZ;
			isEven = isEvenZ;
		}

		// Creating BoxMesh
		MeshBox mesh = new MeshBox(nodes, elements, materials, boundaries);

		return mesh;
	}

	// TODO продумать как лучьше хранить границы
	public final static String left = "левая";
	public final static String right = "правая";
	public final static String front = "передняя";
	public final static String back = "задняя";
	public final static String top = "верхняя";
	public final static String bottom = "нижняя";

	private Map<String, Boundary> initBoundariesMap(Box box) {
		// TODO Убрать возможные ошибки
		// TODO Сейчас площадь каждой грани задается статически с расчета что
		// фигура имеет форму
		// паралеллепипеда, переделать более универсально

		Map<String, Boundary> boundaries = new HashMap<String, Boundary>();

		// left x = 0
		boundaries.put(
				left,
				new Boundary(left, new ArrayList<Node>(), box.getWidth()
						* box.getHeight()));
		// reght x = xLenght;
		boundaries.put(right,
				new Boundary(right, new ArrayList<Node>(), box.getWidth()
						* box.getHeight()));

		boundaries.put(
				top,
				new Boundary(top, new ArrayList<Node>(), box.getLenght()
						* box.getWidth()));
		boundaries.put(bottom,
				new Boundary(bottom, new ArrayList<Node>(), box.getLenght()
						* box.getWidth()));
		boundaries.put(front,
				new Boundary(front, new ArrayList<Node>(), box.getLenght()
						* box.getHeight()));
		boundaries.put(back,
				new Boundary(back, new ArrayList<Node>(), box.getLenght()
						* box.getHeight()));

		return boundaries;
	}
}
