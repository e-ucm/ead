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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.asynk.SendMockupProject;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.engine.I18N;

public class SendEmailPane extends PositionedHiddenPanel {

	private TextButton attach;

	public SendEmailPane(final Controller controller, Skin skin, Actor reference) {
		super(skin, Position.CENTER, reference);

		setBackground(skin.getDrawable("dialog"));

		I18N i18n = controller.getApplicationAssets().getI18N();

		Value littlePad = Value.percentHeight(0.03f, reference);
		Value normalPad = Value.percentHeight(0.1f, reference);
		Value labelSize = Value.percentWidth(0.9f, reference);

		Label mss = new Label(i18n.m("about.emailPane"), skin);

		mss.setWrap(true);
		add(mss).colspan(3).width(labelSize).pad(normalPad);
		row();
		add(new Label(i18n.m("about.sendEmail"), skin)).center().colspan(3)
				.padBottom(littlePad);

		attach = new TextButton(i18n.m("about.emailAttach"), skin, "white");
		final TextButton notAttach = new TextButton(
				i18n.m("about.emailNotAttach"), skin, "white");
		final TextButton cancel = new TextButton(i18n.m("cancel"), skin,
				"white");

		ClickListener listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == attach) {
					controller.action(SendMockupProject.class);
				} else if (listenerActor == notAttach) {
					MockupPlatform platform = (MockupPlatform) controller
							.getPlatform();
					platform.sendMail(controller.getApplicationAssets()
							.getI18N());
				} else if (listenerActor == cancel) {
					hide();
				}

			}
		};
		attach.addListener(listener);
		notAttach.addListener(listener);
		cancel.addListener(listener);

		row();
		add(attach).padBottom(normalPad);
		add(notAttach).padBottom(normalPad);
		add(cancel).padBottom(normalPad);
	}
}
