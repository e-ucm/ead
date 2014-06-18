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
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.mockup.gallery.ElementGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.Gallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.SceneGallery;
import es.eucm.ead.editor.view.builders.mockup.menu.PlayScreen;
import es.eucm.ead.editor.view.builders.mockup.menu.ProjectScreen;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.Icon;
import es.eucm.ead.engine.I18N;

/**
 * Panel with auxiliary navigation buttons.
 */
public class NavigationPanel extends HiddenPanel {

	private static final float ICON_PAD_LEFT = 15f, ICON_PAD_RIGHT = 20f;
	private static final String IC_EDITELEMENT = "ic_element",
			IC_EDITSTAGE = "ic_scene", IC_PLAYGAME = "ic_playgame",
			IC_GALLERY = "ic_gallery", IC_GOBACK = "ic_goback";
	private static final String LABEL_MARGIN = "  ";

	private Button projectButton;
	private Button editElementButton;
	private Button editSceneButton;
	private Button galleryButton;
	private Button lanuchGameButton;

	public NavigationPanel(Vector2 viewport, Controller controller, Skin skin) {
		super(skin);
		super.stageBackground = null;
		setBackground(stageBackground);
		final I18N i18n = controller.getApplicationAssets().getI18N();

		setVisible(false);

		final Label projectLabel = new Label(i18n.m("general.mockup.project")
				+ LABEL_MARGIN, skin);
		projectLabel.setAlignment(Align.left);

		final Image returnProject = new Image(skin.getDrawable(IC_GOBACK));

		this.projectButton = new Button(skin, "navigationPanelRest");

		this.projectButton.add(returnProject).padLeft(ICON_PAD_LEFT);
		this.projectButton.add(projectLabel).expandX().fillX();

		final Label editElementLabel = new Label(
				i18n.m("general.mockup.elements") + LABEL_MARGIN, skin);
		editElementLabel.setAlignment(Align.left);
		final Icon editElementImg = new Icon(viewport,
				skin.getDrawable(IC_EDITELEMENT));

		this.editElementButton = new Button(skin, "navigationPanelRest");
		this.editElementButton.add(editElementImg).padLeft(ICON_PAD_LEFT)
				.padRight(ICON_PAD_RIGHT);

		this.editElementButton.add(editElementLabel).expandX().fillX();

		final Label editSceneLabel = new Label(i18n.m("general.mockup.scenes")
				+ LABEL_MARGIN, skin);
		editSceneLabel.setAlignment(Align.left);
		final Icon editSceneImg = new Icon(viewport,
				skin.getDrawable(IC_EDITSTAGE));

		this.editSceneButton = new Button(skin, "navigationPanelRest");
		this.editSceneButton.add(editSceneImg).padLeft(ICON_PAD_LEFT)
				.padRight(ICON_PAD_RIGHT);

		this.editSceneButton.add(editSceneLabel).expandX().fillX();

		final Label galleryLabel = new Label(i18n.m("general.mockup.gallery")
				+ LABEL_MARGIN, skin);
		galleryLabel.setAlignment(Align.left);
		final Icon galleryImg = new Icon(viewport, skin.getDrawable(IC_GALLERY));

		this.galleryButton = new Button(skin, "navigationPanelRest");
		this.galleryButton.add(galleryImg).padLeft(ICON_PAD_LEFT)
				.padRight(ICON_PAD_RIGHT);

		this.galleryButton.add(galleryLabel).expandX().fillX();

		final Label lanuchGameLabel = new Label(i18n.m("general.mockup.play")
				+ LABEL_MARGIN, skin);
		lanuchGameLabel.setAlignment(Align.left);
		final Icon lanuchGameImg = new Icon(viewport,
				skin.getDrawable(IC_PLAYGAME));

		this.lanuchGameButton = new Button(skin, "navigationPanelRest");
		this.lanuchGameButton.add(lanuchGameImg).padLeft(ICON_PAD_LEFT)
				.padRight(ICON_PAD_RIGHT);

		this.lanuchGameButton.add(lanuchGameLabel).expandX().fillX();

		this.projectButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, ProjectScreen.class));
		this.editElementButton.addListener(new ActionOnClickListener(
				controller, ChangeView.class, ElementGallery.class));
		this.editSceneButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, SceneGallery.class));
		this.galleryButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, Gallery.class));
		this.lanuchGameButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, PlayScreen.class));

		defaults().expand().fill().uniform();
		add(this.projectButton);
		row();
		add(this.editElementButton);
		row();
		add(this.editSceneButton);
		row();
		add(this.galleryButton);
		row();
		add(this.lanuchGameButton);
	}

	public Array<Button> getButtonsPanelGoOut() {
		Array<Button> buttons = new Array<Button>();
		buttons.add(this.editElementButton);
		buttons.add(this.editSceneButton);
		buttons.add(this.galleryButton);
		buttons.add(this.lanuchGameButton);
		buttons.add(this.projectButton);
		return buttons;
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
}
