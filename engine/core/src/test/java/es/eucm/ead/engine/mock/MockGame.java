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
package es.eucm.ead.engine.mock;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.schema.actions.Action;

public class MockGame {

	private Actor actor;

	private MockApplication application;

	public static void initStatics() {
		new MockGame();
	}

	public MockGame() {
		this("testgame");
	}

	public MockGame(String path) {
		Engine engine = new Engine();
		application = new MockApplication(engine, 800, 600);
		application.start();
		engine.setLoadingPath(path, true);
	}

	public void act() {
		application.act();
	}

	/**
	 * @return Returns a dummy actor, with position set to (0, 0), scale set to
	 *         (1, 1), rotation set to 0, width and height set to 0 and color
	 *         set to #FFFFFFFF
	 */
	public Actor getDummyActor() {
		if (actor == null) {
			actor = new Actor();
			Engine.sceneView.getStage().addActor(actor);
		}
		return actor;
	}

	public void addActionToDummyActor(Action action) {
		Actor actor = getDummyActor();
		actor.addAction((com.badlogic.gdx.scenes.scene2d.Action) Engine.factory
				.getEngineObject(action));
	}

	/**
	 * Resets the dummy actor, setting its position to (0, 0), its scale to (1,
	 * 1), its rotation to 0, its width and height to 0 and its color to
	 * #FFFFFFFF
	 */
	public void resetDummyActor() {
		Actor actor = getDummyActor();
		actor.setPosition(0, 0);
		actor.setScale(1, 1);
		actor.setRotation(0);
		actor.setSize(0, 0);
		actor.setColor(Color.WHITE);
	}

	/**
	 * Generate a press event in the given screen coordinates
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public void press(int x, int y) {
		Engine.stage.touchDown(x, y, 0, Buttons.LEFT);
	}

	/**
	 * Generate a release event in the given screen coordinates
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public void release(int x, int y) {
		Engine.stage.touchUp(x, y, 0, Buttons.LEFT);
	}

	public MockApplication getApplication() {
		return application;
	}
}
