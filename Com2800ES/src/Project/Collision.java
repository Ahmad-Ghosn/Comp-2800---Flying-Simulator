package Project;
import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Vector3d;

public class Collision extends Behavior {

    private TransformGroup plane1TG;
    private TransformGroup plane2TG;
    private boolean collided = false;
    private WakeupOnCollisionEntry wEnter;
	private WakeupOnCollisionExit wExit;
    public Collision(TransformGroup plane1TG, TransformGroup plane2TG) {
        this.plane1TG = plane1TG;
        this.plane2TG = plane2TG;
    }

    @Override
    public void initialize() {
    	wEnter = new WakeupOnCollisionEntry(((Group) plane2TG.getChild(0)).getChild(0), WakeupOnCollisionEntry.USE_BOUNDS);
		wExit = new WakeupOnCollisionExit(plane2TG.getChild(0), WakeupOnCollisionExit.USE_BOUNDS);
		wakeupOn(wEnter); // initialize the behavior
    }



    public boolean isCollided() {
        return collided;
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> arg0) {
    	while (arg0.hasNext()) {
    		WakeupCriterion event = arg0.next();
    		if (event instanceof WakeupOnCollisionEntry && collided == false) {
	    	collided = !collided; // collision has taken place
	
			if (collided) { // change color to highlight 'shape'
				System.out.println("hit");
				wakeupOn(wExit); // keep the color until no collision
			}
    		}
    		else if (event instanceof WakeupOnCollisionExit && collided == true) {
    			  // change color back to its original
    			collided = !collided;
    					if (!collided) {
    						System.out.println("bin");
    						wakeupOn(wEnter); // wait for collision happens
    					}
    				
    		}
    	}
    }
}
/*
import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Vector3d;

public class Collision extends Behavior {

    private TransformGroup plane1TG;
    private TransformGroup plane2TG;
    private boolean collided = false;

    public Collision(TransformGroup plane1TG, TransformGroup plane2TG) {
        this.plane1TG = plane1TG;
        this.plane2TG = plane2TG;
    }

    @Override
    public void initialize() {
        this.wakeupOn(new WakeupOnElapsedFrames(0));
    }


    private void checkCollision() {
        Bounds plane1Bounds = ( ((Group) ((Group) plane1TG.getChild(0)).getChild(0)).getChild(0)).getBounds();
        Bounds plane2Bounds = ((Shape3D) plane2TG.getChild(0)).getBounds();

        Transform3D plane1Transform = new Transform3D();
        plane1TG.getTransform(plane1Transform);
        Vector3d plane1Position = new Vector3d();
        plane1Transform.get(plane1Position);

        Transform3D plane2Transform = new Transform3D();
        plane2TG.getTransform(plane2Transform);
        Vector3d plane2Position = new Vector3d();
        plane2Transform.get(plane2Position);

        if (plane1Bounds.intersect(plane2Bounds)) {
            if (plane1Position.dot(plane2Position) < 10.0) {
                collided = true;
            } else {
                collided = false;
            }
        } else {
            collided = false;
        }
    }

    public boolean isCollided() {
        return collided;
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> arg0) {
        // TODO Auto-generated method stub
    	checkCollision();
    	if (isCollided())
    		System.out.println("hit");
    }
} */