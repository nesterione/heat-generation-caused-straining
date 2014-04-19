package by.nesterenya.fem;

import java.nio.FloatBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import by.nesterenya.fem.GlDisplay.DisplayType;
import by.nesterenya.fem.analysis.StaticStructuralAlalysis;
import by.nesterenya.fem.analysis.result.StaticStructuralResult;
import by.nesterenya.fem.element.Element;
import by.nesterenya.fem.element.Node;
import by.nesterenya.fem.element.Node.Axis;
import by.nesterenya.fem.primitives.Box;

public class SceneFactory {
	
	public static DrawDelegate getDelegete(DisplayType displayType, StaticStructuralAlalysis analysis) {
	
		DrawDelegate delegate = null;
		
		switch(displayType) {
		
		case MODEL:
			delegate = new Model(analysis);
			break;
		case MESH:
			delegate = new Mesh(analysis);
			break;
		case DEFORMATION:
			delegate = new Deformation(analysis);
			break;
		case STRAIN:
			delegate = new Strain(analysis);
			break;
		case NODAL_STRAIN:
			delegate = new NodalStrain(analysis);
			break;
		case STRAIN_ENERGY:
			delegate = new StrainEnergy(analysis);
			break;
		case STRAIN_TEMPERATURE:
			delegate = new StrainTemperature(analysis);
			break;
		default:
		   delegate = null;
		   break;
		}
		 	
		return delegate;   
	}

	private static int[] orderNodesTet = {0,1,2,0,1,3,1,2,3,0,2,3};
	
	private static void drawGlVertex3d(GL2 gl,Node node) throws Exception {
	    double x = node.getPosition(Axis.X);
	    double y = node.getPosition(Axis.Y);
	    double z = node.getPosition(Axis.Z);
		
		gl.glVertex3d(x,y,z);
	}
	
	private static void drawGLVertex3d_deformation(GL2 gl, Node node, StaticStructuralAlalysis analysis) throws Exception {
			
		double x = node.getPosition(Axis.X);
		double y = node.getPosition(Axis.Y);
		double z = node.getPosition(Axis.Z);
				
		StaticStructuralResult result = analysis.getResult();
		double dx = result.getDeformation(node).getX();
		double dy = result.getDeformation(node).getY();
		double dz = result.getDeformation(node).getZ();
			
		gl.glVertex3d(x+dx, y+dy, z+dz);
	}
	
	private static void setColor(GL2 gl, double min, double max, double value) {
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
	}
	
	private static class Model implements DrawDelegate {

		private StaticStructuralAlalysis analysis;
		
		public Model(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}
		
		@Override
		public void handle(GL2 gl) {
			Box box = analysis.getGeometry();
			
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

			GLPrimitives.drawBox(gl, box);

			gl.glColor3f(0.3f, 0.3f, 0.3f);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			    
			GLPrimitives.drawBox(gl, box);
			    
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

			gl.glPopMatrix();
		}
		
	}
	
	private static class Mesh implements DrawDelegate {

		private StaticStructuralAlalysis analysis;
		
		public Mesh(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}
		
