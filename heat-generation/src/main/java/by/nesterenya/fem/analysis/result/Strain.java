package by.nesterenya.fem.analysis.result;

/**
 * Strain of 3d element like of thetraid
 * keep six components of strain
 * 
 * It have each element
 */
public class Strain {
	private double ex;
	private double ey;
	private double ez;
	
	private double y_xy;
	private double y_xz;
	private double y_yz;
	//TODO проверить порядо параметров
	public Strain(double ex, double ey, double ez, double y_xy,double y_yz, double y_xz) {
		setEx(ex);
		setEy(ey);
		setEz(ez);
		setYxy(y_xy);
		setYxz(y_xz);
		setYyz(y_yz);
	}
	
	public double getEx() {
		return ex;
	}
	public void setEx(double ex) {
		this.ex = ex;
	}
	public double getEy() {
		return ey;
	}
	public void setEy(double ey) {
		this.ey = ey;
	}
	public double getEz() {
		return ez;
	}
	public void setEz(double ez) {
		this.ez = ez;
	}
	public double getYxy() {
		return y_xy;
	}
	public void setYxy(double y_xy) {
		this.y_xy = y_xy;
	}
	public double getYxz() {
		return y_xz;
	}
	public void setYxz(double y_xz) {
		this.y_xz = y_xz;
	}
	public double getYyz() {
		return y_yz;
	}
	public void setYyz(double y_yz) {
		this.y_yz = y_yz;
	}
	
	
	
	
}
