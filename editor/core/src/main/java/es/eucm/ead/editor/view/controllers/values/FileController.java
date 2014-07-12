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
package es.eucm.ead.editor.view.controllers.values;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.view.controllers.constraints.FileExistConstraint;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.engine.assets.GameAssets;

public class FileController extends ValueController<FileWidget, String>
		implements FileChooserListener {

	private GameAssets gameAssets;

	private Platform platform;

	private boolean folder;

	@Override
	public void build(Controller controller, FileWidget widget) {
		super.build(controller, widget);
		this.platform = controller.getPlatform();
		this.gameAssets = controller.getEditorGameAssets();
	}

	@Override
	protected void initialize() {
		widget.addButtonListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (folder) {
					platform.askForFolder(FileController.this);
				} else {
					platform.askForFile(FileController.this);
				}
				return true;
			}
		});
		widget.getTextField().addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				change(widget.getTextField().getText());
				return true;
			}
		});
	}

	public FileController folder() {
		this.folder = true;
		return this;
	}

	public FileController mustExist(boolean mustExist) {
		this.addConstraint(new FileExistConstraint(gameAssets, mustExist));
		return this;
	}

	@Override
	public void fileChosen(String path) {
		if (path != null) {
			change(path);
		}
	}

	@Override
	public void setWidgetValue(String value) {
		if (!widget.getTextField().getText().equals(value)) {
			widget.setText(value);
		}
	}

}
