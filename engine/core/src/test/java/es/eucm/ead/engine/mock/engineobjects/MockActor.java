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
package es.eucm.ead.engine.mock.engineobjects;

import es.eucm.ead.engine.actions.AbstractAction;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.actions.Action;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MockActor extends SceneElementActor {

	private List<Action> actionsExpected;

	public MockActor() {
		super();
		actionsExpected = new ArrayList<Action>();
	}

	/**
	 * Tells the test game to wait for an action in the global space. If a
	 * different action comes, an assertion fail is invoked
	 * 
	 * @param action
	 *            the action to expect
	 * @return itself
	 */
	public MockActor expectAction(Action action) {
		actionsExpected.add(action);
		return this;
	}

	@Override
	public void addAction(com.badlogic.gdx.scenes.scene2d.Action action) {
		super.addAction(action);
		check(action);
	}

	private void check(com.badlogic.gdx.scenes.scene2d.Action action) {
		if (action instanceof AbstractAction) {
			check(((AbstractAction) action).getSchema());
		}
	}

	private void check(Action action) {
		if (!actionsExpected.isEmpty()) {
			assertTrue(actionsExpected.remove(0) == action);
		}
	}

	public boolean expectingActions() {
		return !actionsExpected.isEmpty();
	}
}
