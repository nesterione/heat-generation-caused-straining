package by.nesterenya.fem.analysis.result;

import java.util.Map;

import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;

public class DynamicStructuralResult extends StaticStructuralResult{

	private double activeTime = 0;
	private double timeStep = 0.05;
	private double endTime = timeStep;
	
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	
	public synchronized void nextTime() {
		this.activeTime +=timeStep;
		if(activeTime > endTime) {
			this.activeTime = 0;
		}
	}
	
	public DynamicStructuralResult(Map<Node, Deformation> deformations,
			Map<Element, Strain> strains,
			Map<Element, StrainEnergy> strainEnergies,
			Map<Node, Temperature> temperatures) {
		super(deformations, strains, strainEnergies, temperatures);
	}

	@Override public Deformation getDeformation(Node node) {
		Deformation def = super.getDeformation(node);
		
		double pos = activeTime - (int)activeTime;
		if(((int)activeTime)%2 == 1) {
			pos = 1-pos;
		}
		
		double x = def.getX()*pos;
		double y = def.getY()*pos;
		double z = def.getZ()*pos;
		
		return new Deformation(x,y,z);
	};
	
	@Override public double getTotalStrain(Element element) 
	{
		double pos = activeTime - (int)activeTime;
		if(((int)activeTime)%2 == 1) {
			pos = 1-pos;
		}
		  
		return super.getTotalStrain(element)*pos;
	};
	
	@Override 
	public Temperature getTemperature(Node node) {
		
		double r = super.getTemperature(node).getValue();
		r = r*activeTime;
		return new Temperature(r);
	};
	
	@Override 
	public double getMaxTemperature() {
		
		double rez = super.getMaxTemperature();
		
		return rez*endTime;
	}
}
