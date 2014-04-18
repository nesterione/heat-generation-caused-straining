package by.nesterenya.fem.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.nesterenya.fem.analysis.StaticDeformationAlalysis;
import by.nesterenya.fem.analysis.result.Deformation;
import by.nesterenya.fem.analysis.result.NodalStrain;
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
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class StaticDeformationSolver {
	
	/**
	 * Count nodes in element
	 */
	private final static int COUNT_NODES = 4;
	
	/**
	 * Degrees of freedom
	 * In this problem is three.Stress along x, y and z
	 */
	private final static int DEGREES_OF_FREEDOM = 3;
	
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
		for (ILoad load : analysis.getLoads()) {
			if (load instanceof StaticEvenlyDistributedLoad) {
				addLoad((StaticEvenlyDistributedLoad) load, R);
			}
		}

		// Fix support
		for (ILoad load : analysis.getLoads()) {
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
	
	private Map<Node, Deformation> evaluateDeformationResult(double[] result) {
		
		Map<Node, Deformation> deformations = new HashMap<>();
		int dodesCount = analysis.getMesh().getNodes().size();
		
		for(int i = 0; i< dodesCount; i++) {	
			double defX = result[i*DEGREES_OF_FREEDOM];
			double defY = result[i*DEGREES_OF_FREEDOM + 1];
			double defZ = result[i*DEGREES_OF_FREEDOM + 2];
					
			Deformation def = new Deformation(defX, defY, defZ);
					
			Node node = analysis.getMesh().getNodes().get(i);
			deformations.put(node, def);
		}
		
		return deformations;
	}
	
	private double[] evaluateElementsDeformations(Element element, Map<Node, Deformation> deformations) throws Exception {
		double[] currentDeformations = new double[COUNT_NODES*DEGREES_OF_FREEDOM];
		for(int i = 0; i < COUNT_NODES; i++) {
			
			Deformation deformation = deformations.get(element.getNode(i));
			currentDeformations[i*DEGREES_OF_FREEDOM] = deformation.getX();
			currentDeformations[i*DEGREES_OF_FREEDOM+1] = deformation.getY();
			currentDeformations[i*DEGREES_OF_FREEDOM+2] = deformation.getZ();
		}
		
		return currentDeformations;
	}
	
	private Map<Element, Strain> evaluateStrainResult(Map<Node, Deformation> deformations) throws Exception {
		
		Map<Element, Strain> strains = new HashMap<>();
		List<Element> elements = analysis.getMesh().getElements();
		
		for (Element element : elements) {
			
			double[][] B  = formMatrixB(element);
			double[] currentDeformations = evaluateElementsDeformations(element, deformations);
			double[] e = MUL(B, currentDeformations);
			Strain strain = new Strain(e[0], e[1], e[2], e[3], e[4], e[5]);
			strains.put(element, strain);
		}
		
		return strains;
	}
	
	/**
	 * form values of equivalent of strain in nodes
	 * TODO maybe to delete COUNT_NODES and other and move it into appropriate classes
	 * @throws Exception 
	 */
	private Map<Node, NodalStrain> evaluateNodalStrainResult(Map<Element, Strain> strains) throws Exception {
		
		Map<Node, NodalStrain> nodalStrains = new HashMap<>();
		
		for(Element element : analysis.getMesh().getElements()) {
			
			Strain strain = strains.get(element);
			for(int i =0;i< COUNT_NODES; i++) {
				
				Node node = element.getNode(i);
				
				if(nodalStrains.containsKey(node)) {
					NodalStrain nodalStrain = nodalStrains.get(node);
					double newVal = nodalStrain.getValue() + strain.evalTotalStrain()/2;
					nodalStrain.setValue(newVal);
				}
				
				nodalStrains.put(node, new NodalStrain(strain.evalTotalStrain()));
			}
		}
		
		return nodalStrains;
	}
	
	/**
	 *
	 * energyValue = 0.5 * Ve* E * e*e;
	 * or 0.5 * {res}^Т * [K] * {res}
	 *	
	 */
	private Map<Element, StrainEnergy> evaluateStrainEnergyResult(Map<Node, Deformation> deformations) throws Exception {
		
		List<Element> elements = analysis.getMesh().getElements();
		Map<Element, StrainEnergy> strainEnergies = new HashMap<>();
		
		for (Element element : elements) {
			double[] res = evaluateElementsDeformations(element, deformations);
			double[][] locK = formLocalK(element);
			
			double[] dd = MUL(res, locK);		
			double energy = MUL(dd,res)*0.5;
			
			strainEnergies.put(element, new StrainEnergy(energy));
		}
		
		return strainEnergies;
	}
	
	public void Solve() throws Exception {
		
		double[][] gK = formGlobalK();
		double[] R = new double[gK.length];
		setBoundaries(gK, R);
		
		//Calculation of Deformation 
		double[] result = gausSLAU(gK, R);
				
		Map<Node, Deformation> deformations = evaluateDeformationResult(result);
		Map<Element, Strain> strains = evaluateStrainResult(deformations);
		Map<Node, NodalStrain> nodalStrains = evaluateNodalStrainResult(strains);
		Map<Element, StrainEnergy> strainEnergies = evaluateStrainEnergyResult(deformations);
		
		List<Element> elements = analysis.getMesh().getElements();
		int nodeSize = analysis.getMesh().getNodes().size();
	
		//DeformationInNode[] defInNodes = new DeformationInNode[nodeSize];
		Temperature[] temperatures = new Temperature[nodeSize];
		
		for (Element element : elements) {
			
			Material material = (Material) element.getMatherial();

			//TODO loop
		    double energy = 1;
			
			//
			// Calculation Strain Temperature
			//
			double c = material.getSpecificHeatCapacity();
			double ro = material.getDensity();
			//TODO выяснить или нужент тут объем
			double Cv = c*ro/**Ve*/;
			
			double dT = energy/Cv;
			
			for(int i =0;i< COUNT_NODES; i++) {
				
				int ind_sj = element.getNode(i).getGlobalIndex();
				
				//если значение в узле нету
				if(temperatures[ind_sj] == null) {
					temperatures[ind_sj] = new Temperature(dT);
				} else {
					double nt = (temperatures[ind_sj].getValue() + dT)/2.0;
					temperatures[ind_sj] = new Temperature(nt);
				}
			}
			
		}
		
		StaticStructuralResult res = new StaticStructuralResult(deformations, strains, strainEnergies);
		res.setDeformationInNode(nodalStrains);
		res.setTemperatures(temperatures);
		analysis.setResult(res);
	}
}
