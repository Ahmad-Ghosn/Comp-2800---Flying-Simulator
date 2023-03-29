package Flight;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3d;

public abstract class Import_Plane {
	protected abstract Node create_object();
	public abstract Node position_object();
	
	protected TransformGroup objTG;
	protected BranchGroup objBG;
	protected Shape3D obj_shape;
	
	protected double scale = 1;
	protected double[] angle = {0, 0, 0};
	protected Vector3d post = new Vector3d(0, 0, 0);
	
	private BranchGroup load_shape(String objName){
		ObjectFile file = new ObjectFile(ObjectFile.RESIZE, (float)(60 * Math.PI/180));
		Scene scene = null;
		try {
			scene = file.load("objects/" + objName + ".obj");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return scene.getSceneGroup();
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
		
		objBG = load_shape(objName);
		
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

