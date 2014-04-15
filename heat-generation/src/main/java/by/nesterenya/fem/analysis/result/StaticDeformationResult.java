package by.nesterenya.fem.analysis.result;

//TODO подумать о замене массивов на коллекции
public class StaticDeformationResult implements IResult {

	private DeformationInNode[] deformationInNode;
	private Deformation[] deformations;
	private Strain[] strains;
	
	public StaticDeformationResult(Deformation[] deformations, Strain[] strains) {
		setDeformations(deformations);
		setStrains(strains);
	}

	public Deformation[] getDeformations() {
		return deformations;
	}

	public void setDeformations(Deformation[] deformations) {
		this.deformations = deformations;
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
}
