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
package es.eucm.ead.engine.systems;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.DialogueComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;

/**
 * A system for instantiating in-game dialog bubbles. Can be used for menus.
 */
public class DialogueSystem extends IteratingSystem {

	public static final String DIALOGUE_STYLE = "welcome";

	private VariablesManager variablesManager;
	private GameLoop engine;
	private GameAssets assets;
	private GameView gameView;
	private EntitiesLoader entitiesLoader;

	public DialogueSystem(GameView gameView, GameLoop engine,
			VariablesManager variablesManager, GameAssets assets, EntitiesLoader entitiesLoader) {
		super(Family.getFamilyFor(DialogueComponent.class));
		this.engine = engine;
		this.variablesManager = variablesManager;
		this.assets = assets;
		this.gameView = gameView;
		this.entitiesLoader = entitiesLoader;
	}

	private EngineEntity createTemporaryEntity(int x, int y, String key) {
		ModelEntity dialogueEntity = new ModelEntity();
		dialogueEntity.setX(x);
		dialogueEntity.setY(y);

		Label label = new Label();
		label.setText(key);
		label.setStyle(DIALOGUE_STYLE);

		dialogueEntity.getComponents().add(label);

		EngineEntity ee = entitiesLoader.toEngineEntity(dialogueEntity);
		Gdx.app.debug("[DS]", "added dialogue entity (at " + x + ", " + y + ")");

		EngineEntity parent = gameView.getLayer(Layer.SCENE_HUD);
		parent.getGroup().addActorAt(0, ee.getGroup());
		return ee;
	}

	private void addTouchCallback(final EngineEntity dialogueEntity,
			final DialogueComponent dialogue,
			final DialogueComponent.DialogueCallback callback) {
		Group entityGroup = dialogueEntity.getGroup();
		entityGroup.setTouchable(Touchable.enabled);
		entityGroup.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.debug("[DS]", "clicked dialogue (at " + x + ", " + y
						+ ")");
				callback.dialogueChanged(dialogue);
			}
		});
	}

	private void createDialogue(String[] keys, DialogueComponent dialogue) {
		int x = 10;
		int y = Gdx.graphics.getHeight() - 40;
		for (String key : keys) {
			EngineEntity dialogueEntity = createTemporaryEntity(x, y, key);
			addTouchCallback(dialogueEntity, dialogue, dialogue.getCallback());
			dialogue.getRenderingEntities().add(dialogueEntity);
			y -= 30;
		}
	}

	private void createMenu(String[] keys, DialogueComponent dialogue) {
		int x = 10;
		int y = Gdx.graphics.getHeight() - 40;
		int choice = 0;
		for (String key : keys) {
			EngineEntity dialogueEntity = createTemporaryEntity(x, y, key);
			addTouchCallback(dialogueEntity, dialogue, new MenuCallback(
					choice++));
			dialogue.getRenderingEntities().add(dialogueEntity);
			y -= 30;
		}
	}

	/**
	 * Internal wrapper for menu choices
	 */
	private class MenuCallback implements DialogueComponent.DialogueCallback {
		private int choice;

		public MenuCallback(int choice) {
			this.choice = choice;
		}

		@Override
		public void dialogueChanged(DialogueComponent component) {
			component.setMenuChoice(choice);
			component.getCallback().dialogueChanged(component);
		}
	}

	/**
	 * Iterate through the conversation nodes until the conversation finishes.
	 * 
	 * @param entity
	 * @param delta
	 */
	@Override
	public void processEntity(Entity entity, float delta) {

		DialogueComponent dialogue = entity
				.getComponent(DialogueComponent.class);

		if ( ! dialogue.isDisplayed()) {
			dialogue.setDisplayed(true);
			Gdx.app.debug("[DS]", "displaying dialogue " + dialogue);
			if (dialogue.isMenu()) {
				createMenu(dialogue.getKeys(), dialogue);
			} else {
				createDialogue(dialogue.getKeys(), dialogue);
			}
		} else if (dialogue.isDismissed()) {
			entity.remove(DialogueComponent.class);
		}
	}
}
