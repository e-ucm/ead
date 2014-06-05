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
package es.eucm.ead.editor.control.actions.model.scene.transform;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.schema.entities.ModelEntity;

public abstract class TransformSelection extends ModelAction implements
		ModelListener<SelectionEvent> {

	protected TransformSelection(boolean initialEnable,
			boolean allowNullArguments, Class... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		updateEnable(controller.getModel().getSelection());
		controller.getModel().addSelectionListener(this);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		updateEnable(event.getSelection());
	}

	private void updateEnable(Array<Object> selection) {
		if (selection.size > 0) {
			for (Object o : selection) {
				if (!(o instanceof ModelEntity)) {
					setEnabled(false);
					return;
				}
			}
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
}
