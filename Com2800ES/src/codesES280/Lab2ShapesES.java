package codesES280;

import java.awt.Font;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Cone;
import org.jogamp.java3d.utils.geometry.GeometryInfo;
import org.jogamp.vecmath.*;

public abstract class Lab2ShapesES  {
	protected abstract Node create_Object();	           // use 'Node' for both Group and Shape3D
	public abstract Node position_Object();
}
class ConeShapes extends Lab2ShapesES {
	private TransformGroup objTG;                          // use 'objTG' to position an object
	public ConeShapes() {
		Transform3D translator = new Transform3D();        // 4x4 matrix for translation
		translator.setTranslation(new Vector3f(0f, 0f, 0.3f));
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotX(Math.PI / -2);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition
		trfm.mul(translator);                              // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);                  // set the combined transformation
		
		objTG.addChild(create_Object());
	}
	protected Node create_Object() {
		return new Cone(0.6f, 0.6f, CommonsES.obj_Appearance(CommonsES.Orange));
	}
	public Node position_Object() {
		return objTG;
	}
}
class CircleShapes extends Lab2ShapesES {
	int sides;
	public CircleShapes(int n) { //constructor determines # of sides to use
		if (n < 5) //needs 5 sides min to encapsulate the star
			sides = 5;
		else //this rounds the value to the nearest 5 as all sections must have the same number of sides
			sides = n-n%5;
	}
	protected Node create_Object() {
		float r = 0.6f, x, y;                              // vertices at 0.6 away from origin
		Point3f coor[] = new Point3f[sides];                   // declare n points for circle based on constructor
		int c[] = {sides}; int d[] = {1};
		for (int i = sides-1; i >= 0; i--) {                     // define coordinates for circle shape
			x = (float) Math.cos(Math.PI / 180 * (90 + 72.0 / (sides/5) * i)) * r; //sides/5 sides in between each star point
			y = (float) Math.sin(Math.PI / 180 * (90 + 72.0 / (sides/5) * i)) * r; //so use star equation but with 72/(sides/5)
			coor[sides-1-i] = new Point3f(x, y, -0.6f);            // use z-value to position star shape
		}
		GeometryInfo g = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
		g.setCoordinates(coor);
		g.setStripCounts(c);
		g.setContourCounts(d);
		System.out.println("success");
		return new Shape3D(g.getGeometryArray());  
	}
	public Node position_Object() {
		return create_Object();
	}
}
class A extends Lab2ShapesES {
	private TransformGroup objTG;                          // use 'objTG' to position an object
	private int sides;
	public A(int sides) {
		this.sides = sides;
		Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotX(Math.PI / -2);
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition                   // apply translation next
		trfm.mul(rotator);                                 // apply rotation first
		objTG = new TransformGroup(trfm);                  // set the combined transformation
		CircleShapes z = new CircleShapes(sides);
		objTG.addChild(z.position_Object());
		objTG.addChild(create_Object());
		
	}
	protected Node create_Object() {
		float r = 0.6f, x, y;                              // vertices at 0.6 away from origin
		Point3f coor[] = new Point3f[sides];                   // declare n points for circle based on constructor
		TriangleArray lineArr = new TriangleArray(3*sides, TriangleArray.COLOR_3 | TriangleArray.COORDINATES);
		double z = 360.0/sides;
		for (int i = 0; i < sides; i++) {                     // define coordinates for circle shape
			x = (float) Math.cos(Math.PI / 180 * (90 + z * i)) * r; //sides/5 sides in between each star point
			y = (float) Math.sin(Math.PI / 180 * (90 + z * i)) * r; //so use star equation but with 72/(sides/5)
			coor[i] = new Point3f(x, 0.6f, y);            // use z-value to position star shape
		}
		int p;
		for (int i = 0; i < sides; i++) {
			p = i*3;
			lineArr.setCoordinate(p, coor[i]);         // define point pairs for each line
			lineArr.setCoordinate(p + 1, coor[(i+1)%sides]); //% makes sure last and first point connect
			lineArr.setCoordinate(p+2, new Point3f(0,0,0));
			lineArr.setColor(p+2, CommonsES.Blue);
			lineArr.setColor(p, CommonsES.clr_list[i%8]);
			lineArr.setColor(p+1, CommonsES.clr_list[(i+1)%8]);
		}
		return new Shape3D(lineArr);  
	}
	public Node position_Object() {
		return objTG;
	}
}

class Stwing extends Lab2ShapesES {
	private TransformGroup objTG;                              // use 'objTG' to position an object
	private String str;
	public Stwing(String str_ltrs) {
		str = str_ltrs;		
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.2);
		
		objTG = new TransformGroup(scaler);
		objTG.addChild(create_Object());		   // apply scaling to change the string's size
	}
	protected Node create_Object() {
		Font my2DFont = new Font("Arial", Font.PLAIN, 1);  // font's name, style, size
		FontExtrusion myExtrude = new FontExtrusion();
		Font3D font3D = new Font3D(my2DFont, myExtrude);		
		Point3f pos = new Point3f(str.length()/4f, 0, -3f);// position for the string 
		Text3D text3D = new Text3D(font3D, str, pos); 
		text3D.setPath(Text3D.PATH_LEFT);// create a text3D object
		return new Shape3D(text3D);
	}
	public Node position_Object() {
		return objTG;
	}
}
