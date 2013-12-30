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
package es.eucm.ead.mockup.core.view.renderers;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.mockup.core.view.ui.CircularGroup;

public class ProjectMenuRenderer extends ScreenRenderer {

	private Group rest;

	@Override
	public void create() {

		rest = new Group();
		rest.setVisible(false);

		Button t1 = new TextButton("Crear", skin, "default-thin");
		Button t2 = new TextButton("Elemento", skin);
		Button t3 = new TextButton("Galería", skin);
		Button t4 = new TextButton("Lanzar Juego", skin);
		Button t5 = new TextButton("Escena", skin);

		CircularGroup cg = new CircularGroup(halfstageh - 60, 90, 360, true, t1,
				t2, t3, t4, t5);
		cg.setX(halfstagew);
		cg.setY(halfstageh);

		rest.addActor(cg);

		stage.addActor(rest);
	}

	@Override
	public void show() {
		rest.setVisible(true);
		MainMenuRenderer.optionsGroup.setVisible(true);
	}

	@Override
	public void draw() {
		stage.draw();
	}

	@Override
	public void hide() {
		rest.setVisible(false);
		MainMenuRenderer.optionsGroup.setVisible(false);
	}
}
