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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Groups a {@link ModelEntity} with its engine representation, in form of
 * {@link Group}
 */
public class EngineEntity extends Entity implements Poolable {

	private ModelEntity modelEntity;

	private Group group;

	public EngineEntity() {
		group = new Group() {
			@Override
			public Actor hit(float x, float y, boolean touchable) {
				Actor actor = super.hit(x, y, touchable);
				return actor == this ? null : actor;
			}
		};
		group.setUserObject(this);
	}

	public void setGroup(Group group) {
		this.group.setUserObject(null);
		group.setUserObject(this);
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public ModelEntity getModelEntity() {
		return modelEntity;
	}

	public void setModelEntity(ModelEntity modelEntity) {
		this.modelEntity = modelEntity;
		group.setPosition(modelEntity.getX(), modelEntity.getY());
		group.setOrigin(modelEntity.getOriginX(), modelEntity.getOriginY());
		group.setRotation(modelEntity.getRotation());
		group.setScale(modelEntity.getScaleX(), modelEntity.getScaleY());
	}

	@Override
	public Component remove(Class<? extends Component> componentType) {
		Component component = super.remove(componentType);
		if (component != null) {
			Pools.free(component);
		}
		return component;
	}

	@Override
	public void reset() {
		removeAll();
		flags = 0;
		group.clear();
		group.remove();
		group.setOrigin(0, 0);
		group.setBounds(0, 0, 0, 0);
		group.setScale(1.0f, 1.0f);
		group.setRotation(0.f);
		group.setColor(Color.WHITE);
		modelEntity = null;
	}

}
