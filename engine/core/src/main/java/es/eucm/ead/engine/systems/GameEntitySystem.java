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

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.NullComponent;

/**
 * Created by jtorrente on 21/11/2015.
 */
public abstract class GameEntitySystem extends IteratingSystem {

	protected Class<? extends Component> gameComponentClass;

	protected Component gameComponent;

	protected GameLoop gameLoop;

	public GameEntitySystem(GameLoop gameLoop) {
		this(Family.all(NullComponent.class).get(), gameLoop);
	}

	public GameEntitySystem(GameLoop gameLoop,
			Class<? extends Component> gameComponentClass) {
		this(Family.all(NullComponent.class).get(), gameLoop,
				gameComponentClass);
	}

	public GameEntitySystem(Family family, GameLoop gameLoop) {
		this(family, gameLoop, null);
	}

	public GameEntitySystem(Family family, GameLoop gameLoop,
			Class<? extends Component> gameComponentClass) {
		super(family);
		this.gameLoop = gameLoop;
		this.gameComponentClass = gameComponentClass;
		gameComponent = null;
	}

	@Override
	public void update(float deltaTime) {
		init();
		if (isInit()) {
			super.update(deltaTime);
		}
	}

	protected boolean isInit() {
		return gameComponent != null;
	}

	protected void init() {
		if (isInit()) {
			return;
		}
		gameComponent = getGameComponent(gameComponentClass, gameLoop);
		if (isInit()) {
			doInit(gameComponent);
		}
	}

	protected void doInit(Component gameComponent) {

	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// Empty impl so subclasses don't have to implement it if they don't
		// need it
	}

	public static <T extends Component> T getGameComponent(Class<T> clazz,
			GameLoop gameLoop) {
		ImmutableArray<Entity> entities = gameLoop.getEntitiesFor(Family.all(
				clazz).get());
		if (entities.size() > 0) {
			return entities.first().getComponent(clazz);
		}
		return null;
	}
}
