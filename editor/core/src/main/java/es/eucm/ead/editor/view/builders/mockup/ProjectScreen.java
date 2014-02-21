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
package es.eucm.ead.editor.view.builders.mockup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.Options;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.engine.I18N;

public class ProjectScreen implements ViewBuilder {

	public static final String NAME = "mockup_project";
	private static final float PREF_BUTTON_WIDTH = .075F;
	private static final String IC_EDITELEMENT = "ic_editelement",
			IC_EDITSTAGE = "ic_editstage", IC_PLAYGAME = "ic_playgame",
			IC_GALLERY = "ic_gallery", IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .2F;
	private static final float INITIALSCENEBUTTON_FONT_SCALE = .6F;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		Skin skin = controller.getEditorAssets().getSkin();
		I18N i18n = controller.getEditorAssets().getI18N();

		final ImageButton backButton = new ImageButton(skin, "ic_goback") {
			@Override
			public float getPrefWidth() {
				// We make sure it's a square and return the prefWidth
				return Math.max(super.getPrefHeight(), Gdx.graphics.getWidth()
						* PREF_BUTTON_WIDTH);
			}

			@Override
			public float getPrefHeight() {
				// We make sure it's a square
				return Math.max(super.getPrefHeight(), getPrefWidth());
			}
		};
		backButton.getImageCell().expand().fill();
		backButton.addListener(new ActionOnClickListener(controller,
				ChangeView.NAME, InitialScreen.NAME));

		final TextField projectNameField = new TextField("", skin);
		String msg = i18n.m("project.untitled");
		projectNameField.setMessageText(msg);
		Table topLeftWidgets = new Table().left().top().debug();
		topLeftWidgets.setFillParent(true);
		topLeftWidgets.add(backButton);
		topLeftWidgets.add(projectNameField).width(
				skin.getFont("default-font").getBounds(msg).width * 1.2f);

		final MenuButton scene, element, play, gallery, takePictureButton, initialSceneButton, recordVideoButton;
		scene = new MenuButton(i18n.m("general.mockup.scenes"), skin,
				IC_EDITSTAGE);
		element = new MenuButton(i18n.m("general.mockup.elements"), skin,
				IC_EDITELEMENT);
		gallery = new MenuButton(i18n.m("general.mockup.gallery"), skin,
				IC_GALLERY);
		play = new MenuButton(i18n.m("general.mockup.play"), skin, IC_PLAYGAME);

		takePictureButton = new BottomProjectMenuButton(
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				controller, ChangeView.NAME, "TAKEPHOTO_VIEW_HERE");
		initialSceneButton = new BottomProjectMenuButton(
				i18n.m("general.mockup.initial-scene"), skin, "icon-blitz",
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				controller, ChangeView.NAME, "GALLERY_VIEW_HERE");
		initialSceneButton.getLabel().setFontScale(INITIALSCENEBUTTON_FONT_SCALE);
		recordVideoButton = new BottomProjectMenuButton(
				i18n.m("general.mockup.video"), skin, IC_VIDEOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				controller, ChangeView.NAME, "RECORDVIDEO_VIEW_HERE");
		Table bottomButtons = new Table().debug().bottom();
		bottomButtons.setFillParent(true);
		bottomButtons.add(takePictureButton);
		bottomButtons.add(initialSceneButton).expandX();
		bottomButtons.add(recordVideoButton);

		Options opt = new Options(controller, skin);
		opt.setFillParent(true);

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

	@Override
	public void initialize(Controller controller) {
	}

	@Override
	public void release(Controller controller) {
	}
}
