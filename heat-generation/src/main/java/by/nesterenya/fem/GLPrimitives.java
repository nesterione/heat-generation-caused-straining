package by.nesterenya.fem;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import by.nesterenya.fem.primitives.Box;

public class GLPrimitives {

  /**
   * Рисует прямоугольник
   * 
   * @param gl - используемый класс для opengl
   * @param box - примертив для отрисовки
   */
  public static void drawBox(GL2 gl, Box box) {
    gl.glBegin(GL2GL3.GL_QUADS);

    // Bottom
    gl.glVertex3d(0, 0, 0);
    gl.glVertex3d(0, box.getWidth(), 0);
    gl.glVertex3d(box.getLenght(), box.getWidth(), 0);
    gl.glVertex3d(box.getLenght(), 0, 0);

    // Top
    gl.glVertex3d(0, 0, box.getHeight());
    gl.glVertex3d(box.getLenght(), 0, box.getHeight());
    gl.glVertex3d(box.getLenght(), box.getWidth(), box.getHeight());
    gl.glVertex3d(0, box.getWidth(), box.getHeight());
   
    // Fromt
    gl.glVertex3d(box.getLenght(), 0, 0);
    gl.glVertex3d(box.getLenght(), box.getWidth(), 0.0f);
    gl.glVertex3d(box.getLenght(), box.getWidth(), box.getHeight());
    gl.glVertex3d(box.getLenght(), 0, box.getHeight());

    // Back
    gl.glVertex3d(0, 0, 0);
    gl.glVertex3d(0, 0, box.getHeight()); 
    gl.glVertex3d(0, box.getWidth(), box.getHeight());
    gl.glVertex3d(0, box.getWidth(), 0.0f);

    // Left
    gl.glVertex3d(0, 0, 0);
    gl.glVertex3d(box.getLenght(), 0, 0.0f); 
    gl.glVertex3d(box.getLenght(), 0, box.getHeight());
   gl.glVertex3d(0, 0, box.getHeight());

    // Right
    gl.glVertex3d(0, box.getWidth(), 0);
    gl.glVertex3d(0, box.getWidth(), box.getHeight());
    gl.glVertex3d(box.getLenght(), box.getWidth(), box.getHeight());
    gl.glVertex3d(box.getLenght(), box.getWidth(), 0.0f);

    gl.glEnd();
  }

  public static void drawCoordinateSystem(GL2 gl) {
    gl.glPushMatrix();
    gl.glLineWidth(2);
    
    gl.glBegin(GL.GL_LINES);
    
    gl.glLineWidth(1);
    gl.glPopMatrix();    
    
    gl.glColor3d(1, 0, 0);
    gl.glVertex3d(0,0,0);
    gl.glVertex3d(100,0,0);
    
    gl.glColor3f(0.5f, 0, 0);
    gl.glVertex3d(-100,0,0);
    gl.glVertex3d(0,0,0);
    
    gl.glColor3f(0, 1, 0);
    gl.glVertex3d(0,0,0);
    gl.glVertex3d(0,100,0);
    
    gl.glColor3f(0, 0.5f, 0);
    gl.glVertex3d(0,-100,0);
    gl.glVertex3d(0,0,0);
    
    gl.glColor3f(0, 0, 0.5f);  
    gl.glVertex3d(0,0,-100);
    gl.glVertex3d(0,0,0);
    
    gl.glColor3f(0, 0, 1);
    gl.glVertex3d(0,0,0);
    gl.glVertex3d(0,0,100);
    
    gl.glEnd();
    
    gl.glLineWidth(1);
  }  
}

