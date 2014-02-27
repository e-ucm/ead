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
package es.eucm.ead.editor.view.builders.classic.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.controllers.DialogController.DialogButtonListener;
import es.eucm.ead.editor.view.controllers.OptionsController;
import es.eucm.ead.editor.view.controllers.OptionsController.ChangeListener;
import es.eucm.ead.editor.view.controllers.options.FileOptionController;
import es.eucm.ead.editor.view.controllers.options.OptionController;
import es.eucm.ead.editor.view.controllers.options.StringOptionController;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.options.OptionsPanel;
import es.eucm.ead.engine.I18N;

import java.util.Map;

public class NewProjectDialog implements DialogBuilder {

	public static String NAME = "newproject";

	private Controller controller;

	public NewProjectDialog() {

	}

	@Override
	public String getName() {
		return NAME;
	}

	public Dialog build(Controller c) {
		this.controller = c;
		I18N i18N = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();

		final OptionsController builder = new OptionsController(controller,
				skin);

		builder.i18nPrefix("project");
		final StringOptionController titleOption = builder.string("title", 100)
				.maxLength(50).minLength(1);

		builder.text("description", 100, 3).maxLength(255).change("");

		final FileOptionController folderOption = builder.file("folder")
				.folder().mustExist(false);

		ChangeListener changeListener = new ChangeListener() {

			private String rootFolder;

			private String title = "";

			@Override
			public void valueUpdated(final OptionController source,
					String field, Object value) {
				if (source == folderOption) {
					String path = value.toString();
					rootFolder = (path.endsWith("/") ? path : path + "/")
							+ title;
				} else if (source == titleOption) {
					title = value.toString();
					folderOption.setWidgetValue(rootFolder + value);
					folderOption.checkConstraints(rootFolder + value);
					builder.setValue("folder", rootFolder + value);
				}
			}
		};

		builder.addChangeListener(changeListener);

		String path = Gdx.files.external("").file().getAbsolutePath()
				+ "/eadgames/";
		folderOption.change(path);
		titleOption.change(i18N.m("project.untitled"));

		OptionsPanel p = builder.getPanel();

		final DialogController dialogController = new DialogController(skin);

		Dialog dialog = dialogController.title(i18N.m("project.settings"))
				.root(p).getDialog();

		dialogController.closeButton(i18N.m("general.cancel"));
		dialogController.button(i18N.m("general.ok"), true,
				new DialogButtonListener() {
					@Override
					public void selected() {
						Map<String, Object> values = builder.getValues();
						String title = values.get("title").toString();
						String description = values.get("description")
								.toString();
						String projectFolder = values.get("folder").toString();

						Project project = new Project();
						project.setTitle(title);
						project.setDescription(description);

						controller.action(NewGame.NAME, projectFolder, project);
						dialogController.close();
					}
				});
		return dialog;
	}
}
