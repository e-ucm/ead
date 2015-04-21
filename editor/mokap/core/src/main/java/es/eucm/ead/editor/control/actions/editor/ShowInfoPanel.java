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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.SliderPagesWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class ShowInfoPanel extends EditorAction {

	private static final float BIG_PAD = WidgetBuilder.dpToPixels(48),
			NORMAL_PAD = WidgetBuilder.dpToPixels(24),
			LITTLE_PAD = WidgetBuilder.dpToPixels(12);
	private static final float ANIMATION_TIME = 0.5f;

	private Skin skin;

	private I18N i18n;

	private Preferences preferences;

	ClickListener skipListener;

	public static enum TypePanel {
		ACCURATE_SELECTION, COMPOSE, ZONES, INTRODUCTION, MULTIPLE_SELECTION, PLAY
	}

	public ShowInfoPanel() {
		super(true, true, TypePanel.class, String.class);
	}

	@Override
	public void initialize(final Controller controller) {
		super.initialize(controller);
		skin = controller.getApplicationAssets().getSkin();
		i18n = controller.getApplicationAssets().getI18N();
		preferences = controller.getPreferences();

		skipListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				final Actor root = (Actor) actor.getUserObject();
				hide(root);
			}
		};
	}

	private Actor createIntroduction() {

		ModalTable introduction = new ModalTable(skin);
		introduction.setWidth(Gdx.graphics.getWidth());
		introduction.align(Align.top);

		Label intro = new Label(i18n.m("info.panel.make.multimedia"), skin);
		intro.setAlignment(Align.center);

		Label intro2 = new Label(i18n.m("info.panel.barrier.imagination"), skin);
		intro2.setAlignment(Align.center);

		introduction.add(new Image(skin.getDrawable(SkinConstants.MOKAP_LOGO)))
				.top();
		introduction.row();
		introduction.add(intro).padTop(NORMAL_PAD);
		introduction.row();
		introduction.add(intro2).padTop(NORMAL_PAD);

		SliderPagesWidget sliderPages = new SliderPagesWidget(
				SkinConstants.DRAWABLE_BLANK, skin, introduction, createPage(
						i18n.m("info.panel.make.presentations"),
						SkinConstants.TABLET_PRESENTATION), createPage(
						i18n.m("info.panel.make.games"),
						SkinConstants.TABLET_GAME), createPage(
						i18n.m("info.panel.make.cards"),
						SkinConstants.TABLET_CARD));

		IconButton close = new IconButton(SkinConstants.IC_CLOSE, skin,
				SkinConstants.STYLE_SLIDER_PAGES);
		close.setUserObject(sliderPages);
		close.addListener(skipListener);
		close.pack();
		close.setBounds(Gdx.graphics.getWidth() - close.getWidth(),
				Gdx.graphics.getHeight() - close.getHeight(), close.getWidth(),
				close.getHeight());
		sliderPages.addActor(close);

		introduction.setBackObjetive(sliderPages);

		return sliderPages;
	}

	private Actor createPage(String text, String image) {

		Table page = new Table(skin) {
			@Override
			public float getPrefWidth() {
				return Gdx.graphics.getWidth();
			}
		};
		page.setWidth(Gdx.graphics.getWidth());
		page.align(Align.top);

		Label label = new Label(text, skin);
		label.setAlignment(Align.center);

		page.add(new Image(skin.getDrawable(image))).top();
		page.row();
		page.add(label).padTop(NORMAL_PAD);

		return page;
	}

	private Actor createInfo(String mainImage, String text, String... images) {
		Table panel = new ModalTable(skin);
		panel.setTouchable(Touchable.enabled);
		panel.setFillParent(true);
		panel.setBackground(skin.getDrawable(SkinConstants.DRAWABLE_SEMI_BLANK));
		panel.defaults().pad(LITTLE_PAD);

		TextButton skip = new TextButton(i18n.m("continue"), skin);
		skip.addListener(skipListener);
		skip.setUserObject(panel);

		Label label = new Label(text, skin);
		label.setWrap(true);
		label.setAlignment(Align.center);

		panel.add(new Image(skin.getDrawable(mainImage))).top()
				.padTop(NORMAL_PAD);
		panel.row();
		panel.add(label).width(Gdx.graphics.getWidth() - BIG_PAD * 2);
		panel.row();

		if (images.length > 0) {
			LinearLayout linear = new LinearLayout(true);
			for (String image : images) {
				linear.add(new Image(skin.getDrawable(image)));
			}
			panel.add(linear);
			panel.row();
		}

		panel.add(skip).bottom().expandY();

		return panel;
	}

	@Override
	public void perform(Object... args) {
		TypePanel type = (TypePanel) args[0];
		String preference = (String) args[1];

		if (preferences.getBoolean(preference, true)) {
			preferences.putBoolean(preference, false);
			preferences.flush();

			Actor actor = null;
			if (type.equals(TypePanel.ACCURATE_SELECTION)) {
				actor = createInfo(SkinConstants.MOKAP_ACCURATE_SELECT,
						i18n.m("info.panel.accurate.selection"),
						SkinConstants.TABLET_ACCURATE_SELECT);
			} else if (type.equals(TypePanel.COMPOSE)) {
				actor = createInfo(SkinConstants.MOKAP_COMPOSE,
						i18n.m("info.panel.mode.compose"),
						SkinConstants.TABLET_INSERT);
			} else if (type.equals(TypePanel.ZONES)) {
				actor = createInfo(SkinConstants.MOKAP_ZONE,
						i18n.m("info.panel.tool.zone"),
						SkinConstants.TABLET_ZONE);
			} else if (type.equals(TypePanel.INTRODUCTION)) {
				actor = createIntroduction();
			} else if (type.equals(TypePanel.MULTIPLE_SELECTION)) {
				actor = createInfo(SkinConstants.MOKAP_MULTIPLE_SELECT,
						i18n.m("info.panel.multiple.selection"),
						SkinConstants.TABLET_MULTIPLE_SELECT);
			} else if (type.equals(TypePanel.PLAY)) {
				actor = createInfo(SkinConstants.MOKAP_CUP,
						i18n.m("info.panel.mode.play"),
						SkinConstants.TABLET_PRAY);
			}

			actor.clearActions();
			actor.addAction(Actions.sequence(Actions.alpha(0.0f),
					Actions.alpha(1.0f, ANIMATION_TIME, Interpolation.exp5Out)));
			controller.getViews().addToModalsContainer(actor);
		}

	}

	private void hide(final Actor actor) {
		Runnable hidePanel = new Runnable() {
			@Override
			public void run() {
				actor.remove();
			}
		};
		actor.addAction(Actions.sequence(
				Actions.alpha(0.0f, ANIMATION_TIME, Interpolation.exp5Out),
				Actions.run(hidePanel)));
	}

	private class ModalTable extends Table implements BackListener {

		private Actor backObjetive;

		public ModalTable(Skin skin) {
			super(skin);
			backObjetive = this;
		}

		@Override
		public boolean onBackPressed() {
			hide(backObjetive);
			return true;
		}

		@Override
		public float getPrefWidth() {
			return Gdx.graphics.getWidth();
		}

		public void setBackObjetive(Actor actor) {
			backObjetive = actor;
		}
	}
}