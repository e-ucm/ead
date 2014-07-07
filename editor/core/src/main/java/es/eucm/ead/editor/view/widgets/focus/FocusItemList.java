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
package es.eucm.ead.editor.view.widgets.focus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class FocusItemList extends ScrollPane {

	protected static final float PAD = 5F;

	protected LinearLayout itemsList;

	private FocusItem previousFocus;

	public FocusItemList() {
		super(null);

		itemsList = new LinearLayout(true);
		setWidget(itemsList);
		itemsList.pad(PAD);

		setScrollingDisabled(false, true);

		addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();

				while (target != null && !(target instanceof FocusItem)) {
					target = target.getParent();
				}

				if (target != null) {
					FocusItem curr = ((FocusItem) target);
					setFocus(curr);
				}
			}
		});
	}

	public void addFocusItem(FocusItem image) {
		image.pad(PAD);
		itemsList.add(image).margin(PAD);
	}

	protected void setFocus(FocusItem newFocus) {
		if (newFocus != previousFocus) {
			if (previousFocus != null) {
				previousFocus.setFocus(false);
			}
			newFocus.setFocus(true);
			previousFocus = newFocus;
		}
	}

}
