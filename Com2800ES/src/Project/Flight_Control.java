package Project;

/*
 * This is the behaviour dedicated to processing keypresses and transforming the relevant objects.
 * Up Arrow -- Throttle up
 * Down Arrow -- Throttle down
 * A -- Yaw left
 * D -- Yaw right
 * W -- Pitch down
 * S -- Pitch up
 * Q -- Roll left
 * E -- Roll right
 *
 * Dalyn Stephens - COMP2800 Final Project
 */

import java.awt.event.KeyEvent;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.*;

public class Flight_Control {
	private Vector3d navVec;
	private long time;
	private double throttle;
	//Multiply accel value that is applied each frame and max speed by the throttle's value
	private final double maxThrottle;
	private final double throttleStep;
	private double curSpeed;
	private final double maxBwdSpeed;
	private final double yawAngle;
	private final double pitchAngle;
	private final double rollAngle;
	private Vector3d gravity;
	private Vector3d a = new Vector3d();	//Vector for value to apply
	private Vector3d dv = new Vector3d();
	private Point3d dp = new Point3d();
	private Quat4d udQuat = new Quat4d();
	private Quat4d lrQuat = new Quat4d();
	private Quat4d rolQuat = new Quat4d();
	private Vector3d vpPos = new Vector3d();
	private double vpScale;
	private Quat4d vpQuat = new Quat4d();
	private Transform3D vpTrans = new Transform3D();
	private Matrix4d mat = new Matrix4d();
	private Transform3D nominal = new Transform3D();
	private TransformGroup targetTG;
	private TransformGroup followTG;
	private TransformGroup scene;
	private Collision joe;
	private int key_state = 0;
	private int modifier_key_state = 0;
	
	public Flight_Control(TransformGroup targetTG, TransformGroup followTG, TransformGroup scene) {
		this.targetTG = targetTG;	//Plane TG
		this.followTG = followTG;	//Camera TG
		this.scene = scene;
		joe = new Collision(targetTG, scene);
		joe.setSchedulingBounds(scene.getChild(0).getBounds());
		targetTG.addChild(joe);
		targetTG.getTransform(this.nominal);
		
		this.navVec = new Vector3d(0.0, 0.0, 0.0);		//Track prev location
		
		this.gravity = new Vector3d(0.0, 9.8, 0.0);	//Gravity (Calculate to stay perpendicular to 'ground')
		
		this.yawAngle = 0.5d;
		this.pitchAngle = 1.0d;
		this.rollAngle = 2.0d;
		
		this.curSpeed = 0.0d;		//Track current speed
		this.maxBwdSpeed = -2.0d;	//Separate speed for reversing
		
		this.throttleStep = 0.001d;		//How much the throttle changes when the up arrow is held
		this.throttle = 0.0d;			//How much acceleration to apply each frame
		this.maxThrottle = 1.0d;
		
		this.time = System.currentTimeMillis();
	}
	
	private long getDeltaTime() {
		long newTime = System.currentTimeMillis();
		long deltaTime = newTime - this.time;
		this.time = newTime;
		return deltaTime > 2000L ? 0L : deltaTime;
	}
	
	private void genRotQuat(double av, int axis, Quat4d q) {
		q.x = q.y = q.z = 0.0;
		q.w = Math.cos(av / 2.0);
		double b = 1.0 - q.w * q.w;
		if (b > 0.0) {
			b = Math.sqrt(b);
			if (av < 0.0) {
				b = -b;
			}
			
			if (axis == 0) {
				q.x = b;
			} else if (axis == 1) {
				q.y = b;
			} else {
				q.z = b;
			}
		}
	}
	
	private void throttleKeyAdd(double throttleStep){
		if (Math.abs(this.throttle + throttleStep) < this.maxThrottle){
			this.throttle += throttleStep;
		}
	}
	
	private Vector3d getRelativeGravity(){
		Vector3d grav = new Vector3d(0.0d, -1.0d, 0.0d);
		this.mat.transform(grav);
		grav.y *= 9;
		return grav;
	}
	
