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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
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

	private ShaderComponent shader;

	public void setShader(ShaderComponent shader) {
		this.shader = shader;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (shader != null) {
			batch.setShader(shader.getShaderProgram());
			shader.prepare();
		}
		super.draw(batch, parentAlpha);
		if (shader != null) {
			batch.setShader(null);
		}
	}

	@Override
	protected void positionChanged() {
		updateBoundingArea();
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
		}
	}

	@Override
	public void setScaleY(float scaleY) {
		if (scaleY != getScaleY()) {
			super.setScaleY(scaleY);
		}
	}

	@Override
	public void setScale(float scaleXY) {
		if (scaleXY != getScaleX() || scaleXY != getScaleY()) {
			super.setScale(scaleXY);
			updateBoundingArea();
		}
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		if (scaleX != getScaleX() || scaleY != getScaleY()) {
			super.setScale(scaleX, scaleY);
			updateBoundingArea();
		}
	}

	@Override
	public void scaleBy(float scale) {
		super.scaleBy(scale);
		updateBoundingArea();
	}

	@Override
	public void scaleBy(float scaleX, float scaleY) {
		super.scaleBy(scaleX, scaleY);
		updateBoundingArea();
	}

	@Override
	public void setRotation(float degrees) {
		if (degrees != getRotation()) {
			super.setRotation(degrees);
			updateBoundingArea();
		}
	}

	@Override
	public void rotateBy(float amountInDegrees) {
		super.rotateBy(amountInDegrees);
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

	@Override
	public void layout() {
		for (Actor actor : getChildren()) {
			if (actor instanceof Layout) {
				((Layout) actor).pack();
			}
		}
	}

	/*
	 * Just call entity's parent to update its bounding area. Invoked when
	 * x,y,origin,scale, rotation, width, height or children are updated.
	 */
	private void updateBoundingArea() {
		if (getUserObject() == null
				|| !(getUserObject() instanceof EngineEntity)) {
			return;
		}
		EngineEntity entity = (EngineEntity) getUserObject();
		entity.updateBoundingArea();
	}
}
