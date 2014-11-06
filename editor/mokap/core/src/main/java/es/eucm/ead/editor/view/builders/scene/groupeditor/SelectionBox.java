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
package es.eucm.ead.editor.view.builders.scene.groupeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Actor to represent the selection box over the selected objects
 */
public class SelectionBox extends Group {

	private static final Color pressedColor = new Color(0.5f, 0.5f, 0.5f, 0.3f);

	private static final Color selectedColor = new Color(0, 1, 1, 0.3f);

	private static final Color movingColor = new Color(0, 0, 1, 0.3f);

	private static final int PRESSED = 0, SELECTED = 1, MOVING = 2;

	private Actor target;

	private int state;

	private Drawable selectionBackground;

	private float initialPinchRotation;

	private float initialRotation;

	public Actor getTarget() {
		return target;
	}

	public void setTarget(Actor target, Drawable selectionBackground) {
		this.target = target;
		this.selectionBackground = selectionBackground;
		this.state = PRESSED;
		readTargetBounds();
	}

	public void readTargetBounds() {
		setBounds(target.getX(), target.getY(), target.getWidth(),
				target.getHeight());
		setScale(target.getScaleX(), target.getScaleY());
		setRotation(target.getRotation());
		setOrigin(target.getWidth() / 2.0f, target.getHeight() / 2.0f);
	}

	@Override
	public void act(float delta) {
		if (target.getActions().size > 0) {
			readTargetBounds();
		}
	}

	public void setInitialPinchRotation(float initialPinchRotation) {
		this.initialPinchRotation = initialPinchRotation;
		this.initialRotation = getRotation();
	}

	public void selected() {
		this.state = SELECTED;
	}

	public void moving() {
		Gdx.input.vibrate(50);
		this.state = MOVING;
	}

	public boolean isPressed() {
		return state == PRESSED;
	}

	public boolean isSelected() {
		return state == SELECTED;
	}

	public boolean isMoving() {
		return state == MOVING;
	}

	public void updateRotation(float degrees) {
		super.setRotation(initialRotation + degrees - initialPinchRotation);
		target.setRotation(getRotation());
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		target.setPosition(x, y);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		switch (state) {
		case PRESSED:
			batch.setColor(pressedColor);
			break;
		case SELECTED:
			batch.setColor(selectedColor);
			break;
		case MOVING:
			batch.setColor(movingColor);
			break;
		}
		selectionBackground.draw(batch, 0, 0, getWidth(), getHeight());
		batch.setColor(Color.WHITE);
		super.drawChildren(batch, parentAlpha);
	}

}
