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
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.ImageChooser;
import es.eucm.ead.editor.view.widgets.ToggleImageButton;
import es.eucm.ead.editor.view.widgets.layouts.LeftRightLayout;
import es.eucm.ead.editor.view.widgets.options.OptionsPanel;

public class ShowDialog extends EditorAction {

	public static final String NAME = "showDialog";

	public ShowDialog() {
		super(NAME, true);
	}

	@Override
	public void perform(Object... args) {
		Skin skin = controller.getEditorAssets().getSkin();
		Dialog dialog = new Dialog(controller.getEditorAssets().getSkin());
		dialog.title("Project Settings");

		OptionsPanel panel = new OptionsPanel(controller.getEditorAssets()
				.getSkin());
		panel.string("Title:", "This the title of the game project.", 150);
		panel.text("Description:", "The description", 150, 5);
		panel.file("Project folder:",
				"Where all the project files will be stored");

		panel.custom(
				"Icon:",
				"A 512x512 image for the icon for the application.",
				new ImageChooser(controller.getEditorAssets().getSkin(), 64, 64));

		Drawable bakground = skin.getDrawable("secondary-bg");
		LeftRightLayout qualityButtons = new LeftRightLayout(bakground);
		qualityButtons.margin(15.0f).pad(15.0f);

		qualityButtons.left(new ToggleImageButton(skin
				.getDrawable("quality169"), skin));
		qualityButtons.left(new ToggleImageButton(
				skin.getDrawable("quality43"), skin));

		panel.custom("Aspect ration:", "Aspect ratio of the game",
				qualityButtons);

		qualityButtons = new LeftRightLayout(bakground);
		qualityButtons.margin(5.0f).pad(10.0f);

		qualityButtons.left(new ToggleImageButton(
				skin.getDrawable("qualitysd"), skin));
		qualityButtons.left(new ToggleImageButton(
				skin.getDrawable("qualityhd"), skin));

		panel.custom("Quality:", "Quality of the game", qualityButtons);

		dialog.root(panel);

		dialog.button("Cancel", false);
		dialog.button("OK", true);

		dialog.setSize(dialog.getPrefWidth() + 1, dialog.getPrefHeight() + 2);
		controller.getViews().getRootContainer().addActor(dialog);
		dialog.center();
		dialog.invalidateHierarchy();
	}
}
