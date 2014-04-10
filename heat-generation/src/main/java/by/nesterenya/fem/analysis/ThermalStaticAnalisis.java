package by.nesterenya.fem.analysis;

import java.util.List;

import by.nesterenya.fem.analysis.init.DataInitThermalStatic;
import by.nesterenya.fem.analysis.result.StaticThermalResult;
import by.nesterenya.fem.boundary.ILoad;
import by.nesterenya.fem.mesh.IMesh;
import by.nesterenya.fem.primitives.Box;
import by.nesterenya.fem.solver.ThermalStaticSolver;

public class ThermalStaticAnalisis extends Analysis{
 
  public DataInitThermalStatic getDataInit() {
    return (DataInitThermalStatic) dataInit;
  }
  
  public void setDataInit(DataInitThermalStatic initData) {
    this.dataInit = initData;
  }
   
  private StaticThermalResult result;

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

  public StaticThermalResult getResult() {
    return result;
  }

  public void setResult(StaticThermalResult result) {
    this.result = (StaticThermalResult) result;
  }

  @Override
  public void solve() {
    //TODO задать РЕШАТЕЛЬ
    ThermalStaticSolver solver = new ThermalStaticSolver(this);
    solver.Solve();
  }
  
}
