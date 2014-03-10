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
package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;

/**
 * Displays the recent projects on the initial screen. The maximum number of
 * recent projects displayed is 8.
 */
public class RecentProjects extends ScrollPane {

	private static final int MAX_RECENT_PROJECTS = 8;
	private static final Float PREF_WIDTH = .8f;
	private int addedProjects;
	private final Table projs;
	private final Vector2 viewport;

	public RecentProjects(Vector2 viewport) {
		super(null);

		this.viewport = viewport;
		final float DEFAULT_PAD = 10f;
		this.projs = new Table();
		this.projs.pad(DEFAULT_PAD);
		this.projs.defaults().space(DEFAULT_PAD);

		this.addedProjects = 0;

		setScrollingDisabled(false, true);
		setWidget(this.projs);
	}

	@Override
	public float getPrefWidth() {
		return this.viewport.x * PREF_WIDTH;
	}

	public void clearRecents() {
		this.projs.clear();
		this.addedProjects = 0;
	}

	public void addRecent(ProjectButton recent) {
		if (this.addedProjects < MAX_RECENT_PROJECTS) {
			this.projs.add(recent);
			++this.addedProjects;
		}
	}
}
