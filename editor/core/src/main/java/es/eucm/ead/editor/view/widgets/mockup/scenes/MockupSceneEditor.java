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
package es.eucm.ead.editor.view.widgets.mockup.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.editor.view.widgets.groupeditor.Handles;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.BrushStrokes;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;

/**
 * This widget holds the edition of a scene in Android. Also contains
 * {@link BrushStrokes}.
 * 
 */
public class MockupSceneEditor extends SceneEditor {

	private static final int HANDLE_CIRCLE_SIZE = 12;

	private static final int HANDLE_SQUARE_SIZE = 14;

	private static final int ROTATION_HANDLE_OFFSET = 44;

	private BrushStrokes brushStrokes;

	private Rectangle scissorBounds;

	public MockupSceneEditor(Controller controller) {
		super(controller);
		setFillParent(true);
		scissorBounds = new Rectangle();

		groupEditor.addListener(new ActorGestureListener() {

			private float rotation, scaleX, scaleY;
			private boolean rotationStarted, scaleStarted;
			private Handles handles = groupEditor.getGroupEditorDragListener()
					.getModifier().getHandles();

			@Override
			public void zoom(InputEvent event, float initialDistance,
					float distance) {
				Actor influencedActor = handles.getInfluencedActor();
				if (influencedActor == null) {
					return;
				} else if (!scaleStarted) {
					scaleStarted = true;
					scaleX = influencedActor.getScaleX();
					scaleY = influencedActor.getScaleY();
				}

				float ratio = distance / initialDistance;
				influencedActor.setScaleX(ratio * scaleX);
				influencedActor.setScaleY(ratio * scaleY);
			}

			@Override
			public void pinch(InputEvent event, Vector2 initialPointer1,
					Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
				Actor influencedActor = handles.getInfluencedActor();
				if (influencedActor == null) {
					return;
				} else if (!rotationStarted) {
					rotationStarted = true;
					rotation = influencedActor.getRotation();
				}

				Vector2 a = initialPointer2.sub(initialPointer1);
				Vector2 b = pointer2.sub(pointer1);

				float deltaRot = MathUtils.atan2(b.y, b.x)
						- MathUtils.atan2(a.y, a.x);
				float deltaRotDeg = (deltaRot * MathUtils.radiansToDegrees + 360);

				influencedActor.setRotation((deltaRotDeg + rotation) % 360);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (rotationStarted || scaleStarted) {
					rotationStarted = scaleStarted = false;

					// Notify the controller that something has changed so the
					// model gets updated
					Actor influencedActor = handles.getInfluencedActor();
					if (influencedActor == null) {
						return;
					}
					groupEditor.getGroupEditorDragListener().fireTransformed(
							influencedActor);
				}
			}
		});
	}

	public GroupEditor getSceneview() {
		return groupEditor;
	}

	public void setBrushStrokes(BrushStrokes brushStrokes) {
		this.brushStrokes = brushStrokes;
		addActor(brushStrokes);
	}

	@Override
	protected GroupEditorConfiguration createGroupEditorConfiguration() {

		GroupEditorConfiguration config = new GroupEditorConfiguration();
		config.setRotationHandleOffset(ROTATION_HANDLE_OFFSET);
		config.setHandleSquareSize(HANDLE_SQUARE_SIZE);
		config.setHandleCircleSize(HANDLE_CIRCLE_SIZE);

		return config;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		// Enable scissors for widget area and draw the widget.
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) scissorBounds.x, (int) scissorBounds.y,
				(int) scissorBounds.width, (int) scissorBounds.height);
		super.drawChildren(batch, parentAlpha);
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
	}

	@Override
	public void layout() {
		super.layout();
		brushStrokes.setBounds(getX(), getY(), getWidth(), getHeight());
		brushStrokes.invalidate();
		this.scissorBounds.set(getX(), getY(), getWidth(), getHeight());
		super.getStage().calculateScissors(scissorBounds, scissorBounds);
		fixScissorBounds();
		groupEditor.fit();
	}

	private void fixScissorBounds() {
		scissorBounds.x = Math.round(scissorBounds.x);
		scissorBounds.y = Math.round(scissorBounds.y);
		scissorBounds.width = Math.round(scissorBounds.width);
		scissorBounds.height = Math.round(scissorBounds.height);
		if (scissorBounds.width < 0) {
			scissorBounds.width = -scissorBounds.width;
			scissorBounds.x -= scissorBounds.width;
		}
		if (scissorBounds.height < 0) {
			scissorBounds.height = -scissorBounds.height;
			scissorBounds.y -= scissorBounds.height;
		}
	}
}
