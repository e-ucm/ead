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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * Widget that can be resized by dragging its sides.
 * 
 */
public class StretchableButton extends LinearLayout {

	/**
	 * Using in {@link DraggablePart} to resize the {@link StretchableButton}
	 */
	private static final Vector2 vector = new Vector2();
	private static final Vector2 parentVector = new Vector2();

	private Container container;

	private boolean leftDragged;
	private boolean rightDragged;

	protected DraggablePart left;
	protected DraggablePart right;

	/**
	 * If true, can not be enlarged to the left
	 */
	private boolean lockL2L;

	/**
	 * If true, can not be enlarged to the right
	 */
	private boolean lockR2R;

	public StretchableButton(Skin skin) {
		super(true);
		container = new StretchablePart();
		this.init(skin);
	}

	public StretchableButton(Actor actor, Skin skin) {
		super(true);
		container = new StretchablePart(actor);
		this.init(skin);
	}

	public StretchableButton(Actor actor, Drawable drawable, Skin skin) {
		super(true);
		container = new StretchablePart(actor);
		container.setBackground(drawable);
		this.init(skin);
	}

	public StretchableButton(Actor actor, Float width, Skin skin) {
		super(true);
		container = new StretchablePart(actor, width);
		this.init(skin);
	}

	public StretchableButton(Actor actor, Float width, Drawable drawable,
			Skin skin) {
		super(true);
		container = new StretchablePart(actor, width);
		container.setBackground(drawable);
		this.init(skin);
	}

	private void init(Skin skin) {
		leftDragged = false;
		rightDragged = false;
		lockL2L = false;
		lockR2R = false;
		left = new DraggablePart(skin, this, container, true);
		right = new DraggablePart(skin, this, container, false);
		this.add(left).expandY();
		this.add(container);
		container.toBack();
		this.add(right).expandY();
	}

	public boolean isDragLeft() {
		return leftDragged;
	}

	public boolean isDragRight() {
		return rightDragged;
	}

	public void lockLeftToLeft() {
		lockL2L = true;
	}

	public void unlockLeftToLeft() {
		lockL2L = false;
	}

	public void lockRightToTight() {
		lockR2R = true;
	}

	public void unlockRightToTight() {
		lockR2R = false;
	}

	public void setTotalWidth(float width) {
		container.setWidth(width - left.getWidth() - right.getWidth());
	}

	/**
	 * The draggable lateral sides of {@link StretchableButton}
	 * 
	 */
	protected class DraggablePart extends TextButton {

		public DraggablePart(Skin skin, final StretchableButton parent,
				final Container container, final boolean first) {
			super(" ", skin);

			this.addListener(new InputListener() {

				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {

					vector.set(getX(), 0);
					localToAscendantCoordinates(parent.getParent(), vector);

					if (first && container.getWidth() >= 0) {
						parent.rightDragged = false;
						parent.leftDragged = true;
					} else if (container.getWidth() >= 0) {
						parent.leftDragged = false;
						parent.rightDragged = true;
					}
					return true;
				}

				@Override
				public void touchDragged(InputEvent event, float x, float y,
						int pointer) {

					vector.set(x, 0);
					localToAscendantCoordinates(parent.getParent(), vector);

					if (first) {
						parentVector.set(parent.getX() + container.getWidth(),
								0);
						if (vector.x < parentVector.x || !lockL2L) {
							container.setWidth(parentVector.x - vector.x);
							if (container.getWidth() > 0) {
								parent.setX(vector.x);
							}
						}
					} else {
						parentVector.set(parent.getX(), 0);
						if (!lockR2R || vector.x - parentVector.x <= 0) {
							parent.setTotalWidth(vector.x - parentVector.x);
						}
					}

					if (container.getWidth() <= 0) {
						container.setWidth(0);
					}

					container.invalidateHierarchy();
				}

				@Override
				public void touchUp(InputEvent event, float x, float y,
						int pointer, int button) {
					parent.leftDragged = false;
					parent.rightDragged = false;
				}
			});
		}
	}

	/**
	 * The center part of {@link StretchableButton}
	 * 
	 */
	protected class StretchablePart extends Container {

		public StretchablePart() {
			super();
			this.setWidth(0);
		}

		public StretchablePart(Actor actor) {
			super(actor);
			this.setWidth(actor.getWidth());
		}

		public StretchablePart(Actor actor, float width) {
			super(actor);
			this.setWidth(width);
		}

		@Override
		public float getPrefWidth() {
			return this.getWidth();
		}
	}

	public void setWidget(Actor actor) {
		this.container.setWidget(null);
		this.container.setWidget(actor);
	}

	public Actor getWidget() {
		return container.getWidget();
	}
}
