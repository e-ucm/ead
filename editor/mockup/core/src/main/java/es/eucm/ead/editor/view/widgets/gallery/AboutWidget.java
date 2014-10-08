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
package es.eucm.ead.editor.view.widgets.gallery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.gallery.CreditsView;
import es.eucm.ead.editor.view.widgets.SendEmailPane;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;
import es.eucm.ead.engine.I18N;

public class AboutWidget extends IconWithScalePanel {

	private static final String URL = "http://www.e-ucm.es/p/mockup";

	private TextButton web;

	private TextButton credits;

	private TextButton contact;

	private SendEmailPane emailPane;

	public AboutWidget(final Controller controller, final Actor root) {
		super("about", 0, controller.getApplicationAssets().getSkin());

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18n = controller.getApplicationAssets().getI18N();

		float smallPad = Gdx.graphics.getHeight() * .01f;
		float normalPad = Gdx.graphics.getHeight() * .03f;
		float bigPad = Gdx.graphics.getHeight() * .1f;

		emailPane = new SendEmailPane(controller, skin, root);

		web = new TextButton(i18n.m("about.web"), skin, "to_color");
		web.pad(normalPad);
		web.setColor(Color.ORANGE);
		contact = new TextButton(i18n.m("about.contact"), skin, "to_color");
		contact.pad(normalPad);
		contact.setColor(Color.ORANGE);
		credits = new TextButton(i18n.m("about.credits"), skin, "to_color");
		credits.pad(normalPad);
		credits.setColor(Color.ORANGE);

		ClickListener listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == web) {
					controller.getPlatform().browseURL(URL);
				} else if (listenerActor == credits) {
					controller.action(ChangeMockupView.class,
							CreditsView.class,
							Transitions.getSlideTransition(true));
				} else if (listenerActor == contact) {
					emailPane.show();
				}
				hidePanel();
			}
		};

		web.addListener(listener);
		credits.addListener(listener);
		contact.addListener(listener);

		panel.top();
		panel.add(credits).expandX().fill()
				.pad(bigPad, normalPad, smallPad, normalPad);
		panel.row();
		panel.add(web).expandX().fill()
				.pad(bigPad, normalPad, smallPad, normalPad);
		panel.row();
		panel.add(new Label(i18n.m("about.problems") + ":", skin)).pad(bigPad,
				normalPad, smallPad, normalPad);
		panel.row();
		panel.add(contact).expandX().fill()
				.pad(smallPad, normalPad, smallPad, normalPad);

	}

}
