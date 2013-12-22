package es.eucm.ead.mockup.core.control;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.mockup.core.control.handlers.LoadingHandler;
import es.eucm.ead.mockup.core.control.handlers.MenuHandler;
import es.eucm.ead.mockup.core.control.handlers.ScreenHandler;
import es.eucm.ead.mockup.core.facade.IActionResolver;
import es.eucm.ead.mockup.core.model.Screens;
import es.eucm.ead.mockup.core.utils.Pair;
import es.eucm.ead.mockup.core.view.renderers.LoadingRenderer;
import es.eucm.ead.mockup.core.view.renderers.MenuRenderer;
import es.eucm.ead.mockup.core.view.renderers.ScreenRenderer;

/**
 * The main controller for Mockup Editor.
 * Controlls the different views or screens and stores a reference to
 * Editor's Controller providing access to 
 * <ul>
 * <li>persistent editor preferences</li>
 * <li>internationalized messages (i18n)</li>
 * <li>currently-edited game</li>
 * <li>project controller (in charge of creating and managing games)</li>
 * <li>command-manager (for undo/redo)</li>
 * <li>actions (reusable editor calls)</li>
 * </ul>
 */
public class MockupController{

	private Map<Screens, Pair<ScreenRenderer, ScreenHandler>> states = 
			new HashMap<Screens, Pair<ScreenRenderer, ScreenHandler>>();

	private Controller controller;
	private RendererController rendererCtr;	
	private EventController eventCtr;
	private IActionResolver resolver;

	public MockupController(IActionResolver resolver){
		this.resolver = resolver;
		ScreenHandler.mockupController = this;
		ScreenRenderer.mockupController = this;
		ScreenRenderer.am = new AssetManager();
		Gdx.input.setCatchBackKey(true);

		LoadingHandler lh = new LoadingHandler(ScreenRenderer.am);
		lh.create();
		LoadingRenderer lr = new LoadingRenderer(); 
		lr.create();
		this.states = new HashMap<Screens, Pair<ScreenRenderer, ScreenHandler>>();
		this.states.put(Screens.MENU, 
				new Pair<ScreenRenderer, ScreenHandler>
		(new MenuRenderer(), new MenuHandler()));
		this.states.put(Screens.LOADING, 
				new Pair<ScreenRenderer, ScreenHandler> 
		(lr, lh));

		this.eventCtr  = new EventController();
		this.rendererCtr = new RendererController();
		
		changeTo(Screens.LOADING);
	}

	public void create(){
		for(Pair<ScreenRenderer, ScreenHandler> _p : this.states.values()){
			_p.getFirst().create();
			_p.getSecond().create();
		}
		eventCtr.create();
	}

	/**
	 * Updates the handlers through EventController.
	 * @param delta Elapsed time since the game last updated.
	 */
	public void act(float delta){
		this.eventCtr.act(delta);
	}

	/**
	 * Draws the renderers through RendererController.
	 */
	public void draw() {
		this.rendererCtr.draw();		
	}

	public void changeTo(Screens next){
		Pair<ScreenRenderer, ScreenHandler> _p = this.states.get(next);
		this.rendererCtr.changeTo(_p.getFirst());
		this.eventCtr.changeTo(_p.getSecond());
	}

	public Controller getController() {
		return this.controller;
	}

	public IActionResolver getResolver() {
		return this.resolver;
	}

	public void resize(int width, int height) { }

	public void pause() {
		this.eventCtr.pause();
	}

	public void resume() {
		this.eventCtr.resume();
	}

	public void dispose() {
		ScreenRenderer.stage.dispose();
		ScreenRenderer.stage = ScreenHandler.stage = null;

		ScreenRenderer.am.dispose();
		ScreenRenderer.am = null;
	}
}
