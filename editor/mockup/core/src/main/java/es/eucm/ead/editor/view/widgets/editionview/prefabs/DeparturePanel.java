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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.ComponentId;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.irreversibles.scene.AddBehaviorPrefab;
import es.eucm.ead.editor.control.actions.irreversibles.scene.ChangeBehaviorEffect;
import es.eucm.ead.editor.control.actions.irreversibles.scene.RemoveBehavior;
import es.eucm.ead.editor.control.actions.model.SelectionGoToNewScene;
import es.eucm.ead.editor.view.widgets.editionview.SceneButton;
import es.eucm.ead.editor.view.widgets.editionview.ScenesTableList;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.GoScene;

public class DeparturePanel extends PrefabComponentPanel {

	private static final float PAD = 20;

	private ScenesTableList table;

	public DeparturePanel(float iconPad, float size,
			final Controller controller, Actor touchable) {
		super("gateway_reverse80x80", iconPad, size, "edition.exits",
				ComponentId.PREFAB_EXIT, controller, touchable);

		InputListener makeExit = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				SceneButton button = (SceneButton) event.getListenerActor();
				String sceneId = controller.getModel().getIdFor(
						button.getScene());

				if (component == null) {
					component = new Behavior();
					component.setId(componentId);
					((Behavior) component).setEvent(new Touch());
					controller.action(AddBehaviorPrefab.class, component);
				}
				GoScene goScene;
				if (((Behavior) component).getEffects().size > 0) {
					goScene = (GoScene) ((Behavior) component).getEffects()
							.first();
				} else {
					goScene = new GoScene();
					goScene.setSceneId("");
				}

				if (sceneId != null && sceneId.equals(goScene.getSceneId())) {
					controller.action(RemoveBehavior.class, component);
				} else {
					goScene.setSceneId(sceneId);
					controller.action(ChangeBehaviorEffect.class, component,
							goScene);
				}

				setUsed(true);
			}
		};

		table = new ScenesTableList(controller, makeExit, "scene");

		ScrollPane scroll = new ScrollPane(table, skin, "white");
		panel.add(scroll).expandY().fill();
		panel.row();

		TextButton newScene = new TextButton(i18n.m("edition.exits.newScene")
				+ " \n(" + i18n.m("edition.exits.newSceneInfo") + ")", skin,
				"white");
		panel.add(newScene).expandX().fill().pad(0, PAD, PAD, PAD);

		newScene.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				controller.action(SelectionGoToNewScene.class, component,
						componentId);
				getPanel().hide();
			};
		});

	}

	@Override
	protected void actualizePanel() {
		if (component != null) {
			table.selectScene(((GoScene) ((Behavior) component).getEffects()
					.first()).getSceneId());
		} else {
			table.deselectAll();
		}
	}
}
