package by.nesterenya.fem.boundary;

public class Support implements Load {
	private Boundary boundary;

	public Support(Boundary boundary) {
		this.setBoundary(boundary);
	}

	public Boundary getBoundary() {
		return boundary;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	@Override
	public String toString() {
		return boundary.getName() + ": Зафиксирована";
	}
}
