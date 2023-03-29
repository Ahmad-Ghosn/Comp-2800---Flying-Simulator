//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Flight;

import java.awt.event.KeyEvent;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.Matrix4d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Quat4d;
import org.jogamp.vecmath.Vector3d;

public class Flight_Control {
	private Vector3d navVec;
	private long time;
	private Vector3d fwdAcc;
	private Vector3d bwdAcc;
	private Vector3d leftAcc;
	private Vector3d rightAcc;
	private Vector3d upAcc;
	private Vector3d downAcc;
	private Vector3d fwdDrag;
	private Vector3d bwdDrag;
	private Vector3d leftDrag;
	private Vector3d rightDrag;
	private Vector3d upDrag;
	private Vector3d downDrag;
	private double fwdVMax;
	private double bwdVMax;
	private double leftVMax;
	private double rightVMax;
	private double upVMax;
	private double downVMax;
	private float leftRotAngle;
	private float rightRotAngle;
	private float upRotAngle;
	private float downRotAngle;
	private double mmx;
	private Vector3d a = new Vector3d();
	private Vector3d dv = new Vector3d();
	private Point3d dp = new Point3d();
	private Quat4d udQuat = new Quat4d();
	private Quat4d lrQuat = new Quat4d();
	private Vector3d vpPos = new Vector3d();
	private double vpScale;
	private Quat4d vpQuat = new Quat4d();
	private Matrix4d vpMatrix = new Matrix4d();
	private Transform3D vpTrans = new Transform3D();
	private Matrix4d mat = new Matrix4d();
	private Vector3d nda = new Vector3d();
	private Vector3d temp = new Vector3d();
	private Transform3D nominal = new Transform3D();
	private TransformGroup targetTG;
	private static final int UP_ARROW = 1;
	private static final int DOWN_ARROW = 2;
	private static final int LEFT_ARROW = 4;
	private static final int RIGHT_ARROW = 8;
	private static final int PLUS_SIGN = 16;
	private static final int MINUS_SIGN = 32;
	private static final int PAGE_UP = 64;
	private static final int PAGE_DOWN = 128;
	private static final int HOME_DIR = 256;
	private static final int HOME_NOMINAL = 512;
	private static final int SHIFT = 1024;
	private static final int ALT = 2048;
	private static final int META = 4096;
	private static final int KEY_UP = 8192;
	private static final int KEY_DOWN = 16384;
	private int key_state = 0;
	private int modifier_key_state = 0;
	
