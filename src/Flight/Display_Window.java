package Flight;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Display_Window extends JPanel implements MouseListener {
	
	private static JFrame frame;
	private static Canvas3D canvas;
	private static SimpleUniverse su;
	private static BranchGroup sceneBG = new BranchGroup();
	
	public static BranchGroup create_Scene_flight(){
		//Create the Canvas and SU earlier (extrapolate this to a new f'n) to attach the behaviour to it
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		
		su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		
		Background background = new Background();
		background.setImage(new TextureLoader("textures/background-HillyField.jpg",null).getImage());
		background.setImageScaleMode(Background.SCALE_FIT_MAX);
		background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE));
		background.setColor(CommonsDS.Grey);
		
		TransformGroup sceneTG = new TransformGroup();
		TransformGroup planeTG = new TransformGroup();
		planeTG.setName("Plane");
		
		planeTG.addChild(new Temp_Box().position_object());
		Flight_Control_Behaviour control = new Flight_Control_Behaviour(planeTG, su.getViewingPlatform().getViewPlatformTransform());
		control.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
		//sceneTG.addChild(control);
		
		sceneTG.addChild(new Temp_Ground().position_object());
		
		sceneTG.addChild(planeTG);
		sceneBG.addChild(CommonsDS.add_Lights(CommonsDS.White, 1));
		sceneBG.addChild(control);
		sceneBG.addChild(background);
		sceneBG.addChild(sceneTG);
		
		return sceneBG;
	}
	
	public Display_Window(BranchGroup sceneBG) {
		
		canvas.addMouseListener(this);
		
		TransformGroup viewTransform = su.getViewingPlatform().getViewPlatformTransform();
		Point3d center = new Point3d(0, 0, 0);             // define the point where the eye looks at
		Vector3d up = new Vector3d(0, 1, 0);               // define camera's up direction
		Transform3D view_TM = new Transform3D();
		view_TM.lookAt(new Point3d(0.0d, 1.0d, -4.0d), center, up);
		view_TM.invert();
		viewTransform.setTransform(view_TM);
		
		//sceneBG.addChild(CommonsDS.key_Navigation(su));     // allow key navigation
		
		sceneBG.compile();                                  // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                         // attach the scene to SimpleUniverse
		
		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}
	
	public static void main(String[] args){
		frame = new JFrame("Flight App");
		frame.getContentPane().add(new Display_Window(create_Scene_flight()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { }
	
	@Override
	public void mousePressed(MouseEvent e) { }
	
	@Override
	public void mouseReleased(MouseEvent e) { }
	
	@Override
	public void mouseEntered(MouseEvent e) { }
	
	@Override
	public void mouseExited(MouseEvent e) {	}
}
