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
package es.eucm.ead.editor.control.actions.model;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.schema.components.Reference;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

/**
 * 
 * <p>
 * Adds a reference to a library element to the current scene.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0] </strong> <em>{@link String}</em> The {@link RepoElement}
 * already imported to the local library.</dd>
 * </dl>
 * </p>
 * 
 */
public class AddLibraryReference extends ModelAction {

	private AddSceneElement addSceneElement;

	public AddLibraryReference() {
		super(true, false, String.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		addSceneElement = controller.getActions().getAction(
				AddSceneElement.class);
	}

	@Override
	public Command perform(Object... args) {
		ModelEntity entity = new ModelEntity();
		Reference ref = new Reference();
		ref.setId(args[0] + "/" + ModelStructure.CONTENTS_FOLDER
				+ ModelStructure.ENTITY_FILE);
		entity.getComponents().add(ref);
		return addSceneElement.perform(entity);
	}
}
