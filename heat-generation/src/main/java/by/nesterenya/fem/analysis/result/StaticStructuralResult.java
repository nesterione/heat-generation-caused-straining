package by.nesterenya.fem.analysis.result;

import java.util.Collection;
import java.util.Map;

import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;

import static java.lang.Math.*;

//TODO Возможно следует разместить тип результата в отдельные объекты
public class StaticStructuralResult implements Result {
	
	private StrainEnergy[] strainEnergy;
	private Temperature[] temperatures;
	
	public StaticStructuralResult(Map<Node, Deformation> deformations, Map<Element, Strain> strains, StrainEnergy[] strainEnergy) {
		setDeformations(deformations);
		setStrains(strains);
		setStrainEnergy(strainEnergy);
	}

	public StrainEnergy[] getStrainEnergy() {
		return strainEnergy;
	}

	public void setStrainEnergy(StrainEnergy[] strainEnergy) {
		this.strainEnergy = strainEnergy;
	}

	public Temperature[] getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Temperature[] temperatures) {
		this.temperatures = temperatures;
	}

	private double maxOfThree(double x, double y, double z) {
		if(x>y&&x>z) {
			return x;
		}
		if(y>x&&y>z) 
			return y;
		return z;
	}
	
	
	//
	// Deformations
	//
	private Map<Node, Deformation> deformations;
	private Double minDeformation = null;
	private Double maxDeformation = null;
	
	public double getMinDeformation() {
		
		if(minDeformation==null) {
			
			Collection<Deformation> defs = deformations.values();
			double min = Double.MAX_VALUE;
			
			for(Deformation def : defs ) {
				double current = maxOfThree(def.getX(), def.getY(), def.getZ());
				if(min> current) {
					min = current;
				}
			}
			
			minDeformation = min;
		}
		
		return minDeformation;
	}
	
	public double getMaxDeformation() {
		
		if(maxDeformation==null) {
			
			Collection<Deformation> defs = deformations.values();
			double max = Double.MIN_VALUE;
			
			for(Deformation def : defs ) {
				double current = maxOfThree(def.getX(), def.getY(), def.getZ());
				if(max < current) {
					max = current;
				}
			}
			maxDeformation = max;
			//TODO убрать
			//System.out.println(">" + maxDeformation);
		}
		
		return maxDeformation;
	}
	
	
	public Deformation getDeformation(Node node) {
		return deformations.get(node);
	}
	
	public void setDeformations(Map<Node, Deformation> deformations) {
		this.deformations = deformations;
	}
	
	//
	// Strains
	//
	private Map<Element, Strain> strains;
	private Double minStrain = null;
	private Double maxStrain = null;
	
	public double getMinStrain() {
		
		if(minStrain==null) {
			
			Collection<Strain> strs = strains.values();
			double min = Double.MAX_VALUE;
			
			for(Strain str : strs ) {
				double current = str.evalTotalStrain();
				if(min> current) {
					min = current;
				}
			}
			
			minStrain = min;
		}
		
		return minStrain;
	}
	
	public double getMaxStrain() {
		
		if(maxStrain==null) {
			
			Collection<Strain> strs = strains.values();
			double max = Double.MIN_VALUE;
			
			for(Strain str : strs ) {
				double current = str.evalTotalStrain();
				if(max < current) {
					max = current;
				}
			}
			maxStrain = max;
			//TODO убрать
			System.out.println("Strain >" + maxStrain);
		}
		
		return maxStrain;
	}
	
	public Strain getStrain(Element element) {
		return strains.get(element);
	}
	
	public double getTotalStrain(Element element) {
		Strain strain = strains.get(element);
		return strain.evalTotalStrain();
	}

	public void setStrains(Map<Element, Strain> strains) {
		this.strains = strains;
	}
	
	
	//
	// Nodal Strain
	// 
	private Map<Node, NodalStrain> nodalStrains;
	private Double minNodalStrain = null;
	private Double maxNodalStrain = null;
	
public double getMinNodalStrain() {
		
		if(minNodalStrain==null) {
			
			Collection<NodalStrain> strs = nodalStrains.values();
			double min = Double.MAX_VALUE;
			
			for(NodalStrain str : strs ) {
				double current = str.getValue();
				if(min> current) {
					min = current;
				}
			}
			
			minNodalStrain = min;
		}
		
		return minNodalStrain;
	}
	
	public double getMaxNodalStrain() {
		
		if(maxNodalStrain==null) {
			
			Collection<NodalStrain> strs = nodalStrains.values();
			double max = Double.MIN_VALUE;
			
			for(NodalStrain str : strs ) {
				double current = str.getValue();
				if(max < current) {
					max = current;
				}
			}
			maxNodalStrain = max;
			//TODO убрать
			System.out.println("Nodal Strain >" + maxNodalStrain);
		}
		
		return maxNodalStrain;
	}
	
	public double getNodadStrain(Node node) {
		return nodalStrains.get(node).getValue();
	}

	public void setDeformationInNode(Map<Node, NodalStrain> nodalStrains) {
		this.nodalStrains = nodalStrains;
	}
}
