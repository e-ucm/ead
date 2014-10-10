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
package es.eucm.ead.editor.view.widgets.editionview.draw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.editionview.SlideColorPicker;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithFadePanel;

public class ColorPicker extends IconWithFadePanel {

	private static final ClickListener colorClicked = new ClickListener() {

		public void clicked(InputEvent event, float x, float y) {
			Actor listenerActor = event.getListenerActor();
			ColorPicker colorPicker = ((ColorPicker) listenerActor
					.getUserObject());
			colorPicker.currentColor = null;
			colorPicker.newColor = false;
			Color color = listenerActor.getColor();
			colorPicker.picker.updatePosition(color);
			colorPicker.updateColor(color);

		};
	};
	private static final int MAX_COLORS = 8;
	private static final int MAX_COLS = 4;

	protected SlideColorPicker picker;

	private boolean newColor;
	private Actor reference;
	private Array<Cell<Actor>> colors;
	private Skin skin;
	private Image currentColor;

	public ColorPicker(boolean bottom, Skin skin) {
		this(bottom, skin, null);
	}

	public ColorPicker(boolean bottom, Skin skin, Actor reference) {
		super("colorpicker80x80", 5f, skin, bottom ? Position.BOTTOM
				: Position.TOP, "checkable");
		this.skin = skin;
		this.reference = reference == null ? this : reference;
		colors = new Array<Cell<Actor>>(MAX_COLORS);
		for (int i = 0; i < MAX_COLORS; ++i) {
			colors.add(panel.add());
			if (i != 0 && i % MAX_COLS == MAX_COLS - 1) {
				panel.row();
			}
		}
		picker = new SlideColorPicker(skin) {
			@Override
			protected void colorChanged(Color newColor) {
				updateColor(newColor);
				updateNewColor();
			}

			@Override
			public void draw(Batch batch, float parentAlpha) {
				super.draw(batch, parentAlpha);
				batch.setColor(Color.WHITE);
			}
		};
		panel.setReference(this.reference);

		if (!bottom) {
			panel.add(picker).colspan(MAX_COLS);
		} else {
			panel.add(picker).colspan(MAX_COLS);
		}
	}

	private void updateColor(Color newColor) {
		getIcon().setColor(newColor);
		colorChanged(newColor);
	}

	protected void colorChanged(Color newColor) {

	}

	public void colorChanged() {
		colorChanged(picker.getPickedColor());
		updateNewColor();
	}

	public void showPanel() {
		if (reference.getY() - panel.getPrefHeight() < 0) {
			panel.setPosition(Position.TOP);
		} else {
			panel.setPosition(Position.BOTTOM);
		}
		picker.updateTexture();
		super.showPanel();
		newColor = true;
	}

	private void updateNewColor() {
		if (newColor) {
			newColor = false;
			currentColor = new Image(skin.getDrawable("rectangle")) {
				@Override
				public void draw(Batch batch, float parentAlpha) {
					super.draw(batch, parentAlpha);
					batch.setColor(Color.WHITE);
				}
			};
			currentColor.setUserObject(this);
			currentColor.addListener(colorClicked);
			for (int i = colors.size - 2; i >= 0; --i) {
				Actor actor = colors.get(i).getActor();
				if (actor != null) {
					colors.get(i + 1).setActor(actor);
				}
			}
			colors.first().setActor(currentColor);
			currentColor.setScale(0f);
			currentColor.setOrigin(Align.center);
			currentColor.addAction(Actions.scaleTo(1f, 1f, .5f,
					Interpolation.swingOut));
		}
		if (currentColor != null) {
			currentColor.getColor().set(picker.getPickedColor());
		}
	}

	@Override
	public void hidePanel() {
		super.hidePanel();
		if (currentColor == null) {
			for (Cell<Actor> cell : colors) {
				Actor actor = cell.getActor();
				if (actor != null && actor.getColor().equals(currentColor)) {
					return;
				}
			}
			newColor = true;
			updateNewColor();
		}
	}
}
