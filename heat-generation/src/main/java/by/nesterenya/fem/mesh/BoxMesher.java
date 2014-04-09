package by.nesterenya.fem.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.nesterenya.fem.boundary.Boundary;
import by.nesterenya.fem.element.IElement;
import by.nesterenya.fem.element.INode;
import by.nesterenya.fem.element.Node3d;
import by.nesterenya.fem.element.Tet4n;
import by.nesterenya.fem.element.material.IMaterial;
import by.nesterenya.fem.primitives.Box;

public class BoxMesher implements IMesher {

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
	public IMesh formMesh() throws Exception {
		Map<String, Boundary> boundaries = initBoundariesMap(getBox());
		List<INode> nodes = new ArrayList<>();
		List<IElement> elements = new ArrayList<>();
		Map<Integer, IMaterial> materials = new HashMap<>();

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

		// цикл по OZ
		for (int k = 0; k < nCntOZ; k++, oz += stepOZ) {
			oy = 0;
			for (int j = 0; j < nCntOY; j++, oy += stepOY) {
				ox = 0;
				for (int i = 0; i < nCntOX; i++, ox += stepOX) {

					INode tempNode = new Node3d(ox, oy, oz);

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

		for (int iz = 0; iz < nCntOZ - 1; iz++)
			for (int iy = 0; iy < nCntOY - 1; iy++) {
				for (int ix = 0; ix < nCntOX - 1; ix++) {
					int i = nodesOnLayerCount * iz + iy * nCntOX + ix;
					int r = nodesOnLayerCount * iz + iy * nCntOX + (ix + 1);
					int p = nodesOnLayerCount * iz + (iy + 1) * nCntOX
							+ (ix + 1);
					int n = nodesOnLayerCount * iz + (iy + 1) * nCntOX + ix;
					int j = nodesOnLayerCount * (iz + 1) + iy * nCntOX + ix;
					int s = nodesOnLayerCount * (iz + 1) + iy * nCntOX
							+ (ix + 1);
					int m = nodesOnLayerCount * (iz + 1) + (iy + 1) * nCntOX
							+ (ix + 1);
					int k = nodesOnLayerCount * (iz + 1) + (iy + 1) * nCntOX
							+ ix;

					// Убрать длинные строки

					// knip
					elements.add(new Tet4n(0, new INode[] { nodes.get(n),
							nodes.get(k), nodes.get(m), nodes.get(j) },
							materials, 0));

					// ijkm
					elements.add(new Tet4n(0, new INode[] { nodes.get(m),
							nodes.get(p), nodes.get(n), nodes.get(i) },
							materials, 0));

					// kmpj
					elements.add(new Tet4n(0, new INode[] { nodes.get(n),
							nodes.get(i), nodes.get(j), nodes.get(m) },
							materials, 0));

					// rsim
					elements.add(new Tet4n(0, new INode[] { nodes.get(s),
							nodes.get(r), nodes.get(p), nodes.get(i) },
							materials, 0));

					// mpir
					elements.add(new Tet4n(0, new INode[] { nodes.get(i),
							nodes.get(j), nodes.get(m), nodes.get(s) },
							materials, 0));

					// rjim
					elements.add(new Tet4n(0, new INode[] { nodes.get(i),
							nodes.get(p), nodes.get(s), nodes.get(m) },
							materials, 0));
				}
			}

		// Creating BoxMesh
		Mesh mesh = new Mesh(nodes, elements, materials, boundaries);

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
				new Boundary(left, new ArrayList<INode>(), box.getWidth()
						* box.getHeight()));
		// reght x = xLenght;
		boundaries.put(right,
				new Boundary(right, new ArrayList<INode>(), box.getWidth()
						* box.getHeight()));

		boundaries.put(
				top,
				new Boundary(top, new ArrayList<INode>(), box.getLenght()
						* box.getWidth()));
		boundaries.put(bottom,
				new Boundary(bottom, new ArrayList<INode>(), box.getLenght()
						* box.getWidth()));
		boundaries.put(front,
				new Boundary(front, new ArrayList<INode>(), box.getLenght()
						* box.getHeight()));
		boundaries.put(back,
				new Boundary(back, new ArrayList<INode>(), box.getLenght()
						* box.getHeight()));

		return boundaries;
	}
}
