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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.BrushStrokes;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;

/**
 * This widget holds the edition of a scene in Android. Also contains
 * {@link BrushStrokes}.
 * 
 */
public class MockupSceneEditor extends SceneEditor {

	private BrushStrokes brushStrokes;

	public MockupSceneEditor(Controller controller) {
		super(controller);
		setFillParent(true);
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

	public Actor getSceneview() {
		return groupEditor;
	}

	public void setBrushStrokes(BrushStrokes brushStrokes) {
		this.brushStrokes = brushStrokes;

		addActor(brushStrokes);
	}

	@Override
	protected GroupEditorConfiguration createGroupEditorConfiguration() {

		GroupEditorConfiguration config = new GroupEditorConfiguration();
		config.setRotationHandleOffset(40);
		config.setHandleSquareSize(12);
		config.setHandleCircleSize(10);
		return config;
	}

	@Override
	public void layout() {
		super.layout();
		brushStrokes.setBounds(0, 0, getWidth(), getHeight());
		brushStrokes.invalidate();
	}
}
