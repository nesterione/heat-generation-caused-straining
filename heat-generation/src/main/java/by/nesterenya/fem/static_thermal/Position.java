package by.nesterenya.fem.static_thermal;

public class Position {
	private double zoom;
	private double move_x;
	private double move_y;
	private double angle_x;
	private double angle_y;
	private double angle_z;
	
	public Position() {
		setZoom(1);
		setMove_x(0);
		setMove_y(0);
		setAngle_x(0);
		setAngle_y(0);
		setAngle_z(0);
	}
	
	public double getZoom() {
		return zoom;
	}
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
	public double getMove_x() {
		return move_x;
	}
	public void setMove_x(double move_x) {
		this.move_x = move_x;
	}
	public double getMove_y() {
		return move_y;
	}
	public void setMove_y(double move_y) {
		this.move_y = move_y;
	}
	public double getAngle_x() {
		return angle_x;
	}
	public void setAngle_x(double angle_x) {
		this.angle_x = angle_x;
	}
	public double getAngle_y() {
		return angle_y;
	}
	public void setAngle_y(double angle_y) {
		this.angle_y = angle_y;
	}
	public double getAngle_z() {
		return angle_z;
	}
	public void setAngle_z(double angle_z) {
		this.angle_z = angle_z;
	}
	
	//TODO сделать обращение через свойства
	  public void addToZoom(double deltaZoom) {
	    zoom += deltaZoom;
	  }

	  public void addToMoveX(double deltaX) {
	    move_x += deltaX;
	  }

	  public void addToMoveY(double deltaY) {
	    move_y += deltaY;
	  }

	  public void addToAngleX(double deltaX) {
	    angle_x += deltaX;
	  }

	  public void addToAngleY(double deltaY) {
	    angle_y += deltaY;
	  }

	
}
