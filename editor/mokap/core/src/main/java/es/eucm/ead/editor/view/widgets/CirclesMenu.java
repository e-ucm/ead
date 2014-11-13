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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import es.eucm.ead.editor.view.Modal;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class CirclesMenu extends LinearLayout implements Modal {

	private static final float TIME = 0.22f;

	private int align;

	/**
	 * 
	 * @param align
	 *            It indicates where the menu aligns when is opened/closed.
	 *            {@link Align#left} or {@link Align#right} will create an
	 *            horizontal menu. {@link Align#top} and {@link Align#bottom}
	 *            will create a vertical menu. Other values are not supported
	 */
	public CirclesMenu(int align) {
		super(align == Align.left || align == Align.right);
		this.align = align;
	}

	@Override
	public void show() {
		for (Actor child : getChildren()) {
			child.clearActions();
		}
		pack();
		layout();
		for (Actor child : getChildren()) {
			float x = child.getX();
			float y = child.getY();
			if (isHorizontal()) {
				switch (align) {
				case Align.right:
					child.setPosition(getWidth() - child.getWidth(), 0);
					break;
				default:
					child.setPosition(0, 0);
					break;
				}
			} else {
				switch (align) {
				case Align.top:
					child.setPosition(0, getHeight() - child.getHeight());
					break;
				default:
					child.setPosition(0, 0);
					break;
				}
			}
			child.addAction(Actions.moveTo(x, y, TIME, Interpolation.swingOut));
		}
	}

	@Override
	public void hide(Runnable runnable) {
		for (Actor child : getChildren()) {
			child.clearActions();
			float x = 0;
			float y = 0;
			if (isHorizontal() && align == Align.right) {
				x = getWidth() - child.getWidth();
			} else if (align == Align.top) {
				y = getHeight() - child.getHeight();
			}
			child.addAction(Actions.moveTo(x, y, TIME / 2.0f,
					Interpolation.exp5Out));
		}

		if (runnable != null) {
			addAction(Actions.sequence(Actions.delay(TIME / 2.0f),
					Actions.run(runnable)));
		}
	}

	@Override
	public boolean hideAlways() {
		return true;
	}
}
