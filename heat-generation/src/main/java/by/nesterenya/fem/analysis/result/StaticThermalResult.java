package by.nesterenya.fem.analysis.result;

public class StaticThermalResult implements Result{
  
	private double T[];

	public double[] getT() {
		return T;
	}

	public void setT(double t[]) {
		T = t;
	}
	
	public StaticThermalResult(double T[]) {
		setT(T);
	}
}
