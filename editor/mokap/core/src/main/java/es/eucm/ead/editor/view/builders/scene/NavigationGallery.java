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
import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.PlayView;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ScenesGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.engine.I18N;

public class NavigationGallery extends ScenesGallery {

	private I18N i18N;

	public NavigationGallery(Controller controller) {
		super(Gdx.graphics.getHeight() / 2.15f, 1, controller,
				SkinConstants.STYLE_NAVIGATION);
		gallery.pad(0);
		i18N = controller.getApplicationAssets().getI18N();
	}

	@Override
	public Cell addTile(String id, String title, String thumbnailPath) {
		Cell cell = super.addTile(id, title, thumbnailPath);
		cell.setName(id);
		return cell;
	}

	@Override
	public void prepare() {
		super.prepare();

		for (Actor actor : gallery.getChildren()) {
			if (actor instanceof Cell) {
				((Cell) actor).checked(false);
			}
		}

		Context context = controller.getModel().getSelection()
				.getContext(Selection.SCENE);
		if (context != null && context.getSelection().length > 0) {
			String sceneId = controller.getModel().getIdFor(
					context.getSelection()[0]);
			if (sceneId != null) {
				Actor actor = gallery.findActor(sceneId);
				if (actor instanceof Cell) {
					((Cell) actor).checked(true);
				}
			}
		}
	}

	@Override
	public void clear() {
		super.clear();
		gallery.add(
				WidgetBuilder.button(SkinConstants.IC_HOME, i18N.m("project"),
						SkinConstants.STYLE_CONTEXT, SetSelection.class,
						Selection.PROJECT, Selection.RESOURCE)).usePrefHeight();
		gallery.add(
				WidgetBuilder.button(SkinConstants.IC_PLAY, i18N.m("test.all"),
						SkinConstants.STYLE_CONTEXT, ChangeView.class,
						PlayView.class)).usePrefHeight();
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getHeight() - WidgetBuilder.dpToPixels(56);
	}
}
