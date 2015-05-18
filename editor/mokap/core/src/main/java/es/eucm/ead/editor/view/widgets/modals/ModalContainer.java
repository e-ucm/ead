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
package es.eucm.ead.editor.view.widgets.modals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.view.Modal;

public class ModalContainer extends Container<Actor> implements Modal {

	public ModalContainer(Skin skin, Actor actor) {
		this(skin.get(ModalContainerStyle.class), actor);
	}

	public ModalContainer(ModalContainerStyle style, Actor actor) {
		super(actor);
		setBackground(style.background);
		setTouchable(Touchable.enabled);
	}

	@Override
	public void show(Views views) {
		clearActions();
		getColor().a = 0.0f;
		addAction(Actions.alpha(1.0f, 0.25f));
		if (getActor() instanceof Modal) {
			((Modal) getActor()).show(views);
		}
	}

	@Override
	public void hide(Runnable runnable) {
		clearActions();
		if (getActor() instanceof Modal) {
			addAction(Actions.alpha(0.0f, 0.25f));
			((Modal) getActor()).hide(runnable);
		} else {
			addAction(Actions.sequence(Actions.alpha(0.0f, 0.25f),
					Actions.run(runnable)));
		}
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}

	@Override
	public boolean hideAlways() {
		if (getActor() instanceof Modal) {
			return ((Modal) getActor()).hideAlways();
		}
		return false;
	}

	public static class ModalContainerStyle {

		public Drawable background;

	}
}
