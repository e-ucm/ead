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

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.view.widgets.Notification;

public class Toasts {

	private MockupController controller;
	private Notification notification;

	public Toasts(Controller controller) {
		notification = new Notification(controller.getApplicationAssets()
				.getSkin());
		this.controller = (MockupController) controller;
	}

	public void showNotification(String text) {
		showNotification(text, -1F);
	}

	public void showNotification(String text, float timeout) {
		changeText(text);
		notification.show(controller.getRootComponent().getStage(), timeout);
	}

	public boolean isNotificationShowing() {
		return notification.isShowing();
	}

	public void hideNotification() {
		notification.hide();
	}

	/**
	 * Changes the text of the notification.
	 */
	private void changeText(String newText) {
		List<Cell> cells = notification.getCells();
		if (cells.isEmpty()) {
			notification.add(newText);
			Label label = (Label) cells.get(0).getWidget();
			label.setAlignment(Align.center);
		} else {
			Object widget = cells.get(0).getWidget();
			if (widget instanceof Label) {
				Label label = (Label) widget;
				label.setText(newText);
			}
		}
	}
}
