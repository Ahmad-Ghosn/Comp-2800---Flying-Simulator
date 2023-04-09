package Project;

import java.io.FileNotFoundException;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3d;

import codesES280.CommonsES;

public class Import_Plane {
	public Import_Plane() {
		Transform3D trans = new Transform3D();
    	trans.setScale(1.0);
    	//trans.rotY(Math.PI/2.0d);
    	objTG = new TransformGroup(trans);
    	BranchGroup scene = load_plane("big");
		objTG.addChild(scene);
	}	
	public Node position_object() {
		return objTG;
	}
	
	protected TransformGroup objTG;
	protected BranchGroup objBG;
	protected Shape3D obj_shape;
	
	protected double scale = 1;
	protected double[] angle = {0, 0, 0};
	protected Vector3d post = new Vector3d(0, 0, 0);
	
	private BranchGroup load_plane(String objName){
		int flags = ObjectFile.RESIZE;
        ObjectFile f = new ObjectFile(flags);
        org.jogamp.java3d.Shape3D nel = null;
        Scene s = null;
        try {
            s = f.load("src\\Project\\objects\\" + objName + ".obj");
            for (int i = 0; i < s.getSceneGroup().numChildren(); i++) {
	            nel = (Shape3D) s.getSceneGroup().getChild(i);
	            TextureLoad.loadTexture("a", nel);
            }
            
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
        objBG = s.getSceneGroup();
        return objBG;
	}
	
	protected void transform_object(String objName){
		Transform3D transform = new Transform3D();
		
		Transform3D scaler = new Transform3D();
		scaler.setScale(scale);
		
		Transform3D translater = new Transform3D();
		translater.setTranslation(post);
		
		Transform3D rotater = new Transform3D();
		rotater.rotX(angle[0]);
		rotater.rotY(angle[1]);
		rotater.rotZ(angle[2]);
		
		transform.mul(rotater);
		transform.mul(scaler);
		transform.mul(translater);
		
		objTG = new TransformGroup(transform);
		
		objBG = load_plane(objName);
		
		obj_shape = (Shape3D)objBG.getChild(0);
		obj_shape.setName(objName);
	}
}

class Temp_Box extends Import_Plane {
	public Temp_Box(){
		objTG = new TransformGroup();
		objTG.addChild(create_object());
	}
	
	protected Node create_object(){
		return new Box(0.3f,0.3f,1f, CommonsDS.obj_Appearance(CommonsDS.Cyan));
	}
	
	public Node position_object(){
		return objTG;
	}
}

class Temp_Ground extends Import_Plane {
	public Temp_Ground(){
		Transform3D translater = new Transform3D();
		translater.setTranslation(new Vector3d(0.0d, -2.0d, 0.0d));
		objTG = new TransformGroup(translater);
		objTG.addChild(create_object());
	}
	
	protected Node create_object(){
		return new Box(150.0f, 0.3f, 150.0f, CommonsDS.obj_Appearance(CommonsDS.Green));
	}
	
	public Node position_object(){
		return objTG;
	}
}