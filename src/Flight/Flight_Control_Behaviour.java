package Flight;

/*
* This is the behaviour dedicated to registering keypresses towards flying the plane
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

import org.jogamp.java3d.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;

public class Flight_Control_Behaviour extends Behavior implements KeyListener {
	private WakeupCriterion k_down;
	private WakeupCriterion k_up;
	private WakeupOnElapsedFrames frame;
	private WakeupCondition wake;
	private Boolean listener;
	private Flight_Control keyNavigator_target;
	private KeyEvent k_event;
	private LinkedList eventq;

	private WakeupCriterion[] wakeList;
	public Flight_Control_Behaviour(TransformGroup target, TransformGroup view) {
		target.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		this.k_down = new WakeupOnAWTEvent(401);
		this.k_up = new WakeupOnAWTEvent(402);
		this.frame = new WakeupOnElapsedFrames(0);
		this.wakeList = new WakeupCriterion[]{this.k_down, this.k_up, this.frame};
		this.wake = new WakeupOr(this.wakeList);
		this.listener = false;
		this.keyNavigator_target = new Flight_Control(target, view);
	}
	
	public void initialize() {
		if (this.listener){
			this.k_down = new WakeupOnBehaviorPost(this,401);
			this.k_up = new WakeupOnBehaviorPost(this, 402);
			this.wakeList[0] = k_down;
			this.wakeList[1] = k_up;
			this.wake = new WakeupOr(wakeList);
			this.eventq = new LinkedList();
		}
		this.wakeupOn(this.wake);
	}
	
	@Override
	public void processStimulus(Iterator<WakeupCriterion> criteria) {
		boolean sawFrame = false;
		
		while(true){
			while (criteria.hasNext()){	//Loop across the iterator of all wakeup events
				WakeupCriterion event = criteria.next();
				if (event instanceof WakeupOnAWTEvent){
					WakeupOnAWTEvent event_AWT = (WakeupOnAWTEvent) event;
					AWTEvent[] events = event_AWT.getAWTEvent();
					this.processAWTEvent(events);
				} else if (event instanceof WakeupOnElapsedFrames && this.k_event != null) {
					sawFrame = true;	//Update once per frame
				} else if (event instanceof WakeupOnBehaviorPost) {
					while(true) {
						synchronized (this.eventq) { 	//Use a thread to read in key events and not back them up
							if(this.eventq.isEmpty()) {
								break;
							}
							this.k_event = (KeyEvent) this.eventq.remove(0);	//Pull the bottom event off
								//if it's a key press or key release event, process it
							if(this.k_event.getID() == 401 || this.k_event.getID() == 402) {
								this.keyNavigator_target.processKeyEvent(this.k_event);
							}
						}
					}
				}
			}
			if(sawFrame){	//If a frame has passed since last update, update position
				this.keyNavigator_target.integrateTransformChanges();
			}
			this.wakeupOn(wake);
			return;
		}
	}
	
	
	//When the behaviour is fired, make sure it was a key pressed or key released event from the Posts
	private void processAWTEvent(AWTEvent[] events){
		for(int loop = 0; loop < events.length; ++loop) {
			if (events[loop] instanceof KeyEvent) {
				this.k_event = (KeyEvent)events[loop];
				if (this.k_event.getID() == 401 || this.k_event.getID() == 402) {
					//System.out.println("AWTEvent");	//Temp
					this.keyNavigator_target.processKeyEvent(this.k_event);
				}
			}
		}
	}
	
	
	//Handle keypresses
	@Override
	public void keyPressed(KeyEvent e) {
		synchronized(this.eventq) {
			this.eventq.add(e);
			if (this.eventq.size() == 1) {
				this.postId(401);
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		synchronized(this.eventq) {
			this.eventq.add(e);
			if (this.eventq.size() == 1) {
				this.postId(402);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }
}
