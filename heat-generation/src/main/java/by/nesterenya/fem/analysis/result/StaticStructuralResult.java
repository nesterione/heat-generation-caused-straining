package by.nesterenya.fem.analysis.result;

import java.util.Collection;
import java.util.Map;

import by.nesterenya.fem.element.Node;

//TODO подумать о замене массивов на коллекции
public class StaticStructuralResult implements Result {

	private Double minDeformation = null;
	private Double maxDeformation = null;
	
	private Map<Node, Deformation> deformations;
	
	private DeformationInNode[] deformationInNode;
	private Strain[] strains;
	private StrainEnergy[] strainEnergy;
	private Temperature[] temperatures;
	
	
	
	
	//TODO: подумать, может результат хранить для каждого элемента
	
	public StaticStructuralResult(Map<Node, Deformation> deformations, Strain[] strains, StrainEnergy[] strainEnergy) {
		setDeformations(deformations);
		setStrains(strains);
		setStrainEnergy(strainEnergy);
	}

	public Strain[] getStrains() {
		return strains;
	}

	public void setStrains(Strain[] strains) {
		this.strains = strains;
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

	/*public Map<Node, Deformation> getDoformations_test() {
		return doformations_test;
	}*/

	public Deformation getDeformation(Node node) {
		return deformations.get(node);
	}
	
	private double maxOfThree(double x, double y, double z) {
		if(x>y&&x>z) {
			return x;
		}
		if(y>x&&y>z) 
			return y;
		return z;
	}
	
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
	
	public void setDeformations(Map<Node, Deformation> deformations) {
		this.deformations = deformations;
	}
}
