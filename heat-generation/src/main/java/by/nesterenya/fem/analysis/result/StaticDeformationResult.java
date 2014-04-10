package by.nesterenya.fem.analysis.result;

public class StaticDeformationResult implements IResult {

	private double[] X;
	private double[] Y;
	private double[] Z;

	public StaticDeformationResult(double[] X, double[] Y, double[] Z) {
		setX(X);
		setY(Y);
		setZ(Z);
	}

	public double[] getX() {
		return X;
	}

	public void setX(double[] x) {
		X = x;
	}

	public double[] getY() {
		return Y;
	}

	public void setY(double[] y) {
		Y = y;
	}

	public double[] getZ() {
		return Z;
	}

	public void setZ(double[] z) {
		Z = z;
	}
}
