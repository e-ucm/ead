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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.mockup.gallery.ElementGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.Gallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.SceneGallery;
import es.eucm.ead.editor.view.builders.mockup.menu.ProjectScreen;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.Icon;
import es.eucm.ead.engine.I18N;

/**
 * Panel with auxiliary navigation buttons.
 */
public class NavigationPanel extends HiddenPanel {

	private static final float ICON_PAD_LEFT = 15f;
	private static final String IC_EDITELEMENT = "ic_editelement",
			IC_EDITSTAGE = "ic_editstage", IC_PLAYGAME = "ic_playgame",
			IC_GALLERY = "ic_gallery", IC_GOBACK = "ic_goback";
	private static final float PANEL_PAD = 15f;

	public NavigationPanel(Vector2 viewport, Controller controller, Skin skin) {
		super(skin);
		super.stageBackground = null;
		setBackground("dialogDim");
		final I18N i18n = controller.getApplicationAssets().getI18N();

		setVisible(false);

		final Label projectLabel = new Label(i18n.m("general.mockup.project"),
				skin);
		projectLabel.setAlignment(Align.center);

		final Image projectImg = new Image(skin.getDrawable(IC_GOBACK)); // back
		// project
		// img
		final Button projectButton = new Button(skin, "navigationPanelProject");

		projectButton.add(projectImg).padLeft(ICON_PAD_LEFT);
		projectButton.add(projectLabel).expand();

		final Label editElementLabel = new Label(
				i18n.m("general.mockup.elements"), skin);
		editElementLabel.setAlignment(Align.center);
		final Icon editElementImg = new Icon(viewport,
				skin.getDrawable(IC_EDITELEMENT)); // edit
		// element
		// img
		final Button editElementButton = new Button(skin, "navigationPanelRest");
		editElementButton.add(editElementImg).padLeft(ICON_PAD_LEFT);
		editElementButton.add(editElementLabel).expandX();

		final Label editSceneLabel = new Label(i18n.m("general.mockup.scenes"),
				skin);
		editSceneLabel.setAlignment(Align.center);
		final Icon editSceneImg = new Icon(viewport,
				skin.getDrawable(IC_EDITSTAGE)); // edit
		// scene
		// img
		final Button editSceneButton = new Button(skin, "navigationPanelRest");
		editSceneButton.add(editSceneImg).padLeft(ICON_PAD_LEFT);
		editSceneButton.add(editSceneLabel).expandX();

		final Label galleryLabel = new Label(i18n.m("general.mockup.gallery"),
				skin);
		galleryLabel.setAlignment(Align.center);
		final Icon galleryImg = new Icon(viewport, skin.getDrawable(IC_GALLERY)); // gallery
		// img
		final Button galleryButton = new Button(skin, "navigationPanelRest");
		galleryButton.add(galleryImg).padLeft(ICON_PAD_LEFT);
		galleryButton.add(galleryLabel).expandX();

		final Label lanuchGameLabel = new Label(i18n.m("general.mockup.play"),
				skin);
		lanuchGameLabel.setAlignment(Align.center);
		final Icon lanuchGameImg = new Icon(viewport,
				skin.getDrawable(IC_PLAYGAME)); // launch
		// img
		final Button lanuchGameButton = new Button(skin, "navigationPanelRest");
		lanuchGameButton.add(lanuchGameImg).padLeft(ICON_PAD_LEFT);
		lanuchGameButton.add(lanuchGameLabel).expandX();

		projectButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, ProjectScreen.NAME));
		editElementButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, ElementGallery.NAME));
		editSceneButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, SceneGallery.NAME));
		galleryButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, Gallery.NAME));

		pad(PANEL_PAD);
		defaults().expand().fill().space(PANEL_PAD).uniform();
		add(projectButton);
		row();
		add(editElementButton);
		row();
		add(editSceneButton);
		row();
		add(galleryButton);
		row();
		add(lanuchGameButton);
	}

	@Override
	public void show() {
		if (super.fadeDuration > 0) {
			setPosition(-getStage().getWidth(), getY());
			addAction(Actions.parallel(Actions.alpha(0), Actions.moveTo(0,
					getY(), super.fadeDuration, Interpolation.sineOut), Actions
					.fadeIn(super.fadeDuration, Interpolation.fade)));

		}
		setVisible(true);
	}

	@Override
	public void hide() {
		if (super.fadeDuration > 0) {
			addAction(Actions.parallel(Actions.sequence(
					Actions.fadeOut(super.fadeDuration, Interpolation.fade),
					Actions.run(super.hideRunnable)), Actions.moveTo(
					-getWidth(), getY(), super.fadeDuration)));
		} else {
			setVisible(false);
		}
	}

	@Override
	public float getPrefHeight() {
		return 200;
	}
}
