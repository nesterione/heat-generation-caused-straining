package by.nesterenya.fem.solver;

import by.nesterenya.fem.analysis.Result;
import by.nesterenya.fem.analysis.ThermalStaticAnalisis;
import by.nesterenya.fem.boundary.ILoad;
import by.nesterenya.fem.boundary.StaticTemperature;
import by.nesterenya.fem.element.IElement;
import by.nesterenya.fem.element.INode;
import by.nesterenya.fem.element.INode.Dim;
import by.nesterenya.fem.element.material.Material;

/**
 * Solver of static thermal problem
 * 
 */
public class ThermalStaticSolver {

	/**
	 * Soleve static thermal problem
	 * 
	 * 
	 * @param thermalAnalis
	 *            Object with properties of thermal problem
	 */
	public ThermalStaticSolver(ThermalStaticAnalisis thermalAnalis) {
		this.analis = thermalAnalis;
	}

	private final int RANK_LOKAL_H = 4;

	private ThermalStaticAnalisis analis;

	/**
	 * Vector of thermal loads
	 */
	protected double[] R;

	// double k;

	// double c;

	// double ro;

	// public double[][] T;

	private final int RANK_B_COL = 4;
	private final int RANK_B_ROW = 3;

	// Формирование координатной матрицы Mx1 для заданных координт
	private double[][] formMatrixA(double x1, double y1, double z1, double x2,
			double y2, double z2, double x3, double y3, double z3, double x4,
			double y4, double z4) {
		double retH[][] = new double[RANK_LOKAL_H][RANK_LOKAL_H];

		// Заполняем верхнюю половину матрицы
		retH[0][0] = 1;
		retH[0][1] = x1;
		retH[0][2] = y1;
		retH[0][3] = z1;
		retH[1][0] = 1;
		retH[1][1] = x2;
		retH[1][2] = y2;
		retH[1][3] = z2;
		retH[2][0] = 1;
		retH[2][1] = x3;
		retH[2][2] = y3;
		retH[2][3] = z3;
		retH[3][0] = 1;
		retH[3][1] = x4;
		retH[3][2] = y4;
		retH[3][3] = z4;

		return retH;
	}

	protected double[][] formMatrixB() {
		double[][] retB = new double[RANK_B_ROW][RANK_B_COL];

		retB[0][1] = 1;
		retB[1][2] = 1;
		retB[2][3] = 1;

		return retB;
	}

	// TODO удалить
	int co = 0;

	protected double[][] formLocalMxH(double x1, double y1, double z1,
			double x2, double y2, double z2, double x3, double y3, double z3,
			double x4, double y4, double z4, int elemNum) throws Exception {

		double[][] B = formMatrixB();
		double[][] Mx1 = formMatrixA(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4,
				y4, z4);

		// Вычисляем объем элемента
		double[][] md = { { 1, x1, y1, z1 }, { 1, x2, y2, z2 },
				{ 1, x3, y3, z4 }, { 1, x4, y4, z4 } };

		double Ve = Math.abs(MMath.DET(md)) / 6.0;

		double[][] Q = MMath.MUL(B, MMath.INV(Mx1));// TODO Warning

		double[][] H = MMath.MUL(MMath.MUL(MMath.T(Q), Q), Ve
				* ((Material) analis.getMesh().getElements().get(elemNum)
						.getMatherial()).getThermalConductivity());

		/*
		 * mesh.getMaterial(analis.getMesh().getElements().get(elemNum)).
		 * thermalConductivity][);
		 */
		return H;
	}

	/**
	 * Count nodes of element
	 */
	final int COUNT_NODES = 4;

	/**
	 * Count degrees of freedom of node
	 */
	final int DEGREES_OF_FREEDOM = 1;

	double[][] gH;

	public void formAllGlovalMatrix() throws Exception {
		// размерность глобальной матрицы равна количеству узлов
		// / <summary>
		// / Глобальная матрица теплопроводности
		// / </summary>
		gH = new double[analis.getMesh().getNodes().size()][analis.getMesh()
				.getNodes().size()];

		// Цикл обходяций все конечные элементы
		// TODO WARN
		for (int i = 0; i < analis.getMesh().getElements().size()/*
																 * getNodes().size
																 * ()
																 */; i++) {

			IElement element = analis.getMesh().getElements().get(i);
			INode node0 = element.getNode(0);
			INode node1 = element.getNode(1);
			INode node2 = element.getNode(2);
			INode node3 = element.getNode(3);

			// TODO убрать эти грабли
			double[][] H = formLocalMxH(node0.getValueOfDemention(Dim.X),
					node0.getValueOfDemention(Dim.Y),
					node0.getValueOfDemention(Dim.Z),
					node1.getValueOfDemention(Dim.X),
					node1.getValueOfDemention(Dim.Y),
					node1.getValueOfDemention(Dim.Z),
					node2.getValueOfDemention(Dim.X),
					node2.getValueOfDemention(Dim.Y),
					node2.getValueOfDemention(Dim.Z),
					node3.getValueOfDemention(Dim.X),
					node3.getValueOfDemention(Dim.Y),
					node3.getValueOfDemention(Dim.Z), i);

			//
			// Формирование глобальных матриц
			//
			for (int si = 0; si < COUNT_NODES; si++)
				for (int sj = 0; sj < COUNT_NODES; sj++)
					for (int ki = 0; ki < DEGREES_OF_FREEDOM; ki++)
						for (int kj = 0; kj < DEGREES_OF_FREEDOM; kj++) {
							IElement elem = analis.getMesh().getElements()
									.get(i);
							// TODO Возможна ошибка при нахождении индекса в
							// коллекции
							int ind_si = analis.getMesh().getNodes()
									.lastIndexOf(elem.getNode(si));
							int ind_sj = analis.getMesh().getNodes()
									.lastIndexOf(elem.getNode(sj));

							gH[ind_si * DEGREES_OF_FREEDOM + ki][ind_sj
									* DEGREES_OF_FREEDOM + kj] += H[si
									* DEGREES_OF_FREEDOM + ki][sj
									* DEGREES_OF_FREEDOM + kj];

						}
		}

		// Init vector of thermal loads
		 R = new double[gH.length];
	}

	public void Solve() {
		try {
			formAllGlovalMatrix();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		// Init Fixed thermal on boundaries
		for (ILoad boundary : analis.getLoads()) {
			for (INode node : boundary.getBoundary().getNodes()) {

				R[getGlobalNumberNode(node) * DEGREES_OF_FREEDOM] = ((StaticTemperature) boundary)
						.getTemperature();

				for (int ii = 0; ii < gH.length; ii++) {
					gH[getGlobalNumberNode(node) * DEGREES_OF_FREEDOM][ii] = 0;
				}
				
				gH[getGlobalNumberNode(node) * DEGREES_OF_FREEDOM][getGlobalNumberNode(node)
						* DEGREES_OF_FREEDOM] = 1;
			}
		}

		double[] T1 = null;
		try {
			T1 = MMath.gausSLAU_NCH(gH, R);
		} catch (Exception e) {
			e.printStackTrace();
		}

		analis.setResult(new Result(T1));
	}

	private int getGlobalNumberNode(INode node) {
		// TODO добавить обработку ошибок
		return analis.getMesh().getNodes().lastIndexOf(node);
	}

}
