package by.nesterenya.fem.analysis.result;

/**
 * Deformation of 3d element like of thetraid
 * keep three components of deformation by X,Y and Z
 * 
 * It have a each node of element
 */
public class Deformation {
	
	private double x;
	private double y;
	private double z;
	
	public Deformation(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

}
