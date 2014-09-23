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
package es.eucm.ead.editor.view.widgets.helpmessage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;

public abstract class HelpMessage extends PositionedHiddenPanel {

	private static final float DEFAULT_SPACING = 15F;
	protected static final float IN_DURATION = .3F;
	protected static final float OUT_DURATION = .2F;

	private HelpMessage nextMessage;

	public HelpMessage(Skin skin, Position position, Actor reference) {
		super(skin, position, reference);
		setBackground(skin
				.getDrawable(position == Position.BOTTOM ? "bubble_top"
						: (position == Position.RIGHT ? "bubble_left"
								: "dialog")));
		padBottom(getPadBottom() + DEFAULT_SPACING);
		padRight(getPadRight() + DEFAULT_SPACING);
		padTop(getPadTop() + DEFAULT_SPACING);
		padLeft(getPadLeft() + DEFAULT_SPACING);
		defaults().space(DEFAULT_SPACING);
	}

	public void show() {
		getColor().a = 0f;
		show(fadeIn(IN_DURATION, Interpolation.fade));
	}

	@Override
	public void hide() {
		hide(getHideAction());
	}

	private Action getHideAction() {
		return fadeOut(OUT_DURATION, Interpolation.fade);
	}

	@Override
	public void hide(Action action) {
		if (nextMessage != null) {
			Gdx.app.postRunnable(showNextMessage);
		}
		super.hide(action);
	}

	public void setNextMessage(HelpMessage nextMessage) {
		this.nextMessage = nextMessage;
	}

	private Runnable showNextMessage = new Runnable() {

		@Override
		public void run() {
			nextMessage.show();
		}
	};
}
