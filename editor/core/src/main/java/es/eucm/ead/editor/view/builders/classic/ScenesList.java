/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.builders.classic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.ToggleImageButton;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;

public class ScenesList extends AbstractWidget {

	private Controller controller;

	private Skin skin;

	private float prefSize;

	private TopBottomLayout container;

	private ScrollPane scrollPane;

	public ScenesList(Controller controller, Skin skin) {
		this.controller = controller;
		this.skin = skin;
		container = new TopBottomLayout();
		scrollPane = new ScrollPane(container);
		addActor(scrollPane);
	}

	public ScenesList scene(String scene) {
		container.top(new SceneWidget(scene));
		return this;
	}

	public Actor getBackground() {
		return container;
	}

	public void clearScenes() {
		container.clearChildren();
	}

	public ScenesList prefSize(float prefSize) {
		this.prefSize = prefSize;
		return this;
	}

	@Override
	public float getPrefWidth() {
		return container.getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		return container.getPrefHeight();
	}

	@Override
	public void layout() {
		setBounds(scrollPane, 0, 0, getWidth(), getHeight());
	}

	public class SceneWidget extends AbstractWidget {

		private ToggleImageButton button;

		private Label label;

		public SceneWidget(String scene) {
			button = new ToggleImageButton(skin.getDrawable("blank"), skin);
			button.addListener(new ActionOnClickListener(controller, EditScene.NAME, scene));
			label = new Label(scene, skin);
			label.setColor(Color.BLACK);
			label.setAlignment(Align.center);
			label.setWrap(true);
			label.setTouchable(Touchable.disabled);
			addActor(button);
			addActor(label);
		}

		@Override
		public float getPrefWidth() {
			return prefSize;
		}

		@Override
		public float getPrefHeight() {
			return prefSize;
		}

		@Override
		public void layout() {
			setBounds(button, 0, 0, getWidth(), getHeight());
			setPosition(label, getWidth() / 2.0f, getHeight() / 2.0f);
		}
	}

}
