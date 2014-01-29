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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.editor.platform.Platform;

public class Controller {

	private Group rootView;

	private Model model;

	private Platform platform;

	private Assets assets;

	private Views views;

	private Actions actions;

	private Preferences preferences;

	public Controller(Platform platform, Assets assets, Model model,
			Group rootView) {
		this.platform = platform;
		this.assets = assets;
		this.rootView = rootView;
		this.model = model;
		this.views = new Views();
		this.actions = new Actions(this);
		this.preferences = new Preferences(assets);
	}

	public Model getModel() {
		return model;
	}

	public Assets getAssets() {
		return assets;
	}

	public Platform getPlatform() {
		return platform;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void view(String viewName) {
		Actor actor = views.getView(viewName, this);
		rootView.clear();
		rootView.addActor(actor);
	}

	public void action(String actionName, Object... args) {
		actions.perform(actionName, args);
	}

	public String getLoadingPath() {
		return assets.getLoadingPath();
	}

	public void setGamePath(String gamePath) {
		assets.setGamePath(gamePath, false);
		model.load(gamePath);
	}
}
