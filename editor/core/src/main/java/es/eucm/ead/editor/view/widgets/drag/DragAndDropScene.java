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
package es.eucm.ead.editor.view.widgets.drag;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.drag.Modifier.Handle;
import es.eucm.ead.editor.view.widgets.drag.Modifier.OriginHandle;
import es.eucm.ead.editor.view.widgets.drag.Modifier.RotationHandle;

/**
 * A widget were all children are movable, rotatable and scalable.
 */
public class DragAndDropScene extends AbstractWidget {

	private ShapeRenderer shapeRenderer;

	private Modifier modifier;

	public DragAndDropScene(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
		this.modifier = new Modifier(shapeRenderer);
		addListener(new SceneListener());
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		batch.end();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(0, 0, getWidth(), getHeight());
		shapeRenderer.end();
		batch.begin();
	}

	/**
	 * Adjust the modifier to the transformation of the given actor
	 */
	public void setModifierFor(Actor actor) {
		modifier.readActorTransformation(actor);
		int position = getChildren().indexOf(actor, true);
		addActorAt(position + 1, modifier);
	}

	private class SceneListener extends DragListener {

		public static final int NONE = 0, MOVING = 1, RESIZING = 2,
				ROTATING = 3;

		private int state;

		private Actor selectedActor;

		private Actor draggedActor;

		public SceneListener() {
			setTapSquareSize(0);
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			Actor target = event.getTarget();
			if (target == DragAndDropScene.this) {
				selectedActor = null;
				modifier.remove();
			} else if (target != modifier && !(target instanceof Handle)
					&& target != selectedActor) {
				setModifierFor(target);
				selectedActor = target;
			}
			return super.touchDown(event, x, y, pointer, button);
		}

		@Override
		public void dragStart(InputEvent event, float x, float y, int pointer) {
			Actor target = event.getTarget();
			if (target == DragAndDropScene.this) {
				// selection
			} else if (target instanceof RotationHandle) {
				state = ROTATING;
				draggedActor = target;
				((RotationHandle) target).startRotation(selectedActor);
			} else if (target instanceof Handle) {
				state = RESIZING;
				draggedActor = target;
			} else if (target != modifier) {
				state = MOVING;
				draggedActor = target;
			}
		}

		@Override
		public void drag(InputEvent event, float x, float y, int pointer) {
			switch (state) {
			case MOVING:
				draggedActor.setPosition(draggedActor.getX() - getDeltaX(),
						draggedActor.getY() - getDeltaY());
				modifier.readActorTransformation(selectedActor);
				break;
			case RESIZING:
				if (draggedActor instanceof OriginHandle) {
					((OriginHandle) draggedActor).updateOrigin(selectedActor,
							x, y);
					modifier.applyHandleTransformation(selectedActor);
				} else {
					((Handle) draggedActor).updatePosition(selectedActor,
							draggedActor.getX() - getDeltaX(),
							draggedActor.getY() - getDeltaY());
					modifier.applyHandleTransformation(selectedActor);
				}
				break;
			case ROTATING:
				((RotationHandle) draggedActor).updateRotation(selectedActor,
						x, y);
				modifier.readActorTransformation(selectedActor);
				break;
			}
		}

		@Override
		public void dragStop(InputEvent event, float x, float y, int pointer) {
			state = NONE;
			draggedActor = null;
		}
	}
}
