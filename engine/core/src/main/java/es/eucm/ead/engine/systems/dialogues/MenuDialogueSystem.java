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

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.components.dialogues.DialogueComponent;
import es.eucm.ead.engine.components.dialogues.MenuDialogueComponent;
import es.eucm.ead.engine.entities.EngineEntity;

/**
 * A system for instantiating in-game dialog bubbles.
 */
public class MenuDialogueSystem extends DialogueSystem {

	public MenuDialogueSystem(GameView gameView, EntitiesLoader entitiesLoader) {
		super(MenuDialogueComponent.class, gameView, entitiesLoader);
	}

	@Override
	protected void addTouchCallback(final EngineEntity dialogueEntity,
			final DialogueComponent dialogue, final int lineNumber) {
		Group entityGroup = dialogueEntity.getGroup();
		entityGroup.setTouchable(Touchable.enabled);
		entityGroup.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((MenuDialogueComponent) dialogue).setMenuChoice(lineNumber);
				dialogue.setChanged(true);
			}
		});
	}
}
