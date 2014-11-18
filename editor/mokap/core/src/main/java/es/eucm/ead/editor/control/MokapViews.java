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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.project.ProjectView;

public class MokapViews extends Views implements BackListener {

	public MokapViews(Controller controller, Group viewsCtr, Group modalsCtr) {
		super(controller, viewsCtr, modalsCtr);
		resendTouch = false;
	}

	@Override
	public void onBackPressed() {
		if (!hideModalIfNeeded()) {
			if (currentView instanceof BackListener) {
				((BackListener) currentView).onBackPressed();
			} else {
				ViewBuilder nextView = currentView;
				back();
				if (nextView == currentView) {
					controller.action(ChangeView.class, ProjectView.class);
				}
			}
		}
	}

	public void hideOnscreenKeyboard() {
		Gdx.input.setOnscreenKeyboardVisible(false);
		Stage stage = modalsContainer.getStage();
		if (stage != null) {
			stage.setKeyboardFocus(null);
			stage.unfocusAll();
		}
	}
}
