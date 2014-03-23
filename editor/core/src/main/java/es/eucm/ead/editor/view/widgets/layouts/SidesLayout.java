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
package es.eucm.ead.editor.view.widgets.layouts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public abstract class SidesLayout extends AbstractWidget {

	protected Array<Actor> first;

	protected Array<Actor> second;

	protected float margin = 0.0f;

	protected float pad = 0.0f;

	private Drawable background;

	public SidesLayout() {
		this(null);
	}

	public SidesLayout(Drawable background) {
		this.background = background;
		first = new Array<Actor>();
		second = new Array<Actor>();
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	@Override
	public float getMaxWidth() {
		return getPrefWidth();
	}

	@Override
	public float getMaxHeight() {
		return getPrefHeight();
	}

	public void addFirst(Actor actor) {
		first.add(actor);
		addActor(actor);
	}

	public void addSecond(Actor actor) {
		addSecond(actor, 0);
	}

	public void addFirst(Actor actor, int index) {
		first.insert(index, actor);
		addActor(actor);
	}

	public void addSecond(Actor actor, int index) {
		second.insert(index, actor);
		addActor(actor);
	}

	public void removeFirst(Actor actor) {
		removeActorFromArray(first, actor);
	}

	public void removeSecond(Actor actor) {
		removeActorFromArray(second, actor);
	}

	private void removeActorFromArray(Array<Actor> array, Actor actor) {
		for (int i = 0; i < array.size; i++) {
			if (array.get(i) == actor) {
				array.removeIndex(i);
				break;
			}
		}
		removeActor(actor);
	}

	public SidesLayout margin(float margin) {
		this.margin = margin;
		return this;
	}

	public SidesLayout pad(float pad) {
		this.pad = pad;
		return this;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, getX(), getY(), getWidth(), getHeight());
		}
		super.draw(batch, parentAlpha);
	}

	/**
	 * Clears out all children actors, not only from the group, but also from
	 * the {@link #first} and {@link #second} lists that are used to determine
	 * the layout.
	 * 
	 * This method is meant to be invoked each time the component using this
	 * layout is reinitialized. For example, when a new game is created and the
	 * scenes list must be updated. See
	 * {@link es.eucm.ead.editor.view.builders.classic.ScenesList#clearScenes()}
	 * for more details on the example.
	 */
	@Override
	public void clearChildren() {
		super.clearChildren();
		first.clear();
		second.clear();
	}
}
