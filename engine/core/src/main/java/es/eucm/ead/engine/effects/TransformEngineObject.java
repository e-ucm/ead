/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import es.eucm.ead.schema.effects.Transform;
import es.eucm.ead.schema.components.Transformation;

public class TransformEngineObject extends EffectEngineObject<Transform> {

	private ParallelAction action;

	@Override
	protected boolean delegate(float delta) {
		return action.act(delta);
	}

	@Override
	public void initialize(Transform schemaObject) {
		action = new ParallelAction();
		action.setActor(actor);
		float duration = Math.max(0, schemaObject.getDuration());
		boolean relative = schemaObject.isRelative();
		Transformation t = schemaObject.getTransformation();
		float x = t.getX();
		float y = t.getY();
		float rot = t.getRotation();
		float sx = t.getScaleX();
		float sy = t.getScaleY();
		addAction(relative ? Actions.moveBy(x, y, duration) : Actions.moveTo(x,
				y, duration));
		addAction(relative ? Actions.rotateBy(rot, duration) : Actions
				.rotateTo(rot, duration));
		addAction(relative ? Actions.scaleBy(sx, sy, duration) : Actions
				.scaleTo(sx, sy, duration));

		es.eucm.ead.schema.components.Color c = t.getColor();
		Color color = actor.getColor();
		if (c != null) {
			float r = (relative ? color.r + c.getR() : c.getR());
			float g = (relative ? color.g + c.getG() : c.getG());
			float b = (relative ? color.b + c.getB() : c.getB());
			float a = (relative ? color.a + c.getA() : c.getA());
			addAction(Actions.color(new Color(r, g, b, a).clamp(), duration));
		}
	}

	private void addAction(Action a) {
		a.setActor(actor);
		action.addAction(a);
	}
}
