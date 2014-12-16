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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class LoadingBar extends Container<Image> {

	float prefHeight;

	public LoadingBar(Skin skin, float prefHeight) {
		this(skin.get(LoadingBarStyle.class), prefHeight);
	}

	public LoadingBar(LoadingBarStyle style, float prefHeight) {
		background(style.background);
		this.prefHeight = prefHeight;
		setActor(new Image(style.knobBefore));
		setClip(true);
	}

	@Override
	public void layout() {
		super.layout();

		Image image = getActor();
		image.clearActions();
		image.addAction(Actions.forever(Actions.sequence(
				Actions.sizeTo(0, getHeight()),
				Actions.moveTo(0, 0),
				Actions.parallel(Actions.moveTo(getWidth(), 0, 1f),
						Actions.sizeTo(getWidth() * .5f, getHeight(), .5f)))));
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	/**
	 * 
	 * @param completion
	 *            a value from 0 to 1.
	 */
	public void setCompletion(float completion) {
		Image image = getActor();
		if (image.getActions().size > 0) {
			image.clearActions();
			image.setX(0f);
		}
		image.setWidth(getWidth() * MathUtils.clamp(completion, 0f, 1f));
	}

	static public class LoadingBarStyle {

		public Drawable background;

		public Drawable knobBefore;

		public LoadingBarStyle() {
		}

		public LoadingBarStyle(Drawable background, Drawable knobBefore) {
			this.background = background;
			this.knobBefore = knobBefore;
		}
	}
}