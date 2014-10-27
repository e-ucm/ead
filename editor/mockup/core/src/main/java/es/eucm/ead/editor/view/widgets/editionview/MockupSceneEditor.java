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
import com.badlogic.gdx.math.Rectangle;
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
import es.eucm.ead.editor.view.widgets.groupeditor.Modifier;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;
import es.eucm.ead.schema.entities.ModelEntity;

public class MockupSceneEditor extends SceneEditor {

	private static final int HANDLE_CIRCLE_SIZE = 15;

	private static final int HANDLE_SQUARE_SIZE = 15;

	private static final int ROTATION_HANDLE_OFFSET = 45;

	private static final boolean MULTIPLE_SELECTION = true;

	private static final boolean NESTED_GROUP_EDITION = true;

	private SelectionListener elementSelected;

	private ElementContext context;

	private final Rectangle scissorBounds;
	private final float topPad;

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
						context.show(entity, actor);
					} else {
						context.show(null, null);
					}
				} else {
					context.show(null, null);
				}
			} else {
				context.show(null, null);
			}
		}
	};

	public MockupSceneEditor(final Controller controller, String topStyle) {
		super(controller);

		Skin skin = controller.getApplicationAssets().getSkin();
		ToolbarStyle toolbarStyle = skin.get(topStyle, ToolbarStyle.class);
		topPad = toolbarStyle.background.getBottomHeight();

		scissorBounds = new Rectangle();
		context = new ElementContext(controller, this);
		elementSelected = new SelectionListener() {
			private boolean isElement;

			@Override
			public void modelChanged(SelectionEvent event) {
				if (isElement) {
					if (event.getType() == SelectionEvent.Type.FOCUSED) {
						Gdx.app.postRunnable(updateSelection);
					}
				} else {
					context.show(null, null);
				}
			}

			@Override
			public boolean listenToContext(String contextId) {
				isElement = Selection.SCENE_ELEMENT.equals(contextId);
				return isElement || Selection.SCENE.equals(contextId);
			}
		};
		groupEditor.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				context.show(null, null);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.postRunnable(updateSelection);
			}
		});
	}

	public Group getContainer() {
		return groupEditor.getGroupEditorDragListener().getContainer();
	}

	public Modifier getModifier() {
		return groupEditor.getGroupEditorDragListener().getModifier();
	}

	public Group getRootGroup() {
		return groupEditor.getGroupEditorDragListener().getRootGroup();
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

		scissorBounds.set(getX(), getY(), getWidth(), getHeight() + topPad);
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
		config.drawHandles = false;

		return config;
	}
}
