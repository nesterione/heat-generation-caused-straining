package by.nesterenya.fem.analysis;

import java.util.ArrayList;
import java.util.List;

import by.nesterenya.fem.boundary.ILoad;
import by.nesterenya.fem.mesh.IMesh;
import by.nesterenya.fem.mesh.Mesh;
import by.nesterenya.fem.primitives.Box;


/**
 * Super class for all types of problem
 * @author igor
 *
 */
 public abstract class Analysis {
  //TODO переместить границы в geometry
  // Геометрия модели
  protected Box geometry;
  
  // Список нагрузок
  protected List<ILoad> loads = new ArrayList<>();
  
  // Сетка 
  protected IMesh mesh;
  
  
  protected IDataInit dataInit;
  
  //Результат исследования
  protected IResult result;
  
  public abstract List<ILoad> getLoads();
  public abstract Box getGeometry();
  public abstract IMesh getMesh();
  public abstract IResult getResult();
  
  //TODO этот метод суда не вписывается, подумать куда переместить
  //public abstract String getSelectedPlane();
  
  public abstract void solve();
}
