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

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.SelectionGoToNewScene;
import es.eucm.ead.editor.control.actions.irreversibles.scene.AddTouchEffect;
import es.eucm.ead.editor.control.actions.irreversibles.scene.ChangeBehaviorEffect;
import es.eucm.ead.editor.control.actions.irreversibles.scene.RemoveBehavior;
import es.eucm.ead.editor.view.widgets.editionview.SceneButton;
import es.eucm.ead.editor.view.widgets.editionview.ScenesTableList;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;

public class DeparturePanel extends PrefabPanel {

	private static final float PAD = 20;

	private ScenesTableList table;

	private Behavior behavior;

	public DeparturePanel(float size, final Controller controller,
			Actor touchable) {
		super("gateway_reverse80x80", size, "edition.exits", controller,
				touchable);

		InputListener makeExit = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				SceneButton button = (SceneButton) event.getListenerActor();
				String sceneId = controller.getModel().getIdFor(
						button.getScene());

				GoScene goScene = new GoScene();
				goScene.setSceneId(sceneId);

				if (behavior == null) {
					controller.action(AddTouchEffect.class, goScene);
				} else {
					GoScene goScen = (GoScene) behavior.getEffects().first();
					String scenId = controller.getModel().getIdFor(
							button.getScene());
					if (scenId != null && goScen.getSceneId().equals(scenId)) {
						controller.action(RemoveBehavior.class, behavior);
					} else {
						controller.action(ChangeBehaviorEffect.class, behavior,
								goScene);
					}
				}
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
				controller.action(SelectionGoToNewScene.class, behavior);
				getPanel().hide();
			};
		});

	}

	@Override
	protected InputListener trashListener() {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (behavior != null) {
					controller.action(RemoveBehavior.class, behavior);
					behavior = null;
					loadItems();
				}
			}
		};
	}

	private void loadBehavior() {
		ModelEntity modelEntity = (ModelEntity) selection
				.getSingle(Selection.SCENE_ELEMENT);
		this.behavior = null;
		for (ModelComponent component : modelEntity.getComponents()) {
			if (component instanceof Behavior) {
				Behavior behavior = (Behavior) component;
				if (behavior.getEffects().first() instanceof GoScene
						&& behavior.getEvent() instanceof Touch) {
					this.behavior = behavior;
					break;
				}
			}
		}
	}

	private void loadItems() {
		if (behavior != null) {
			table.selectScene(((GoScene) behavior.getEffects().first())
					.getSceneId());
		} else {
			table.deselectAll();
		}
	}

	@Override
	protected void showPanel() {
		loadBehavior();
		loadItems();
		super.showPanel();
	}
}
