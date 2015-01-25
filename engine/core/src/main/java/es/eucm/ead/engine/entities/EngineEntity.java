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
package es.eucm.ead.engine.entities;

import ashley.core.Component;
import ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.components.physics.BoundingAreaComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.actors.EntityGroup;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Groups a {@link ModelEntity} with its engine representation, in form of
 * {@link Group}
 */
public class EngineEntity extends Entity implements Poolable {

	private ModelEntity modelEntity;

	private Group group;

	private GameLoop gameLoop;

	private ShaderComponent shader;

	public void setShader(ShaderComponent shader) {
		this.shader = shader;
		if (this.group instanceof EntityGroup) {
			((EntityGroup) this.group).setShader(shader);
		}
	}

	public EngineEntity(GameLoop gameLoop) {
		this.gameLoop = gameLoop;
	}

	public void setGroup(Group group) {
		if (this.group != null) {
			this.group.setUserObject(null);
			this.group.clearListeners();
		}
		group.setUserObject(this);
		this.group = group;
		if (this.group instanceof EntityGroup) {
			((EntityGroup) this.group).setShader(shader);
		}
		readModelEntity();
		updateBoundingArea();
	}

	public Group getGroup() {
		return group;
	}

	public ModelEntity getModelEntity() {
		return modelEntity;
	}

	public void setModelEntity(ModelEntity modelEntity) {
		this.modelEntity = modelEntity;
		readModelEntity();
	}

	private void readModelEntity() {
		if (group != null && modelEntity != null) {
			group.setPosition(modelEntity.getX(), modelEntity.getY());
			group.setOrigin(modelEntity.getOriginX(), modelEntity.getOriginY());
			group.setRotation(modelEntity.getRotation());
			group.setScale(modelEntity.getScaleX(), modelEntity.getScaleY());
		}
	}

	@Override
	public void reset() {
		removeAll();
		flags = 0;
		if (group != null) {
			group.remove();
			for (Actor child : group.getChildren()) {
				Object o = child.getUserObject();
				if (o instanceof EngineEntity) {
					gameLoop.removeEntity((EngineEntity) o);
				}
			}
			group = null;
		}
		modelEntity = null;
	}

	@Override
	public Entity add(Component component) {
		Entity entity = super.add(component);
		if (component instanceof RendererComponent) {
			updateBoundingArea();
		}
		return entity;
	}

	@Override
	public void removeAll() {
		super.removeAll();
		updateBoundingArea();
	}

	@Override
	public Component remove(Class<? extends Component> componentType) {
		Component component = super.remove(componentType);
		if (component != null) {
			Pools.free(component);
		}

		if (componentType.isAssignableFrom(RendererComponent.class)) {
			updateBoundingArea();
		}
		return component;
	}

	/**
	 * Updates this entity's bounding area, in case it has it. It also notifies
	 * ancestors to update their respective bounding areas.
	 */
	public void updateBoundingArea() {
		if (hasComponent(BoundingAreaComponent.class)) {
			getComponent(BoundingAreaComponent.class).update(this);
			if (getGroup() != null
					&& getGroup().getParent() != null
					&& getGroup().getParent().getUserObject() != null
					&& getGroup().getParent().getUserObject() instanceof EngineEntity) {
				EngineEntity parent = (EngineEntity) getGroup().getParent()
						.getUserObject();
				parent.updateBoundingArea();
			}
		}
	}

}
