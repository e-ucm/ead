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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class PlaceHolder extends AbstractWidget {

	private Drawable background;

	private Actor content;

	public void setBackground(Drawable background) {
		this.background = background;
	}

	public void setContent(Actor content) {
		if (this.content != null) {
			this.content.remove();
		}
		this.content = content;
		if (content != null) {
			addActor(content);
		}
	}

	public Actor getContent() {
		return content;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public void layout() {
		if (content != null) {
			setBounds(content, 0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public float getPrefHeight() {
		return content == null ? 0 : getPrefHeight(content);
	}

	@Override
	public float getPrefWidth() {
		return content == null ? 0 : getPrefWidth(content);
	}
}
