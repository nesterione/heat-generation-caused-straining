package by.nesterenya.fem.solver;

import java.util.List;

import by.nesterenya.fem.analysis.StaticDeformationAlalysis;
import by.nesterenya.fem.analysis.result.StaticDeformationResult;
import by.nesterenya.fem.boundary.ILoad;
import by.nesterenya.fem.boundary.StaticEvenlyDistributedLoad;
import by.nesterenya.fem.boundary.Support;
import by.nesterenya.fem.element.IElement;
import by.nesterenya.fem.element.INode;
import by.nesterenya.fem.element.INode.Dim;
import by.nesterenya.fem.element.material.Material;

public class StaticDeformationSolver {
	// TODO F = A*sin(w*t)
	private StaticDeformationAlalysis analisis;

	public StaticDeformationSolver(StaticDeformationAlalysis analisis) {
		this.analisis = analisis;
	}

	// private const int RANK_LOKAL_H = 4;

	// инвертированная координатная матрица
	double[][] B;

	/**
	 * Form cord matrix for current element
	 * @throws Exception 
	 * 
	 */
	public double[][] formMatrixA(IElement element) throws Exception {

		double[][] A = new double[12][12];

		INode node0 = element.getNode(0);
		INode node1 = element.getNode(1);
		INode node2 = element.getNode(2);
		INode node3 = element.getNode(3);
		
		int node_count = 4;
		
		double[] kX = new double[node_count];
		kX[0] = node0.getValueOfDemention(Dim.X);
		kX[1] = node1.getValueOfDemention(Dim.X);
		kX[2] = node2.getValueOfDemention(Dim.X);
		kX[3] = node3.getValueOfDemention(Dim.X);
		
		double[] kY = new double[node_count];
		kY[0] = node0.getValueOfDemention(Dim.Y);
		kY[1] = node1.getValueOfDemention(Dim.Y);
		kY[2] = node2.getValueOfDemention(Dim.Y);
		kY[3] = node3.getValueOfDemention(Dim.Y);
		
		double[] kZ = new double[node_count];
		kZ[0] = node0.getValueOfDemention(Dim.Z);
		kZ[1] = node1.getValueOfDemention(Dim.Z);
		kZ[2] = node2.getValueOfDemention(Dim.Z);
		kZ[3] = node3.getValueOfDemention(Dim.Z);

		for (int i = 0; i < node_count; i++) {
			A[i * 3][0] = 1.0f;
			A[i * 3][1] = kX[i];
			A[i * 3][2] = kY[i];
			A[i * 3][3] = kZ[i];

			A[i * 3 + 1][4] = 1.0f;
			A[i * 3 + 1][5] = kX[i];
			A[i * 3 + 1][6] = kY[i];
			A[i * 3 + 1][7] = kZ[i];

			A[i * 3 + 2][8] = 1.0f;
			A[i * 3 + 2][9] = kX[i];
			A[i * 3 + 2][10] = kY[i];
			A[i * 3 + 2][11] = kZ[i];
		}
		
		return A;
	}

