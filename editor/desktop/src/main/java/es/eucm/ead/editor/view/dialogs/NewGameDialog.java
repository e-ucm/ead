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
package es.eucm.ead.editor.view.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.views.HomeView;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.ClassOptionsController;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.controllers.DialogController.DialogButtonListener;
import es.eucm.ead.editor.view.controllers.OptionsController.ChangeListener;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.File;

public class NewGameDialog implements DialogBuilder {

	private Controller controller;

	private ClassOptionsController<ExtraData> extraData;

	private DialogController dialog;

	private ModelEntity game;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		I18N i18N = controller.getApplicationAssets().getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();

		game = new ModelEntity();
		Documentation doc = Q.getComponent(game, Documentation.class);
		ExtraData extra = new ExtraData();

		doc.setName(i18N.m("project.untitled"));
		extra.folder = controller.getApplicationAssets().toCanonicalPath(
				Gdx.files.external("").file().getAbsolutePath())
				+ "/eadgames/" + doc.getName();

		ClassOptionsController<Documentation> documentation = new ClassOptionsController<Documentation>(
				controller, skin, Documentation.class);
		extraData = new ClassOptionsController<ExtraData>(controller, skin,
				ExtraData.class);

		extraData.addChangeListener(new SizeListener());
		dialog = new DialogController(skin);

		extraData.read(extra);
		documentation.read(doc);

		LinearLayout panel = new LinearLayout(false);
		panel.add(documentation.getPanel()).expandX();
		panel.add(extraData.getPanel()).expandX();

		dialog.content(panel);
		dialog.button(i18N.m("general.create"), new CreateListener());
		dialog.button(i18N.m("general.cancel"), new CancelListener());
	}

	@Override
	public Dialog getDialog(Object... arguments) {
		controller.getCommands().pushStack();
		return dialog.getDialog();
	}

	@Override
	public void release(Controller controller) {

	}

	private class SizeListener implements ChangeListener {

		@Override
		public void valueUpdated(Type type, String field, Object value) {
			if ("quality".equals(field) || "aspectRatio".equals(field)) {
				extraData.getObjectRepresented().updateSize();
			}
		}
	}

	private class CancelListener implements DialogButtonListener {

		@Override
		public void selected() {
			controller.getCommands().popStack(false);
			dialog.hide();
		}
	}

	private class CreateListener implements DialogButtonListener {

		@Override
		public void selected() {
			ExtraData extra = extraData.getObjectRepresented();
			GameData gameData = Q.getComponent(game, GameData.class);
			gameData.setWidth(extra.width);
			gameData.setHeight(extra.height);

			EditState editState = Q.getComponent(game, EditState.class);
			editState.setView(HomeView.class.getName());

			controller.action(NewGame.class, extra.folder, game);
		}
	}

	public enum AspectRatio {
		_169("16:9"), _43("4:3");

		private String value;

		AspectRatio(String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}

	public enum Quality {
		SD, HD, FULLHD
	}

	public class ExtraData {

		@File(folder = true, mustExist = false)
		private String folder;

		private AspectRatio aspectRatio = AspectRatio._169;

		private Quality quality = Quality.SD;

		private int width = 1066;

		private int height = 600;

		public void updateSize() {
			int newWidth = 0;
			int newHeight = 0;
			switch (aspectRatio) {
			case _169:
				switch (quality) {
				case SD:
					newWidth = 1066;
					newHeight = 600;
					break;
				case HD:
					newWidth = 1600;
					newHeight = 900;
					break;
				case FULLHD:
					newWidth = 1920;
					newHeight = 1080;
					break;
				}
				break;
			case _43:
				switch (quality) {
				case SD:
					newWidth = 800;
					newHeight = 600;
					break;
				case HD:
					newWidth = 1024;
					newHeight = 768;
					break;
				case FULLHD:
					newWidth = 1280;
					newHeight = 960;
					break;
				}
				break;
			}
			CompositeCommand compositeCommand = new CompositeCommand() {
				@Override
				public boolean isTransparent() {
					return true;
				}
			};

			compositeCommand.addCommand(new FieldCommand(this, "width",
					newWidth));
			compositeCommand.addCommand(new FieldCommand(this, "height",
					newHeight));
			controller.getCommands().command(compositeCommand);
		}

	}
}
