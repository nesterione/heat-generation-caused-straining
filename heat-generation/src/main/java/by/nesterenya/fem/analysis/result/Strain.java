package by.nesterenya.fem.analysis.result;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

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

	public Strain(double ex, double ey, double ez, double y_xy,double y_yz, double y_xz) {
		setEx(ex);
		setEy(ey);
		setEz(ez);
		setYxy(y_xy);
		setYxz(y_xz);
		setYyz(y_yz);
	}
	
	public double evalTotalStrain() {
		
		//Maximum principal
		double e1_max = (ex + ey)/2 + sqrt( pow((ex+ey)/2,2) + pow(y_xy/2,2));
		double e2_max = (ey + ez)/2 + sqrt( pow((ey+ez)/2,2) + pow(y_yz/2,2));
		double e3_max = (ex + ez)/2 + sqrt( pow((ex+ez)/2,2) + pow(y_xz/2,2));
		
		//Minimum principal
		double e1_min = (ex + ey)/2 - sqrt( pow((ex+ey)/2,2) + pow(y_xy/2,2));
		double e2_min = (ey + ez)/2 - sqrt( pow((ey+ez)/2,2) + pow(y_yz/2,2));
		double e3_min = (ex + ez)/2 - sqrt( pow((ex+ez)/2,2) + pow(y_xz/2,2));
		
		//Maximum Shear
		double ms_1 = e1_max-e1_min;
		double ms_2 = e2_max-e2_min;
		double ms_3 = e3_max-e3_min;
			
		return ms_1+ms_2+ms_3;
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
