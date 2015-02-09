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
package es.eucm.ead.engine.systems.conversations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.renderers.OptionsComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.RemoveEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;

public class OptionsSystem extends IteratingSystem {

	private GameAssets gameAssets;

	private EntitiesLoader entitiesLoader;

	private GameView gameView;

	public OptionsSystem(GameAssets gameAssets, EntitiesLoader entitiesLoader,
			GameView gameView) {
		super(Family.all(OptionsComponent.class).get());
		this.gameAssets = gameAssets;
		this.entitiesLoader = entitiesLoader;
		this.gameView = gameView;
	}

	@Override
	public void processEntity(Entity entity, float v) {
		OptionsComponent options = entity.getComponent(OptionsComponent.class);

		float yOffset = 30;
		int i = 0;

		ModelEntity container = new ModelEntity();
		RemoveEntity removeEntity = new RemoveEntity();
		removeEntity
				.setTarget("(collection sentity (hastag $entity s_option))");

		for (String option : options.getOptions()) {
			ModelEntity text = new ModelEntity();
			Label label = new Label();
			label.setText(i + ": " + gameAssets.getI18N().m(option));
			text.getComponents().add(label);
			text.setX(0);
			text.setY(i * yOffset);
			Behavior behavior = new Behavior();
			behavior.setEvent(new Touch());

			ChangeVar changeVar = new ChangeVar();
			changeVar.setVariable(OptionRuntimeNode
					.getOptionSelectedVar(options.getConversation()));
			changeVar.setExpression("i" + i);
			behavior.getEffects().add(changeVar);
			behavior.getEffects().add(removeEntity);

			Tags tags = new Tags();
			tags.getTags().add("_option");
			text.getComponents().add(tags);

			text.getComponents().add(behavior);

			container.getChildren().add(text);

			i++;
		}

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(container);

		gameView.getLayer(Layer.HUD).getGroup()
				.addActor(engineEntity.getGroup());

		entity.remove(OptionsComponent.class);
	}
}
