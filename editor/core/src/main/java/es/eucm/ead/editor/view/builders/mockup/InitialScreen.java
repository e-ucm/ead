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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.CircularGroup;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.engine.I18N;

public class InitialScreen implements ViewBuilder {

	public static final String NAME = "mockup_initial";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		Skin skin = controller.getEditorAssets().getSkin();
		I18N i18n = controller.getEditorAssets().getI18N();
		CircularGroup group = new CircularGroup();
		group.addActor(new TextButton(i18n.m("general.new"), skin));
		group.addActor(new TextButton(i18n.m("general.open"), skin));
		group.addActor(new TextButton(i18n.m("general.help"), skin));

		Window window = new Window();
		window.root(group);
		return window;
	}
}