		@Override
		public void handle(GL2 gl) {
			gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

		    gl.glEnable(GL2ES1.GL_POINT_SMOOTH); // включаем режим сглаживания точек
		    gl.glPointSize(4);
		    
		    gl.glBegin(GL.GL_POINTS);
		    for (Node node : analysis.getMesh().getNodes()) {
		      try {
				drawGlVertex3d(gl,node);
		      } catch (Exception e) {
				e.printStackTrace();
		      }
		    }
		    gl.glEnd();

		    // Включить отрисовку линий, цвет линий синий
		    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
		    // Смещение, для того чтобы лиини были лучше видны
		    gl.glTranslatef(0, 0, 0.001f);

		    // Отрисовываем все конечные элементы
		    gl.glBegin(GL.GL_TRIANGLES);
		    List<Element> elements = analysis.getMesh().getElements();
		    for (Element element : elements) {
		    	
		    	for(int i = 0; i< orderNodesTet.length; i++) {
		    		try {
		    			Node node = element.getNode(orderNodesTet[i]);
		    			drawGlVertex3d(gl,node);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    	
		    }
		    gl.glEnd();

		    // Выключить отображение полигонов в виде линий
		    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		    gl.glTranslatef(0, 0, -0.001f);
		}
	}
	
	private static class Deformation implements DrawDelegate {

		private StaticStructuralAlalysis analysis;
		
		public Deformation(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}
		
		@Override
		public void handle(GL2 gl) {
			gl.glBegin(GL.GL_TRIANGLES);
		    List<Element> elements = analysis.getMesh().getElements();
		    
		    double min = analysis.getResult().getMinDeformation();
		    double max = analysis.getResult().getMaxDeformation();
		    
		    for (Element element : elements) {
		    	
		    	for(int i = 0; i< orderNodesTet.length; i++) {
			    	 
					try {
						 Node node; node = element.getNode(orderNodesTet[i]);
						 
						  //TODO я учитываю только смещение по Z
				    	  double value = analysis.getResult().getDeformation(node).getZ();
				    	  setColor(gl, min, max, value);
				    	  
				    	  drawGLVertex3d_deformation(gl, node, analysis);
				    	  
					} catch (Exception e) {
						e.printStackTrace();
					} 
		    	}
		    }
		    
		    gl.glEnd();
		}
		
	}
	
	private static class Strain implements DrawDelegate {
		
		private StaticStructuralAlalysis analysis;
		
		public Strain(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}
		
		@Override
		public void handle(GL2 gl) {
			
			gl.glBegin(GL.GL_TRIANGLES);
		    List<Element> elements = analysis.getMesh().getElements();
		    for (Element element : elements) {
		    	
			    double min = analysis.getResult().getMinStrain();
			    double max = analysis.getResult().getMaxStrain();
			    double value = analysis.getResult().getTotalStrain(element);
			    
			    setColor(gl, min, max, value);
		    	for(int i = 0; i< orderNodesTet.length; i++) {
			    	 
					try {
						 Node node = element.getNode(orderNodesTet[i]);
						drawGLVertex3d_deformation(gl,node, analysis);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    }
		    gl.glEnd();
		}
	}
	
	private static class NodalStrain implements DrawDelegate {
		
		private StaticStructuralAlalysis analysis;
		
		public NodalStrain(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}

		@Override
		public void handle(GL2 gl) {
			
			gl.glBegin(GL.GL_TRIANGLES);
			List<Element> elements = analysis.getMesh().getElements();
			
			double min = analysis.getResult().getMinNodalStrain();
			double max = analysis.getResult().getMaxNodalStrain();
			
			for (Element element : elements) {

			 	for(int i = 0; i< orderNodesTet.length; i++) {
			   	  
				 	try {
				 		Node node = element.getNode(orderNodesTet[i]);
				   	  	double value = analysis.getResult().getNodalStrain(node);
				   	  	setColor(gl, min, max, value);
				   	  	
						drawGLVertex3d_deformation(gl,node, analysis);
						
				 	} catch (Exception e) {
				 		e.printStackTrace();
				 	  }
			   	}
		    }
			
		    gl.glEnd();
		}
	}
	
	private static class StrainEnergy implements DrawDelegate {

		private StaticStructuralAlalysis analysis;
		
		public StrainEnergy(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}
		
		@Override
		public void handle(GL2 gl) {
			
			gl.glBegin(GL.GL_TRIANGLES);
		    List<Element> elements = analysis.getMesh().getElements();
		    
		    double min = analysis.getResult().getMinStrainEnergy();
		    double max = analysis.getResult().getMaxStrainEnergy();
		    
		    for (Element element : elements) {
		    	double value = analysis.getResult().getStrainEnergy(element).getValue();
		    	setColor(gl, min, max, value); 
		    	
		    	for(int i = 0; i< orderNodesTet.length; i++) {
			    	try { 
			    		Node node = element.getNode(orderNodesTet[i]);
						drawGLVertex3d_deformation(gl,node, analysis);
		    		} catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    }
		    gl.glEnd();
		}
	}
	
	private static class StrainTemperature implements DrawDelegate {
		
		private StaticStructuralAlalysis analysis;
		
		public StrainTemperature(StaticStructuralAlalysis analysis) {
			this.analysis = analysis;
		}
		
		@Override
		public void handle(GL2 gl) {
			gl.glBegin(GL.GL_TRIANGLES);
		    List<Element> elements = analysis.getMesh().getElements();
		    
		    double min = analysis.getResult().getMinTemperature();
		    double max = analysis.getResult().getMaxTemperature();
		    
		    for (Element element : elements) {

		    	for(int i = 0; i< orderNodesTet.length; i++) {
		    		try {  
			    		Node node = element.getNode(orderNodesTet[i]);
				    	double value = analysis.getResult().getTemperature(node).getValue();
				    	setColor(gl, min, max, value); 
				    	drawGLVertex3d_deformation(gl,node, analysis);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    }
		    gl.glEnd();
		}
	}
}

