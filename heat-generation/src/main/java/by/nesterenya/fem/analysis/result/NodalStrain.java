package by.nesterenya.fem.analysis.result;

public class NodalStrain {
	private double value;
	
	public NodalStrain(double value) {
		setValue(value);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
