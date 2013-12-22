package es.eucm.ead.mockup.core.view.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.mockup.core.control.IEventReceiver;
import es.eucm.ead.mockup.core.control.MockupController;

/**
 * It's responsible for drawing the screen.
 */
public class ScreenRenderer implements IEventReceiver{
	
	/**
	 * Used to draw the UI. 
	 * Has constant width and height defined in Constants.
	 */
	public static Stage stage;
	
	/**
	 * Used for the UI elements.
	 */
	public static Skin skin;
	
	/**
	 * Used to manage UI resources.
	 */
	public static AssetManager am;
	
	/**
	 * Static reference to the main controller.
	 */
	public static MockupController mockupController;
	
	/**
	 * Static reference to the default bitmap font.
	 */
	public static BitmapFont font;
	
	@Override
	public void create(){
		
	}
	
	@Override
	public void show(){
		
	}
	
	/**
	 * Renderer's loop.
	 */
	public void draw(){
		
	}
	
	@Override
	public void hide(){
		
	}
}
