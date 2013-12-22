package es.eucm.ead.mockup.core.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;

import es.eucm.ead.mockup.core.view.renderers.ScreenRenderer;

/**
 * Controlls the renderers, the clear color...
 *
 */
public class RendererController {

	public static Color CLEAR_COLOR = Color.BLACK;
	private ScreenRenderer currentRdr;

	public RendererController(){

	}

	/**
	 * Clears the screen and draws the current renderer.
	 */
	public void draw(){
		clearColor();
		this.currentRdr.draw();
	}
	
	private void clearColor(){
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(CLEAR_COLOR.r, CLEAR_COLOR.g, CLEAR_COLOR.b, CLEAR_COLOR.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Changes the current renderer to the next one.
	 * Triggers the hide() and show() events.
	 * 
	 * @param next The next renderer.
	 */
	public void changeTo(ScreenRenderer next){	

		if(currentRdr != null){
			this.currentRdr.hide();
		}
		this.currentRdr = next;
		this.currentRdr.show();
	}
}
