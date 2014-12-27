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
package es.eucm.ead.editor.editorui.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.widgets.SpinningLogo;
import es.eucm.ead.engine.I18N;

/**
 * Created by jtorrente on 25/12/2014.
 */
public class SpinningLogoTest extends UITest {

	private Array<SpinningLogo> spinningLogoArray = new Array<SpinningLogo>();

	@Override
	public void create() {
		super.create();
		for (SpinningLogo spinningLogo : spinningLogoArray) {
			spinningLogo.reset();
		}
	}

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {
		Group group = new Group();

		// Default
		spinningLogoArray.add(new SpinningLogo(skin));
		group.addActor(spinningLogoArray.get(0));
		Label defaultLogo = new Label("Default", skin);
		defaultLogo.setX(40);
		defaultLogo.setY(10);
		group.addActor(defaultLogo);

		// Custom size, fast, custom location
		spinningLogoArray.add(new SpinningLogo(skin,
				SpinningLogo.FRAME_DURATION_FAST, 250, 200, 300));
		group.addActor(spinningLogoArray.get(1));
		Label customLogo = new Label("Custom size, fast, custom location", skin);
		customLogo.setX(40);
		customLogo.setY(430);
		group.addActor(customLogo);
		return group;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 600;
		config.height = 480;
		config.overrideDensity = 250;
		new LwjglApplication(new SpinningLogoTest(), config);
	}
}
