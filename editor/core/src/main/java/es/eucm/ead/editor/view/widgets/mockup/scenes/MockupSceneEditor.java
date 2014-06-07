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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
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

			private Actor target;

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				this.target = event.getTarget();
			}

			@Override
			public boolean longPress(Actor actor, float x, float y) {
				Group group = groupEditor.getGroupEditorDragListener()
						.getEditedGroupChild(this.target);
				Gdx.app.log("LongPress", group.toString());
				// TODO pop up the edition panel around the group

				return true;
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
		batch.flush();
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
