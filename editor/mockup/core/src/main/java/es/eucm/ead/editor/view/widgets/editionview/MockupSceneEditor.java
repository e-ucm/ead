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

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;

public class MockupSceneEditor extends SceneEditor {

	private static final int HANDLE_CIRCLE_SIZE = 12;

	private static final int HANDLE_SQUARE_SIZE = 14;

	private static final int ROTATION_HANDLE_OFFSET = 44;

	public MockupSceneEditor(Controller controller) {
		super(controller);

	}

	@Override
	protected void addWidgets(Skin skin) {
		super.addWidgets(skin);

	}

	public GroupEditor getGroupEditor() {
		return groupEditor;
	}

	@Override
	public void layout() {
		groupEditor.setBounds(0, 0, getWidth(), getHeight());
		groupEditor.fit(false);
	}

	@Override
	protected GroupEditorConfiguration createGroupEditorConfiguration() {

		GroupEditorConfiguration config = new GroupEditorConfiguration();
		config.setRotationHandleOffset(ROTATION_HANDLE_OFFSET);
		config.setHandleSquareSize(HANDLE_SQUARE_SIZE);
		config.setHandleCircleSize(HANDLE_CIRCLE_SIZE);

		return config;
	}
}
