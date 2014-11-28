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
package es.eucm.ead.editor.view.builders.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.Exit;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ProjectsGallery;

public class HomeView implements ViewBuilder, BackListener {

	private ProjectsGallery view;

	private Controller controller;

	@Override
	public void initialize(Controller c) {
		this.controller = c;
		view = new ProjectsGallery(Gdx.graphics.getHeight() / 2.15f, 3, c);
	}

	@Override
	public Actor getView(Object... args) {
		controller.getWorkerExecutor().cancellAll();
		controller.getPreferences().putString(Preferences.LAST_OPENED_GAME, "");
		view.prepare();
		controller.action(ShowInfoPanel.class, TypePanel.INTRODUCTION,
				Preferences.HELP_INTRODUCTION);
		return view;
	}

	@Override
	public void release(Controller controller) {
		controller.getApplicationAssets().clear();
	}

	@Override
	public boolean onBackPressed() {
		controller.action(Exit.class, false);
		return true;
	}

}
