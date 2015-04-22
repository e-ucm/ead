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
package es.eucm.ead.editor.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.engine.components.renderers.frames.EmptyRendererComponent;
import es.eucm.ead.engine.entities.EngineEntity;

public class EditorEmptyRendererComponent extends EmptyRendererComponent {

	private static Vector2 leftBottom = new Vector2(),
			topRight = new Vector2();

	private Actor actor;

	private Engine engine;

	private Drawable areaDrawable;

	private Drawable hitAllDrawable;

	@Override
	public void setParent(Entity parent) {
		super.setParent(parent);
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public void setDrawables(Drawable drawable, Drawable extendeDrawable) {
		this.areaDrawable = drawable;
		this.hitAllDrawable = extendeDrawable;
	}

	@Override
	public void draw(Batch batch) {
		if (actor == null) {
			actor = ((EngineEntity) getParent()).getGroup();
		}

		if (!engine.isRunning() && getCollider() != null) {
			areaDrawable.draw(batch, 0, 0, width, height);
			if (isHitAll()) {
				leftBottom.set(0, 0);
				actor.stageToLocalCoordinates(leftBottom);
				topRight.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				actor.stageToLocalCoordinates(topRight);
				hitAllDrawable.draw(batch, leftBottom.x, leftBottom.y,
						topRight.x - leftBottom.x, topRight.y - leftBottom.y);
			}
		}
	}

	@Override
	public boolean hit(float x, float y) {
		if (engine.isRunning()) {
			return super.hit(x, y);
		}
		boolean hitAll = isHitAll();
		setHitAll(false);
		boolean hit = super.hit(x, y);
		setHitAll(hitAll);
		return hit;
	}

	@Override
	public void reset() {
		actor = null;
	}
}
