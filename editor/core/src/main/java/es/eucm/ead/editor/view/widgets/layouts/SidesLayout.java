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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public abstract class SidesLayout extends AbstractWidget {

	protected boolean computeInvisibles = true;

	protected Array<Actor> first;

	protected Array<Actor> second;

	protected float widgetsMargin = 0.0f;

	protected float widgetsPad = 0.0f;

	protected float padLeft, padTop, padRight, padBottom;

	private Drawable background;

	protected int align = Align.center;

	protected boolean expand = false;

	public SidesLayout() {
		this(null);
	}

	public SidesLayout computeInvisibles(boolean computeInvisibles) {
		this.computeInvisibles = computeInvisibles;
		return this;
	}

	public SidesLayout align(int align) {
		this.align = align;
		return this;
	}

	protected void expand() {
		expand = true;
	}

	public SidesLayout pad(float pad) {
		padLeft = padTop = padRight = padBottom = pad;
		return this;
	}

	public SidesLayout pad(float padLeft, float padTop, float padRight,
			float padBottom) {
		this.padLeft = padLeft;
		this.padTop = padTop;
		this.padRight = padRight;
		this.padBottom = padBottom;
		return this;
	}

	public SidesLayout padLeft(float padLeft) {
		this.padLeft = padLeft;
		return this;
	}

	public SidesLayout padTop(float padTop) {
		this.padTop = padTop;
		return this;
	}

	public SidesLayout padRight(float padRight) {
		this.padRight = padRight;
		return this;
	}

	public SidesLayout padBottom(float padBottom) {
		this.padBottom = padBottom;
		return this;
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
		second.add(actor);
		addActor(actor);
	}

	public void addFirst(int index, Actor actor) {
		first.insert(index, actor);
		addActor(actor);
	}

	public void addSecond(int index, Actor actor) {
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

	public SidesLayout widgetsMargin(float margin) {
		this.widgetsMargin = margin;
		return this;
	}

	public SidesLayout widgetsPad(float pad) {
		this.widgetsPad = pad;
		return this;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	@Override
	public boolean removeActor(Actor actor) {
		first.removeValue(actor, true);
		second.removeValue(actor, true);
		return super.removeActor(actor);
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
