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
package es.eucm.ead.editor.view.builders.scene;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.project.ProjectView;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ScenesGallery;
import es.eucm.ead.engine.I18N;

public class ProjectNavigation extends ScenesGallery {

	private I18N i18N;

	public ProjectNavigation(Controller controller) {
		super(Gdx.graphics.getHeight() / 2.15f, 1, controller);
		gallery.pad(0);
		gallery.setBackground(controller.getApplicationAssets().getSkin()
				.getDrawable(SkinConstants.DRAWABLE_PAGE_LEFT));
		i18N = controller.getApplicationAssets().getI18N();
	}

	@Override
	public void start() {
		super.start();
		gallery.add(
				WidgetBuilder.button(SkinConstants.IC_HOME, i18N.m("project"),
						SkinConstants.STYLE_CONTEXT, ChangeView.class,
						ProjectView.class)).usePrefHeight();
		gallery.add(
				WidgetBuilder.button(SkinConstants.IC_PLAY, i18N.m("test.all"),
						SkinConstants.STYLE_CONTEXT)).usePrefHeight();
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getHeight() - WidgetBuilder.dpToPixels(56);
	}
}
