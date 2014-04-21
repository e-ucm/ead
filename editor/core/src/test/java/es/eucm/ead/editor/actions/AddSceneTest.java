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

import es.eucm.ead.editor.control.actions.model.AddScene;
import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddSceneTest extends ActionTest {

	/**
	 * Counts the number of times the model is modified with an {@link AddScene}
	 * action. It should get as high as the number of commmands {@link AddScene}
	 * generates ({@link #numberOfCommandsForAddingScene}).
	 */
	private int notifications;

	/**
	 * The total number of commands {@link AddScene} creates.
	 */
	private int numberOfCommandsForAddingScene = 3;

	@Test
	public void testAdd() {
		notifications = 0;
		mockModel.putEntity(ModelEntityCategory.GAME.getCategoryName(),
				new ModelEntity());
		Map<String, ModelEntity> scenes = mockModel
				.getEntities(ModelEntityCategory.SCENE);

		mockController.getModel().addMapListener(scenes,
				new ModelListener<MapEvent>() {
					@Override
					public void modelChanged(MapEvent event) {
						assertEquals(1, event.getMap().size());
						notifications++;
					}
				});

		mockModel.addListListener(
				Model.getComponent(mockModel.getGame(), EditState.class)
						.getSceneorder(), new ModelListener<ListEvent>() {
					@Override
					public void modelChanged(ListEvent event) {
						assertEquals(
								Model.getComponent(mockModel.getGame(),
										EditState.class).getSceneorder().size(),
								1);
						assertTrue(Model
								.getComponent(mockModel.getGame(),
										EditState.class).getSceneorder()
								.contains("scene0"));
						notifications++;
					}
				});

		mockModel.addFieldListener(
				Model.getComponent(mockModel.getGame(), EditState.class),
				new Model.FieldListener() {
					@Override
					public boolean listenToField(FieldNames fieldName) {
						return fieldName == FieldNames.EDIT_SCENE;
					}

					@Override
					public void modelChanged(FieldEvent event) {
						assertEquals(
								"scene0",
								Model.getComponent(mockModel.getGame(),
										EditState.class).getEditScene());
						notifications++;
					}
				});

		mockController.action(AddScene.class);
		assertEquals(numberOfCommandsForAddingScene, notifications);
	}
}
