package by.nesterenya.fem.analysis.result;

//TODO подумать о замене массивов на коллекции
public class StaticDeformationResult implements IResult {

	private DeformationInNode[] deformationInNode;
	private Deformation[] deformations;
	private Strain[] strains;
	private StrainEnergy[] strainEnergy;
	
	//TODO: подумать, может результат хранить для каждого элемента
	
	public StaticDeformationResult(Deformation[] deformations, Strain[] strains, StrainEnergy[] strainEnergy) {
		setDeformations(deformations);
		setStrains(strains);
		setStrainEnergy(strainEnergy);
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

	public StrainEnergy[] getStrainEnergy() {
		return strainEnergy;
	}

	public void setStrainEnergy(StrainEnergy[] strainEnergy) {
		this.strainEnergy = strainEnergy;
	}
}
