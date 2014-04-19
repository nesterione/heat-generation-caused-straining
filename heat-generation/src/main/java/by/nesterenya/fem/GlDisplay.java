package by.nesterenya.fem;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import by.nesterenya.fem.analysis.Analysis;
import by.nesterenya.fem.analysis.DynamicStructuralAnalysis;
import by.nesterenya.fem.analysis.StaticStructuralAlalysis;
import by.nesterenya.fem.analysis.result.DynamicStructuralResult;

//TODO refact this
public class GlDisplay extends GLCanvas implements GLEventListener {

	//TODO сделать битовый вектор вместо этого перечисления
	
	public enum DisplayType {
	    NOTHING, MODEL, MESH, DEFORMATION, STRAIN, NODAL_STRAIN, STRAIN_ENERGY, STRAIN_TEMPERATURE
	};
	
	private Analysis analysis_d;
	
	private GLU glu;

	//private DisplayType displayType = DisplayType.NOTHING;
	private Position position = new Position();
	private ScenePainter painter = new ScenePainter();
	
	/*public DisplayType getDisplayType() {
		return displayType;
	}*/
	
	public void setDisplayType(DisplayType displayType) {
		//TODO ему тут не место
		painter.setDrawDelegate(SceneFactory.getDelegete(displayType, (StaticStructuralAlalysis)analysis_d), position);
		
	}
	
	public Position getPosition() {
		return position;
	}
	
	public GlDisplay() {
		 this.addGLEventListener(this);
		 
		 //Set start position of scene 
		 position.setAngle_x(-20);
		 position.setAngle_y(20);
		 
		 painter.setDrawDelegate(SceneFactory.getDelegete(DisplayType.NOTHING, null), position);
	}
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	@Override
	public void display(GLAutoDrawable drawable) {
		
		painter.Draw(drawable);

		if(analysis_d instanceof DynamicStructuralAnalysis) {
			((DynamicStructuralResult)analysis_d.getResult()).nextTime();
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) { }

	@Override
	public void init(GLAutoDrawable drawable) {
		  // Получить GL контекст
	    GL2 gl = drawable.getGL().getGL2();
	    // Получить GL инструменты
	    glu = new GLU();
	    // Цвет фона
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	    // Установить очистку буфера глубины
	    gl.glClearDepth(1.0f);
	    gl.glEnable(GL_DEPTH_TEST);
	    gl.glDepthFunc(GL_LEQUAL);
	    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // лучшая
	                                                          // настройка
	                                                          // перспективы
	    gl.glShadeModel(GL_SMOOTH); // вклиютить смешение цветов, размытие
	                                // и освещение
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int e, int width,
			int height) {
		 // Получить контекст OpenGL 2
	    GL2 gl = drawable.getGL().getGL2();
	    // Проверка деления на ноль
	    if (height == 0) height = 1;
	    float aspect = (float) width / height;
	    // Установить окна отбражения
	    gl.glViewport(0, 0, width, height);

	    // Установить перспективную проекцию
	    // Выбор матрицы проекций
	    gl.glMatrixMode(GL_PROJECTION);

	    // Сбросить матрицу проекций
	    gl.glLoadIdentity();

	    // fovy, aspect, zNear, zFar
	    glu.gluPerspective(45.0, aspect, 0.1, 100.0);
	    // Включить model-view перемещения
	    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	    // Сбросить матрицу проекций
	    gl.glLoadIdentity();
	}

	public void setAnalysisD(Analysis analysis) {
		this.analysis_d = analysis;
	}
	
}
