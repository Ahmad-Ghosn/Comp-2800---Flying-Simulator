package Project;

import java.io.File;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.utils.image.TextureLoader;

public class TextureLoad {
	public static void loadTexture(String filename, Shape3D shape) {
		TextureLoader text = new TextureLoader("src\\Project\\textures\\" + filename + ".jpg", null);
        ImageComponent2D image = text.getImage(); 
        Texture2D texts = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        texts.setImage(0, image);
        Appearance app = new Appearance();
        app.setTexture(texts);
        shape.setAppearance(app);	
	}
}
