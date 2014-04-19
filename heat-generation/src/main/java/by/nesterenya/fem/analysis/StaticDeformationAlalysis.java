package by.nesterenya.fem.analysis;

import java.util.List;

import by.nesterenya.fem.analysis.result.StaticStructuralResult;
import by.nesterenya.fem.boundary.ILoad;
import by.nesterenya.fem.mesh.IMesh;
import by.nesterenya.fem.primitives.Box;
import by.nesterenya.fem.solver.StaticDeformationSolver;

public class StaticDeformationAlalysis extends Analysis {

	public Box getGeometry() {
		return geometry;
	}

	public void setGeometry(Box geometry) {
		this.geometry = geometry;
	}

	public IMesh getMesh() {
		return mesh;
	}

	public void setMesh(IMesh mesh) {
		this.mesh = mesh;
	}

	public List<ILoad> getLoads() {
		return loads;
	}

	public void setLoads(List<ILoad> loads) {
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
		// TODO задать РЕШАТЕЛЬ
		StaticDeformationSolver solver = new StaticDeformationSolver(this);
		solver.Solve();
	}

}