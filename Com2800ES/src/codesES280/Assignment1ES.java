package codesES280;

import org.jogamp.java3d.loaders.Scene;
import java.awt.Font;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

import Project.TextureLoad;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Assignment1ES extends JPanel {
    private static final long serialVersionUID = 1L;
	private static JFrame frame;
    public Assignment1ES(BranchGroup sceneBG) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        
        SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
        CommonsES.define_Viewer(su, new Point3d(1.0d, 1.0d, 5.0d));
        
        sceneBG.addChild(CommonsES.key_Navigation(su));     // allow key navigation
        sceneBG.compile();		                           // optimize the BranchGroup
        su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse
    
        setLayout(new BorderLayout());
        add("Center", canvas);
        frame.setSize(800, 800);                           // set the size of the JFrame
        frame.setVisible(true);
    }
    
    public static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup();
        TransformGroup sceneTG = new TransformGroup();
        TransformGroup baseTG;
        
        Transform3D baseTrans = new Transform3D();
        baseTrans.setTranslation(new Vector3f(0, -1.1f, 0));
        baseTrans.setScale(new Vector3d(1, 1, 0.25f));
        baseTG = new TransformGroup(baseTrans);
        
       
        sceneTG.addChild(baseTG);

        
        Primitive cylinder = new Cylinder(0.5f, 0.2f, Primitive.GENERATE_NORMALS, 30, 30, CommonsES.obj_Appearance(CommonsES.Orange));
        SharedGroup cylinderSG = new SharedGroup();
        cylinderSG.addChild(cylinder);
        cylinderSG.compile();
        
        TransformGroup[] cylinderTG = new TransformGroup[4];
        for (int i = 0; i < 4; i++) {
            Transform3D cylinderTrans = new Transform3D();
            cylinderTrans.setTranslation(new Vector3f(0.5f * (i >= 2 ? -1 : 1), 0, 0.5f * ((i == 1 || i == 2) ? 1 : -1)));
            cylinderTG[i] = new TransformGroup(cylinderTrans);
            cylinderTG[i].addChild(new Link(cylinderSG));
            baseTG.addChild(cylinderTG[i]);
        }
        
        Primitive box = new Box(0.5f, 0.1f, 1.0f, CommonsES.obj_Appearance(CommonsES.Orange));
        Primitive box2 = new Box(1.0f, 0.1f, 0.5f, CommonsES.obj_Appearance(CommonsES.Orange));
        
        baseTG.addChild(box);
        baseTG.addChild(box2); 
        
        sceneTG.addChild(new StringShapes("ES's Assignment 1").position_Object());
        TransformGroup basTG;
        Transform3D baseTran = new Transform3D();
        baseTran.setScale(new Vector3d(1, 1, 1));
        basTG = new TransformGroup(baseTran);
        basTG.addChild(load_Shape());
        sceneTG.addChild(basTG);
        sceneBG.addChild(CommonsES.add_Lights(CommonsES.White, 1));    
        sceneBG.addChild(CommonsES.rotate_Behavior(10000, sceneTG));        // set rotation speed
        sceneBG.addChild(sceneTG);
        sceneBG.addChild(new AxesShape(1).position_Object());
        return sceneBG;
    }

    private static BranchGroup load_Shape() { //this function will load the obj file
        int flags = ObjectFile.RESIZE;
        ObjectFile f = new ObjectFile(flags);
        org.jogamp.java3d.Shape3D nel = null;
        Scene s = null;
        try {
        	
            s = f.load("C:\\Users\\ebsau\\eclipse-workspace\\Com2800ES\\src\\Project\\objects\\plane.obj");
            nel = (Shape3D) s.getSceneGroup().getChild(0);
           TextureLoad.loadTexture("overlay", nel);
            
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        } catch (ParsingErrorException e) {
            System.err.println(e);
            System.exit(1);
        } catch (IncorrectFormatException e) {
            System.err.println(e);
            System.exit(1);
        }
        return s.getSceneGroup();
    }

    public static void main(String[] args) {
        frame = new JFrame("ES's A1");                 
		frame.getContentPane().add(new Assignment1ES(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class AxesShape {
    float length;
    public AxesShape(float n) { // constructor to set length of axis
        length = n;
    }
    protected Node create_Object() {
        Point3f coor[] = new Point3f[4]; //4 points total with 1 for origin and 3 for axis endpoints
        LineArray lineArr = new LineArray(6, LineArray.COLOR_3 | LineArray.COORDINATES); 
        coor[0] = new Point3f(0f, 0f, 0f); //origin
        coor[1] = new Point3f(length, 0f, 0f); //x
        coor[2] = new Point3f(0f, length, 0f); //y
        coor[3] = new Point3f(0f, 0f, length); //z
        lineArr.setCoordinate(0, coor[0]); //creating the axis lines using setCoordinates and setColor for each
        lineArr.setCoordinate(1, coor[1]);
        lineArr.setCoordinate(2, coor[0]);       
        lineArr.setCoordinate(3, coor[2]);
        lineArr.setCoordinate(4, coor[0]);       
        lineArr.setCoordinate(5, coor[3]);
        lineArr.setColor(0, CommonsES.Red);
        lineArr.setColor(1, CommonsES.Red);
        lineArr.setColor(2, CommonsES.Green);
        lineArr.setColor(3, CommonsES.Green);
        lineArr.setColor(4, CommonsES.Blue);
        lineArr.setColor(5, CommonsES.Blue);
        return new Shape3D(lineArr);  
    }
    public Node position_Object() {
        return create_Object();
    }
}

class StringShapes {
	private TransformGroup objTG;                              
	private String str;
	public StringShapes(String str) {
		this.str = str;		
		Transform3D scaler = new Transform3D();
		scaler.rotY(Math.PI);								// flip the string so it displays properly
		scaler.setScale(0.15);                              // scaling 4x4 matrix 
		objTG = new TransformGroup(scaler);
		objTG.addChild(create_Object());		   // apply scaling to change the string's size
	}
	protected Node create_Object() {
		Font my2DFont = new Font("Arial", Font.PLAIN, 1);  // set font's name, style, size
		FontExtrusion myExtrude = new FontExtrusion();
		Font3D font3D = new Font3D(my2DFont, myExtrude);		

		Point3f pos = new Point3f(-str.length()/4f, -7.7f, 1.5f);// position for the string 
		Text3D text3D = new Text3D(font3D, str, pos);      
		return new Shape3D(text3D, CommonsES.obj_Appearance(CommonsES.Magenta)); // using white color as required
	}
	public Node position_Object() {
		return objTG;
	}
}