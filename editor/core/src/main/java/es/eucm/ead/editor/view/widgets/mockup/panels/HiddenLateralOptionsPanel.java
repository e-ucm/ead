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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeLanguage;
import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.I18N.Lang;

/**
 * Panel with skins and languages settings
 */
public class HiddenLateralOptionsPanel extends HiddenPanel {

	public HiddenLateralOptionsPanel(Controller controller, Skin skin) {
		super(skin);

		setVisible(false);

		I18N i18n = controller.getEditorAssets().getI18N();

		Label skins = new Label(i18n.m("general.mockup.skins").toUpperCase(),
				skin);
		String skinStyle = "default-radio", lineString = "- - - - - - - - - - - - -";
		CheckBox skinDefault = new CheckBox(
				i18n.m("general.mockup.skins.default"), skin, skinStyle);
		skinDefault.addListener(new ActionOnClickListener(controller,
				CombinedAction.class, ChangeSkin.class,
				new Object[] { "default" }, ChangeView.class,
				new Object[] { MainBuilder.NAME }));
		skinDefault.setChecked(true);
		Label line = new Label(lineString, skin);
		Label languages = new Label(i18n.m("menu.editor.language")
				.toUpperCase(), skin);

		Table root = new Table(skin);
		ScrollPane scrollPanel = new ScrollPane(root, skin);
		scrollPanel.setupFadeScrollBars(0f, 0f);
		scrollPanel.setScrollingDisabled(true, false);
		root.add(skins);
		root.row();
		root.add(skinDefault).left();
		root.row();
		root.add(line);
		root.row();
		root.add(languages);

		ButtonGroup languagesGroup = new ButtonGroup();
		for (Lang lang : i18n.getAvailable()) {
			CheckBox lan = new CheckBox(lang.name, skin, skinStyle);
			lan.addListener(new ActionOnClickListener(controller,
					ChangeLanguage.class, lang.code));
			languagesGroup.add(lan);
			if (i18n.getLang().equals(lang.code)) {
				lan.setChecked(true);
			}
			root.row();
			root.add(lan).left();
		}

		this.add(scrollPanel);
	}

	public void show() {
		if (super.fadeDuration > 0) {
			getColor().a = 0f;
			setPosition(getStage().getWidth(), getY());
			addAction(Actions.parallel(Actions.moveTo(getStage().getWidth()
					- getWidth(), getY(), super.fadeDuration,
					Interpolation.sineOut), Actions.fadeIn(super.fadeDuration,
					Interpolation.fade)));

		}
		setVisible(true);
	}

	public void hide() {
		if (super.fadeDuration > 0) {
			addAction(Actions.parallel(Actions.sequence(
					Actions.fadeOut(super.fadeDuration, Interpolation.fade),
					Actions.run(super.hideRunnable)), Actions.moveTo(getStage()
					.getWidth(), getY(), super.fadeDuration)));
		} else {
			setVisible(false);
		}
	}
}
