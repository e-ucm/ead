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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;

/**
 * This class adds to {@link EditorAction} the property of being enabled when a
 * game is loaded in the editor.
 * 
 * Created by Angel on 11/06/2014.
 */
public abstract class EnabledOnloadAction extends EditorAction implements
		Model.ModelListener<LoadEvent> {

	/**
	 * Constructors propagates the call to father class
	 */
	public EnabledOnloadAction(boolean initialEnable, boolean allowNullArguments) {
		super(initialEnable, allowNullArguments);
	}

	protected EnabledOnloadAction(boolean initialEnable,
			boolean allowNullArguments, Class... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
	}

	protected EnabledOnloadAction(boolean initialEnable,
			boolean allowNullArguments, Class[]... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
	}

	/**
	 * When the action is initialized, the {@link EnabledOnloadAction} will add
	 * itself as {@link LoadEvent}listener in the controller and be
	 * enabled/disabled taking into account if the game is loaded or not
	 */
	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		controller.getModel().addLoadListener(this);
		setEnabled(controller.getModel().getGame() != null);
	}

	/**
	 * Modifies the {@link Action#enabled} attribute.
	 * 
	 * NOTE: {@link Action#setEnabled(boolean)} notify the view listeners to
	 * change their appearance accordingly.
	 * 
	 */
	@Override
	public void modelChanged(LoadEvent event) {
        setEnabled(event.getType() == LoadEvent.Type.LOADED && !checkAdditionalPreconditions());
	}

	/**
	 * The {@link Action} could be waiting for other events besides the
	 * {@link LoadEvent} to be enabled.
	 * 
	 * This method is called at {@link EnabledOnloadAction#modelChanged} and
	 * children classes could override it to
	 * 
	 */
	public boolean checkAdditionalPreconditions() {
		return false;
	}

}
