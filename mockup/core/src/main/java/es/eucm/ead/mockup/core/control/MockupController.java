/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.mockup.core.control;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.mockup.core.control.handlers.LoadingHandler;
import es.eucm.ead.mockup.core.control.handlers.MainMenuHandler;
import es.eucm.ead.mockup.core.control.handlers.ProjectMenuHandler;
import es.eucm.ead.mockup.core.control.handlers.SceneEditionHandler;
import es.eucm.ead.mockup.core.control.handlers.ScreenHandler;
import es.eucm.ead.mockup.core.control.listeners.FocusListener;
import es.eucm.ead.mockup.core.facade.IActionResolver;
import es.eucm.ead.mockup.core.model.Screen;
import es.eucm.ead.mockup.core.model.Screens;
import es.eucm.ead.mockup.core.utils.Pair;
import es.eucm.ead.mockup.core.view.renderers.LoadingRenderer;
import es.eucm.ead.mockup.core.view.renderers.MainMenuRenderer;
import es.eucm.ead.mockup.core.view.renderers.ProjectMenuRenderer;
import es.eucm.ead.mockup.core.view.renderers.SceneEditionRenderer;
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
public class MockupController {

	private Map<Screens, Pair<ScreenRenderer, ScreenHandler>> states = new HashMap<Screens, Pair<ScreenRenderer, ScreenHandler>>();

	private Controller controller;
	private RendererController rendererCtr;
	private EventController eventCtr;
	private IActionResolver resolver;

	public MockupController(IActionResolver resolver) {
		this.resolver = resolver;
		ScreenHandler.mockupController = this;
		Screen.mockupController = this;
		Screen.am = new AssetManager();
		Gdx.input.setCatchBackKey(true);

		this.states = new HashMap<Screens, Pair<ScreenRenderer, ScreenHandler>>();
		this.states.put(Screens.MAIN_MENU,
				new Pair<ScreenRenderer, ScreenHandler>(new MainMenuRenderer(),
						new MainMenuHandler()));
		this.states.put(Screens.PROJECT_MENU,
				new Pair<ScreenRenderer, ScreenHandler>(
						new ProjectMenuRenderer(), new ProjectMenuHandler()));
		this.states.put(Screens.SCENE_EDITION,
				new Pair<ScreenRenderer, ScreenHandler>(
						new SceneEditionRenderer(), new SceneEditionHandler()));

		this.eventCtr = new EventController();
		this.rendererCtr = new RendererController();

		LoadingHandler lh = new LoadingHandler();
		LoadingRenderer lr = new LoadingRenderer();
		lh.create();
		lr.create();
		this.eventCtr.setCurrentController(lh);
		this.rendererCtr.setCurrentRenderer(lr);
	}

	public void create() {
		for (Pair<ScreenRenderer, ScreenHandler> _p : this.states.values()) {
			ScreenRenderer sr = _p.getFirst();
			ScreenHandler sh = _p.getSecond();
			sr.create();
			sh.create();
		}
		eventCtr.create();
	}

	/**
	 * Updates the handlers through EventController.
	 * @param delta Elapsed time since the game last updated.
	 */
	public void act(float delta) {
		this.eventCtr.act(delta);
	}

	/**
	 * Draws the renderers through RendererController.
	 */
	public void draw() {
		this.rendererCtr.draw();
	}

	public void changeTo(Screens next) {
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

	public void resize(int width, int height) {
	}

	public void pause() {
		this.eventCtr.pause();
	}

	public void resume() {
		this.eventCtr.resume();
	}

	public void dispose() {
		Screen.stage.dispose();
		Screen.stage = null;

		Screen.am.dispose();
		Screen.am = null;
	}

	public void show(FocusListener focusListener) {
		focusListener.show();
	}

	public void hide(FocusListener focusListener) {
		focusListener.hide();
	}
}
