package by.nesterenya.fem.solver;

import java.util.List;
import java.util.Map;

import by.nesterenya.fem.analysis.StaticStructuralAlalysis;
import by.nesterenya.fem.analysis.result.Deformation;
import by.nesterenya.fem.analysis.result.NodalStrain;
import by.nesterenya.fem.analysis.result.StaticStructuralResult;
import by.nesterenya.fem.analysis.result.Strain;
import by.nesterenya.fem.analysis.result.StrainEnergy;
import by.nesterenya.fem.analysis.result.Temperature;
import by.nesterenya.fem.boundary.Load;
import by.nesterenya.fem.boundary.StaticEvenlyDistributedLoad;
import by.nesterenya.fem.boundary.Support;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.Node.Axis;
import by.nesterenya.fem.element.material.Material;
import static by.nesterenya.fem.solver.MMath.*;

//TODO refact this
public class StaticStructuralSolver {
	
	/**
	 * Count nodes in element
	 */
	private final static int COUNT_NODES = 4;
	
	/**
	 * Degrees of freedom
	 * In this problem is three.Stress along x, y and z
	 */
	private final static int DEGREES_OF_FREEDOM = 3;
	
	private StaticStructuralAlalysis analysis;

	public StaticStructuralSolver(StaticStructuralAlalysis staticStructuralAnalysis) {
		this.analysis = staticStructuralAnalysis;
	}

	/**
	 * Form cord matrix for current element
	 * @throws Exception 
	 * 
	 */
	public static double[][] formMatrixA(Element element) throws Exception {

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
	public static double[][] formMatrixQ() {

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
	public static double[][] formMatrixE(double G, double v) {

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

	public static double[][] formMatrixN(Element element) throws Exception {
		
		double[][] A = formMatrixA(element);		
		double[][] N = INV(A);
		
		return N;
	}
	
	public static double[][] formMatrixB(Element element) throws Exception {
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
	public static double[][] formLocalK(Element element) throws Exception {
		
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
	
	public double[][] formGlobalK() throws Exception {
		List<Element> elements = analysis.getMesh().getElements();
		int nodesCount = analysis.getMesh().getNodes().size();
		int dimentionGK = nodesCount * DEGREES_OF_FREEDOM;
		// Initialization Global Stiffness Matrix 
		double[][] gK = new double[dimentionGK][dimentionGK];
		
		// For each element do ...
		for (Element element : elements) {
			
			double[][] K = formLocalK(element);

			// Add current local matrix to global
			
			for (int si = 0; si < COUNT_NODES; si++)
				for (int sj = 0; sj < COUNT_NODES; sj++)
					for (int ki = 0; ki < DEGREES_OF_FREEDOM; ki++)
						for (int kj = 0; kj < DEGREES_OF_FREEDOM; kj++) {
							
							int gSi = element.getNode(si).getGlobalIndex();
							int gSj = element.getNode(sj).getGlobalIndex();
							int gI = gSi * DEGREES_OF_FREEDOM + ki;
							int gJ = gSj * DEGREES_OF_FREEDOM + kj;
							int locI = si * DEGREES_OF_FREEDOM + ki;
							int locJ = sj * DEGREES_OF_FREEDOM + kj;
							
							gK[gI][gJ] += K[locI][locJ];
						}
		}
		
		return gK;
	}

	public void setBoundaries(double[][] gK, double[] R) throws Exception {

		// It's important to Fix Support past adding all loads
		for (Load load : analysis.getLoads()) {
			if (load instanceof StaticEvenlyDistributedLoad) {
				addLoad((StaticEvenlyDistributedLoad) load, R);
			}
		}

		// Fix support
		for (Load load : analysis.getLoads()) {
			if (load instanceof Support) {
				fixNodes(gK, R, load.getBoundary().getNodes());
			}
		}
	}

	private void fixNodes(double[][] gK, double[] R, List<Node> fixedNodes) {
		for (Node node : fixedNodes) {
			
			int idx = node.getGlobalIndex();
			for (int j = 0; j < DEGREES_OF_FREEDOM; j++) {
				
				// global index in matrix gK
				int gIdx = idx * DEGREES_OF_FREEDOM + j;
				for (int k = 0; k < gK.length; k++) {
					gK[gIdx][k] = 0;
					gK[k][gIdx] = 0;
				}
				R[gIdx] = 0;
				gK[gIdx][gIdx] = 1;
			}
		}
	}

	private void addLoad(StaticEvenlyDistributedLoad distributedLoad, double[] R) throws Exception {
		
		//It's not best method to adding distributed load
		double loadOnSquare = distributedLoad.getLoad()/distributedLoad.getSquare();
		List<Node> loadedNodes = distributedLoad.getBoundary().getNodes();
		double loadOnEachNode = loadOnSquare/(loadedNodes.size());
		
		//TODO still load added only on Z axis, it's bad, add direction in class StaticEvenlyDistributedLoad
		for (Node node : loadedNodes) {
			R[(node.getGlobalIndex() * DEGREES_OF_FREEDOM) + 2] += loadOnEachNode;
		}
	}
	
	public void Solve() throws Exception {
		
		double[][] gK = formGlobalK();
		double[] R = new double[gK.length];
		setBoundaries(gK, R);
		
		//Calculation of Deformation 
		double[] result = gausSLAU(gK, R);
				
		StaticResultEvaluator evaluator = new StaticResultEvaluator(analysis);
		
		Map<Node, Deformation> deformations = evaluator.evaluateDeformationResult(result);
		Map<Element, Strain> strains = evaluator.evaluateStrainResult(deformations);
		Map<Node, NodalStrain> nodalStrains = evaluator.evaluateNodalStrainResult(strains);
		Map<Element, StrainEnergy> strainEnergies = evaluator.evaluateStrainEnergyResult(deformations);
		Map<Node, Temperature> temperatures = evaluator.evaluateStrainTempereature(strainEnergies);
		
		StaticStructuralResult res = new StaticStructuralResult(deformations, strains, strainEnergies,temperatures);
		res.setNodalStrain(nodalStrains);
		
		analysis.setResult(res);
	}
}
