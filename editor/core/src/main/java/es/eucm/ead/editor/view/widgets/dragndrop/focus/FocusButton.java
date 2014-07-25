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
package es.eucm.ead.editor.view.widgets.dragndrop.focus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.dragndrop.focus.FocusItemList.FocusEvent;

public class FocusButton extends Button {

	private static final float PAD = 5F;

	private static final FocusRunnable focusRunnable = new FocusRunnable();

	public FocusButton(Skin skin) {
		super(skin, "focus");
		defaults().space(PAD);
		pad(PAD);
	}

	@Override
	public void setChecked(boolean isChecked) {
		boolean wasChecked = isChecked();
		super.setChecked(isChecked);
		if (!wasChecked && isChecked) {
			focusRunnable.focusActor = this;
			Gdx.app.postRunnable(focusRunnable);
		}
	}

	private static class FocusRunnable implements Runnable {

		private Actor focusActor;

		@Override
		public void run() {
			if (focusActor != null) {
				fireFocus();
			}
		}

		/**
		 * Fires that this {@link #focusActor} has gained focus
		 */
		private void fireFocus() {
			FocusEvent dropEvent = Pools.obtain(FocusEvent.class);
			dropEvent.setActor(focusActor);
			focusActor.fire(dropEvent);
			Pools.free(dropEvent);
		}
	}
}
