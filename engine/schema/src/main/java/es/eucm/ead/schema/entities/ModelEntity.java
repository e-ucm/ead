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

package es.eucm.ead.schema.entities;

import es.eucm.ead.schema.components.ModelComponent;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic unit for interactive elements in eAdventure. An entity contain a set of
 * components defining its behavior and appearance.
 * 
 */
@Generated("org.jsonschema2pojo")
public class ModelEntity {

	private float x = 0.0F;
	private float y = 0.0F;
	private float originX = 0.0F;
	private float originY = 0.0F;
	private float rotation = 0.0F;
	private float scaleX = 1.0F;
	private float scaleY = 1.0F;
	/**
	 * A list with the components forming this entity
	 * 
	 */
	private List<ModelComponent> components = new ArrayList<ModelComponent>();
	/**
	 * Entity children
	 * 
	 */
	private List<ModelEntity> children = new ArrayList<ModelEntity>();

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getOriginX() {
		return originX;
	}

	public void setOriginX(float originX) {
		this.originX = originX;
	}

	public float getOriginY() {
		return originY;
	}

	public void setOriginY(float originY) {
		this.originY = originY;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * A list with the components forming this entity
	 * 
	 */
	public List<ModelComponent> getComponents() {
		return components;
	}

	/**
	 * A list with the components forming this entity
	 * 
	 */
	public void setComponents(List<ModelComponent> components) {
		this.components = components;
	}

	/**
	 * Entity children
	 * 
	 */
	public List<ModelEntity> getChildren() {
		return children;
	}

	/**
	 * Entity children
	 * 
	 */
	public void setChildren(List<ModelEntity> children) {
		this.children = children;
	}

}
