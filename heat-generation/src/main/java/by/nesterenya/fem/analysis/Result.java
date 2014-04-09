package by.nesterenya.fem.analysis;

public class Result implements IResult{
  
	private double T[];

	public double[] getT() {
		return T;
	}

	public void setT(double t[]) {
		T = t;
	}
	
	public Result(double T[]) {
		setT(T);
	}
  
}
