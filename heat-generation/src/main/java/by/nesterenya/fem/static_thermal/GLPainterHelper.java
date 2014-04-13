package by.nesterenya.fem.static_thermal;

import java.nio.FloatBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.naming.spi.DirStateFactory.Result;

import by.nesterenya.fem.mesh.IMesh;
import by.nesterenya.fem.analysis.StaticDeformationAlalysis;
import by.nesterenya.fem.analysis.ThermalStaticAnalisis;
import by.nesterenya.fem.analysis.result.Deformation;
import by.nesterenya.fem.analysis.result.StaticDeformationResult;
import by.nesterenya.fem.analysis.result.Strain;
import by.nesterenya.fem.element.*;
import by.nesterenya.fem.element.INode.Dim;
import by.nesterenya.fem.primitives.Box;

public class GLPainterHelper {
	
	// TODO Может быть переместить в лучшее место, может методы расширения
	private static void drawGlVertex3d(GL2 gl,INode node) throws Exception {
	    gl.glVertex3d(node.getValueOfDemention(Dim.X), node.getValueOfDemention(Dim.Y),
	        node.getValueOfDemention(Dim.Z));
	  }

	public static void plotMesh(GL2 gl, Position position, IMesh mesh) throws Exception {
	    gl.glTranslatef(0.0f, 0.0f, -6.0f);
	    gl.glScaled(position.getZoom(), position.getZoom(), position.getZoom());

	    // TODO изменить положение камеры правильным образом
	    // gluLookAt(0.0, 0.0, 25.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

	    gl.glRotated(position.getAngle_x(), 0.0, 1.0, 0.0);
	    gl.glRotated(position.getAngle_y(), 1.0, 0.0, 0.0);

	    // Рисуем координатные оси
	    GLPrimitives.drawCoordinateSystem(gl);

	    gl.glTranslated(position.getMove_x(),position.getMove_y(), 0);

	    gl.glEnable(GL2ES1.GL_ALPHA_TEST);
	    gl.glEnable(GL.GL_BLEND);
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

	    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

	    gl.glEnable(GL2ES1.GL_POINT_SMOOTH); // включаем режим сглаживания точек
	    gl.glPointSize(4);
	    gl.glBegin(GL.GL_POINTS);


	    for (INode node : mesh.getNodes()) {
	      drawGlVertex3d(gl,node);
	    }

	    gl.glEnd();

	    // Включить отрисовку линий, цвет линий синий
	    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
	    // Смещение, для того чтобы лиини были лучше видны
	    gl.glTranslatef(0, 0, 0.001f);

	    // Отрисовываем все конечные элементы
	    gl.glBegin(GL.GL_TRIANGLES);
	    List<IElement> elements = mesh.getElements();
	    for (IElement element : elements) {
	      INode node0 = element.getNode(0);
	      INode node1 = element.getNode(1);
	      INode node2 = element.getNode(2);
	      INode node3 = element.getNode(3);

	      drawGlVertex3d(gl,node0);
	      drawGlVertex3d(gl,node1);
	      drawGlVertex3d(gl,node2);

	      drawGlVertex3d(gl,node0);
	      drawGlVertex3d(gl,node1);
	      drawGlVertex3d(gl,node3);

	      drawGlVertex3d(gl,node1);
	      drawGlVertex3d(gl,node2);
	      drawGlVertex3d(gl,node3);

	      drawGlVertex3d(gl,node0);
	      drawGlVertex3d(gl,node2);
	      drawGlVertex3d(gl,node3);
	    }
	    gl.glEnd();

	    // Выключить отображение полигонов в виде линий
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
	    // Сместить в исходное положение
	    //gl.glTranslatef(0, 0, -0.001f);

	    //gl.glColor4d(0.85, 0.85, 0.85, 0.45f);
	    
	    //GLPrimitives.drawBox(gl, analysis.getGeometry());

	    //drawLoads();
	    //drawSelectedPlate();

	    gl.glDisable(GL.GL_BLEND);
	    gl.glDisable(GL2ES1.GL_ALPHA_TEST);

	    gl.glFlush();
	}
	
