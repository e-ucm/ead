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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.RenameCurrentScene;
import es.eucm.ead.editor.control.actions.editor.ShowModal;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.listeners.LongPressListener;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ScenesGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.engine.utils.EngineUtils;

/**
 * Created by angel on 26/11/14.
 */
public class ProjectScenesGallery extends ScenesGallery {

	private ContextMenu sceneActions;

	private SceneSelectionListener sceneSelectionListener = new SceneSelectionListener();

	public ProjectScenesGallery(float rowHeight, int columns, Controller cont) {
		super(rowHeight, columns, cont);
		addListener(new LongPressListener() {
			@Override
			public void longPress(float x, float y) {
				getStage().cancelTouchFocus();
				Actor actor = EngineUtils.getDirectChild(gallery.getGrid(),
						hit(x, y, true));
				if (actor instanceof Cell) {
					Tile tile = (Tile) ((Cell) actor).getActor();
					controller.action(EditScene.class, tile.getName(), false);
					controller.action(ShowModal.class, sceneActions, x, y);
				}
			}
		});

		Button edit = WidgetBuilder.button(SkinConstants.IC_EDIT,
				i18N.m("edit"), SkinConstants.STYLE_CONTEXT, EditScene.class);

		Button initial = WidgetBuilder.button(SkinConstants.IC_ONE,
				i18N.m("scene.initial"), SkinConstants.STYLE_CONTEXT,
				ChangeInitialScene.class);
		Button rename = WidgetBuilder.button(SkinConstants.IC_TEXT,
				i18N.m("rename"), SkinConstants.STYLE_CONTEXT,
				RenameCurrentScene.class);

		sceneActions = WidgetBuilder.iconLabelContextPanel(edit, initial,
				rename);
		sceneActions.addHideRunnable(new Runnable() {
			@Override
			public void run() {
				if (Selection.SCENE.equals(controller.getModel().getSelection()
						.getCurrentContext().getId())) {
					controller.action(SetSelection.class, Selection.PROJECT,
							Selection.RESOURCE);
				}
			}
		});
	}

	@Override
	public void prepare() {
		super.prepare();
		controller.getModel().addSelectionListener(sceneSelectionListener);
	}

	@Override
	public void release() {
		super.release();
		controller.getModel().removeSelectionListener(sceneSelectionListener);
	}

	public class SceneSelectionListener implements SelectionListener {

		@Override
		public boolean listenToContext(String contextId) {
			return Selection.RESOURCE.equals(contextId);
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			if (event.getType() == Type.FOCUSED) {
				gallery.uncheckAll();
				for (Object o : event.getSelection()) {
					if (o instanceof String) {
						Cell cell = (Cell) EngineUtils.getDirectChild(
								gallery.getGrid(),
								gallery.findActor((String) o));
						if (cell != null) {
							cell.checked(true);
						}
					}
				}
			}
		}
	}
}
