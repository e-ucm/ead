/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.EnginePlayer;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;
import es.eucm.ead.schemax.GameStructure;

/**
 * View that shows the engine in debug mode
 */
public class PlayView implements ViewBuilder {

	private static final String IC_GO_BACK = "back80x80";

	private Controller controller;

	private EnginePlayer enginePlayer;
	private Stack window;

	@Override
	public void initialize(final Controller controller) {
		this.controller = controller;
		enginePlayer = new EnginePlayer(controller.getEngine().getGameLoop());
		enginePlayer.setFillParent(true);

		Skin skin = controller.getApplicationAssets().getSkin();
		float viewportHeight = controller.getPlatform().getSize().y;
		float iconSize = viewportHeight * BaseGallery.ICON_SIZE;
		float iconPad = viewportHeight * BaseGallery.ICON_PAD;
		Button back = new ToolbarIcon(IC_GO_BACK, iconPad, iconSize, skin,
				"inverted") {
			@Override
			public void layout() {
				super.layout();
				setBounds(0f, getParent().getHeight() - getPrefHeight(),
						getPrefWidth(), getPrefHeight());
			}
		};
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((MockupViews) controller.getViews()).onBackPressed();
			}
		});

		window = new Stack();
		window.setFillParent(true);
		window.add(enginePlayer);
		window.add(back);
	}

	@Override
	public Actor getView(Object... args) {
		controller.action(ForceSave.class);
		Engine engine = controller.getEngine();
		engine.getGameLoader().loaded(GameStructure.GAME_FILE,
				controller.getModel().getGame());
		engine.setGameView(enginePlayer);
		engine.play();
		return window;
	}

	@Override
	public void release(Controller controller) {
		Engine engine = controller.getEngine();
		engine.stop();
		engine.setGameView(null);
	}
}
