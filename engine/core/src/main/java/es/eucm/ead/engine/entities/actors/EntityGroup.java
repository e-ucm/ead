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
package es.eucm.ead.engine.entities.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * Convenient extension of {@link Group} that regenerates the holding entity's
 * bounding area each time x, y, origin, scale, rotation, width, height or
 * children are updated.
 * 
 * Created by Javier Torrente on 4/07/14.
 */
public class EntityGroup extends AbstractWidget {

	private Array<Polygon> collider;

	private ShaderComponent shader;

	public void setShader(ShaderComponent shader) {
		this.shader = shader;
	}

	public void setCollider(Array<Polygon> collider) {
		this.collider = collider;
	}

	public Array<Polygon> getCollider() {
		return collider;
	}

	@Override
	public void addActor(Actor actor) {
		if (actor != null) {
			super.addActor(actor);
		}
	}

	/**
	 * Resets the renderer to its initial state
	 */
	public void restart() {
		for (Actor actor : getChildren()) {
			if (actor instanceof EntityGroup) {
				((EntityGroup) actor).restart();
			}
		}
	}

	/**
	 * Changes the state of the renderer
	 */
	public void changeState(String stateTag) {
		for (Actor actor : getChildren()) {
			if (actor instanceof EntityGroup) {
				((EntityGroup) actor).changeState(stateTag);
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float r = batch.getColor().r;
		float g = batch.getColor().g;
		float b = batch.getColor().b;
		float a = batch.getColor().a;
		batch.setColor(getColor().r * r, getColor().g * g, getColor().b * b,
				parentAlpha);
		if (shader != null) {
			batch.setShader(shader.getShaderProgram());
			shader.prepare();
			super.draw(batch, parentAlpha);
			batch.setShader(null);
		} else {
			super.draw(batch, parentAlpha);
		}
		batch.setColor(r, g, b, a);
	}

	@Override
	protected void positionChanged() {
		updateBoundingArea();
		invalidateBoundsHierarchy();
	}

	@Override
	public void setTouchable(Touchable touchable) {
		super.setTouchable(touchable == Touchable.enabled
				&& getChildren().size > 1 ? Touchable.childrenOnly : touchable);
	}

	@Override
	protected void sizeChanged() {
		updateBoundingArea();
		super.sizeChanged();
	}

	@Override
	public void setOriginX(float originX) {
		if (originX != getOriginX()) {
			super.setOriginX(originX);
			updateBoundingArea();
		}
	}

	@Override
	public void setOriginY(float originY) {
		if (originY != getOriginY()) {
			super.setOriginY(originY);
			updateBoundingArea();
		}
	}

	@Override
	public void setOrigin(float originX, float originY) {
		if (originX != getOriginX() || originY != getOriginY()) {
			super.setOrigin(originX, originY);
		}
	}

	@Override
	public void setScaleX(float scaleX) {
		if (scaleX != getScaleX()) {
			super.setScaleX(scaleX);
			invalidateBoundsHierarchy();
		}
	}

	@Override
	public void setScaleY(float scaleY) {
		if (scaleY != getScaleY()) {
			super.setScaleY(scaleY);
			invalidateBoundsHierarchy();
		}
	}

	@Override
	public void setScale(float scaleXY) {
		if (scaleXY != getScaleX() || scaleXY != getScaleY()) {
			super.setScale(scaleXY);
			updateBoundingArea();
			invalidateBoundsHierarchy();
		}
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		if (scaleX != getScaleX() || scaleY != getScaleY()) {
			super.setScale(scaleX, scaleY);
			updateBoundingArea();
			invalidateBoundsHierarchy();
		}
	}

	@Override
	public void scaleBy(float scale) {
		super.scaleBy(scale);
		updateBoundingArea();
		invalidateBoundsHierarchy();
	}

	@Override
	public void scaleBy(float scaleX, float scaleY) {
		super.scaleBy(scaleX, scaleY);
		updateBoundingArea();
		invalidateBoundsHierarchy();
	}

	@Override
	public void setRotation(float degrees) {
		if (degrees != getRotation()) {
			super.setRotation(degrees);
			updateBoundingArea();
			invalidateBoundsHierarchy();
		}
	}

	@Override
	public void rotateBy(float amountInDegrees) {
		super.rotateBy(amountInDegrees);

		invalidateBoundsHierarchy();
		updateBoundingArea();
	}

	@Override
	protected void childrenChanged() {
		updateBoundingArea();
		super.childrenChanged();
		if (getChildren().size > 1) {
			setTouchable(Touchable.childrenOnly);
		}
	}

	public void invalidateBoundsHierarchy() {
		if (getParent() instanceof EntityGroup) {
			((EntityGroup) getParent()).invalidateBoundsHierarchy();
		}
	}

	/*
	 * Just call entity's parent to update its bounding area. Invoked when
	 * x,y,origin,scale, rotation, width, height or children are updated.
	 */
	private void updateBoundingArea() {
		invalidateHierarchy();
		if (getUserObject() == null
				|| !(getUserObject() instanceof EngineEntity)) {
			return;
		}
		EngineEntity entity = (EngineEntity) getUserObject();
		entity.updateBoundingArea();
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		if (actor == null || actor == this) {
			Array<Polygon> collider = getCollider();
			if (collider != null && collider.size > 0) {
				int polygonsHit = 0;
				for (Polygon p : collider) {
					if (p.contains(x, y)) {
						polygonsHit++;
					}
				}
				return polygonsHit % 2 == 1 ? this : null;
			}
		}
		return actor;
	}
}
