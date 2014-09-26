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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A {@link HiddenPanel} that can be positioned according to the
 * {@link #position}.
 */
public class PositionedHiddenPanel extends HiddenPanel {

	private static final Vector2 TEMP = new Vector2();

	protected float space;
	protected Actor reference;
	private Position position;

	public static enum Position {
		BOTTOM, RIGHT, CENTER, TOP
	}

	public PositionedHiddenPanel(Skin skin) {
		this(skin, null, null);
	}

	public PositionedHiddenPanel(Skin skin, Position position, Actor reference) {
		super(skin);
		this.position = position;
		this.reference = reference;
	}

	public void setReference(Actor actor) {
		this.reference = actor;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void show() {
		show((Action) null);
	}

	@Override
	public void layout() {
		updatePositionPanel();
		super.layout();
	}

	public void show(Action action) {
		show(reference.getStage(), action);
	}

	@Override
	public void show(Stage stage, Action action) {
		if (stage != null) {
			updatePositionPanel();
			super.show(stage, action);
		}
	}

	public void setSpace(float space) {
		this.space = space;
	}

	public void updatePositionPanel() {
		reference.localToStageCoordinates(TEMP.set(0f, 0f));
		positionPanel(TEMP.x, TEMP.y);
	}

	/**
	 * Invoked when this panels is going to be shown, use this method to decide
	 * the bounds of the panel.
	 * 
	 * @param y
	 *            position of the reference in {@link Stage} coordinates.
	 * @param x
	 *            position of the reference in {@link Stage} coordinates.
	 */
	protected void positionPanel(float x, float y) {
		pack();
		float panelPrefHeight = Math.min(getHeight(), Gdx.graphics.getHeight());
		float panelPrefWidth = Math.min(getWidth(), Gdx.graphics.getWidth());
		if (position == Position.RIGHT) {
			float coordinateY = 0;
			if (reference.getParent() != null
					&& reference.getParent().getHeight() == 0) {
				coordinateY = reference.getY() + reference.getHeight()
						- getPrefHeight();
				if (coordinateY < 0) {
					coordinateY = 0;
				}
				if (getPrefHeight() > reference.getY() + reference.getHeight()) {
					panelPrefHeight = reference.getY() + reference.getHeight();
				} else {
					panelPrefHeight = getPrefHeight();
				}
			} else if (reference.getParent() != null) {
				panelPrefHeight = Math.min(panelPrefHeight, reference
						.getParent().getHeight());
				coordinateY = y + (reference.getHeight() - panelPrefHeight)
						* .5f;
				if (coordinateY < 0) {
					if (panelPrefHeight > reference.getParent().getParent()
							.getHeight()) {
						panelPrefHeight = reference.getParent().getParent()
								.getHeight();
					}
					coordinateY = 0;
				} else if (coordinateY + panelPrefHeight > reference
						.getParent().getHeight()) {
					coordinateY = reference.getParent().getParent().getHeight()
							- panelPrefHeight;
					if (coordinateY < 0) {
						if (panelPrefHeight > reference.getParent().getParent()
								.getHeight()) {
							panelPrefHeight = reference.getParent().getParent()
									.getHeight();
						}
						coordinateY = 0;
					}
				}
			}
			setPanelBounds(x + space + reference.getWidth(), coordinateY,
					panelPrefWidth, panelPrefHeight);
		} else if (position == Position.BOTTOM) {
			setPanelBounds(
					Math.max(0f, x + (reference.getWidth() - panelPrefWidth)
							* .5f), y - space - panelPrefHeight,
					panelPrefWidth, panelPrefHeight);
		} else if (position == Position.CENTER) {
			setPanelBounds(x + (reference.getWidth() - panelPrefWidth) * .5f, y
					+ (reference.getHeight() - panelPrefHeight) * .5f,
					panelPrefWidth, panelPrefHeight);
		} else if (position == Position.TOP) {
			setPanelBounds(
					Math.max(0f, x + (reference.getWidth() - panelPrefWidth)
							* .5f), y + reference.getHeight() + space,
					panelPrefWidth, panelPrefHeight);
		}
	}

	/**
	 * Rounds and sets the given bounds for the {@link #panel}.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	protected void setPanelBounds(float x, float y, float width, float height) {
		setBounds(MathUtils.round(x), MathUtils.round(y),
				MathUtils.round(width), MathUtils.round(height));
	}
}
