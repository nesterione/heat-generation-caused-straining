package by.nesterenya.fem.analysis.result;

import java.util.Collection;
import java.util.Map;

import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;

//TODO Возможно следует разместить тип результата в отдельные объекты
public class StaticStructuralResult implements Result {
	
	public StaticStructuralResult(Map<Node, Deformation> deformations, Map<Element, Strain> strains, Map<Element, StrainEnergy> strainEnergies, Map<Node, Temperature> temperatures ) {
		setDeformations(deformations);
		setStrains(strains);
		setStrainEnergies(strainEnergies);
		setTemperatures(temperatures);
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
	
	public Map<Node, Deformation> getDeformations() {
		return deformations;
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
	
	public Map<Element, Strain> getStrains() {
		return strains;
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
	
	public double getNodalStrain(Node node) {
		return nodalStrains.get(node).getValue();
	}

	public void setNodalStrain(Map<Node, NodalStrain> nodalStrains) {
		this.nodalStrains = nodalStrains;
	}
	
	//
	// Strain Energy
	//
	Map<Element, StrainEnergy> strainEnergies;
	private Double minStrainEnergy = null;
	private Double maxStrainEnergy = null;
	
	public double getMinStrainEnergy() {
		
		if(minStrainEnergy==null) {
			
			Collection<StrainEnergy> strs = strainEnergies.values();
			double min = Double.MAX_VALUE;
			
			for(StrainEnergy str : strs ) {
				double current = str.getValue();
				if(min> current) {
					min = current;
				}
			}
			
			minStrainEnergy = min;
		}
		
		return minStrainEnergy;
	}
	
	public double getMaxStrainEnergy() {
		
		if(maxStrainEnergy==null) {
			
			Collection<StrainEnergy> strs = strainEnergies.values();
			double max = Double.MIN_VALUE;
			
			for(StrainEnergy str : strs ) {
				double current = str.getValue();
				if(max < current) {
					max = current;
				}
			}
			maxStrainEnergy = max;
			//TODO убрать
			System.out.println("Strain Energy >" + maxStrainEnergy);
		}
		
		return maxStrainEnergy;
	}
	
	public StrainEnergy getStrainEnergy(Element element) {
		return strainEnergies.get(element);
	}
	
	public Map<Element, StrainEnergy> getStrainEnergies() {
		return strainEnergies;
	}

	public void setStrainEnergies(Map<Element, StrainEnergy> strainEnergies) {
		this.strainEnergies = strainEnergies;
	}
	
	//
	// Strain Temperature
	//
	private Map<Node, Temperature> temperatures;
	private Double minTemperature = null;
	private Double maxTemperature = null;
	
	public double getMinTemperature() {
		
		if(minTemperature==null) {
			
			Collection<Temperature> strs = temperatures.values();
			double min = Double.MAX_VALUE;
			
			for(Temperature str : strs ) {
				double current = str.getValue();
				if(min> current) {
					min = current;
				}
			}
			
			minTemperature = min;
		}
		
		return minTemperature;
	}
	
	public double getMaxTemperature() {
		
		if(maxTemperature==null) {
			
			Collection<Temperature> strs = temperatures.values();
			double max = Double.MIN_VALUE;
			
			for(Temperature str : strs ) {
				double current = str.getValue();
				if(max < current) {
					max = current;
				}
			}
			maxTemperature = max;
			//TODO убрать
			System.out.println("Temperature >" + maxTemperature);
		}
		
		return maxTemperature;
	}
	
	public Temperature getTemperature(Node node) {
		return temperatures.get(node);
	}
	
	public Map<Node, Temperature> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Map<Node, Temperature> temperatures) {
		this.temperatures = temperatures;
	}
}
