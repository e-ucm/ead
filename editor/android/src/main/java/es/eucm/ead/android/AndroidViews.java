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
package es.eucm.ead.android;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Controller.BackListener;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.view.widgets.mockup.Notification;

public class AndroidViews extends Views implements BackListener {

	private static final float DEFAULT_TIMEOUT = 3F;
	private Notification dialogNotification;

	public AndroidViews(Controller controller, Group rootContainer) {
		super(controller, rootContainer, rootContainer);
		dialogNotification = new Notification(controller.getApplicationAssets()
				.getSkin());
	}

	@Override
	public void onBackPressed() {
		if (currentView instanceof BackListener) {
			((BackListener) super.currentView).onBackPressed();
		} else {
			back();
		}
	}

	public void showDialog(String name, Object... arguments) {
		if (arguments.length > 0) {
			dialogNotification.clearChildren();

			Label text = new Label(arguments[0].toString(), controller
					.getApplicationAssets().getSkin());
			text.setAlignment(Align.center);
			text.setWrap(true);

			dialogNotification.text(text).width(
					getViewsContainer().getStage().getWidth() * .7f);
			dialogNotification.show(getViewsContainer().getStage(),
					DEFAULT_TIMEOUT);
		}
	}
}
