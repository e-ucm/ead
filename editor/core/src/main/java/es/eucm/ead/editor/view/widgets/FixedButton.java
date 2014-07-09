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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

/**
 * Button that when the mouse are on it change its appearance.
 */
public class FixedButton extends Table {

	private IconButton gatherButton;
	private IconButton deployButton;

	private TextButton pole;

	private Cell<IconButton> image;

	/**
	 * By default the button have a appearance of <b>icon1</b> and changes it by
	 * <b>icon2</b> when the mouse are over it. The default appearance always is
	 * less than the other appearance.
	 * 
	 * @param icon1
	 * @param icon2
	 * @param skin
	 */
	public FixedButton(Drawable icon1, Drawable icon2, Skin skin) {
		super();

		pole = new TextButton(" ", skin);

		add(pole);
		align(Align.left);

		image = add();

		deployButton = new IconButton(icon2, skin);
		gatherButton = new GatherIconButton(icon1, deployButton, skin);

		image.setWidget(gatherButton);
		image.bottom();

		this.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				image.setWidget(null);
				image.setWidget(deployButton);
				image.center();
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer,
					Actor toActor) {
				image.setWidget(null);
				image.setWidget(gatherButton);
				image.bottom();
			}
		});
	}

	@Override
	public float getPrefHeight() {
		if (pole == null) {
			return super.getPrefHeight();
		} else {
			return pole.getPrefHeight();
		}
	}

	@Override
	public float getPrefWidth() {
		if (pole == null || deployButton == null) {
			return super.getPrefWidth();
		} else {
			return pole.getPrefWidth() + deployButton.getPrefWidth();
		}
	}

	private class GatherIconButton extends IconButton {

		private Button sib;

		public GatherIconButton(Drawable icon, Skin skin) {
			super(icon, skin);
		}

		public GatherIconButton(Drawable icon, Button button, Skin skin) {
			super(icon, skin);
			this.sib = button;
		}

		@Override
		public float getPrefHeight() {
			if (sib == null)
				return super.getPrefHeight();
			else
				return sib.getPrefHeight() / 2;
		}

		@Override
		public float getPrefWidth() {
			if (sib == null)
				return super.getPrefWidth();
			else
				return sib.getPrefWidth() / 2;
		}

	}
}
