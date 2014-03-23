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
package es.eucm.ead.editor.view.builders.classic.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.NewGame;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.controllers.DialogController.DialogButtonListener;
import es.eucm.ead.editor.view.controllers.OptionsController;
import es.eucm.ead.editor.view.controllers.OptionsController.ChangeListener;
import es.eucm.ead.editor.view.controllers.options.FileOptionController;
import es.eucm.ead.editor.view.controllers.options.OptionController;
import es.eucm.ead.editor.view.controllers.options.StringOptionController;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.ToggleImageButton;
import es.eucm.ead.editor.view.widgets.layouts.LeftRightLayout;
import es.eucm.ead.editor.view.widgets.options.OptionsPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.game.EditorGame;

import java.util.Map;

public class NewProjectDialog implements DialogBuilder {

	public static String NAME = "newproject";

	private Controller controller;

	private OptionsController optionsController;

	public NewProjectDialog() {

	}

	@Override
	public String getName() {
		return NAME;
	}

	public Dialog build(Controller c, Object... arguments) {
		this.controller = c;
		I18N i18N = controller.getApplicationAssets().getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();

		optionsController = new OptionsController(controller, skin);

		optionsController.i18nPrefix("project");
		final StringOptionController titleOption = optionsController
				.string("title", 100).maxLength(50).minLength(1);

		optionsController.text("description", 100, 3).maxLength(255).change("");

		final FileOptionController folderOption = optionsController
				.file("folder", 200).folder().mustExist(false);

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
					optionsController.setValue("folder", rootFolder + value);
				}
			}
		};

		optionsController.addChangeListener(changeListener);

		String path = controller.getApplicationAssets().toCanonicalPath(
				Gdx.files.external("").file().getAbsolutePath())
				+ "/eadgames/";
		folderOption.change(path);
		titleOption.change(i18N.m("project.untitled"));

		createResolutionButtons(skin);

		OptionsPanel p = optionsController.getPanel();
		return createDialog(p, i18N, skin);

	}

	private void createResolutionButtons(Skin skin) {
		Drawable bg = skin.getDrawable("secondary-bg");

		LeftRightLayout aspectRatio = new LeftRightLayout(bg);
		aspectRatio.left(new ToggleImageButton(skin
				.getDrawable("aspectRatio169"), skin));
		aspectRatio.left(new ToggleImageButton(skin
				.getDrawable("aspectRatio43"), skin));

		optionsController.toggleImages("aspectRatio")
				.button(skin.getDrawable("aspectRatio169"), "169")
				.button(skin.getDrawable("aspectRatio43"), "43").change("169");

		optionsController.toggleImages("quality")
				.button(skin.getDrawable("qualitysd"), "sd")
				.button(skin.getDrawable("qualityhd"), "hd")
				.button(skin.getDrawable("qualityfullhd"), "fullhd")
				.change("sd");
	}

	private Dialog createDialog(OptionsPanel p, I18N i18N, Skin skin) {
		final DialogController dialogController = new DialogController(skin);

		Dialog dialog = dialogController.title(i18N.m("project.settings"))
				.root(p).getDialog();

		dialogController.button(i18N.m("general.ok"), true,
				new DialogButtonListener() {
					@Override
					public void selected() {
						Map<String, Object> values = optionsController
								.getValues();
						// Project
						String title = values.get("title").toString();
						String description = values.get("description")
								.toString();
						String projectFolder = values.get("folder").toString();

						// FIXME I don't think all this stuff should be done
						// here.
						EditorGame game = controller.getTemplates().createGame(
								title, description);
						// Set the appVersion for this game
						game.setAppVersion(controller.getAppVersion());

						// Game
						String aspectRatio = values.get("aspectRatio")
								.toString();
						String quality = values.get("quality").toString();

						Vector2 baseResolution = new Vector2();
						float multiplier = 1.0f;

						if ("43".equals(aspectRatio)) {
							baseResolution.set(800, 600);
						} else if ("169".equals(aspectRatio)) {
							baseResolution.set(1066, 600);
						}

						if ("sd".equals(quality)) {
							multiplier = 1.0f;
						} else if ("hd".equals(quality)) {
							multiplier = 1.5f;
						} else if ("fullhd".equals(quality)) {
							multiplier = 2.0f;
						}

						baseResolution.scl(multiplier);

						game.setWidth(Math.round(baseResolution.x));
						game.setHeight(Math.round(baseResolution.y));

						controller.action(NewGame.class, projectFolder, game);
						dialogController.close();
					}
				});
		dialogController.closeButton(i18N.m("general.cancel"));
		return dialog;
	}
}
