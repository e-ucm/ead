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
package es.eucm.ead.engine.systems.dialogues;

import ashley.core.Component;
import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.components.dialogues.DialogueComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;

/**
 * A system for instantiating in-game dialog widgets. Used mostly for game
 * conversations, but not directly tied to conversations and therefore generic.
 */
public abstract class DialogueSystem extends IteratingSystem {

	public static final String DIALOGUE_STYLE = "welcome";

	private static final int DIALOGUE_LINE_HEIGHT = 25;
	private static final int DIALOGUE_X_OFFSET = 10;
	private static final int DIALOGUE_Y_OFFSET = 10;
	private static final int DIALOGUE_SPACING = 10;

	private GameView gameView;
	private EntitiesLoader entitiesLoader;
	private Class<? extends Component> dialogueClass;

	/**
	 * @param dialogueClass
	 *            the type of dialogues that will be managed by this component.
	 *            This is needed because Ashley does such a terrible job out of
	 *            managing component inheritance.
	 * @param gameView
	 * @param entitiesLoader
	 */
	public DialogueSystem(Class<? extends Component> dialogueClass,
			GameView gameView, EntitiesLoader entitiesLoader) {
		super(Family.getFamilyFor(dialogueClass));
		this.dialogueClass = dialogueClass;
		this.gameView = gameView;
		this.entitiesLoader = entitiesLoader;
	}

	protected EngineEntity createTemporaryEntity(int x, int y, String key) {
		ModelEntity dialogueEntity = new ModelEntity();
		dialogueEntity.setX(x);
		dialogueEntity.setY(y);

		Label label = new Label();
		label.setText(key);
		label.setStyle(DIALOGUE_STYLE);

		dialogueEntity.getComponents().add(label);
		EngineEntity ee = entitiesLoader.toEngineEntity(dialogueEntity);
		EngineEntity parent = gameView.getLayer(Layer.SCENE_HUD);
		parent.getGroup().addActorAt(0, ee.getGroup());
		return ee;
	}

	protected void addTouchCallback(final EngineEntity dialogueEntity,
			final DialogueComponent dialogue, final int lineNumber) {
		Group entityGroup = dialogueEntity.getGroup();
		entityGroup.setTouchable(Touchable.enabled);
		entityGroup.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				dialogue.setChanged(true);
			}
		});
	}

	protected void createDialogue(Array<String> keys, DialogueComponent dialogue) {
		int x = DIALOGUE_X_OFFSET;
		int y = Gdx.graphics.getHeight()
				- (DIALOGUE_LINE_HEIGHT + DIALOGUE_Y_OFFSET);
		int choice = 0;
		for (String key : keys) {
			EngineEntity dialogueEntity = createTemporaryEntity(x, y, key);
			addTouchCallback(dialogueEntity, dialogue, choice++);
			dialogue.getRenderingEntities().add(dialogueEntity);
			y -= (DIALOGUE_LINE_HEIGHT + DIALOGUE_SPACING);
		}
	}

	/**
	 * Display and dismiss dialogues as appropriate
	 * 
	 * @param entity
	 * @param delta
	 */
	@Override
	public void processEntity(Entity entity, float delta) {

		Component c = entity.getComponent(dialogueClass);
		DialogueComponent dialogue = (DialogueComponent) c;

		if (!dialogue.isDisplayed()) {
			dialogue.setDisplayed(true);
			createDialogue(dialogue.getKeys(), dialogue);
		} else if (dialogue.isDismissed()) {
			entity.remove(dialogueClass);
		}
	}
}
