package Project;
import org.jogamp.java3d.loaders.Scene;

import java.awt.Font;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Environment {
    
    private static final long serialVersionUID = 1L;
	protected TransformGroup objTG;
	protected BranchGroup objBG;
	protected Shape3D obj_shape;
	public static Appearance app;
    public Environment() {
    	Transform3D trans = new Transform3D();
    	trans.setScale(20.0);
    	trans.setTranslation(new Vector3d(0,-20, 0));
    	objTG = new TransformGroup(trans);
    	objTG.addChild(load_Shape());
    }
   

    public static Appearance obj_Appearance(String fileName) {
        Material mtl = new Material(); // define material's attributes

        File materialsfile = new File("src/codesKS280/" +fileName+ ".mtl");
        
        if(!materialsfile.exists()){
        	System.out.println("MTL File not found.");
            return null;
        }
        
        Materials materials = new Materials(fileName);
        
        mtl.setShininess(materials.shininess);
        mtl.setAmbientColor(materials.ambient);
        mtl.setDiffuseColor(materials.diffuse);
        mtl.setSpecularColor(materials.specular);
        mtl.setEmissiveColor(materials.emissive);
        mtl.setLightingEnable(true);
        
        app = new Appearance();
        app.setMaterial(mtl); // set appearance's material
        
        if(materials.transparency != 1.0f)
            app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 1-materials.transparency));
        
        if(materials.texture != null)
            app.setTexture(materials.texture);

        app.setUserData(app.getTexture());
        
        return app;
    }  
    
    
    private BranchGroup load_Shape() {
        int flags = ObjectFile.LOAD_ALL;
        ObjectFile f = new ObjectFile(flags, (float) (60 * Math.PI / 180.0));
        Scene s = null;
        Appearance app = new Appearance();
        BranchGroup object;
        try {
            s = f.load("src\\Project\\objects\\ball.obj");
            Shape3D shape = (Shape3D) s.getSceneGroup().getChild(0);
            TextureLoad.loadTexture("overlay", shape);
            
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        } catch(ParsingErrorException e) {
            System.err.println(e);
            System.exit(1);
        } catch (IncorrectFormatException e) {
            System.err.println(e);
            System.exit(1);
        }

        return s.getSceneGroup();
    }
    public Node position_object() {
    	return objTG;
    }
   
    public static Background createBG(String filename) {
        Background bg = new Background();                                                               // create background
        bg.setImage(new TextureLoader("src\\Project\\textures\\" + filename + ".jpg", null).getImage());                                      // add image
        bg.setImageScaleMode(Background.SCALE_FIT_MAX);                                                 // scale to fit screen
        bg.setApplicationBounds(new BoundingSphere(new Point3d(0, 0, 0), 100000000));            // set bounding sphere as big as possible
        return bg;
    }
}

class Materials {
	public float shininess = 0;
    public Color3f ambient = new Color3f();
    public Color3f diffuse = new Color3f();
    public Color3f specular = new Color3f();
    public Color3f emissive = new Color3f();
    public float transparency = 0;
    public Texture texture = null;
    
    public Materials(String fileName) {
        File materialsfile = new File("src/codesKS280/"+fileName+".mtl");
        Scanner sc = null;

        try { sc = new Scanner(materialsfile); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        
        for(int i = 0; i < 4; i++) 
            sc.nextLine();
        
        while(sc.hasNext()) {
            switch(sc.next()) {
            case "Ns": shininess = sc.nextFloat(); continue;
            case "Ka": ambient = new Color3f(sc.nextFloat(),sc.nextFloat(),sc.nextFloat()); continue;
            case "Kd": diffuse = new Color3f(sc.nextFloat(),sc.nextFloat(),sc.nextFloat()); continue;
            case "Ks": specular = new Color3f(sc.nextFloat(),sc.nextFloat(),sc.nextFloat()); continue;
            case "Ke": emissive = new Color3f(sc.nextFloat(),sc.nextFloat(),sc.nextFloat()); continue;
            case "d" : transparency = sc.nextFloat(); continue;
            case "map_Kd": texture = getTexture(sc.next()); continue;
            case "newmtl": break;
            default: continue;
            }
            break;
        }

        sc.close();

    }
    
    private static Texture getTexture(String fileName) {
        TextureLoader loader = new TextureLoader(fileName, null);
        ImageComponent2D image = loader.getImage();        // load the image
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());

        texture.setImage(0, image);                        // set image for the texture
        return texture;
    }
	
}