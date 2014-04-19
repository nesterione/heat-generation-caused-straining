package by.nesterenya.fem.analysis;

import java.util.List;

import by.nesterenya.fem.analysis.result.StaticStructuralResult;
import by.nesterenya.fem.boundary.Load;
import by.nesterenya.fem.mesh.Mesh;
import by.nesterenya.fem.primitives.Box;
import by.nesterenya.fem.solver.StaticDeformationSolver;

public class StaticDeformationAlalysis extends Analysis {

	public Box getGeometry() {
		return geometry;
	}

	public void setGeometry(Box geometry) {
		this.geometry = geometry;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public List<Load> getLoads() {
		return loads;
	}

	public void setLoads(List<Load> loads) {
		this.loads = loads;
	}

	public StaticStructuralResult getResult() {
		return (StaticStructuralResult)result;
	}

	public void setResult(StaticStructuralResult result) {
		this.result = (StaticStructuralResult) result;
	}

	@Override
	public void solve() throws Exception {
		StaticDeformationSolver solver = new StaticDeformationSolver(this);
		solver.Solve();
	}

}