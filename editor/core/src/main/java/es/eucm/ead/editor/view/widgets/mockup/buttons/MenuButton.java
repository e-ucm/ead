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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;

/**
 * A button displayed in the MainMenu and PanelMenu Screens.
 */
public class MenuButton extends Button {

	private static final float PREF_WIDTH = .2F;
	private static final float PAD_LARGE = 17f, PAD_SMALL = 10f;

	protected final Vector2 viewport;
	protected Label label;

	public enum Position {
		TOP, BOTTOM, RIGHT, LEFT
	}

	private final Position pos;

	public MenuButton(Vector2 viewport, String name, Skin skin,
			String iconRegion, Position pos) {
		super(skin);
		this.viewport = viewport;
		this.pos = pos;
		initialize(name, skin, iconRegion);
	}

	public MenuButton(Vector2 viewport, String name, Skin skin,
			String iconRegion, Position pos, Controller controller,
			Class<?> actionClass, Object... args) {
		super(skin);
		this.viewport = viewport;
		this.pos = pos;
		initialize(name, skin, iconRegion);
		addListener(new ActionOnClickListener(controller, actionClass, args));
	}

	private void initialize(String name, Skin skin, String iconRegion) {
		final Image sceneIcon = new Image(skin.getRegion(iconRegion));
		sceneIcon.setScaling(Scaling.fit);

		this.label = new Label(name, skin);
		this.label.setWrap(true);

		switch (pos) {
		case TOP:
			pad(PAD_LARGE, PAD_LARGE, PAD_SMALL, PAD_LARGE);
			this.label.setAlignment(Align.center);
			add(this.label).expandX().fillX();
			row();
			add(sceneIcon).expand().fill();
			break;
		case BOTTOM:
			pad(PAD_LARGE, PAD_LARGE, PAD_SMALL, PAD_LARGE);
			this.label.setAlignment(Align.center);
			add(sceneIcon).expand().fill();
			row();
			add(this.label).expandX().fillX();
			break;
		case LEFT:
			pad(PAD_LARGE, PAD_SMALL, PAD_LARGE, PAD_SMALL);
			this.label.setAlignment(Align.right);
			add(this.label).right().expandX().fillX();
			add(sceneIcon).left().fill();
			break;
		case RIGHT:
			add(sceneIcon).right().fill();
			pad(PAD_LARGE, PAD_SMALL, PAD_LARGE, PAD_SMALL);
			this.label.setAlignment(Align.left);
			add(this.label).left().expandX().fillX();
		}
	}

	public void setAligmentText(int alig) {
		this.label.setAlignment(alig);
	}

	@Override
	public float getPrefWidth() {
		return this.viewport == null ? 0 : this.viewport.x * PREF_WIDTH;
	}

	@Override
	public float getPrefHeight() {
		// We make sure it's a square
		return getPrefWidth();
	}

	public Label getLabel() {
		return this.label;
	}
}
