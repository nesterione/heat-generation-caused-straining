package by.nesterenya.fem.analysis.result;

public class StrainEnergy {
	private double value;
	
	public StrainEnergy(double value) {
		setValue(value);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