	//The max speed is the point at which curspeed/y == maxThrottle*x
	//The min flight speed is the point at which log(curspeed)*6 < gravity
	private double getAccel(){	//Force to be applied forwards relative to the plane
		return this.throttle*10;	//Caps the acceleration at 50
	}
	private double getDrag(){	//Force to be applied backwards relative to plane
		return this.curSpeed/50;			//Caps the speed by being relative to speed
	}
	private double getLift(){	//Force to be applied upwards relative to the plane
		if (curSpeed > 0){
			return Math.log(Math.abs(this.curSpeed))*3;	//Gives a nice curve that counters gravity quickly, and doesn't get too high
		} else return 0.0d;
	}
	
	public void integrateTransformChanges() {
		if (!joe.isCollided()) {
			this.targetTG.getTransform(this.vpTrans);
			this.vpScale = this.vpTrans.get(this.vpQuat, this.vpPos);
			double deltaTime = (double)this.getDeltaTime();
			deltaTime *= 0.001;
			
			this.a.x = this.a.y = this.a.z = 0.0;		//Zero the update vector
			
			//Check for throttle changes (Up/Down keys)
			if ((this.key_state & 1) != 0 && (this.key_state & 2) == 0) {	//If key_state 1 bit is active, it's the up key
				this.throttleKeyAdd(throttleStep);
			} else if ((this.key_state & 1) == 0 && (this.key_state & 2) != 0) {	//likewise for the 2 bit and down key
				this.throttleKeyAdd(-throttleStep);
			}
			
			this.gravity = getRelativeGravity();
			
			//Apply forces to plane
			//Apply all forces as long as they aren't more negative than the max reverse
			this.a.z = ((this.curSpeed += (getAccel() - getDrag() - this.gravity.z)*deltaTime) < this.maxBwdSpeed) ?
					this.maxBwdSpeed : getAccel() - getDrag() - this.gravity.z;
			this.a.y = getLift() + this.gravity.y;									//up/down motion
			this.a.x = -this.gravity.x;												//left/right motion
			
			//System.out.println(this.navVec);
			
			//apply change in position to current position
			this.dv.scale(deltaTime, this.a);	//Scale dv by a and how much time has passed (get deltaV)
			//Temporary, while there isn't any collision set up prevents the 'plane' from falling through the ground
			if (this.vpPos.y + dv.y < 0.0d){
				dv.y = 0.0d;
			}
			
			//System.out.println(dv.y);
			this.navVec.add(this.dv);			//Add deltaV to the previous position
			this.dp.scale(deltaTime, this.navVec);	//set the change in position equal to the scaled position
			
			double lrAng = 0.0;
			double udAng = 0.0;
			double rolAng = 0.0;
			
			//Check for l/r rotation with 'A' and 'D' when alt key is not pressed
			if ((this.key_state & 4) != 0 && (this.key_state & 8) == 0) {
				lrAng = -this.yawAngle;		//Rotate left with negative
			} else if ((this.key_state & 4) == 0 && (this.key_state & 8) != 0) {
				lrAng = this.yawAngle;		//Rotate right with positive
			}
			
			//Check for up/down rotation with 'W' and 'S' when alt key is not pressed
			if ((this.key_state & 64) != 0 && (this.key_state & 128) == 0) {
				udAng = this.pitchAngle;	//Rotate up with positive
			} else if ((this.key_state & 64) == 0 && (this.key_state & 128) != 0) {
				udAng = -this.pitchAngle;	//Rotate down with negative
			}
			
			if ((this.key_state & 16) != 0 && (this.key_state & 32) == 0) {
				rolAng = this.rollAngle;
			} else if ((this.key_state & 16) == 0 && (this.key_state & 32) != 0) {
				rolAng = -this.rollAngle;
			}
			
			lrAng *= deltaTime;
			udAng *= deltaTime;
			rolAng *= deltaTime;
			this.vpQuat.inverse();
			
			//Apply the left/right rotation
			if (lrAng != 0.0) {
				this.genRotQuat(lrAng, 1, this.lrQuat);
				this.vpQuat.mul(this.lrQuat, this.vpQuat);
			}
			//Apply the up/down rotation
			if (udAng != 0.0) {
				this.genRotQuat(udAng, 0, this.udQuat);
				this.vpQuat.mul(this.udQuat, this.vpQuat);
			}
			//Apply the roll rotation
			if (rolAng != 0.0){
				this.genRotQuat(rolAng, 2, this.rolQuat);
				this.vpQuat.mul(this.rolQuat, this.vpQuat);
			}
			
			//Handle rotations to ensure correct position relative to direction changes
			this.vpQuat.inverse();
			this.vpQuat.normalize();
			this.mat.set(this.vpQuat);
			this.mat.transform(this.dp);
			
			this.vpPos.add(this.dp);
			if ((this.key_state & 512) != 0) {
				this.resetVelocity();
				this.vpScale = this.nominal.get(this.vpQuat, this.vpPos);
				this.curSpeed = 0.0d;
			}
			
			//Set up camera position
			Transform3D offset = new Transform3D();
			Vector3d up = new Vector3d(0, 1, 0);
			Point3d camLoc = new Point3d(0.0d, 1.5d, 5.0d);
			//Use the existing matrix to get the relative 'up' and camera offset locations
			this.mat.transform(camLoc);
			this.mat.transform(up);
			offset.lookAt(
					new Point3d(this.vpPos.x - camLoc.x, this.vpPos.y + camLoc.y, this.vpPos.z - camLoc.z - 1),
					new Point3d(this.vpPos),
					up
			);
			offset.invert();
			vpTrans.invert();
			//Apply the updated transform to the group.
			this.vpTrans.set(this.vpQuat, this.vpPos, 1.0d);
			this.targetTG.setTransform(this.vpTrans);
			this.followTG.setTransform(offset);
		}
		else {
			resetVelocity();
			vpTrans.setTranslation(new Vector3d(0,0,0));
			this.targetTG.setTransform(vpTrans);
		}
		}
	
