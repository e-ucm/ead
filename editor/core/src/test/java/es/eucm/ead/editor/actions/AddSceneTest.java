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
package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddSceneTest extends EditorActionTest {

	/**
	 * Notifications counts the number of times the model is modified with an
	 * addscene action. It should get as high as the number of commmands
	 * {@link es.eucm.ead.editor.control.actions.AddScene} generates (currently
	 * 4)
	 */
	private int notifications;

	/**
	 * The total number of commands
	 * {@link es.eucm.ead.editor.control.actions.AddScene} creates. Currently
	 * this is 4.
	 */
	private int numberOfCommandsForAddingScene = 4;

	@Override
	protected Class getEditorAction() {
		return AddScene.class;
	}

	@Before
	public void setUp() {
		super.setUp();
		notifications = 0;
		mockModel.setGame(new Game());
		mockModel.setGameMetadata(new GameMetadata());
	}

	@Test
	public void testAdd() {
		Map<String, Scene> scenes = mockModel.getScenes();
		scenes.clear();

		Map<String, SceneMetadata> scenesMetadata = mockModel
				.getScenesMetadata();
		scenesMetadata.clear();

		mockController.getModel().addMapListener(scenes,
				new ModelListener<MapEvent>() {
					@Override
					public void modelChanged(MapEvent event) {
						assertEquals(event.getMap().size(), 1);
						notifications++;
					}
				});

		mockController.getModel().addMapListener(scenesMetadata,
				new ModelListener<MapEvent>() {
					@Override
					public void modelChanged(MapEvent event) {
						assertEquals(event.getMap().size(), 1);
						notifications++;
					}
				});

		mockModel.addListListener(mockModel.getGameMetadata().getSceneorder(),
				new ModelListener<ListEvent>() {
					@Override
					public void modelChanged(ListEvent event) {
						assertEquals(mockModel.getGameMetadata()
								.getSceneorder().size(), 1);
						assertTrue(mockModel.getGameMetadata().getSceneorder()
								.contains("scene0"));
						notifications++;
					}
				});

		mockModel.addFieldListener(mockModel.getGameMetadata(),
				new Model.FieldListener() {
					@Override
					public boolean listenToField(FieldNames fieldName) {
						return fieldName == FieldNames.EDIT_SCENE;
					}

					@Override
					public void modelChanged(FieldEvent event) {
						assertEquals("scene0", mockModel.getGameMetadata()
								.getEditScene());
						notifications++;
					}
				});

		mockController.action(action);
		assertEquals(notifications, numberOfCommandsForAddingScene);
	}
}
