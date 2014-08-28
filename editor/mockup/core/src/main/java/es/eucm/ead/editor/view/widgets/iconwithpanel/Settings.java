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
package es.eucm.ead.editor.view.widgets.iconwithpanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.ChangeLanguage;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.I18N.Lang;

public class Settings extends IconWithScalePanel {

	private static final float NORMAL_PAD = 40;
	private static final float SMALL_PAD = 20;

	public Settings(final Controller controller, float iconPad, float size) {
		super("settings80x80", iconPad, 0, size, controller
				.getApplicationAssets().getSkin());
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18n = controller.getApplicationAssets().getI18N();

		String skinStyle = "default-radio";
		final CheckBox helpMessages = new CheckBox(
				i18n.m("settings.enableHelpMessages"), skin);

		Label languages = new Label(i18n.m("settings.language").toUpperCase(),
				skin);
		Label more = new Label(i18n.m("settings.more").toUpperCase(), skin);

		panel.top();
		panel.add(languages).padTop(NORMAL_PAD + SMALL_PAD)
				.padBottom(SMALL_PAD);
		panel.row();

		ChangeListener listener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listener = event.getListenerActor();
				if (listener == helpMessages) {
					controller.getPreferences().putBoolean(
							Preferences.ENABLE_HELP_MSGS,
							helpMessages.isChecked());
				} else if (((CheckBox) actor).isChecked()) {
					Object userObject = actor.getUserObject();
					if (userObject != null) {
						controller.action(ChangeLanguage.class,
								userObject.toString());
					}
				}
			}

		};
		helpMessages.addListener(listener);
		helpMessages.setChecked(controller.getPreferences().getBoolean(
				Preferences.ENABLE_HELP_MSGS, true));

		ButtonGroup languagesGroup = new ButtonGroup();
		for (Lang lang : i18n.getAvailable()) {
			CheckBox lan = new CheckBox(lang.name, skin, skinStyle);
			lan.setUserObject(lang.code);
			languagesGroup.add(lan);
			if (i18n.getLang().equals(lang.code) && !lan.isChecked()) {
				lan.setChecked(true);
			}
			lan.addListener(listener);
			panel.add(lan).left().padLeft(SMALL_PAD);
			panel.row();
		}

		panel.add(more).padTop(NORMAL_PAD + SMALL_PAD);
		panel.row();
		panel.add(helpMessages).left().pad(SMALL_PAD);
	}
}
