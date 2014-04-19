package by.nesterenya.fem.analysis;

import by.nesterenya.fem.analysis.result.DynamicStructuralResult;
import by.nesterenya.fem.analysis.result.StaticStructuralResult;

public class DynamicStructuralAnalysis extends StaticStructuralAlalysis{
	
	@Override
	public void solve() throws Exception {
		super.solve();
		StaticStructuralResult sr = getResult();
		
		DynamicStructuralResult dr = new DynamicStructuralResult(
				sr.getDeformations(), 
				sr.getStrains(), 
				sr.getStrainEnergies(), 
				sr.getTemperatures());
		
		//TODO
		dr.setEndTime(5);
		
		this.setResult(dr);
	}
	
	
}
