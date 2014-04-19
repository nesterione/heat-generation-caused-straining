package by.nesterenya.fem.solver;

import static by.nesterenya.fem.solver.MMath.MUL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.nesterenya.fem.analysis.StaticStructuralAlalysis;
import by.nesterenya.fem.analysis.result.Deformation;
import by.nesterenya.fem.analysis.result.NodalStrain;
import by.nesterenya.fem.analysis.result.Strain;
import by.nesterenya.fem.analysis.result.StrainEnergy;
import by.nesterenya.fem.analysis.result.Temperature;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.material.Material;

public class StaticResultEvaluator {
	
	StaticStructuralAlalysis analysis;
	final static int DEGREES_OF_FREEDOM = 3;
	final static int COUNT_NODES = 4;
	
	public StaticResultEvaluator(StaticStructuralAlalysis analysis) {
		this.analysis = analysis;
	}
	
	public Map<Node, Deformation> evaluateDeformationResult(double[] result) {
		
		Map<Node, Deformation> deformations = new HashMap<>(analysis.getMesh().getNodes().size());
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
	
	public Map<Element, Strain> evaluateStrainResult(Map<Node, Deformation> deformations) throws Exception {
		
		Map<Element, Strain> strains = new HashMap<>(analysis.getMesh().getElements().size());
		List<Element> elements = analysis.getMesh().getElements();
		
		for (Element element : elements) {
			
			double[][] B  = StaticStructuralSolver.formMatrixB(element);
			double[] currentDeformations = evaluateElementsDeformations(element, deformations);
			double[] e = MUL(B, currentDeformations);
			Strain strain = new Strain(e[0], e[1], e[2], e[3], e[4], e[5]);
			strains.put(element, strain);
		}
		
		return strains;
	}
	
	/**
	 * form values of equivalent of strain in nodes
	 * @throws Exception 
	 */
	public Map<Node, NodalStrain> evaluateNodalStrainResult(Map<Element, Strain> strains) throws Exception {
		
		Map<Node, NodalStrain> nodalStrains = new HashMap<>(analysis.getMesh().getNodes().size());
		
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
	public Map<Element, StrainEnergy> evaluateStrainEnergyResult(Map<Node, Deformation> deformations) throws Exception {
		
		List<Element> elements = analysis.getMesh().getElements();
		Map<Element, StrainEnergy> strainEnergies = new HashMap<>(elements.size());
		
		for (Element element : elements) {
			double[] res = evaluateElementsDeformations(element, deformations);
			double[][] locK = StaticStructuralSolver.formLocalK(element);
			
			double[] dd = MUL(res, locK);		
			double energy = MUL(dd,res);
			// mult on 0.5
			
			strainEnergies.put(element, new StrainEnergy(energy));
		}
		
		return strainEnergies;
	}
	
	public Map<Node, Temperature> evaluateStrainTempereature(Map<Element, StrainEnergy> energies) throws Exception {
		
		Map<Node, Temperature> temperatures = new HashMap<>(analysis.getMesh().getNodes().size());
		List<Element> elements = analysis.getMesh().getElements();
		
		for (Element element : elements) {
			
			Material material = (Material) element.getMatherial();

			double energy = energies.get(element).getValue();
			double c = material.getSpecificHeatCapacity();
			double ro = material.getDensity();
			
			//Dou't need Volume of element because strain energy already for volume
			double Cv = c*ro;
			double dT = energy/Cv;
			
			for(int i =0;i< COUNT_NODES; i++) {
				
				Node node = element.getNode(i);
				
				if(temperatures.containsKey(node)) {
					Temperature t = temperatures.get(node);
					double newVal = Math.max(t.getValue(), dT);
					t.setValue(newVal);
				} else {
					temperatures.put(node, new Temperature(dT));
				}
			}
		}
		
		return temperatures;
	}
	
	
}
