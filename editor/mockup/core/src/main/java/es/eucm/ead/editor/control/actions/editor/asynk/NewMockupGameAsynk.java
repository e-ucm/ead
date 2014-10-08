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
package es.eucm.ead.editor.control.actions.editor.asynk;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.NewMockupGame;
import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;
import es.eucm.ead.editor.view.builders.gallery.ScenesView;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Creates a new eAdventure Mockup game asynchronously.
 * 
 * @see NewMockupGame
 */
public class NewMockupGameAsynk extends BackgroundExecutorAction<String> {

	private NewMockupGame newGame;

	public NewMockupGameAsynk() {
		super(new Class[] { String.class, ModelEntity.class, Transition.class });
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		newGame = controller.getActions().getAction(NewMockupGame.class);
	}

	@Override
	protected String getProcessingI18N() {
		return "newGame";
	}

	@Override
	protected String getErrorProcessingI18N() {
		return "newGame.error";
	}

	@Override
	protected String doInBackground() {
		return newGame.createNewGame(args);
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			newGame.openNewGame(result);
			controller.action(ChangeMockupView.class, ScenesView.class,
					(Transition) args[2]);
		}
	}

}
