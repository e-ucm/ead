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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.widgets.Toolbar.ToolbarStyle;
import es.eucm.ead.editor.view.widgets.editionview.elementcontext.ElementContext;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.editor.view.widgets.groupeditor.Handles;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;
import es.eucm.ead.schema.entities.ModelEntity;

public class MockupSceneEditor extends SceneEditor {

	private static final int HANDLE_CIRCLE_SIZE = 15;

	private static final int HANDLE_SQUARE_SIZE = 15;

	private static final int ROTATION_HANDLE_OFFSET = 45;

	private static final boolean MULTIPLE_SELECTION = false;

	private static final boolean NESTED_GROUP_EDITION = false;

	private SelectionListener elementSelected;

	private ElementContext context;

	private final Rectangle scissorBounds;
	private final float leftPad;
	private final float topPad;
	private boolean hasSelection;

	private Runnable updateSelection = new Runnable() {

		@Override
		public void run() {
			Object[] selection = controller.getModel().getSelection()
					.get(Selection.SCENE_ELEMENT);
			if (selection.length > 0) {
				Object object = selection[0];
				if (object instanceof ModelEntity) {
					ModelEntity entity = (ModelEntity) object;
					Actor actor = findActor(entity);
					if (actor != null) {
						hasSelection = true;
						context.show(entity, actor);
					} else {
						hasSelection = false;
						context.show(null, null);
					}
				} else {
					hasSelection = false;
					context.show(null, null);
				}
			}
		}
	};

	public MockupSceneEditor(final Controller controller, String leftStyle,
			String topStyle) {
		super(controller);

		Skin skin = controller.getApplicationAssets().getSkin();
		ToolbarStyle toolbarStyle = skin.get(leftStyle, ToolbarStyle.class);
		leftPad = toolbarStyle.background.getRightWidth();
		toolbarStyle = skin.get(topStyle, ToolbarStyle.class);
		topPad = toolbarStyle.background.getBottomHeight();

		scissorBounds = new Rectangle();
		context = new ElementContext(controller, this);
		hasSelection = false;
		elementSelected = new SelectionListener() {

			@Override
			public void modelChanged(SelectionEvent event) {
				if (event.getType() == SelectionEvent.Type.FOCUSED) {
					Gdx.app.postRunnable(updateSelection);
				} else if (event.getType() == SelectionEvent.Type.REMOVED) {
					hasSelection = false;
				}
			}

			@Override
			public boolean listenToContext(String contextId) {
				return Selection.SCENE_ELEMENT.equals(contextId);
			}
		};

		groupEditor.addListener(new InputListener() {
			private Vector2 pointer1 = new Vector2();
			private Vector2 pointer2 = new Vector2();
			private Vector2 initialPointer1 = new Vector2();
			private Vector2 initialPointer2 = new Vector2();

			private float rotation, scaleX, scaleY;
			private Handles handles = groupEditor.getGroupEditorDragListener()
					.getModifier().getHandles();
			private boolean pinching;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!hasSelection) {
					return false;
				}
				Actor influencedActor = handles.getInfluencedActor();
				if (influencedActor == null) {
					return false;
				}
				context.show(null, null);
				scaleX = influencedActor.getScaleX();
				scaleY = influencedActor.getScaleY();
				rotation = influencedActor.getRotation();
				if (pointer < 2) {
					if (pointer == 0) {
						initialPointer1.set(x, y);
						pointer1.set(initialPointer1);
					} else {
						// Start pinch.
						initialPointer2.set(x, y);
						pointer2.set(initialPointer2);
						pinching = true;
					}
				}
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {

				if (pointer == 1) {
					pointer2.set(x, y);
				} else {
					pointer1.set(Gdx.input.getX(0), Gdx.input.getY(0));
					getStage().screenToStageCoordinates(pointer1);
					groupEditor.stageToLocalCoordinates(pointer1);
				}
				// handle pinch zoom
				if (pinching) {
					Actor influencedActor = handles.getInfluencedActor();
					if (influencedActor == null) {
						return;
					}

					float intialDistance = initialPointer1.dst(initialPointer2);
					float distance = pointer1.dst(pointer2);

					pinch(influencedActor, initialPointer1, initialPointer2,
							pointer1, pointer2);
					zoom(influencedActor, intialDistance, distance);
				}
			}

			public void pinch(Actor actor, Vector2 initialPointer1,
					Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {

				float x1 = initialPointer2.x - initialPointer1.x;
				float y1 = initialPointer2.y - initialPointer1.y;
				float x2 = pointer2.x - pointer1.x;
				float y2 = pointer2.y - pointer1.y;

				float deltaRot = MathUtils.atan2(y2, x2)
						- MathUtils.atan2(y1, x1);
				float deltaRotDeg = (deltaRot * MathUtils.radiansToDegrees + 360);

				actor.setRotation((deltaRotDeg + rotation) % 360);
			}

			public void zoom(Actor actor, float initialDistance, float distance) {

				float ratio = distance / initialDistance;
				actor.setScaleX(ratio * scaleX);
				actor.setScaleY(ratio * scaleY);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.postRunnable(updateSelection);
				if (pinching) {
					pinching = false;

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

	public Group getContainer() {
		return groupEditor.getGroupEditorDragListener().getContainer();
	}

	@Override
	public void prepare() {
		super.prepare();
		groupEditor.fit(false);
		controller.getModel().addSelectionListener(elementSelected);
	}

	@Override
	protected void readSceneContext() {
		super.readSceneContext();
		groupEditor.fit(false);
	}

	@Override
	public void layout() {
		groupEditor.setBounds(0, 0, getWidth(), getHeight());
		groupEditor.fit(false);

		scissorBounds.set(getX() - leftPad, getY(), getWidth() + 4 * leftPad,
				getHeight() + topPad);
		getStage().calculateScissors(scissorBounds, scissorBounds);
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
	public void release() {
		super.release();
		controller.getModel().removeSelectionListener(elementSelected);
	}

	@Override
	protected GroupEditorConfiguration createGroupEditorConfiguration() {

		GroupEditorConfiguration config = new GroupEditorConfiguration();
		config.setRotationHandleOffset(ROTATION_HANDLE_OFFSET);
		config.setNestedGroupEdition(NESTED_GROUP_EDITION);
		config.setMultipleSelection(MULTIPLE_SELECTION);
		config.setHandleSquareSize(HANDLE_SQUARE_SIZE);
		config.setHandleCircleSize(HANDLE_CIRCLE_SIZE);

		return config;
	}
}