	public static void plotModel(GL2 gl, Position position, Box model) {

	    gl.glTranslatef(0.0f, 0.0f, -6.0f);
	    gl.glScaled(position.getZoom(), position.getZoom(), position.getZoom());

	    // TODO изменить положение камеры правильным образом
	    // gluLookAt(0.0, 0.0, 25.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

	    gl.glRotated(position.getAngle_x(), 0.0, 1.0, 0.0);
	    gl.glRotated(position.getAngle_y(), 1.0, 0.0, 0.0);

	    // Рисуем координатные оси
	    GLPrimitives.drawCoordinateSystem(gl);

	    gl.glTranslated(position.getMove_x(), position.getMove_y(), 0);

	    gl.glPushMatrix();

	    // Материал серебро
	    float ambient[] = {0.0215f, 0.1745f, 0.0215f, 1.0f};
	    float diffuse[] = {0.07568f, 0.61424f, 0.07568f, 1.0f};
	    float specular[] = {0.508273f, 0.508273f, 0.508273f, 1.0f};
	    float shine = 0.4f;

	    gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, FloatBuffer.wrap(ambient));
	    gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
	    gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, FloatBuffer.wrap(specular));
	    gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, shine * 128.0f);

	    gl.glColor3f(0.83f, 0.83f, 0.83f);

	    // TODO Можно добавить обстрактную фабрику для создания метода геометрии
	    GLPrimitives.drawBox(gl, model);

	    gl.glColor3f(0.3f, 0.3f, 0.3f);
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
	    
	    GLPrimitives.drawBox(gl, model);
	    
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

	    gl.glPopMatrix();

	    // Ждать завершения прорисовки
	    gl.glFlush();
	  }
	
	public static void plotThermalResult(GL2 gl, Position position, ThermalStaticAnalisis analysis) throws Exception {

	    gl.glTranslatef(0.0f, 0.0f, -6.0f);

	    gl.glScaled(position.getZoom(), position.getZoom(), position.getZoom()); // screen

	    // TODO изменить положение камеры правильным образом
	    // gluLookAt(0.0, 0.0, 25.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

	    gl.glRotated(position.getAngle_x(), 0.0, 1.0, 0.0);
	    gl.glRotated(position.getAngle_y(), 1.0, 0.0, 0.0);

	    // Рисуем координатные оси
	    GLPrimitives.drawCoordinateSystem(gl);

	    gl.glTranslated(position.getMove_x(), position.getMove_y(), 0);

	    gl.glEnable(GL2ES1.GL_ALPHA_TEST);
	    gl.glEnable(GL.GL_BLEND);
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);


	    gl.glTranslatef(0, 0, 0.001f);
	    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

	    gl.glPointSize(4);
	    gl.glEnable(GL2ES1.GL_POINT_SMOOTH); // включаем режим сглаживания точек

	    gl.glBegin(GL.GL_POINTS);

	    for (INode node : analysis.getMesh().getNodes()) {
	      drawGlVertex3d(gl,node);
	    }

	    gl.glEnd();

	    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

	    gl.glBegin(GL.GL_TRIANGLES);
	    List<IElement> elements = analysis.getMesh().getElements();
	    for (IElement element : elements) {
	    
	      INode node0 = element.getNode(0);
	      INode node1 = element.getNode(1);
	      INode node2 = element.getNode(2);
	      INode node3 = element.getNode(3);

	      DrawGLColor3f(gl, node0, analysis);
	      drawGlVertex3d(gl,node0);
	      DrawGLColor3f(gl,node1, analysis);
	      drawGlVertex3d(gl,node1);
	      DrawGLColor3f(gl,node2, analysis);
	      drawGlVertex3d(gl,node2);

	      DrawGLColor3f(gl,node0, analysis);
	      drawGlVertex3d(gl,node0);
	      DrawGLColor3f(gl,node1, analysis);
	      drawGlVertex3d(gl,node1);
	      DrawGLColor3f(gl,node3, analysis);
	      drawGlVertex3d(gl,node3);

	      DrawGLColor3f(gl,node1, analysis);
	      drawGlVertex3d(gl,node1);
	      DrawGLColor3f(gl,node2, analysis);
	      drawGlVertex3d(gl,node2);
	      DrawGLColor3f(gl,node3, analysis);
	      drawGlVertex3d(gl,node3);

	      DrawGLColor3f(gl,node0, analysis);
	      drawGlVertex3d(gl,node0);
	      DrawGLColor3f(gl,node2, analysis);
	      drawGlVertex3d(gl,node2);
	      DrawGLColor3f(gl,node3, analysis);
	      drawGlVertex3d(gl,node3);
	    }
	    gl.glEnd();

	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
	    gl.glTranslatef(0, 0, -0.001f);

	    gl.glDisable(GL.GL_BLEND);
	    gl.glDisable(GL2ES1.GL_ALPHA_TEST);
	    gl.glFlush();
	  }
	
	public static void plotDeformationResult(GL2 gl, Position position, StaticDeformationAlalysis analysis) throws Exception {
		gl.glTranslatef(0.0f, 0.0f, -6.0f);

	    gl.glScaled(position.getZoom(), position.getZoom(), position.getZoom()); // screen

	    // TODO изменить положение камеры правильным образом
	    // gluLookAt(0.0, 0.0, 25.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

	    gl.glRotated(position.getAngle_x(), 0.0, 1.0, 0.0);
	    gl.glRotated(position.getAngle_y(), 1.0, 0.0, 0.0);

	    // Рисуем координатные оси
	    GLPrimitives.drawCoordinateSystem(gl);

	    gl.glTranslated(position.getMove_x(), position.getMove_y(), 0);

	    gl.glEnable(GL2ES1.GL_ALPHA_TEST);
	    gl.glEnable(GL.GL_BLEND);
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);


	    gl.glTranslatef(0, 0, 0.001f);
	    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

	    gl.glPointSize(4);
	    gl.glEnable(GL2ES1.GL_POINT_SMOOTH); // включаем режим сглаживания точек

	    gl.glEnd();

	    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

	    gl.glBegin(GL.GL_TRIANGLES);
	    List<IElement> elements = analysis.getMesh().getElements();
	    for (IElement element : elements) {

	      INode node0 = element.getNode(0);
	      INode node1 = element.getNode(1);
	      INode node2 = element.getNode(2);
	      INode node3 = element.getNode(3);
	     
	      DrawGLColor3fStruct(gl, node0, analysis);
	      drawGLVertex3d_deformation(gl, node0, analysis);
	      DrawGLColor3fStruct(gl,node1, analysis);
	      drawGLVertex3d_deformation(gl,node1, analysis);
	      DrawGLColor3fStruct(gl,node2, analysis);
	      drawGLVertex3d_deformation(gl,node2, analysis);

	      DrawGLColor3fStruct(gl,node0, analysis);
	      drawGLVertex3d_deformation(gl,node0, analysis);
	      DrawGLColor3fStruct(gl,node1, analysis);
	      drawGLVertex3d_deformation(gl,node1, analysis);
	      DrawGLColor3fStruct(gl,node3, analysis);
	      drawGLVertex3d_deformation(gl,node3, analysis);

	      DrawGLColor3fStruct(gl,node1, analysis);
	      drawGLVertex3d_deformation(gl,node1, analysis);
	      DrawGLColor3fStruct(gl,node2, analysis);
	      drawGLVertex3d_deformation(gl,node2, analysis);  
	      DrawGLColor3fStruct(gl,node3, analysis);
	      drawGLVertex3d_deformation(gl,node3, analysis);

	      DrawGLColor3fStruct(gl,node0, analysis);
	      drawGLVertex3d_deformation(gl,node0, analysis);  
	      DrawGLColor3fStruct(gl,node2, analysis);
	      drawGLVertex3d_deformation(gl,node2, analysis);
	      DrawGLColor3fStruct(gl,node3, analysis);
	      drawGLVertex3d_deformation(gl,node3, analysis);
	    }
	    gl.glEnd();

	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
	    gl.glTranslatef(0, 0, -0.001f);

	    gl.glDisable(GL.GL_BLEND);
	    gl.glDisable(GL2ES1.GL_ALPHA_TEST);
	    gl.glFlush();
	}
	
	public static void plotStrainResult(GL2 gl, Position position, StaticDeformationAlalysis analysis) throws Exception {
		gl.glTranslatef(0.0f, 0.0f, -6.0f);

	    gl.glScaled(position.getZoom(), position.getZoom(), position.getZoom()); // screen

	    // TODO изменить положение камеры правильным образом
	    // gluLookAt(0.0, 0.0, 25.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

	    gl.glRotated(position.getAngle_x(), 0.0, 1.0, 0.0);
	    gl.glRotated(position.getAngle_y(), 1.0, 0.0, 0.0);

	    // Рисуем координатные оси
	    GLPrimitives.drawCoordinateSystem(gl);

	    gl.glTranslated(position.getMove_x(), position.getMove_y(), 0);

	    gl.glEnable(GL2ES1.GL_ALPHA_TEST);
	    gl.glEnable(GL.GL_BLEND);
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);


	    gl.glTranslatef(0, 0, 0.001f);
	    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

	    gl.glPointSize(4);
	    gl.glEnable(GL2ES1.GL_POINT_SMOOTH); // включаем режим сглаживания точек

	    gl.glEnd();

	    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

	    gl.glBegin(GL.GL_TRIANGLES);
	    List<IElement> elements = analysis.getMesh().getElements();
	    for (IElement element : elements) {

	      INode node0 = element.getNode(0);
	      INode node1 = element.getNode(1);
	      INode node2 = element.getNode(2);
	      INode node3 = element.getNode(3);
	     
	      
	      //Strain for all element
	      DrawGLColor3fStrain(gl, element, analysis);
	      
	      drawGLVertex3d_deformation(gl,node0, analysis);
	      drawGLVertex3d_deformation(gl,node1, analysis);
	      drawGLVertex3d_deformation(gl,node2, analysis);

	      drawGLVertex3d_deformation(gl,node0, analysis);
	      drawGLVertex3d_deformation(gl,node1, analysis);
	      drawGLVertex3d_deformation(gl,node3, analysis);

	      drawGLVertex3d_deformation(gl,node1, analysis);
	      drawGLVertex3d_deformation(gl,node2, analysis);  
	      drawGLVertex3d_deformation(gl,node3, analysis);
 
	      drawGLVertex3d_deformation(gl,node0, analysis);  
	      drawGLVertex3d_deformation(gl,node2, analysis);
	      drawGLVertex3d_deformation(gl,node3, analysis);
	    }
	    gl.glEnd();

	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
	    gl.glTranslatef(0, 0, -0.001f);

	    gl.glDisable(GL.GL_BLEND);
	    gl.glDisable(GL2ES1.GL_ALPHA_TEST);
	    gl.glFlush();
	}
	
	
	//TODO проверить изменение растояния в у STREEs вектор изменения растояния между узлами
	
	
	private static void DrawGLColor3fStrain(GL2 gl, IElement element, StaticDeformationAlalysis analysis) {
		int indexElement = analysis.getMesh().getElements().indexOf(element);
		
		Strain strain = analysis.getResult().getStrains()[indexElement];
		
		//TODO восможно слаживать значенмя нужно не так
		double vectorOfDeformation = calcVL(strain);
		
		setColorStraining(gl, vectorOfDeformation, analysis.getResult());
	}
	
	
	
	// TODO Может быть переместить в лучшее место, может методы расширения а лучьше Dependesy Injecton
	private static void drawGLVertex3d_deformation(GL2 gl, INode node, StaticDeformationAlalysis analysis) throws Exception {
		
		int numberNode = analysis.getMesh().getNodes().lastIndexOf(node);
		double x = node.getValueOfDemention(Dim.X);
		double y = node.getValueOfDemention(Dim.Y);
		double z = node.getValueOfDemention(Dim.Z);
		
		Deformation[] deformations = analysis.getResult().getDeformations();
		double dx = deformations[numberNode].getX();
		double dy = deformations[numberNode].getY();
		double dz = deformations[numberNode].getZ();
		
		gl.glVertex3d(x+dx, y+dy, z+dz);
	}
	
	private static void DrawGLColor3fStruct(GL2 gl, INode node, StaticDeformationAlalysis analysis) {
		int indexNode = analysis.getMesh().getNodes().lastIndexOf(node);
		setColorStructal(gl, analysis.getResult().getDeformations()[indexNode].getZ(), analysis.getResult());
	}
	
	private static double calcVL(Strain strain){
		return Math.sqrt( strain.getEx()*strain.getEx()+strain.getEy()*strain.getEy()+strain.getEz()*strain.getEz()  );
	}
	
	private static int setColorStraining(GL2 gl, double value, StaticDeformationResult result) {
	    
		Strain[] strains = result.getStrains();
		//TODO Оптимизировать, без лишних пересчетов
	    double min = calcVL(strains[0]); 
	    double max = calcVL(strains[0]);
	    
	    for(int i= 0;i<result.getDeformations().length;i++) {
	    	double curValue = calcVL(strains[i]);
	    	
	      if(min> curValue) {
	        min = curValue;
	      }
	      if(max< curValue) {
	        max = curValue;
	      }
	    }
	    
	    double step = (max - min) / 9.0f;
	    int color = 0;

	    for (double st = min + step; color < 9; st += step, color++)
	      if (value <= st) {
	        break;
	      }

	    switch (color) {
	      case 0:
	        gl.glColor3f(0.0f, 0.0f, 1.0f); // синий
	        break;
	      case 1:
	        gl.glColor3f(0.078f, 0.482f, 0.98f); // светлосиний
	        break;
	      case 2:
	        gl.glColor3f(0.086f, 0.906f, 0.973f); // голубой
	        break;
	      case 3:
	        gl.glColor3f(0.094f, 0.961f, 0.573f); // голубоватый
	        break;
	      case 4:
	        gl.glColor3f(0.0f, 1.0f, 0.0f); // зелёный
	        break;
	      case 5:
	        gl.glColor3f(0.62f, 0.984f, 0.075f); // зеленоватый
	        break;
	      case 6:
	        gl.glColor3f(0.957f, 0.98f, 0.078f); // желтый
	        break;
	      case 7:
	        gl.glColor3f(0.988f, 0.667f, 0.070f); // оранжевый
	        break;
	      case 8:
	        gl.glColor3f(1.0f, 0.0f, 0.0f); // красный
	        break;
	      default:
	        gl.glColor3f(1.0f, 0.0f, 0.0f); // красный
	        break;
	    }

	    return color;
}
	
	private static void DrawGLColor3f(GL2 gl, INode node, ThermalStaticAnalisis analysis) {
	    setColorThermal(gl, analysis.getResult().getT()[analysis.getMesh().getNodes().lastIndexOf(node)]);
	}
	
	private static int setColorStructal(GL2 gl, double value, StaticDeformationResult result) {
		    //TODO Оптимизировать, без лишних пересчетов
		    double min = result.getDeformations()[0].getZ(); 
		    double max = result.getDeformations()[0].getZ();
		    
		    for(int i= 0;i<result.getDeformations().length;i++) {
		      if(min> result.getDeformations()[i].getZ()) {
		        min = result.getDeformations()[i].getZ();
		      }
		      if(max< result.getDeformations()[i].getZ()) {
		        max = result.getDeformations()[i].getZ();
		      }
		    }
		    
		    double step = (max - min) / 9.0f;
		    int color = 0;

		    for (double st = min + step; color < 9; st += step, color++)
		      if (value <= st) {
		        break;
		      }

		    switch (color) {
		      case 0:
		        gl.glColor3f(0.0f, 0.0f, 1.0f); // синий
		        break;
		      case 1:
		        gl.glColor3f(0.078f, 0.482f, 0.98f); // светлосиний
		        break;
		      case 2:
		        gl.glColor3f(0.086f, 0.906f, 0.973f); // голубой
		        break;
		      case 3:
		        gl.glColor3f(0.094f, 0.961f, 0.573f); // голубоватый
		        break;
		      case 4:
		        gl.glColor3f(0.0f, 1.0f, 0.0f); // зелёный
		        break;
		      case 5:
		        gl.glColor3f(0.62f, 0.984f, 0.075f); // зеленоватый
		        break;
		      case 6:
		        gl.glColor3f(0.957f, 0.98f, 0.078f); // желтый
		        break;
		      case 7:
		        gl.glColor3f(0.988f, 0.667f, 0.070f); // оранжевый
		        break;
		      case 8:
		        gl.glColor3f(1.0f, 0.0f, 0.0f); // красный
		        break;
		      default:
		        gl.glColor3f(1.0f, 0.0f, 0.0f); // красный
		        break;
		    }

		    return color;
		    
	}
	
	private static int setColorThermal(GL2 gl, double value) {
	    // TODO WARM COLOR CALC
	    double maxT = 400;
	    double minT = 300;
	    
	    double step = (maxT - minT) / 9.0f;
	    int color = 0;

	    for (double st = minT + step; color < 9; st += step, color++)
	      if (Math.abs(value) <= Math.abs(st)) {
	        break;
	      }

	    switch (color) {
	      case 0:
	        gl.glColor3f(0.0f, 0.0f, 1.0f); // синий
	        break;
	      case 1:
	        gl.glColor3f(0.078f, 0.482f, 0.98f); // светлосиний
	        break;
	      case 2:
	        gl.glColor3f(0.086f, 0.906f, 0.973f); // голубой
	        break;
	      case 3:
	        gl.glColor3f(0.094f, 0.961f, 0.573f); // голубоватый
	        break;
	      case 4:
	        gl.glColor3f(0.0f, 1.0f, 0.0f); // зелёный
	        break;
	      case 5:
	        gl.glColor3f(0.62f, 0.984f, 0.075f); // зеленоватый
	        break;
	      case 6:
	        gl.glColor3f(0.957f, 0.98f, 0.078f); // желтый
	        break;
	      case 7:
	        gl.glColor3f(0.988f, 0.667f, 0.070f); // оранжевый
	        break;
	      case 8:
	        gl.glColor3f(1.0f, 0.0f, 0.0f); // красный
	        break;
	      default:
	        gl.glColor3f(1.0f, 0.0f, 0.0f); // красный
	        break;
	    }

	    return color;
	  }
}
