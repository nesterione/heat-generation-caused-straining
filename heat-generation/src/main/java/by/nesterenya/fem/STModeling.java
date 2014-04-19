package by.nesterenya.fem;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;

import by.nesterenya.fem.GlDisplay.DisplayType;
import by.nesterenya.fem.analysis.StaticStructuralAlalysis;
import by.nesterenya.fem.boundary.Load;
import by.nesterenya.fem.boundary.StaticEvenlyDistributedLoad;
import by.nesterenya.fem.boundary.Support;
import by.nesterenya.fem.element.material.Material;
import by.nesterenya.fem.mesh.BoxMesher;
import by.nesterenya.fem.mesh.Mesh;
import by.nesterenya.fem.mesh.Mesher;
import by.nesterenya.fem.primitives.Box;

import com.jogamp.opengl.util.FPSAnimator;

public class STModeling implements ActionListener {

	/**
	 * OpenGL Viewer
	 */
	final GlDisplay glDisplay = new GlDisplay();

	/**
	 * Timer for refresh image in <code> glDisplay </code>
	 */
	final FPSAnimator animator = new FPSAnimator(glDisplay, 20, true);

	/**
	 * Object current window
	 */
	static STModeling window;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		window = new STModeling();
		window.frame.setVisible(true);
		window.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	public STModeling() {
		initialize();
	}

