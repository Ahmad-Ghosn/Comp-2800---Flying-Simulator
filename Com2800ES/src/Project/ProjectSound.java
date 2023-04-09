package Project;

/* Copyright material for the convenience of students working on Lab Exercises */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.jdesktop.j3d.examples.sound.PointSoundBehavior;
import org.jdesktop.j3d.examples.sound.audio.JOALMixer;



import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.vecmath.*;


public class ProjectSound extends JPanel implements KeyListener{

	public final static BoundingSphere hundredBS = new BoundingSphere(new Point3d(), 100.0);
    
    static Alpha rotationAlpha;
    static Alpha rotationBeta;
    
    private static PointSound s1;
    private static PointSound s2;
    
	private static final long serialVersionUID = 1L;
	private static JFrame frame;

	
	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Scene() throws IOException {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		

/*-------------------------------------------------------------------------------------------------------*/
		
		BufferedReader reader = new BufferedReader(new FileReader("src/codesAG280/AllSounds.txt"));
    	
		String fileName = reader.readLine();
    	s1 = addSound(fileName);
    	
    	reader.close();
    	
		sceneTG.addChild(s1);
		
    	s1.setMute(true);
    	

/*-------------------------------------------------------------------------------------------------------*/


		sceneBG.addChild(sceneTG);                         // keep the following stationary
		sceneBG.addChild(CommonsDS.add_Lights(CommonsDS.White, 1));

		return sceneBG;
	}

	/* NOTE: Keep the constructor for each of the labs and assignments */
	public ProjectSound(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		canvas.addKeyListener(this);
		
		
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		
		/*Here*/
		enableAudio(su);
		/*to Here*/
		
		CommonsDS.define_Viewer(su, new Point3d(0.25d, 0.25d, 10.0d));   // set the viewer's location
		
		sceneBG.addChild(CommonsDS.key_Navigation(su));               // allow key navigation
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		frame = new JFrame("AG's Project");                   // NOTE: change XY to student's initials
		try {
			frame.getContentPane().add(new ProjectSound(create_Scene()));
		} catch (IOException e) {
			e.printStackTrace();
		}  // start the program
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	

/*-------------------------------------------------------------------------------------------------------*/
	
	/* a function to enable audio device via JOAL */
	private void enableAudio(SimpleUniverse simple_U) {

		JOALMixer mixer = null;		                         // create a null mixer as a joalmixer
		Viewer viewer = simple_U.getViewer();
		viewer.getView().setBackClipDistance(20.0f);         // make object(s) disappear beyond 20f 

		if (mixer == null && viewer.getView().getUserHeadToVworldEnable()) {			                                                 
			mixer = new JOALMixer(viewer.getPhysicalEnvironment());
			if (!mixer.initialize()) {                       // add mixer as audio device if successful
				System.out.println("Open AL failed to init");
				viewer.getPhysicalEnvironment().setAudioDevice(null);
			}
		}
	}
	
/*-------------------------------------------------------------------------------------*/
	
	 private static PointSound addSound(String soundFile) throws IOException {
			URL url = null;
			String filename = "src/codesAG280/SoundFoulder/" + soundFile;
			try {
				url = new URL("file", "localhost", filename);
			} catch (Exception e) {
				System.out.println("Can't open " + filename);
			}
			
			PointSound s = new PointSound();                    // create and position a point sound		
			s.setCapability(s.ALLOW_MUTE_WRITE);
			s.setCapability(s.ALLOW_INITIAL_GAIN_WRITE);
			
			PointSoundBehavior player = new PointSoundBehavior(s, url, new Point3f(0.0f, 0.0f, 0.0f));
			player.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
			return s;
		}
	 

/*-------------------------------------------------------------------------------------------------------*/
	 
	@Override
	public void keyTyped(KeyEvent e) {
		// Auto-generated method stub
		
	}
	
	private float i = 1;
	
	@Override
	public void keyPressed(KeyEvent ke) {

/*-------------------------------------------------------------------------------------------------------*/
        if (ke.getKeyCode() == KeyEvent.VK_A) {
        	s1.setMute(false);						//set true or false
        }
        
        if (ke.getKeyCode() == KeyEvent.VK_S) {
        	i -= 0.1;
        	s1.setInitialGain(i);					//decreases the volume
        }
        if (ke.getKeyCode() == KeyEvent.VK_W) {
        	i += 0.1;
        	s1.setInitialGain(i);					//increases the volume
        }

/*-------------------------------------------------------------------------------------------------------*/
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// Auto-generated method stub
		
	}}