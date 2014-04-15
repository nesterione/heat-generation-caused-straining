package by.nesterenya.fem.solver;

import java.util.ArrayList;
import java.util.List;

import by.nesterenya.fem.analysis.Analysis;
import by.nesterenya.fem.analysis.StaticDeformationAlalysis;
import by.nesterenya.fem.analysis.result.Deformation;
import by.nesterenya.fem.analysis.result.DeformationInNode;
import by.nesterenya.fem.analysis.result.StaticDeformationResult;
import by.nesterenya.fem.analysis.result.Strain;
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
		
		
		//NOTE переделал по новому зенкевичу
		double a = 1 - locMy;
		double b = (1-2*locMy)/2.0;
		double c = locMy;
		
		double[][] E = { 
			{ a, c, c, 0, 0, 0 }, 
			{ c, a, c, 0, 0, 0 },
			{ c, c, a, 0, 0, 0 }, 
			{ 0, 0, 0, b, 0, 0 },
			{ 0, 0, 0, 0, b, 0 }, 
			{ 0, 0, 0, 0, 0, b } };

		double kafE = locG/((1+locMy)*(1-2.0*locMy));
		E = MMath.MUL(E, kafE);

		
		/*
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
*/
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

	public void setBoundaries(double[][] gK) throws Exception {

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

	private void addLoad(StaticEvenlyDistributedLoad distrubutedLoad) throws Exception {
		
		//Улучшенный вариант задания равномерно-распределеннной нагрузки
		
		double nodeLoad = distrubutedLoad.getLoad()
				/ distrubutedLoad.getSquare();
		
		nodeLoad = nodeLoad/(distrubutedLoad.getBoundary().getNodes().size());
		
		for (INode node : distrubutedLoad.getBoundary().getNodes()) {

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
		
		
		//Calculation of Deformation 
		R = MMath.gausSLAU(gK, R);
		
		//form deformation result
		int nodeSize = analisis.getMesh().getNodes().size();
		Deformation[] deformations = new Deformation[nodeSize];
		
		for(int i = 0; i< nodeSize; i++) {
			
			double defX = R[i*DEGREES_OF_FREEDOM];
			double defY = R[i*DEGREES_OF_FREEDOM + 1];
			double defZ = R[i*DEGREES_OF_FREEDOM + 2];
			
			deformations[i] = new Deformation(defX, defY, defZ);
		}
		
		//Calculation of Strain
		List<IElement> elements =  analisis.getMesh().getElements();
		
		Strain[] strains = new Strain[elements.size()];
		
		double[][] Q  = formMatrixQ();
		
		DeformationInNode[] defInNodes = new DeformationInNode[nodeSize];
		
		for (IElement element : elements) {
			
			// Формируем координатную матрицу для текущего элемента
			double[][] A = formMatrixA(element);		
			B = MMath.INV(A);
			
			double[][] QQ = MMath.MUL(Q, B); 
			
			// Tetr element has 4 nodes
			final int COUNT_NODES_IN_ELEMENT = 4;
			double[] curDeff = new double[DEGREES_OF_FREEDOM*COUNT_NODES_IN_ELEMENT];
			
			for(int i = 0; i < COUNT_NODES_IN_ELEMENT; i++) {
				INode node = element.getNode(i);
				int nodeNumber = analisis.getMesh().getNodes().indexOf(node);
				
				curDeff[i*DEGREES_OF_FREEDOM] = deformations[nodeNumber].getX();
				curDeff[i*DEGREES_OF_FREEDOM + 1] = deformations[nodeNumber].getY();
				curDeff[i*DEGREES_OF_FREEDOM + 2] = deformations[nodeNumber].getZ();
			}
		
			double[] e_e = MMath.MUL(QQ, curDeff);
			
			int elemNumber = analisis.getMesh().getElements().indexOf(element);
			
			strains[elemNumber] = new Strain(e_e[0], e_e[1], e_e[2], e_e[3], e_e[4], e_e[5]);
		
		
			//form values of ecvivalent of deformation in nodes
			for(int i =0;i< COUNT_NODES; i++) {
				int ind_sj = analisis.getMesh().getNodes()
						.lastIndexOf(element.getNode(i));
				
				double eInNode = e_e[0]+e_e[1]+e_e[2];
				
				//если значение в узле нету
				if(defInNodes[ind_sj] == null) {
					defInNodes[ind_sj] = new DeformationInNode(eInNode);
				} else {
					double nDef = (defInNodes[ind_sj].getValue() + eInNode)/2.0;
					defInNodes[ind_sj] = new DeformationInNode(nDef);
				}
				
			}
			
		}
		
		StaticDeformationResult result = new StaticDeformationResult(deformations, strains);
		result.setDeformationInNode(defInNodes);
		analisis.setResult(result);
		
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
