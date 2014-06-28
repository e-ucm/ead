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
package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.buttons.SceneButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Displays all the scenes in the model. Has the option to display an additional
 * entry that is the equivalent of a new scene. Also if no {@link #prefHeight}
 * value is specified, it's height will be the same as the height of two
 * entries.
 */
public class Scenes extends SelectablesScroll<SceneButton> {

	private float prefHeight;
	private SceneButton newScene;
	private Controller controller;
	private SceneButton currScene;
	private boolean hasNewScene;

	/**
	 * Creates a vertical {@link SelectablesScroll} with a height of two entries
	 * and contains a new scene entry.
	 */
	public Scenes(Controller controller) {
		this(controller, -1, true);
	}

	public Scenes(Controller controller, float prefHeight, boolean hasNewScene) {
		super(controller.getPlatform().getSize(), false);
		this.controller = controller;
		this.prefHeight = prefHeight;
		this.hasNewScene = hasNewScene;

		ApplicationAssets appAssets = controller.getApplicationAssets();
		I18N i18n = appAssets.getI18N();
		Skin skin = appAssets.getSkin();

		ModelEntity emptyEntity = new ModelEntity();
		Note note = new Note();
		note.setTitle(i18n.m("scenes.newScene"));
		note.setDescription(i18n.m("scenes.newScene.description"));
		emptyEntity.getComponents().add(note);
		newScene = new SceneButton(viewport, i18n, emptyEntity, "", skin,
				controller);
		newScene.setIcon(skin.getRegion("ic_new"));

		cards.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();

				while (target != null && !(target instanceof SceneButton)) {
					target = target.getParent();
				}

				if (target != null) {
					SceneButton targetEntity = (SceneButton) target;
					if (targetEntity != currScene) {
						targetEntity.select();
						if (currScene != null) {
							currScene.deselect();
						}
						currScene = targetEntity;
					} else if (currScene != null && !currScene.isSelected()) {
						currScene.select();
					}
				}
			}
		});
	}

	/**
	 * Iterates through the current {@link SceneButton}s and selects the one
	 * that has the same key as the parameter.
	 * 
	 * @param key
	 */
	public void setSelected(String key) {
		for (Actor child : cards.getChildren()) {
			if (child instanceof SceneButton) {
				SceneButton sceneChild = (SceneButton) child;
				if (sceneChild.getKey().equals(key)) {
					if (currScene != sceneChild) {
						if (currScene.isSelected())
							currScene.deselect();
						currScene = sceneChild;
						if (!sceneChild.isSelected())
							sceneChild.select();
					} else if (!currScene.isSelected()) {
						currScene.select();
					}
					break;
				}
			}
		}
	}

	@Override
	public float getPrefHeight() {
		return prefHeight == -1 ? newScene.getPrefHeight() * 2f : viewport.y
				* prefHeight;
	}

	/**
	 * Equivalent to {@link #refresh(boolean) refresh(true)}.
	 */
	public void refresh() {
		refresh(true);
	}

	/**
	 * Iterates through the model scenes and adds new {@link SceneButton}s to
	 * this scroll pane. If {@link #hasNewScene} flag is true, a new scene
	 * button will be added at the beginning. If autoSelect is true, the first
	 * entry will be selected.
	 */
	public void refresh(boolean autoSelect) {
		cards.clearActions();
		cards.clearChildren();

		if (hasNewScene) {
			addSelectable(newScene);
			currScene = newScene;
		}

		ApplicationAssets appAssets = controller.getApplicationAssets();
		I18N i18n = appAssets.getI18N();
		Skin skin = appAssets.getSkin();

		Map<String, Object> entities = controller.getModel().getResources(
				ResourceCategory.SCENE);

		boolean selectedFirst = hasNewScene;
		for (Entry<String, Object> scene : entities.entrySet()) {

			SceneButton sceneButton = new SceneButton(viewport, i18n,
					(ModelEntity) scene.getValue(), scene.getKey(), skin,
					controller);

			if (!selectedFirst && !hasNewScene) {
				currScene = sceneButton;
				selectedFirst = true;
			}

			addSelectable(sceneButton);
		}

		if (autoSelect && !currScene.isSelected()) {
			currScene.select();
		}
	}

	public SceneButton getSelected() {
		if (currScene.isSelected())
			return currScene;
		return null;
	}
}
