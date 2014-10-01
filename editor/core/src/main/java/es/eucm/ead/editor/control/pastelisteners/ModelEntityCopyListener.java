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
package es.eucm.ead.editor.control.pastelisteners;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Clipboard.CopyListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildFromEntity;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;

public class ModelEntityCopyListener implements CopyListener<ModelEntity> {

	private static final float OFFSET = .007f;

	private Controller controller;

	public ModelEntityCopyListener(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void cut(ModelEntity object) {
		Parent parent = Q.getComponent(object, Parent.class);
		if (parent.getParent() != null) {
			controller.action(RemoveChildFromEntity.class, parent.getParent(),
					object);
		}
	}

	@Override
	public void paste(ModelEntity object) {
		controller.action(AddSceneElement.class, processPasteElement(object));
	}

	@Override
	public void paste(Array<ModelEntity> objects) {
		processPasteElements(objects);
		controller.action(AddSceneElement.class, objects);
	}

	private ModelEntity processPasteElement(ModelEntity object) {
		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);

		float offsetX = gameData.getWidth() * OFFSET;
		float offsetY = gameData.getHeight() * OFFSET;

		addOffset(object, offsetX, offsetY, gameData);

		return object;
	}

	private Array<ModelEntity> processPasteElements(Array<ModelEntity> objects) {
		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);

		float offsetX = gameData.getWidth() * OFFSET;
		float offsetY = gameData.getHeight() * OFFSET;

		for (ModelEntity object : objects) {
			addOffset(object, offsetX, offsetY, gameData);
		}

		return objects;
	}

	private void addOffset(ModelEntity object, float offsetX, float offsetY,
			GameData gameData) {
		float nextX = object.getX() + offsetX;
		if (nextX >= gameData.getWidth()) {
			object.setX(gameData.getWidth() - offsetX);
		} else {
			object.setX(nextX);
		}

		float nextY = object.getY() + offsetY;
		if (nextY >= gameData.getHeight()) {
			object.setY(gameData.getHeight() - offsetY);
		} else {
			object.setY(nextY);
		}
	}
}
