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
package es.eucm.ead.editor.view.builders.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.PlayView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.home.HomeView;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * Project view. A list with the scenes of the project
 */
public class ProjectView implements ViewBuilder, BackListener {

	private Controller controller;

	private LinearLayout view;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		Skin skin = controller.getApplicationAssets().getSkin();
		view = new LinearLayout(false);
		view.add(buildToolbar(skin)).expandX();
		view.add(
				new ProjectScenesGallery(Gdx.graphics.getHeight() / 2.15f, 3,
						controller)).expand(true, true).top();
	}

	@Override
	public Actor getView(Object... args) {
		return view;
	}

	@Override
	public void release(Controller controller) {
	}

	private Actor buildToolbar(Skin skin) {
		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);

		LinearLayout project = new LinearLayout(true);
		project.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_GO, null,
				ChangeView.class, HomeView.class));
		project.addSpace();
		project.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_PLAY, null,
				ChangeView.class, PlayView.class));

		toolbar.addWidgets(project);
		return toolbar;
	}

	@Override
	public boolean onBackPressed() {
		controller.action(ChangeView.class, HomeView.class);
		return true;
	}
}