	/**
	 * form matrix Q. In this problem the matrix havn't variable.It have only
	 * constants.
	 */
	public double[][] formMatrixQ() {

		double[][] Q = { 
				{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
				{ 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 } };

		return Q;
	}

	/**
	 * form Elastic matrix E [6x6]
	 * 
	 * @param locG
	 *            Yung modulus
	 * @param locMy
	 *            Puasson coefficient
	 * @return
	 */
	public double[][] formMatrixE(double locG, double locMy) {

		// This variables if for better readable code
		double a = locMy / (1 - locMy);
		double b = (1.0f - 2.0f * locMy) / (2.0f * (1.0f - locMy));

		double[][] E = { 
			{ 1, a, a, 0, 0, 0 }, 
			{ a, 1, a, 0, 0, 0 },
			{ a, a, 1, 0, 0, 0 }, 
			{ 0, 0, 0, b, 0, 0 },
			{ 0, 0, 0, 0, b, 0 }, 
			{ 0, 0, 0, 0, 0, b } };

		double kafE = (locG * (1 - locMy)) / ((1 + locMy) * (1 - 2.0f * locMy));
		E = MMath.MUL(E, kafE);

		return E;
	}

	
	
	// const int RANK_B_COL = 4;
	// const int RANK_B_ROW = 3;

	// protected double[,] formMatrixB()
	// {
	// double[,] retB = new double[RANK_B_ROW, RANK_B_COL];
	//
	// retB[0, 1] = 1;
	// retB[1, 2] = 1;
	// retB[2, 3] = 1;
	//
	// return retB;
	// }

	// / <summary>
	// / Вектор нагрузок
	// / </summary>
	private double[] R;

	double k;

	// / <summary>
	// / Количество узлов в элементе
	// / </summary>
	final int COUNT_NODES = 4;

	/**
	 * Degrees of freedom
	 * In this problem is three.Stress along x, y and z
	 */
	final int DEGREES_OF_FREEDOM = 3;

	private double calcVolumeOfElement(IElement element) throws Exception {
		INode node0 = element.getNode(0);
		INode node1 = element.getNode(1);
		INode node2 = element.getNode(2);
		INode node3 = element.getNode(3);
		
		double[][] md = {
				{ 1, 
				node0.getValueOfDemention(Dim.X),
				node0.getValueOfDemention(Dim.Y),
				node0.getValueOfDemention(Dim.Z) },
			    { 1, 
				node1.getValueOfDemention(Dim.X),
				node1.getValueOfDemention(Dim.Y),
				node1.getValueOfDemention(Dim.Z) },
				{ 1, 
				node2.getValueOfDemention(Dim.X),
				node2.getValueOfDemention(Dim.Y),
				node2.getValueOfDemention(Dim.Z) },
				{ 1, 
				node3.getValueOfDemention(Dim.X),
				node3.getValueOfDemention(Dim.Y),
				node3.getValueOfDemention(Dim.Z) } 
			};

		double Ve = Math.abs(MMath.DET(md)) / 6.0;
		return Ve;
	}
	
	public double[][] formGlobalK() throws Exception {
		List<IElement> elements = analisis.getMesh().getElements();
		List<INode> nodes = analisis.getMesh().getNodes();

		double[][] Q = formMatrixQ();

		// Initialization Global Stiffness Matrix 
		double[][] gK = new double[nodes.size() * DEGREES_OF_FREEDOM][nodes.size() * DEGREES_OF_FREEDOM];
		
		
		R = new double[gK.length];

		// For each element do ...
		for (IElement element : elements) {
			
			Material material = (Material) element.getMatherial();

			// TODO WARM в параметрах
			double[][] curE = formMatrixE(material.getElasticModulus(),
					material.getPoissonsRatio());

			// Формируем координатную матрицу для текущего элемента
			double[][] A = formMatrixA(element);
					
			B = MMath.INV(A);

			double Ve = calcVolumeOfElement(element);
			
			// Формирование локальной матрицы жесткости
			double[][] K;
		    K = MMath.MUL(MMath.T(B), MMath.T(Q));
			K = MMath.MUL(K, curE);
			K = MMath.MUL(K, Q);
			K = MMath.MUL(K, B);
			K = MMath.MUL(K, Ve);

			// Записуем текущую локальную матрицу в глобальную
			for (int si = 0; si < COUNT_NODES; si++)
				for (int sj = 0; sj < COUNT_NODES; sj++)
					for (int ki = 0; ki < DEGREES_OF_FREEDOM; ki++)
						for (int kj = 0; kj < DEGREES_OF_FREEDOM; kj++) {

							// TODO Возможна ошибка при нахождении индекса в
							// коллекции
							int ind_si = analisis.getMesh().getNodes()
									.lastIndexOf(element.getNode(si));
							int ind_sj = analisis.getMesh().getNodes()
									.lastIndexOf(element.getNode(sj));

							gK[ind_si * DEGREES_OF_FREEDOM + ki][ind_sj
									* DEGREES_OF_FREEDOM + kj] += K[si
									* DEGREES_OF_FREEDOM + ki][sj
									* DEGREES_OF_FREEDOM + kj];

							// gK[elements[i].uz[si] * 3 + ki,
							// elements[i].uz[sj] * 3 + kj] += K[si * 3 + ki, sj
							// *
							// 3 + kj];
						}
		}
		
		return gK;
	}

	public void setBoundaries(double[][] gK) {

		// Важно чтобы фиксация происходила после указания нагрузок
		for (ILoad load : analisis.getLoads()) {
			if (load instanceof StaticEvenlyDistributedLoad) {
				addLoad((StaticEvenlyDistributedLoad) load);
			}
		}

		// Фиксируем грани
		for (ILoad load : analisis.getLoads()) {
			if (load instanceof Support) {
				fixNodes(gK, load.getBoundary().getNodes());
			}
		}
	}

	private void fixNodes(double[][] gK, List<INode> fixedNodes) {
		for (INode node : fixedNodes) {
			int numberFixedNode = analisis.getMesh().getNodes()
					.lastIndexOf(node);

			for (int j = 0; j < 3; j++) {
				for (int kk = 0; kk < gK.length; kk++) {
					gK[numberFixedNode * 3 + j][kk] = 0;
					gK[kk][numberFixedNode * 3 + j] = 0;
				}

				R[numberFixedNode * 3 + j] = 0;
				gK[numberFixedNode * 3 + j][numberFixedNode * 3 + j] = 1;
			}
		}
	}

	private void addLoad(StaticEvenlyDistributedLoad distrubutedLoad) {

		double nodeLoad = distrubutedLoad.getLoad()
				/ distrubutedLoad.getSquare();

		for (INode node : distrubutedLoad.getBoundary().getNodes()) {
			// TODO Сделать чтобы при добавлении температуры значения доже
			// прибавлялиьс

			// Значение должно прибовлятся к существующему, так как на некоторые
			// узлы уже может быть
			// оказана нагрузка
			R[(analisis.getMesh().getNodes().lastIndexOf(node) * DEGREES_OF_FREEDOM) + 2] += nodeLoad;
		}
	}

	//TODO Убрать из глобальных переменных R
	
	public void Solve() throws Exception {
		double[][] gK = formGlobalK();
		setBoundaries(gK);
		R = MMath.gausSLAU(gK, R);
		
		//form result
		int nodeSize = analisis.getMesh().getNodes().size();
		double[] X = new double[nodeSize];
		double[] Y = new double[nodeSize];
		double[] Z = new double[nodeSize];
		
		for(int i = 0; i< nodeSize; i++) {
			X[i] = R[i*DEGREES_OF_FREEDOM];
			Y[i] = R[i*DEGREES_OF_FREEDOM + 1];
			Z[i] = R[i*DEGREES_OF_FREEDOM + 2];
		}
		
		analisis.setResult(new StaticDeformationResult(X,Y,Z));
	}

	public double getResultX(int i) {

		return R[i * DEGREES_OF_FREEDOM];
	}

	public double getResultY(int i) {
		return R[i * DEGREES_OF_FREEDOM + 1];
	}

	public double getResultZ(int i) {
		return R[i * DEGREES_OF_FREEDOM + 2];
	}
}
