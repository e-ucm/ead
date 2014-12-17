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

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Rename;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.control.actions.model.CloneScene;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ScenesGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.engine.utils.EngineUtils;

/**
 * Created by angel on 26/11/14.
 */
public class ProjectScenesGallery extends ScenesGallery {

	private SceneSelectionListener sceneSelectionListener = new SceneSelectionListener();

	public ProjectScenesGallery(float rows, int columns, Controller cont) {
		super(rows, columns, cont);
		Button edit = WidgetBuilder.button(SkinConstants.IC_EDIT,
				i18N.m("edit"), SkinConstants.STYLE_CONTEXT, EditScene.class);

		Button rename = WidgetBuilder.button(SkinConstants.IC_TEXT,
				i18N.m("rename"), SkinConstants.STYLE_CONTEXT, Rename.class,
				Selection.SCENE);

		Button clone = WidgetBuilder.button(SkinConstants.IC_CLONE,
				i18N.m("clone"), SkinConstants.STYLE_CONTEXT, CloneScene.class);

		Button initial = WidgetBuilder.button(SkinConstants.IC_ONE,
				i18N.m("scene.initial"), SkinConstants.STYLE_CONTEXT,
				ChangeInitialScene.class);

		Button delete = WidgetBuilder.button(SkinConstants.IC_DELETE,
				i18N.m("delete"), SkinConstants.STYLE_CONTEXT);

		WidgetBuilder.actionsOnClick(
				delete,
				new Class[] { DeleteScene.class, ShowToast.class },
				new Object[][] {
						new Object[] {},
						new Object[] {
								controller.getApplicationAssets().getI18N()
										.m("scene.deleted"), 10.0f } });

		setContextMenu(edit, rename, clone, initial, delete);
	}

	@Override
	public void prepare() {
		super.prepare();
		readScene();
		controller.getModel().addSelectionListener(sceneSelectionListener);
	}

	@Override
	public void release() {
		super.release();
		controller.getModel().removeSelectionListener(sceneSelectionListener);
	}

	@Override
	public void tileLongPressed(String tileName) {
		controller.action(EditScene.class, tileName, false);
	}

	@Override
	public void contextMenuHidden() {
		if (Selection.SCENE.equals(controller.getModel().getSelection()
				.getCurrentContext().getId())) {
			controller.action(SetSelection.class, Selection.MOKAP,
					Selection.MOKAP_RESOURCE);
		}
	}

	private void readScene() {
		String resource = (String) controller.getModel().getSelection()
				.getSingle(Selection.MOKAP_RESOURCE);
		gallery.uncheckAll();
		if (resource != null) {
			Cell cell = (Cell) EngineUtils.getDirectChild(gallery.getGrid(),
					gallery.findActor(resource));
			if (cell != null) {
				cell.checked(true);
			}
		}
	}

	public class SceneSelectionListener implements SelectionListener {

		@Override
		public boolean listenToContext(String contextId) {
			return Selection.MOKAP_RESOURCE.equals(contextId);
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			readScene();
		}
	}
}
