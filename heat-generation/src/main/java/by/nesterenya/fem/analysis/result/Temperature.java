package by.nesterenya.fem.analysis.result;

public class Temperature {
	private double value ;
	
	public Temperature(double value) {
		setValue(value);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
