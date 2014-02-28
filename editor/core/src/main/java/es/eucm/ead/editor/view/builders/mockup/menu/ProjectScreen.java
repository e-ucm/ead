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
package es.eucm.ead.editor.view.builders.mockup.menu;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeProjectTitle;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.builders.mockup.gallery.ElementGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.Gallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.SceneGallery;
import es.eucm.ead.editor.view.listeners.ActionForTextFieldListener;
import es.eucm.ead.editor.view.widgets.mockup.Options;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.POSITION;
import es.eucm.ead.engine.I18N;

public class ProjectScreen implements ViewBuilder {

	public static final String NAME = "mockup_project";
	private static final String IC_EDITELEMENT = "ic_editelement",
			IC_EDITSTAGE = "ic_editstage", IC_PLAYGAME = "ic_playgame",
			IC_GALLERY = "ic_gallery", IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera";

	private static final float INITIALSCENEBUTTON_FONT_SCALE = .6F;
	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .2F;
	private static final float TEXT_WIDTH_SCALAR = 1.35F;
	private static final int MAX_PROJ_TITLE_CHARACTERS = 30;
	private TextField projectTitleField;
	/**
	 * Cell that holds the {@link projectTitleField} TextField. Used to change
	 * its size when we change project's title.
	 */
	private Cell<?> projectTitleCell;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(final Controller controller) {
		final Skin skin = controller.getEditorAssets().getSkin();
		final I18N i18n = controller.getEditorAssets().getI18N();
		final Vector2 viewport = controller.getPlatform().getSize();

		final Button backButton = new IconButton(viewport, skin, "ic_goback",
				controller, ChangeView.NAME, InitialScreen.NAME);

		projectTitleField = new TextField("", skin);
		String msg = i18n.m("project.untitled");
		projectTitleField.setMessageText(msg);
		projectTitleField.setMaxLength(MAX_PROJ_TITLE_CHARACTERS);
		projectTitleField.setTextFieldListener(new ActionForTextFieldListener(
				projectTitleField,
				new ActionForTextFieldListener.TextChangedListener() {
					@Override
					public void onTextChanged() {
						projectTitleField.getStage().unfocusAll();
						resizeTextField(skin);
					}
				}, controller, ChangeProjectTitle.NAME));
		final Table topLeftWidgets = new Table().left().top().debug();
		topLeftWidgets.setFillParent(true);
		topLeftWidgets.add(backButton);
		projectTitleCell = topLeftWidgets
				.add(projectTitleField)
				.width(skin.getFont("default-font").getBounds(msg).width
						* TEXT_WIDTH_SCALAR).expandX().left();

		final Button scene, element, play, gallery, takePictureButton, recordVideoButton;
		final MenuButton initialSceneButton;
		scene = new MenuButton(viewport, i18n.m("general.mockup.scenes"), skin,
				IC_EDITSTAGE, POSITION.bottom, controller, ChangeView.NAME,
				SceneGallery.NAME);
		element = new MenuButton(viewport, i18n.m("general.mockup.elements"),
				skin, IC_EDITELEMENT, POSITION.bottom, controller,
				ChangeView.NAME, ElementGallery.NAME);
		gallery = new MenuButton(viewport, i18n.m("general.mockup.gallery"),
				skin, IC_GALLERY, POSITION.bottom, controller, ChangeView.NAME,
				Gallery.NAME);
		play = new MenuButton(viewport, i18n.m("general.mockup.play"), skin,
				IC_PLAYGAME, POSITION.bottom);

		takePictureButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				POSITION.bottom, controller, ChangeView.NAME, Picture.NAME);
		initialSceneButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.initial-scene"), skin, "icon-blitz",
				PREF_BOTTOM_BUTTON_WIDTH * 1.5f, PREF_BOTTOM_BUTTON_HEIGHT,
				POSITION.bottom, controller, ChangeView.NAME, SceneGallery.NAME);
		initialSceneButton.getLabel().setFontScale(
				INITIALSCENEBUTTON_FONT_SCALE);
		recordVideoButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.video"), skin, IC_VIDEOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				POSITION.bottom, controller, ChangeView.NAME, Video.NAME);
		Table bottomButtons = new Table().debug().bottom();
		bottomButtons.setFillParent(true);
		bottomButtons.add(takePictureButton);
		bottomButtons.add(initialSceneButton).expandX();
		bottomButtons.add(recordVideoButton);

		Options opt = new Options(viewport, controller, skin);

		Table window = new Table().debug();
		window.setFillParent(true);
		window.addActor(topLeftWidgets);
		window.row();
		window.add(scene, element, gallery, play);
		window.row();
		window.addActor(bottomButtons);
		window.addActor(opt);
		return window;
	}

	private void resizeTextField(Skin skin) {
		// Now we resize the text field to match it's new text
		final String newTitle = projectTitleField.getText();
		projectTitleField.setCursorPosition(0);
		final float newWidth = skin.getFont("default-font").getBounds(newTitle).width
				* TEXT_WIDTH_SCALAR;
		projectTitleCell.width(Math.max(projectTitleField.getMinWidth(),
				newWidth));
		projectTitleCell.getLayout().invalidateHierarchy();
	}

	@Override
	public void initialize(Controller controller) {
		controller.getProjectAssets().finishLoading();
		projectTitleField
				.setText(controller.getModel().getProject().getTitle());
		if (!projectTitleField.getText().isEmpty()) {
			resizeTextField(controller.getEditorAssets().getSkin());
		}
	}

	@Override
	public void release(Controller controller) {
	}
}
