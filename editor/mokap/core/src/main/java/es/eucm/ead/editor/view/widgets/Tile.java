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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Tile extends AbstractWidget {

	private Actor background;

	private Actor marker;

	private Label label;

	private Container<Label> labelContainer;

	private TileStyle style;

	private ClickListener clickListener = new ClickListener();

	public Tile(Skin skin) {
		this(skin.get(TileStyle.class));
	}

	public Tile(TileStyle tileStyle) {
		labelContainer = new Container<Label>(label = new Label("",
				tileStyle.labelStyle));
		this.style = tileStyle;
		labelContainer.setBackground(tileStyle.labelBackground);
		labelContainer.pad(WidgetBuilder.dpToPixels(8));
		labelContainer.left();
		addActor(labelContainer);
		addListener(clickListener);
	}

	public void setBackground(Actor actor) {
		if (this.background != null) {
			this.background.remove();
		}
		if (actor != null) {
			addActorAt(0, actor);
		}
		this.background = actor;
	}

	public void setMarker(Actor actor) {
		if (this.marker != null) {
			this.marker.remove();
		}

		if (actor != null) {
			addActor(actor);
		}
		this.marker = actor;
	}

	public void setText(String text) {
		label.setText(text);
	}

	@Override
	public float getPrefWidth() {
		return getPrefWidth(background);
	}

	@Override
	public float getPrefHeight() {
		return getPrefHeight(background);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (clickListener.isPressed() && style.pressed != null) {
			style.pressed.draw(batch, 0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public void layout() {
		setBounds(background, 0, 0, getWidth(), getHeight());
		setBounds(labelContainer, 0, 0, getWidth(),
				getPrefHeight(labelContainer));
		if (marker != null) {
			float width = getPrefWidth(marker);
			float height = getPrefHeight(marker);
			setBounds(marker, getWidth() - width, getHeight() - height, width,
					height);
		}
	}

	@Override
	public void setLayoutEnabled(boolean enabled) {
		super.setLayoutEnabled(enabled);
	}

	public static class TileStyle {

		public Drawable labelBackground;

		public Drawable pressed;

		public LabelStyle labelStyle;

	}
}