	private void resetVelocity() {
		this.navVec.x = this.navVec.y = this.navVec.z = 0.0;
	}
	
	public void processKeyEvent(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		int keyChar = keyEvent.getKeyChar();
		if (keyEvent.getID() == 402) {
			if (keyChar == '+') {
				this.key_state &= -17;
			} else {
				switch (keyCode) {
					case 38:	//'Up arrow'
						this.key_state &= -2;
						break;
					case 40:	//'Down arrow'
						this.key_state &= -3;
						break;
					case 61:	//'='
						this.key_state &= -513;
						break;
					case 65:	//'A'
						this.key_state &= -5;
						break;
					case 68:	//'D'
						this.key_state &= -9;
						break;
					case 69:	//'E'
						this.key_state &= -17;
						break;
					case 81:
						this.key_state &= -33;
						break;
					case 83:	//'S'
						this.key_state &= -129;
						break;
					case 87:	//'W'
						this.key_state &= -65;
						break;
					default:
						switch (keyChar) {
							case '-':
								this.key_state &= 257;
						}
				}
			}
		} else if (keyEvent.getID() == 401) {
			if (keyChar == '+') {
				this.key_state |= 16;
			}
			
			switch (keyCode) {
				case 38:	//'Up arrow'
					this.key_state |= 1;
					break;
				case 40:	//'Down arrow'
					this.key_state |= 2;
					break;
				case 61:	//'='
					this.key_state |= 512;
					break;
				case 65:	//'A'
					this.key_state |= 4;
					break;
				case 68:	//'D'
					this.key_state |= 8;
					break;
				case 69:	//'E'
					this.key_state |= 16;
					break;
				case 81:
					this.key_state |= 32;
					break;
				case 83:	//'S'
					this.key_state |= 128;
					break;
				case 87:	//'W'
					this.key_state |= 64;
					break;
				default:
					switch (keyChar) {
						case '-':
							this.key_state |= 256;
					}
			}
		}
		
		if (keyEvent.isShiftDown()) {
			this.modifier_key_state |= 1024;
		} else {
			this.modifier_key_state &= -1025;
		}
		
		if (keyEvent.isMetaDown()) {
			this.modifier_key_state |= 4096;
		} else {
			this.modifier_key_state &= -4097;
		}
		
		if (keyEvent.isAltDown()) {
			this.modifier_key_state |= 2048;
		} else {
			this.modifier_key_state &= -2049;
		}
		
	}
}