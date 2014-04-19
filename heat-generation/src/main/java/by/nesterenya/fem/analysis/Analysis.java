package by.nesterenya.fem.analysis;

import java.util.ArrayList;
import java.util.List;

import by.nesterenya.fem.analysis.init.InitData;
import by.nesterenya.fem.analysis.result.Result;
import by.nesterenya.fem.boundary.Load;
import by.nesterenya.fem.mesh.Mesh;
import by.nesterenya.fem.primitives.Box;

/**
 * Super class for all types of problem
 * @author igor
 *
 */
 public abstract class Analysis {

  protected Box geometry;
  protected List<Load> loads = new ArrayList<>();
  protected Mesh mesh;
  protected InitData dataInit;
  protected Result result;
  
  public abstract List<Load> getLoads();
  public abstract Box getGeometry();
  public abstract Mesh getMesh();
  public abstract Result getResult();
  
  public abstract void solve() throws Exception;
}
