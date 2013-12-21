package es.eucm.ead.mockup.core.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.mockup.core.control.handlers.ScreenHandler;

public class EventController {

	private ScreenHandler currentCtr;
	private InputMultiplexer multiplexer;

	public EventController() {
		this.multiplexer = new InputMultiplexer(ScreenHandler.stage, Engine.stage);

		Gdx.input.setInputProcessor(this.multiplexer);
	}

	public void act(float delta){
		this.currentCtr.act(delta);
	}

	public void changeTo(ScreenHandler next){

		if(currentCtr != null){
			this.currentCtr.hide();
		}
		this.currentCtr = next;
		this.currentCtr.show();
	}
}
