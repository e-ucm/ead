package es.eucm.ead.mockup.core.control;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.mockup.core.control.handlers.MenuScreenHandler;
import es.eucm.ead.mockup.core.control.handlers.ScreenHandler;
import es.eucm.ead.mockup.core.facade.IActionResolver;
import es.eucm.ead.mockup.core.mockupengine.MockupEngine;
import es.eucm.ead.mockup.core.mockupengine.MockupEventListener;
import es.eucm.ead.mockup.core.model.Screens;
import es.eucm.ead.mockup.core.utils.Constants;
import es.eucm.ead.mockup.core.utils.Pair;
import es.eucm.ead.mockup.core.view.renderers.MenuRenderer;
import es.eucm.ead.mockup.core.view.renderers.ScreenRenderer;

public class MockupController{
	
	private Map<Screens, Pair<ScreenRenderer, ScreenHandler>> states = 
			new HashMap<Screens, Pair<ScreenRenderer, ScreenHandler>>();
	{
		this.states.put(Screens.MENU, 
				new Pair<ScreenRenderer, ScreenHandler>
				(new MenuRenderer(), new MenuScreenHandler()));
	}
	
	private Controller controller;
	private RendererController rendererCtr;	
	private EventController eventCtr;
	private IActionResolver resolver;
	
	public MockupController(IActionResolver resolver){
		this.resolver = resolver;
		
		Stage s = new Stage(Constants.SCREENW, Constants.SCREENH, true);
		ScreenRenderer.stage = s;
		ScreenHandler.stage = s;
		ScreenRenderer.skin = new Skin(Gdx.files.internal("mockup/skin/holo-dark-xhdpi.json"));
		Gdx.input.setCatchBackKey(true);
		//ScreenRenderer.sb = Engine.stage.getSpriteBatch();
		//De esta mamera utilizamos la matriz de proyección del Engine para el canvas de dibujado.
		
		
		//Ejemplo de creación de un posible MockupEngineController
		MockupEventListener myListenerController = new MockupEventListener();
		MockupEngine me = new MockupEngine(null);
		me.setMockupEventListener(myListenerController);
		me.create();
		
		this.eventCtr  = new EventController();
		this.rendererCtr = new RendererController();
		
		create();
		
		changeTo(Screens.MENU);
	}
	
	private void create(){
		for(Pair<ScreenRenderer, ScreenHandler> _p : this.states.values()){
			_p.getFirst().create();
			_p.getSecond().create();
		}
	}
	
	public void act(float delta){
		this.eventCtr.act(delta);
	}

	public void draw() {
		this.rendererCtr.draw();		
	}
	
	public void changeTo(Screens next){
		Pair<ScreenRenderer, ScreenHandler> _p = this.states.get(next);
		this.rendererCtr.changeTo(_p.getFirst());
		this.eventCtr.changeTo(_p.getSecond());
	}

	public void resize() { }

	public void pause() {
		
	}

	public void resume() {
		
	}

	public void dispose() {
		
	}
	
}
