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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * 
 * A timeline that show values in seconds
 * 
 */
public class TimelineWidget extends LinearLayout {

	private float height;

	private int lastTime;

	private Skin skin;

	private float pixelsPerSecond;

	private float scale;

	public TimelineWidget(Skin skin, float height, float pixelsPerSecond) {
		this(skin, height, pixelsPerSecond, 10);
	}

	public TimelineWidget(Skin skin, float height, float pixelsPerSecond,
			float scale) {
		super(true, skin.getTiledDrawable("timeline"));
		this.height = height;
		lastTime = 0;
		this.skin = skin;
		this.pixelsPerSecond = pixelsPerSecond;
		this.scale = scale;
	}

	@Override
	public void layout() {
		createTimes();
		float expandedWidth = 0.0f;

		for (Constraints c : constraints) {
			if (c.getActor().isVisible() || computeInvisibles) {
				Actor actor = c.getActor();
				float width = actorWidth(actor)
						+ (expandX(c) ? expandedWidth : 0.0f);

				float x = marginLeft(c);

				float height = expandY(c) ? containerHeight() - paddingHeight()
						- marginHeight(c) : actorHeight(actor);

				float y = getYAligned(c, height);

				setBoundsForActor(actor, x, y, width, height);
			}
		}
	}

	public void createTimes() {
		TextButton t;
		for (int i = lastTime; i <= getWidth() - pixelsPerSecond * scale; i += pixelsPerSecond
				* scale) {
			t = new TextButton(" " + i / pixelsPerSecond + "s", skin);
			if (i - t.getWidth() < getWidth() && i != 0) {
				add(t).margin(i - t.getWidth() / 2, 0, 0, 0);
			}
		}
		lastTime = Math.round(getWidth())
				- Math.round(getWidth() % (pixelsPerSecond * scale));
	}

	@Override
	public float getPrefHeight() {
		return height;
	}

	@Override
	public float getPrefWidth() {
		return getParent().getWidth();
	}
}
