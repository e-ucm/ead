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
package es.eucm.ead.editor.indexes;

import java.util.Map.Entry;

import javax.annotation.Resources;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Relates scene names (stored in {@link Documentation} component) with scene
 * ids
 */
public class SceneNamesIndex extends ControllerIndex implements
		ModelListener<ResourceEvent> {

	@Override
	public void initialize(Controller controller) {
		Model model = controller.getModel();
		model.addResourceListener(this);
		for (Entry<String, Resource> resource : model.getResources(
				ResourceCategory.SCENE).entrySet()) {
			ModelEntity scene = (ModelEntity) resource.getValue().getObject();
			addScene(resource.getKey(), scene);
		}
	}

	private void addScene(String id, ModelEntity scene) {
		addTerm(Q.getName(scene, ""), id);
	}

	@Override
	public void modelChanged(ResourceEvent event) {
		if (event.getCategory() == ResourceCategory.SCENE) {
			switch (event.getType()) {
			case ADDED:
				addScene(event.getId(), (ModelEntity) event.getResource());
				break;
			case REMOVED:
				removeTerm(Q.getName((ModelEntity) event.getResource(), ""),
						event.getId());
				break;
			}
		}
	}
}
