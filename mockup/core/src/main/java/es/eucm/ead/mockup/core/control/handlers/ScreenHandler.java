package es.eucm.ead.mockup.core.control.handlers;

import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.ead.mockup.core.control.IEventReceiver;
import es.eucm.ead.mockup.core.control.MockupController;
import es.eucm.ead.mockup.core.model.Screens;

/**
 * It's responsible for updating the screen.
 * 
 * TEMPORAL: Also handles other events like touchDown, touchDragged or 
 * keyPressed(to handle Keys.Back button event on devices).
 */
public class ScreenHandler implements IEventReceiver{

	/**
	 * Used to draw the UI. 
	 * Has constant width and height defined in Constants.
	 */
	public static Stage stage;	
	
	/**
	 * Static reference to the main controller.
	 */
	public static MockupController mockupController;
	
	/**
	 * Used to go to navigate to the previous screen when the
	 * Kays.Back button is pressed.
	 * (When {onBackKeyPressed()} is triggered) 
	 */
	private Screens previousScreen;

	@Override
	public void create() {
		
	}

	@Override
	public void show() {
		
	}	
	
	/**
	 * Updates the screen.
	 * @param delta elapsed time since the last time.
	 */
	public void act(float delta){	
		
	}

	@Override
	public void hide() {
		
	}
	
	public void pause(){
		
	}
	
	public void resume(){
		
	}
	
	/**
	 * previousScreen must be configured or onBackKeyPressed() will throw an IllegalStateException.
	 * You could also override {onBackKeyPressed()} method instead.
	 * 
	 * @param previousScreen
	 */
	public void setPreviousScreen(Screens previousScreen) {
		this.previousScreen = previousScreen;
	}

	/**
	 * Executed when BACK key was pressed.
	 * If previousScreen is null an IllegalStateException will be thrown.
	 */
	public void onBackKeyPressed() {
		if(previousScreen == null) {
			throw new IllegalStateException("previousScreen is null in " + 
							this.getClass().getSimpleName() + 
							" please configure previousScreen via {setPreviousScreen(Screens previousScreen)} method" +
							" or @Override {onBackKeyPressed()}");
		}
		mockupController.changeTo(previousScreen);
	}

}
