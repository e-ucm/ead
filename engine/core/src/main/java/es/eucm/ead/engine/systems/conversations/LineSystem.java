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

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.LineComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.RemoveEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;

public class LineSystem extends IteratingSystem {

	private GameAssets gameAssets;

	private EntitiesLoader entitiesLoader;

	private GameView gameView;

	public LineSystem(GameAssets gameAssets, EntitiesLoader entitiesLoader,
			GameView gameView) {
		super(Family.getFamilyFor(LineComponent.class));
		this.gameAssets = gameAssets;
		this.entitiesLoader = entitiesLoader;
		this.gameView = gameView;
	}

	@Override
	public void processEntity(Entity entity, float v) {
		LineComponent line = entity.getComponent(LineComponent.class);

		ModelEntity text = new ModelEntity();
		Label label = new Label();
		label.setText(line.getSpeaker() + ": "
				+ gameAssets.getI18N().m(line.getLine()));
		text.getComponents().add(label);
		text.setX(0);
		text.setY(0);
		Behavior behavior = new Behavior();
		behavior.setEvent(new Touch());

		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable(LineRuntimeNode.getLineEndedVar(line
				.getConversation()));
		changeVar.setExpression("btrue");
		behavior.getEffects().add(changeVar);
		behavior.getEffects().add(new RemoveEntity());

		text.getComponents().add(behavior);

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(text);

		gameView.getLayer(Layer.HUD).getGroup()
				.addActor(engineEntity.getGroup());

		entity.remove(LineComponent.class);
	}
}
