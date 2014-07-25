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
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.ui.DisplayableContextMenu;
import es.eucm.ead.editor.ui.scenes.map.SceneWidget;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;
import es.eucm.ead.engine.I18N;

/**
 * This {@link ContextMenu} will be displayed over a {@link SceneWidget}.
 * 
 */
public class SceneContextMenu extends DisplayableContextMenu<SceneWidget> {

	private Controller controller;
	private SceneWidget sceneWidget;
	private ContextMenuItem makeInitial;
	private ContextMenuItem deleteScene;

	public SceneContextMenu(Controller control) {
		super(control.getApplicationAssets().getSkin());
		this.controller = control;
		I18N i18n = controller.getApplicationAssets().getI18N();

		makeInitial = item(i18n.m("general.make-initial"));
		separator();
		deleteScene = item(i18n.m("scene.delete"));

		InputListener itemsListener = new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == deleteScene) {
					controller.action(DeleteScene.class,
							sceneWidget.getSceneId());
					sceneWidget.setChecked(true);
				} else if (listenerActor == makeInitial) {
					controller.action(ChangeInitialScene.class,
							sceneWidget.getSceneId());
					sceneWidget.setChecked(true);
				}
				return true;
			}
		};
		makeInitial.addListener(itemsListener);
		deleteScene.addListener(itemsListener);
	}

	public void displayContext(SceneWidget widget, float x, float y) {
		sceneWidget = widget;
		makeInitial.setDisabled(widget.isInitial());
		controller.action(ShowContextMenu.class, widget, this, x, y);
	}
}
