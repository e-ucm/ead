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
package es.eucm.ead.editor.ui.scenes.map.contextmenus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.ui.DisplayableContextMenu;
import es.eucm.ead.editor.ui.scenes.map.SceneMapWidget;
import es.eucm.ead.editor.ui.scenes.map.SceneWidget;
import es.eucm.ead.editor.view.widgets.layouts.GridLayout;
import es.eucm.ead.editor.view.widgets.layouts.GridLayout.Cell;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;
import es.eucm.i18n.I18N;

/**
 * This {@link ContextMenu} will be displayed over a {@link SceneWidget}.
 * 
 */
public class GridContextMenu extends DisplayableContextMenu<GridLayout> {

	private Cell cell;
	private Controller controller;
	private ContextMenuItem addScene;

	public GridContextMenu(Controller control, SceneMapWidget widget) {
		super(control.getApplicationAssets().getSkin());
		this.controller = control;
		final I18N i18n = controller.getApplicationAssets().getI18N();

		addScene = item(i18n.m("scene.add"));

		InputListener itemsListener = new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == addScene) {
					controller.action(NewScene.class,
							i18n.m("scenes.newScene"), cell.getRow(),
							cell.getColumn());
				}
				return true;
			}
		};
		addScene.addListener(itemsListener);
	}

	public void displayContext(GridLayout widget, float x, float y) {
		Cell cell = widget.getCellAt(x, y);
		if (cell != null) {
			this.cell = cell;
			addScene.setDisabled(cell.getActor() != null);
			controller.action(ShowContextMenu.class, widget, this, x, y);
		}
	}
}