	public Flight_Control(TransformGroup targetTG) {
		this.targetTG = targetTG;
		targetTG.getTransform(this.nominal);
		this.mmx = 128.0;
		this.navVec = new Vector3d(0.0, 0.0, 0.0);
		this.fwdAcc = new Vector3d(0.0, 0.0, -this.mmx);
		this.bwdAcc = new Vector3d(0.0, 0.0, this.mmx);
		this.leftAcc = new Vector3d(-this.mmx, 0.0, 0.0);
		this.rightAcc = new Vector3d(this.mmx, 0.0, 0.0);
		this.upAcc = new Vector3d(0.0, this.mmx, 0.0);
		this.downAcc = new Vector3d(0.0, -this.mmx, 0.0);
		this.fwdDrag = new Vector3d(0.0, 0.0, this.mmx);
		this.bwdDrag = new Vector3d(0.0, 0.0, -this.mmx);
		this.leftDrag = new Vector3d(this.mmx, 0.0, 0.0);
		this.rightDrag = new Vector3d(-this.mmx, 0.0, 0.0);
		this.upDrag = new Vector3d(0.0, -this.mmx, 0.0);
		this.downDrag = new Vector3d(0.0, this.mmx, 0.0);
		this.fwdVMax = -this.mmx;
		this.bwdVMax = this.mmx;
		this.leftVMax = -this.mmx;
		this.rightVMax = this.mmx;
		this.upVMax = this.mmx;
		this.downVMax = -this.mmx;
		this.leftRotAngle = -2.0943952F;
		this.rightRotAngle = 2.0943952F;
		this.upRotAngle = 2.0943952F;
		this.downRotAngle = -2.0943952F;
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
	
	private void accKeyAdd(Vector3d a, Vector3d da, Vector3d drag, double scaleVel) {
		this.nda.scale(scaleVel, da);
		this.nda.sub(drag);
		a.add(this.nda);
	}
	
	public void integrateTransformChanges() {
		this.targetTG.getTransform(this.vpTrans);
		this.vpScale = this.vpTrans.get(this.vpQuat, this.vpPos);
		double deltaTime = (double)this.getDeltaTime();
		deltaTime *= 0.001;
		double scaleVel;
		double scaleRot;
		double scaleScale;
		if ((this.modifier_key_state & 1024) != 0 && (this.modifier_key_state & 4096) == 0) {
			scaleVel = 3.0;
			scaleRot = 2.0;
			scaleScale = 4.0;
		} else if ((this.modifier_key_state & 1024) == 0 && (this.modifier_key_state & 4096) != 0) {
			scaleVel = 0.1;
			scaleRot = 0.1;
			scaleScale = 0.1;
		} else if ((this.modifier_key_state & 1024) != 0 && (this.modifier_key_state & 4096) != 0) {
			scaleVel = 0.3;
			scaleRot = 0.5;
			scaleScale = 0.1;
		} else {
			scaleVel = 1.0;
			scaleRot = 1.0;
			scaleScale = 4.0;
		}
		
		this.a.x = this.a.y = this.a.z = 0.0;
		if ((this.key_state & 1) != 0 && (this.key_state & 2) == 0) {
			this.accKeyAdd(this.a, this.fwdAcc, this.fwdDrag, scaleVel);
		} else if ((this.key_state & 1) == 0 && (this.key_state & 2) != 0) {
			this.accKeyAdd(this.a, this.bwdAcc, this.bwdDrag, scaleVel);
		}
		
		if ((this.modifier_key_state & 2048) != 0 && (this.key_state & 4) != 0 && (this.key_state & 8) == 0) {
			this.accKeyAdd(this.a, this.leftAcc, this.leftDrag, scaleVel);
		} else if ((this.modifier_key_state & 2048) != 0 && (this.key_state & 4) == 0 && (this.key_state & 8) != 0) {
			this.accKeyAdd(this.a, this.rightAcc, this.rightDrag, scaleVel);
		}
		
		if ((this.modifier_key_state & 2048) != 0 && (this.key_state & 64) != 0 && (this.key_state & 128) == 0) {
			this.accKeyAdd(this.a, this.upAcc, this.upDrag, scaleVel);
		} else if ((this.modifier_key_state & 2048) != 0 && (this.key_state & 64) == 0 && (this.key_state & 128) != 0) {
			this.accKeyAdd(this.a, this.downAcc, this.downDrag, scaleVel);
		}
		
		double pre = this.navVec.z + this.a.z * deltaTime;
		Vector3d var10000;
		if (pre < 0.0) {
			if (pre + this.fwdDrag.z * deltaTime < 0.0) {
				this.a.add(this.fwdDrag);
			} else {
				var10000 = this.a;
				var10000.z -= pre / deltaTime;
			}
		} else if (pre > 0.0) {
			if (pre + this.bwdDrag.z * deltaTime > 0.0) {
				this.a.add(this.bwdDrag);
			} else {
				var10000 = this.a;
				var10000.z -= pre / deltaTime;
			}
		}
		
		pre = this.navVec.x + this.a.x * deltaTime;
		if (pre < 0.0) {
			if (pre + this.leftDrag.x * deltaTime < 0.0) {
				this.a.add(this.leftDrag);
			} else {
				var10000 = this.a;
				var10000.x -= pre / deltaTime;
			}
		} else if (pre > 0.0) {
			if (pre + this.rightDrag.x * deltaTime > 0.0) {
				this.a.add(this.rightDrag);
			} else {
				var10000 = this.a;
				var10000.x -= pre / deltaTime;
			}
		}
		
		pre = this.navVec.y + this.a.y * deltaTime;
		if (pre < 0.0) {
			if (pre + this.downDrag.y * deltaTime < 0.0) {
				this.a.add(this.downDrag);
			} else {
				var10000 = this.a;
				var10000.y -= pre / deltaTime;
			}
		} else if (pre > 0.0) {
			if (pre + this.upDrag.y * deltaTime > 0.0) {
				this.a.add(this.upDrag);
			} else {
				var10000 = this.a;
				var10000.y -= pre / deltaTime;
			}
		}
		
		this.dv.scale(deltaTime, this.a);
		this.navVec.add(this.dv);
		if (this.navVec.z < scaleVel * this.fwdVMax) {
			this.navVec.z = scaleVel * this.fwdVMax;
		}
		
		if (this.navVec.z > scaleVel * this.bwdVMax) {
			this.navVec.z = scaleVel * this.bwdVMax;
		}
		
		if (this.navVec.x < scaleVel * this.leftVMax) {
			this.navVec.x = scaleVel * this.leftVMax;
		}
		
		if (this.navVec.x > scaleVel * this.rightVMax) {
			this.navVec.x = scaleVel * this.rightVMax;
		}
		
		if (this.navVec.y > scaleVel * this.upVMax) {
			this.navVec.y = scaleVel * this.upVMax;
		}
		
		if (this.navVec.y < scaleVel * this.downVMax) {
			this.navVec.y = scaleVel * this.downVMax;
		}
		
		this.dp.scale(deltaTime, this.navVec);
		double r = this.vpScale / 1.0;
		this.dp.scale(r, this.dp);
		double lrAng = 0.0;
		double udAng = 0.0;
		if ((this.modifier_key_state & 2048) == 0 && (this.key_state & 4) != 0 && (this.key_state & 8) == 0) {
			lrAng = (double)this.leftRotAngle;
		} else if ((this.modifier_key_state & 2048) == 0 && (this.key_state & 4) == 0 && (this.key_state & 8) != 0) {
			lrAng = (double)this.rightRotAngle;
		}
		
		if ((this.modifier_key_state & 2048) == 0 && (this.key_state & 64) != 0 && (this.key_state & 128) == 0) {
			udAng = (double)this.upRotAngle;
		} else if ((this.modifier_key_state & 2048) == 0 && (this.key_state & 64) == 0 && (this.key_state & 128) != 0) {
			udAng = (double)this.downRotAngle;
		}
		
		lrAng *= scaleRot;
		udAng *= scaleRot;
		lrAng *= deltaTime;
		udAng *= deltaTime;
		this.vpQuat.inverse();
		if (lrAng != 0.0) {
			this.genRotQuat(lrAng, 1, this.lrQuat);
			this.vpQuat.mul(this.lrQuat, this.vpQuat);
		}
		
		if (udAng != 0.0) {
			this.genRotQuat(udAng, 0, this.udQuat);
			this.vpQuat.mul(this.udQuat, this.vpQuat);
		}
		
		this.vpQuat.inverse();
		this.vpQuat.normalize();
		this.mat.set(this.vpQuat);
		this.mat.transform(this.dp);
		if ((this.key_state & 16) != 0) {
			this.vpScale *= 1.0 + scaleScale * deltaTime;
			if (this.vpScale > 1.0E15) {
				this.vpScale = 1.0;
			}
		} else if ((this.key_state & 32) != 0) {
			this.vpScale /= 1.0 + scaleScale * deltaTime;
			if (this.vpScale < 1.0E-13) {
				this.vpScale = 1.0;
			}
		}
		
		this.vpPos.add(this.dp);
		if ((this.key_state & 512) != 0) {
			this.resetVelocity();
			this.vpScale = this.nominal.get(this.vpQuat, this.vpPos);
		}
		
		this.vpTrans.set(this.vpQuat, this.vpPos, this.vpScale);
		this.targetTG.setTransform(this.vpTrans);
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
					case 33:
						this.key_state &= -65;
						break;
					case 34:
						this.key_state &= -129;
						break;
					case 65:
						this.key_state &= -5;
						break;
					case 87:
						this.key_state &= -2;
						break;
					case 68:
						this.key_state &= -9;
						break;
					case 83:
						this.key_state &= -3;
						break;
					case 61:
						this.key_state &= -513;
						break;
					default:
						switch (keyChar) {
							case '-':
								this.key_state &= -33;
						}
				}
			}
		} else if (keyEvent.getID() == 401) {
			if (keyChar == '+') {
				this.key_state |= 16;
			}
			
			switch (keyCode) {
				case 33:
					this.key_state |= 64;
					break;
				case 34:
					this.key_state |= 128;
					break;
				case 65:
					this.key_state |= 4;
					break;
				case 87:
					this.key_state |= 1;
					break;
				case 68:
					this.key_state |= 8;
					break;
				case 83:
					this.key_state |= 2;
					break;
				case 61:
					this.key_state |= 512;
					break;
				default:
					switch (keyChar) {
						case '-':
							this.key_state |= 32;
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
