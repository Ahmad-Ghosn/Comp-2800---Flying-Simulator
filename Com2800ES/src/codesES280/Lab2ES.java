package codesES280;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Lab2ES extends JPanel {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static final int OBJ_NUM = 2;

	/* a function to build the content branch */
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();           // create the scene' BranchGroup
		TransformGroup sceneTG = new TransformGroup();     // create the scene's TransformGroup

		Lab2ShapesES[] lab1Shapes = new Lab2ShapesES[OBJ_NUM];
		lab1Shapes[0] = new Stwing("ES's Lab2");
		lab1Shapes[1] = new A(15);
		
		for (int i = 0; i < OBJ_NUM; i++)
			sceneTG.addChild(lab1Shapes[i].position_Object()); //these objects added to TG that will be rotating
		sceneTG.addChild(new ConeShape().position_Object());
		sceneBG.addChild(CommonsES.add_Lights(CommonsES.White, 1));	
		sceneBG.addChild(CommonsES.rotate_Behavior(7500, sceneTG));	
		sceneBG.addChild(sceneTG);                         // make 'sceneTG' continuous rotating
		sceneBG.addChild(new AxisShape(1).position_Object()); //adds axis to BG and it is not rotating
		return sceneBG;
	}

	/* NOTE: Keep the constructor for each of the labs and assignments */
	public Lab2ES(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		CommonsES.define_Viewer(su, new Point3d(4.0d, 0.0d, 1.0d));
		
		sceneBG.addChild(CommonsES.key_Navigation(su));     // allow key navigation
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		frame = new JFrame("ES's Lab2");                   // NOTE: change ES to student's initials
		frame.getContentPane().add(new Lab2ES(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}