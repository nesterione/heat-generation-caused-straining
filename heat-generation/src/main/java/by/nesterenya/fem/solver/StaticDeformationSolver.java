package by.nesterenya.fem.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.nesterenya.fem.analysis.StaticDeformationAlalysis;
import by.nesterenya.fem.analysis.result.Deformation;
import by.nesterenya.fem.analysis.result.DeformationInNode;
import by.nesterenya.fem.analysis.result.StaticStructuralResult;
import by.nesterenya.fem.analysis.result.Strain;
import by.nesterenya.fem.analysis.result.StrainEnergy;
import by.nesterenya.fem.analysis.result.Temperature;
import by.nesterenya.fem.boundary.ILoad;
import by.nesterenya.fem.boundary.StaticEvenlyDistributedLoad;
import by.nesterenya.fem.boundary.Support;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.Node.Axis;
import by.nesterenya.fem.element.material.Material;
import static by.nesterenya.fem.solver.MMath.*;

public class StaticDeformationSolver {
	
	private StaticDeformationAlalysis analysis;

	public StaticDeformationSolver(StaticDeformationAlalysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * Form cord matrix for current element
	 * @throws Exception 
	 * 
	 */
	private double[][] formMatrixA(Element element) throws Exception {

		double[][] A = new double[12][12];

		Node node0 = element.getNode(0);
		Node node1 = element.getNode(1);
		Node node2 = element.getNode(2);
		Node node3 = element.getNode(3);
		
		int node_count = 4;
		
		double[] kX = new double[node_count];
		kX[0] = node0.getPosition(Axis.X);
		kX[1] = node1.getPosition(Axis.X);
		kX[2] = node2.getPosition(Axis.X);
		kX[3] = node3.getPosition(Axis.X);
		
		double[] kY = new double[node_count];
		kY[0] = node0.getPosition(Axis.Y);
		kY[1] = node1.getPosition(Axis.Y);
		kY[2] = node2.getPosition(Axis.Y);
		kY[3] = node3.getPosition(Axis.Y);
		
		double[] kZ = new double[node_count];
		kZ[0] = node0.getPosition(Axis.Z);
		kZ[1] = node1.getPosition(Axis.Z);
		kZ[2] = node2.getPosition(Axis.Z);
		kZ[3] = node3.getPosition(Axis.Z);

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
	private double[][] formMatrixQ() {

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
	 * @param G
	 *            Yung modulus
	 * @param v
	 *            Puasson coefficient
	 * @return
	 */
	private double[][] formMatrixE(double G, double v) {

		double a = 1 - v;
		double b = (1-2*v)/2.0;
		double c = v;
		
		double[][] E = { 
			{ a, c, c, 0, 0, 0 }, 
			{ c, a, c, 0, 0, 0 },
			{ c, c, a, 0, 0, 0 }, 
			{ 0, 0, 0, b, 0, 0 },
			{ 0, 0, 0, 0, b, 0 }, 
			{ 0, 0, 0, 0, 0, b } };

		double kafE = G/((1+v)*(1-2.0*v));
		E = MUL(E, kafE);

		return E;
	}

	private double[][] formMatrixN(Element element) throws Exception {
		
		double[][] A = formMatrixA(element);		
		double[][] N = INV(A);
		
		return N;
	}
	
	private double[][] formMatrixB(Element element) throws Exception {
		double[][] N = formMatrixN(element);
		double[][] Q = formMatrixQ();
		
		double[][] B = MUL(Q,N);
		return B;
	}

	/**
	 * Local Matrix K
	 * Calc Ve * [N]T*[Q]T*[E]*[Q]*[N] 
	 * or
	 * Ve * [B]T*[E]*[N]
	 * 
	 * @param element
	 * @return
	 * @throws Exception
	 */
	private double[][] formLocalK(Element element) throws Exception {
		
		Material material = (Material) element.getMatherial();
		double G = material.getElasticModulus();
		double v = material.getPoissonsRatio();
		double[][] E = formMatrixE(G, v);
		
		double[][] B = formMatrixB(element);
		double Ve = element.getVolume();

		double[][] K;
		K = MUL(T(B), E);
		K = MUL(K, B);
		K = MUL(K, Ve);
		
		return K;
	}
	
	
	
	
	
	
	
	
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

	public double[][] formGlobalK() throws Exception {
		List<Element> elements = analysis.getMesh().getElements();
		int nodesCount = analysis.getMesh().getNodes().size();
		
		// Initialization Global Stiffness Matrix 
		double[][] gK = new double[nodesCount * DEGREES_OF_FREEDOM][nodesCount * DEGREES_OF_FREEDOM];
		
		R = new double[gK.length];

		// For each element do ...
		for (Element element : elements) {
			
			double[][] K = formLocalK(element);

			// Записуем текущую локальную матрицу в глобальную
			for (int si = 0; si < COUNT_NODES; si++)
				for (int sj = 0; sj < COUNT_NODES; sj++)
					for (int ki = 0; ki < DEGREES_OF_FREEDOM; ki++)
						for (int kj = 0; kj < DEGREES_OF_FREEDOM; kj++) {

							// TODO Возможна ошибка при нахождении индекса в
							// коллекции
							int ind_si = analysis.getMesh().getNodes()
									.lastIndexOf(element.getNode(si));
							int ind_sj = analysis.getMesh().getNodes()
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
		for (ILoad load : analysis.getLoads()) {
			if (load instanceof StaticEvenlyDistributedLoad) {
				addLoad((StaticEvenlyDistributedLoad) load);
			}
		}

		// Фиксируем грани
		for (ILoad load : analysis.getLoads()) {
			if (load instanceof Support) {
				fixNodes(gK, load.getBoundary().getNodes());
			}
		}
	}

	private void fixNodes(double[][] gK, List<Node> fixedNodes) {
		for (Node node : fixedNodes) {
			int numberFixedNode = analysis.getMesh().getNodes()
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
		
		for (Node node : distrubutedLoad.getBoundary().getNodes()) {

			// Значение должно прибовлятся к существующему, так как на некоторые
			// узлы уже может быть
			// оказана нагрузка
			R[(analysis.getMesh().getNodes().lastIndexOf(node) * DEGREES_OF_FREEDOM) + 2] += nodeLoad;
		}
	}

	//TODO Убрать из глобальных переменных R
	
	public void Solve() throws Exception {
		double[][] gK = formGlobalK();
		setBoundaries(gK);
		
		
		//Calculation of Deformation 
		R = gausSLAU(gK, R);
		
		//form deformation result
		int nodeSize = analysis.getMesh().getNodes().size();
		Deformation[] deformations = new Deformation[nodeSize];
		
		Map<Node, Deformation> deformation_test = new HashMap<>();
		
		for(int i = 0; i< nodeSize; i++) {
			
			double defX = R[i*DEGREES_OF_FREEDOM];
			double defY = R[i*DEGREES_OF_FREEDOM + 1];
			double defZ = R[i*DEGREES_OF_FREEDOM + 2];
			
			Deformation def = new Deformation(defX, defY, defZ);
			
			deformations[i] = def;
			
			//TODO : эксперементальная фича
			Node node = analysis.getMesh().getNodes().get(i);
			deformation_test.put(node, def);
		}
		
		//Calculation of Strain
		List<Element> elements =  analysis.getMesh().getElements();
		
		Strain[] strains = new Strain[elements.size()];
		StrainEnergy[] strainEnergy = new StrainEnergy[elements.size()];
		
		double[][] Q  = formMatrixQ();
		
		DeformationInNode[] defInNodes = new DeformationInNode[nodeSize];
		Temperature[] temperatures = new Temperature[nodeSize];
		
		for (Element element : elements) {
			
			// Формируем координатную матрицу для текущего элемента
			double[][] A = formMatrixA(element);		
			double[][] B = INV(A);
			
			double[][] QQ = MUL(Q, B); 
			
			// Tetr element has 4 nodes
			final int COUNT_NODES_IN_ELEMENT = 4;
			double[] curDeff = new double[DEGREES_OF_FREEDOM*COUNT_NODES_IN_ELEMENT];
			
			for(int i = 0; i < COUNT_NODES_IN_ELEMENT; i++) {
				Node node = element.getNode(i);
				int nodeNumber = analysis.getMesh().getNodes().indexOf(node);
				
				curDeff[i*DEGREES_OF_FREEDOM] = deformations[nodeNumber].getX();
				curDeff[i*DEGREES_OF_FREEDOM + 1] = deformations[nodeNumber].getY();
				curDeff[i*DEGREES_OF_FREEDOM + 2] = deformations[nodeNumber].getZ();
			}
		
			double[] e_e = MUL(QQ, curDeff);
			
			int elemNumber = analysis.getMesh().getElements().indexOf(element);
			
			strains[elemNumber] = new Strain(e_e[0], e_e[1], e_e[2], e_e[3], e_e[4], e_e[5]);
		
		
			//form values of ecvivalent of deformation in nodes
			for(int i =0;i< COUNT_NODES; i++) {
				int ind_sj = analysis.getMesh().getNodes()
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
			
			
			//
			// Calculation strain energy
			//
			
			//double energyValue = 0.5 * Ve* E * e*e;
			// или 0.5 * {res}^Т * [K] * {res}
			//TODO переделать обязательно
			int[] idx = new int[COUNT_NODES];
			
			idx[0] = analysis.getMesh().getNodes().indexOf(element.getNode(0));
			idx[1] = analysis.getMesh().getNodes().indexOf(element.getNode(1));
			idx[2] = analysis.getMesh().getNodes().indexOf(element.getNode(2));
			idx[3] = analysis.getMesh().getNodes().indexOf(element.getNode(3));
			
			double[] res = new double[COUNT_NODES * DEGREES_OF_FREEDOM];		
			for(int i =0;i< COUNT_NODES;i++) {
				res[i*DEGREES_OF_FREEDOM] = deformations[idx[i]].getX();
				res[i*DEGREES_OF_FREEDOM + 1] = deformations[idx[i]].getY();
				res[i*DEGREES_OF_FREEDOM + 2] = deformations[idx[i]].getZ();
			}
			
			
			
			//Формируем локальную матрицу жесткости
			Material material = (Material) element.getMatherial();

			// TODO WARM в параметрах
			double[][] curE = formMatrixE(material.getElasticModulus(),
					material.getPoissonsRatio());

			// Формируем координатную матрицу для текущего элемента
			double[][] AA = formMatrixA(element);
					
			double[][] BB = INV(AA);

			double Ve = element.getVolume();

			// Формирование локальной матрицы жесткости
			double[][] KK;
		    KK = MUL(T(BB), T(Q));
			KK = MUL(KK, curE);
			KK = MUL(KK, Q);
			KK = MUL(KK, BB);
			KK = MUL(KK, Ve);
			
			double[] dd = MUL(res, KK);
			
			double energy = MUL(dd,res)*0.5;
			
			strainEnergy[elemNumber] = new StrainEnergy(energy);
		
			
			//
			// Calculation Strain Temperature
			//
			double c = material.getSpecificHeatCapacity();
			double ro = material.getDensity();
			//TODO выяснить или нужент тут объем
			double Cv = c*ro/**Ve*/;
			
			double dT = energy/Cv;
			
			
			for(int i =0;i< COUNT_NODES; i++) {
				
				int ind_sj = analysis.getMesh().getNodes()
						.lastIndexOf(element.getNode(i));
				
				
				//если значение в узле нету
				if(temperatures[ind_sj] == null) {
					temperatures[ind_sj] = new Temperature(dT);
				} else {
					double nt = (temperatures[ind_sj].getValue() + dT)/2.0;
					temperatures[ind_sj] = new Temperature(nt);
				}
			}
		}
		//TODO убрать deformation_test
		StaticStructuralResult result = new StaticStructuralResult(deformation_test, strains, strainEnergy);
		result.setDeformationInNode(defInNodes);
		result.setTemperatures(temperatures);
		analysis.setResult(result);
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