	JFrame frame;

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Static thermal modeling");
		frame.setBounds(100, 100, 658, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		glDisplay.setRealized(true);
		glDisplay.setMinimumSize(new DimensionUIResource(50, 50));
		glDisplay.setMaximumSize(new DimensionUIResource(400, 400));
		glDisplay.setSize(new DimensionUIResource(200, 200));

		glDisplay.addMouseMotionListener(new MouseMotionAdapter() {
			private double lastY = 0;
			private double lastX = 0;

			@Override
			public void mouseDragged(MouseEvent arg0) {

				if (arg0.isControlDown()) {

					double currentX = arg0.getXOnScreen();
					double currentY = arg0.getYOnScreen();

					double oy = Math.abs(currentY - lastY);
					double ox = Math.abs(currentX - lastX);

					if (oy < ox) {

						if (lastX < currentX) {
							glDisplay.getPosition().addToMoveX(0.05);
						} else {
							glDisplay.getPosition().addToMoveX(-0.05);
						}

					} else {
						if (lastY < currentY) {
							glDisplay.getPosition().addToMoveY(-0.05);
						} else {
							glDisplay.getPosition().addToMoveY(0.05);
						}
					}
					lastX = currentX;
					lastY = currentY;

				} else {
					glDisplay.getPosition().setAngle_x(arg0.getX());
					glDisplay.getPosition().setAngle_y(arg0.getY());
				}
			}
		});

		glDisplay.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				glDisplay.getPosition().addToZoom(-0.05 * e.getUnitsToScroll());
			}
		});

		final JSplitPane splitPane = new JSplitPane();

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(20, 1, 2, 5));

		leftPanel.add(new JLabel("Длина пластинки"));
		final JTextField tb_plateLenght = new JTextField();
		leftPanel.add(tb_plateLenght);

		leftPanel.add(new JLabel("Ширина пластинки"));
		final JTextField tb_plateWidth = new JTextField();
		leftPanel.add(tb_plateWidth);

		leftPanel.add(new JLabel("Высота пластинки"));
		final JTextField tb_plateHeight = new JTextField();
		leftPanel.add(tb_plateHeight);

		leftPanel.add(new JLabel("Узлов по OX"));
		final JTextField tb_nodeCountOX = new JTextField();
		leftPanel.add(tb_nodeCountOX);

		leftPanel.add(new JLabel("Узлов по OY"));
		final JTextField tb_nodeCountOY = new JTextField();
		leftPanel.add(tb_nodeCountOY);

		leftPanel.add(new JLabel("Узлов по OZ"));
		final JTextField tb_nodeCountOZ = new JTextField();
		leftPanel.add(tb_nodeCountOZ);

		JButton btn_disp = new JButton();
		btn_disp.setText("Draw");
		btn_disp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				double box_ox = Double.parseDouble(tb_plateLenght.getText());
				double box_oy = Double.parseDouble(tb_plateWidth.getText());
				double box_oz = Double.parseDouble(tb_plateHeight.getText());
				Box box = new Box(box_ox, box_oy, box_oz);
				
				int nodeCountOX =  Integer.parseInt(tb_nodeCountOX.getText());
				int nodeCountOY =  Integer.parseInt(tb_nodeCountOY.getText());
				int nodeCountOZ =  Integer.parseInt(tb_nodeCountOZ.getText());
				
				Mesher mesher = new BoxMesher(box, nodeCountOX, nodeCountOY, nodeCountOZ);
				
				Mesh mesh = null;
				
				try {
					 mesh = mesher.formMesh();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				StaticStructuralAlalysis analysis = new StaticStructuralAlalysis();
				analysis.setGeometry(box);
				analysis.setMesh(mesh);
				
				glDisplay.setAnalysisD(analysis);
				glDisplay.setDisplayType(DisplayType.MESH);
				glDisplay.display();
			}
		});
		leftPanel.add(btn_disp);

		JButton btn_solveDef = new JButton();
		btn_solveDef.setText("Deform");
		btn_solveDef.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			//TODO now I use for calculation fixed values which was hardcoded
			//TODO make a entering values with GUI
				
			double box_ox = Double.parseDouble(tb_plateLenght.getText());
			double box_oy = Double.parseDouble(tb_plateWidth.getText());
			double box_oz = Double.parseDouble(tb_plateHeight.getText());
				
			Box box = new Box(box_ox, box_oy, box_oz);
				
			int nodeCountOX =  Integer.parseInt(tb_nodeCountOX.getText());
			int nodeCountOY =  Integer.parseInt(tb_nodeCountOY.getText());
			int nodeCountOZ =  Integer.parseInt(tb_nodeCountOZ.getText());
				
			Mesher mesher = new BoxMesher(box, nodeCountOX, nodeCountOY, nodeCountOZ);
				
			Mesh mesh = null;
			try {
				 mesh = mesher.formMesh();
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			StaticStructuralAlalysis analysis = new StaticStructuralAlalysis();
			
			analysis.setGeometry(box);
			analysis.setMesh(mesh);
				
			List<Load> loads = new ArrayList<>();
			//TODO: задавать направление действия силы
			loads.add( new StaticEvenlyDistributedLoad(1100000000, analysis.getMesh().getBoundaries().get("верхняя")));    
			
			loads.add( new Support(analysis.getMesh().getBoundaries().get("левая")) );
			loads.add( new Support(analysis.getMesh().getBoundaries().get("правая")) );
			//loads.add( new Support(analysis.getMesh().getBoundaries().get("передняя")) );
			//loads.add( new Support(analysis.getMesh().getBoundaries().get("задняя")) );
			
			Material material = new Material();
			material.setDensity(7850);
			material.setSpecificHeatCapacity(462);
			material.setName("sdf");
			//material.setThermalConductivity(60.5);
			//material.setSpecificHeatCapacity(434);
			material.setPoissonsRatio(0.3);
			material.setElasticModulus(200000000000.0); 
			                       
			
			analysis.getMesh().getMaterial().put(0,material );
			
			analysis.setLoads(loads);
			
			try {
				analysis.solve();
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			// TODO задавать силу F = A*sin(w*t)
			glDisplay.setAnalysisD(analysis);
			glDisplay.setDisplayType(DisplayType.STRAIN_TEMPERATURE);
			glDisplay.display();
			}
		});
		leftPanel.add(btn_solveDef);
		
		splitPane.setLeftComponent(leftPanel);

		JPanel dispPanel = new JPanel();
		splitPane.setRightComponent(dispPanel);
		dispPanel.setLayout(new BorderLayout(0, 0));
		dispPanel.add(glDisplay);

		frame.getContentPane().add(splitPane);

		animator.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}
