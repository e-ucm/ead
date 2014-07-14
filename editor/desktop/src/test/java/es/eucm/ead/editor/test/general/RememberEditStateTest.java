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
package es.eucm.ead.editor.test.general;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.views.HomeView;
import es.eucm.ead.editor.control.views.SceneView;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.nogui.EditorGUITest;
import es.eucm.ead.editor.nogui.actions.OpenMockGame;
import es.eucm.ead.editor.nogui.actions.OpenMockGame.Game;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests that the editor remembers the last state of edition
 */
public class RememberEditStateTest extends EditorGUITest {

	@Override
	protected void collectRunnables(Array<Runnable> runnables) {
		runnables.add(new Runnable() {
			@Override
			public void run() {
				Game game = defaultMockGame();
				controller.action(OpenMockGame.class, game);
				controller.action(ChangeView.class, SceneView.class,
						"scenes/scene1.json");
				controller.action(Save.class);

				// assert edit state has changed in save
				EditState editState = Q.getComponent(controller.getModel()
						.getGame(), EditState.class);

				assertEquals(editState.getView(), controller.getViews()
						.getCurrentView().getClass().getName());
				assertEquals(editState.getArguments().first(), controller
						.getViews().getCurrentArgs()[0]);

				controller.action(ChangeView.class, HomeView.class);
				controller.action(OpenGame.class, game.getPath());

				// assert the view has been reloaded
				assertEquals(SceneView.class, controller.getViews()
						.getCurrentView().getClass());
				assertEquals("scenes/scene1.json", controller.getViews()
						.getCurrentArgs()[0]);

			}
		});
	}

	private Game defaultMockGame() {
		Game game = new Game();
		for (int i = 0; i < 3; i++) {
			game.addScene("scenes/scene" + i + ".json", new ModelEntity());
		}
		File file = editorDesktop.getPlatform().createTempFile(true);
		game.setPath(file.getAbsolutePath(), false);
		return game;
	}
}
