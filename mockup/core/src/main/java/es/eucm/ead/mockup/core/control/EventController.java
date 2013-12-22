package es.eucm.ead.mockup.core.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.mockup.core.control.handlers.ScreenHandler;

/**
 * Controlls the handlers, the input...
 */
public class EventController extends InputAdapter{

	private ScreenHandler currentCtr;
	private InputMultiplexer multiplexer;

	public EventController() {
		
	}
	
	public void create(){
		this.multiplexer = new InputMultiplexer(ScreenHandler.stage, this, Engine.stage);
		Gdx.input.setInputProcessor(this.multiplexer);
	}

	/**
	 * Updates the current renderer.
	 * @param delta Elapsed time since the game last updated.
	 */
	public void act(float delta){
		this.currentCtr.act(delta);
	}

	/**
	 * Changes the current handler to the next one.
	 * Triggers the hide() and show() events.
	 * 
	 * @param next The next handler.
	 */
	public void changeTo(ScreenHandler next){

		if(currentCtr != null){
			this.currentCtr.hide();
		}
		this.currentCtr = next;
		this.currentCtr.show();
	}

	public void pause() {
		this.currentCtr.pause();
	}

	public void resume() {
		this.currentCtr.resume();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK || keycode == Keys.BACKSPACE){
			this.currentCtr.onBackKeyPressed();
		}
		return true;
	}
}
