package by.nesterenya.fem.static_thermal;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

public class ScenePainter {
	
	GL2 gl;
	Position position;
	private DrawDelegate drawDelegate = null;
	
	public void Draw(GLAutoDrawable drawable)
	{
	    gl = drawable.getGL().getGL2();

	    // clear
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

	    // Сброс параметов модели отображения
	    gl.glLoadIdentity();
	    gl.glTranslatef(0.0f, 0.0f, -30.0f);

	    // фон
	    gl.glBegin(GL2GL3.GL_QUADS);
	    // white color
	    gl.glColor3f(0.42f, 0.55f, 0.83f);
	    gl.glVertex2f(30.0f, 20.0f);
	    gl.glVertex2f(-30.0f, 20.0f);
	    // blue color
	    gl.glColor3f(1.0f, 1.0f, 1.0f);
	    gl.glVertex2f(-30.0f, -20.0f);
	    gl.glVertex2f(30.0f, -20.0f);
	    gl.glEnd();

	    
	    // Сместить в нуть на 30
	    gl.glTranslatef(0.0f, 0.0f, 30.0f);

		
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


	    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

	    if (this.drawDelegate != null)
		      this.drawDelegate.handle(gl);

	    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

	    gl.glDisable(GL.GL_BLEND);
	    gl.glDisable(GL2ES1.GL_ALPHA_TEST);
	    gl.glFlush();
	}
	
	public void setDrawDelegate(DrawDelegate delegate, Position position)
	{
	   this.drawDelegate = delegate;
	   this.position = position;
	}
}
