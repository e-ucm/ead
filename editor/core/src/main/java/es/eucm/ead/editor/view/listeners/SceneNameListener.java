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
package es.eucm.ead.editor.view.listeners;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * A {@link FieldListener} that gets notified when the name of the
 * {@link Documentation} of a given {@link ModelEntity} scene has changed. Has a
 * convenience method that is invoked when the value changed. See
 * {@link #nameChanged(String)}.
 * 
 */
public class SceneNameListener implements FieldListener {

	protected String sceneId;
	private Controller controller;
	private Documentation sceneDocumentation;

	public SceneNameListener(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Sets up this listener as the field listener of the {@link Documentation}
	 * of the provided scene.
	 * 
	 */
	public void setUp(ModelEntity scene) {
		if (scene == null) {
			return;
		}

		Model model = controller.getModel();
		Documentation documentation = Q
				.getComponent(scene, Documentation.class);
		if (documentation != sceneDocumentation) {
			remove();
			model.addFieldListener(documentation, this);
			sceneDocumentation = documentation;
			this.sceneId = model.getIdFor(scene);
		}
	}

	/**
	 * Removes this listener from its target, if available.
	 */
	public void remove() {
		if (sceneDocumentation != null) {
			controller.getModel().removeListener(sceneDocumentation, this);
		}
	}

	@Override
	public void modelChanged(FieldEvent event) {
		Object value = event.getValue();
		nameChanged(value == null ? sceneId : value.toString());
	}

	/**
	 * Invoked when the name field of the {@link Documentation} this listener is
	 * attached to has changed. If the new value of the field is null the name
	 * will be the scene id.
	 * 
	 * @param name
	 *            the new value or, if its null, the scene id
	 */
	public void nameChanged(String name) {

	}

	@Override
	public boolean listenToField(String fieldName) {
		return FieldName.NAME.equals(fieldName);
	}

}