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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEditorStyle;
import es.eucm.ead.engine.utils.EngineUtils;

/**
 * Actor to represent the selection box over the selected objects
 */
public class SelectionBox extends Group {

	private static Vector2 origin = new Vector2(), normal = new Vector2(),
			tangent = new Vector2();

	public static enum State {
		PRESSED, SELECTED, MOVING
	}

	private Actor target;

	private State state;

	private Drawable selectedBackground;

	private GroupEditor groupEditor;

	private GroupEditorStyle style;

	public Actor getTarget() {
		return target;
	}

	public void setTarget(Actor target, GroupEditor groupEditor,
			GroupEditorStyle style) {
		this.groupEditor = groupEditor;
		this.target = target;
		this.style = style;
		this.state = State.PRESSED;
		this.selectedBackground = style.selectedBackground;
		setUserObject(target);
		readTargetBounds();
	}

	public void readTargetBounds() {
		EngineUtils.adjustGroup(target);
		origin.set(0, 0);
		tangent.set(target.getWidth(), 0);
		normal.set(0, target.getHeight());

		target.localToAscendantCoordinates(groupEditor.getSceneContainer(),
				origin);
		target.localToAscendantCoordinates(groupEditor.getSceneContainer(),
				tangent);
		target.localToAscendantCoordinates(groupEditor.getSceneContainer(),
				normal);

		setSize(target.getWidth(), target.getHeight());
		setOrigin(target.getOriginX(), target.getOriginY());
		setRotation(target.getRotation());
		EngineUtils.applyTransformation(this, origin, tangent, normal);
	}

	@Override
	public void act(float delta) {
		if (target.getActions().size > 0
				|| !MathUtils.isEqual(target.getWidth(), getWidth(), 0.01f)
				|| !MathUtils.isEqual(target.getHeight(), getHeight(), 0.01f)) {
			readTargetBounds();
		}
		super.act(delta);
	}

	public void selected() {
		this.state = State.SELECTED;
	}

	public void moving() {
		Gdx.input.vibrate(50);
		this.state = State.MOVING;
	}

	public boolean isPressed() {
		return state == State.PRESSED;
	}

	public boolean isSelected() {
		return state == State.SELECTED;
	}

	public boolean isMoving() {
		return state == State.MOVING;
	}

	@Override
	public void moveBy(float deltaX, float deltaY) {
		target.moveBy(deltaX, deltaY);
		super.moveBy(deltaX, deltaY);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		Color color = null;
		switch (state) {
		case PRESSED:
			color = style.pressedColor;
			break;
		case SELECTED:
			color = groupEditor.isOnlySelection() ? style.onlySelectionColor
					: groupEditor.isMultipleSelection() ? style.multiSelectedColor
							: style.selectedColor;
			break;
		case MOVING:
			color = groupEditor.isMultipleSelection() ? style.multiMovingColor
					: style.movingColor;
			break;
		}
		batch.setColor(color.r, color.g, color.b, style.alpha);
		selectedBackground.draw(batch, 0, 0, getWidth(), getHeight());
		batch.setColor(Color.WHITE);
		super.drawChildren(batch, parentAlpha);
	}

}
