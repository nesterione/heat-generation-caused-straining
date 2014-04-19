package by.nesterenya.fem.boundary;

public class StaticEvenlyDistributedLoad implements Load {
	private double load;
	private Boundary boundary;

	public StaticEvenlyDistributedLoad(double load, Boundary boundary) {
		this.setLoad(load);
		this.setBoundary(boundary);
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}

	public Boundary getBoundary() {
		return boundary;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	public double getSquare() {
		return boundary.getSquare();
	}

	@Override
	public String toString() {
		return boundary.getName() + ": нагрузка " + load + " Па";
	}
}
