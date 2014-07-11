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
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;
import org.junit.Test;

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
	private int numberOfCommandsForAddingScene = 2;

	@Test
	public void testAdd() {
		notifications = 0;
		model.putResource(ResourceCategory.GAME.getCategoryPrefix(),
				new ModelEntity());
		Map<String, Object> scenes = model.getResources(ResourceCategory.SCENE);

		controller.getModel().addMapListener(scenes,
				new ModelListener<MapEvent>() {
					@Override
					public void modelChanged(MapEvent event) {
						assertEquals(1, event.getMap().size());
						notifications++;
					}
				});

		model.addListListener(Q.getComponent(model.getGame(), EditState.class)
				.getSceneorder(), new ModelListener<ListEvent>() {
			@Override
			public void modelChanged(ListEvent event) {
				assertEquals(Q.getComponent(model.getGame(), EditState.class)
						.getSceneorder().size, 1);
				assertTrue(Q.getComponent(model.getGame(), EditState.class)
						.getSceneorder().contains("scene0", false));
				notifications++;
			}
		});

		controller.action(AddScene.class);
		assertEquals(numberOfCommandsForAddingScene, notifications);
	}
}
