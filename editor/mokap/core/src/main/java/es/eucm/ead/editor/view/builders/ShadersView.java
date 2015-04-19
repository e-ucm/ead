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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.*;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

/**
 * Shaders view, a gallery with the shaders that can be added as background to
 * the scene.
 */
public class ShadersView implements ViewBuilder, BackListener {

	private Controller controller;

	private ShadersGallery shadersGallery;
	private LinearLayout view;

	@Override
	public void initialize(Controller control) {
		this.controller = control;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		I18N i18N = applicationAssets.getI18N();

		view = new LinearLayout(false);
		view.background(skin.getDrawable(SkinConstants.DRAWABLE_GRAY_100));
		view.add(buildToolbar(skin)).expandX();

		shadersGallery = new ShadersGallery(2.25f, 3, controller);
		view.add(shadersGallery).expand(true, true).top();
	}

	@Override
	public Actor getView(Object... args) {

		return view;
	}

	private Actor buildToolbar(Skin skin) {
		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);

		LinearLayout project = new LinearLayout(true);
		project.add(
				WidgetBuilder.toolbarIcon(SkinConstants.IC_GO, null,
						ChangeView.class, SceneView.class)).left();

		toolbar.addWidgets(project);
		return toolbar;
	}

	@Override
	public void release(Controller controller) {
	}

	@Override
	public boolean onBackPressed() {
		controller.action(ChangeView.class, SceneView.class);
		return true;
	}
}