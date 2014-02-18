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
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;
import es.eucm.ead.editor.view.builders.mockup.InitialScreen;
import es.eucm.ead.editor.view.builders.mockup.ProjectScreen;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls all the views
 */
public class Views {

	private Controller controller;

	private Group rootContainer;

	private Map<String, Actor> viewsCache;

	private Map<String, ViewBuilder> viewsBuilders;

	private String currentViewName;

	private ViewBuilder currentView;

	/**
	 * 
	 * @param controller
	 *            the editor controller
	 * @param rootContainer
	 *            the root container where the main view must be added
	 */
	public Views(Controller controller, Group rootContainer) {
		this.controller = controller;
		this.rootContainer = rootContainer;
		viewsCache = new HashMap<String, Actor>();
		viewsBuilders = new HashMap<String, ViewBuilder>();
		addViews();
	}

	private void addViews() {
		addView(new MainBuilder(controller));
		addView(new InitialScreen());
		addView(new ProjectScreen());
	}

	private void addView(ViewBuilder viewBuilder) {
		viewsBuilders.put(viewBuilder.getName(), viewBuilder);
	}

	/**
	 * Sets as root the view with the given name. Hides any other current view
	 * 
	 * @param name
	 *            the view name
	 */
	public void setView(String name) {
		ViewBuilder builder = viewsBuilders.get(name);
		Actor view = viewsCache.get(name);
		if (view == null) {
			if (builder != null) {
				view = builder.build(controller);
				currentViewName = name;
			}
		}

		if (currentView != null) {
			currentView.release(controller);
		}

		if (builder != null) {
			builder.initialize(controller);
		}

		currentView = builder;

		if (view != null) {
			rootContainer.clear();
			rootContainer.addActor(view);
			if (view instanceof WidgetGroup) {
				((WidgetGroup) view).invalidateHierarchy();
			}
		}
	}

	/**
	 * Clears the views cache. Called whenever all the views must be regenerated
	 * (e.g., when the interface language changed)
	 */
	public void clearCache() {
		viewsCache.clear();
	}

	/**
	 * Reloads the current view
	 */
	public void reloadCurrentView() {
		setView(currentViewName);
	}
}
