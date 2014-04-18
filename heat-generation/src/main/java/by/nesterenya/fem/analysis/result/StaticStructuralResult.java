package by.nesterenya.fem.analysis.result;

import java.util.Collection;
import java.util.Map;

import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;

import static java.lang.Math.*;

//TODO Возможно следует разместить тип результата в отдельные объекты
public class StaticStructuralResult implements Result {
	
	private Double minDeformation = null;
	private Double maxDeformation = null;
	
	private Double minStrain = null;
	private Double maxStrain = null;
	
	private Map<Node, Deformation> deformations;
	private Map<Element, Strain> strains;
	
	private DeformationInNode[] deformationInNode;
	private StrainEnergy[] strainEnergy;
	private Temperature[] temperatures;
	
	public StaticStructuralResult(Map<Node, Deformation> deformations, Map<Element, Strain> strains, StrainEnergy[] strainEnergy) {
		setDeformations(deformations);
		setStrains(strains);
		setStrainEnergy(strainEnergy);
	}

	public DeformationInNode[] getDeformationInNode() {
		return deformationInNode;
	}

	public void setDeformationInNode(DeformationInNode[] deformationInNode) {
		this.deformationInNode = deformationInNode;
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
	private double evalTotalStrain(Strain strain) {

		double ex = strain.getEx();
		double ey = strain.getEy();
		double ez = strain.getEz();
		double yxy = strain.getYxy();
		double yyz= strain.getYyz();
		double yxz = strain.getYxz();
		
		//Maximum principal
		double e1_max = (ex + ey)/2 + sqrt( pow((ex+ey)/2,2) + pow(yxy/2,2));
		double e2_max = (ey + ez)/2 + sqrt( pow((ey+ez)/2,2) + pow(yyz/2,2));
		double e3_max = (ex + ez)/2 + sqrt( pow((ex+ez)/2,2) + pow(yxz/2,2));
		
		//Minimum principal
		double e1_min = (ex + ey)/2 - sqrt( pow((ex+ey)/2,2) + pow(yxy/2,2));
		double e2_min = (ey + ez)/2 - sqrt( pow((ey+ez)/2,2) + pow(yyz/2,2));
		double e3_min = (ex + ez)/2 - sqrt( pow((ex+ez)/2,2) + pow(yxz/2,2));
		
		//Maximum Shear
		double ms_1 = (e1_max-e1_min);
		double ms_2 = (e2_max-e2_min);
		double ms_3 = (e3_max-e3_min);
		
		return ms_1+ms_2+ms_3;
	}
	
	public double getMinStrain() {
		
		if(minStrain==null) {
			
			Collection<Strain> strs = strains.values();
			double min = Double.MAX_VALUE;
			
			for(Strain str : strs ) {
				double current = evalTotalStrain(str);
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
				double current = evalTotalStrain(str);
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
		return evalTotalStrain(strain);
	}

	public void setStrains(Map<Element, Strain> strains) {
		this.strains = strains;
	}
}
