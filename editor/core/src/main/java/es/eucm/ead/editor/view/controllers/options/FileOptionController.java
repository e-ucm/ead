/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.controllers.options;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.view.controllers.OptionsController;
import es.eucm.ead.editor.view.controllers.constraints.FileExistConstraint;
import es.eucm.ead.editor.view.widgets.FileWidget;
import es.eucm.ead.editor.view.widgets.options.Option;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.I18N;

public class FileOptionController extends OptionController<FileWidget, String>
		implements FileChooserListener {

	private Assets assets;

	private Platform platform;

	private boolean folder;

	public FileOptionController(Controller controller, I18N i18N,
			OptionsController optionsController, String field, Option option,
			FileWidget widget) {
		super(i18N, optionsController, field, option, widget);
		this.platform = controller.getPlatform();
		this.assets = controller.getEditorAssets();
	}

	@Override
	protected void initialize() {
		widget.addButtonListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (folder) {
					platform.askForFolder(FileOptionController.this);
				} else {
					platform.askForFile(FileOptionController.this);
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

	public FileOptionController folder() {
		this.folder = true;
		return this;
	}

	public FileOptionController mustExist(boolean mustExist) {
		this.addConstraint(new FileExistConstraint(assets, mustExist));
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
