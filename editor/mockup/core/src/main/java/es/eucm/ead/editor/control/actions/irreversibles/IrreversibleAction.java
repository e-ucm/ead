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
package es.eucm.ead.editor.control.actions.irreversibles;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public abstract class IrreversibleAction extends EditorAction {

	private ResourceCategory category;

	public IrreversibleAction(ResourceCategory category) {
		this(category, false, false);
	}

	public IrreversibleAction(ResourceCategory category, boolean initialEnable,
			boolean allowNullArguments) {
		super(initialEnable, allowNullArguments);
		this.category = category;
	}

	public IrreversibleAction(ResourceCategory category, boolean initialEnable,
			boolean allowNullArguments, Class... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
		this.category = category;
	}

	public IrreversibleAction(ResourceCategory category, boolean initialEnable,
			boolean allowNullArguments, Class[]... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
		this.category = category;
	}

	@Override
	public void perform(Object... args) {
		ModelEntity entity = null;
		ModelEntity parent = null;
		if (category == ResourceCategory.SCENE) {
			entity = (ModelEntity) controller.getModel().getSelection()
					.getSingle(Selection.SCENE_ELEMENT);
			parent = (ModelEntity) controller.getModel().getSelection()
					.getSingle(Selection.SCENE);
		} else if (category == ResourceCategory.GAME) {
			entity = (ModelEntity) controller.getModel().getGame();
			parent = entity;
		}
		action(entity, args);

		Resource resourceFromObject = controller.getModel()
				.getResourceFromObject(parent, category);

		if (resourceFromObject != null) {
			resourceFromObject.setModified(true);
		}
	}

	protected abstract void action(ModelEntity entity, Object[] args);

}
