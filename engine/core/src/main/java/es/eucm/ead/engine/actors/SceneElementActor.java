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
package es.eucm.ead.engine.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.actions.AbstractAction;
import es.eucm.ead.engine.renderers.AbstractRenderer;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Input;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Touch.Event;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;

import java.util.HashMap;
import java.util.Map;

public class SceneElementActor extends AbstractActor<SceneElement> {

	private AbstractRenderer<?> renderer;

	private Map<Event, Array<Action>> touchBehaviors;

	public SceneElementActor() {
		addListener(Engine.engine.getEventListener());
		touchBehaviors = new HashMap<Event, Array<Action>>();
	}

	@Override
	public void initialize(SceneElement sceneElement) {
		readTransformation(element);
		readRenderer(element);
		readActions(element);
		readBehavior(element);
		readChildren(element);
		readProperties(element);
	}

	private void readProperties(SceneElement element) {
		this.setTouchable(element.isEnable() ? Touchable.enabled
				: Touchable.disabled);
		this.setVisible(element.isVisible());
	}

	private void readChildren(SceneElement element) {
		for (SceneElement e : element.getChildren()) {
			this.addActor((Actor) Engine.factory.getElement(e));
		}
	}

	private void readBehavior(SceneElement element) {
		for (Behavior b : element.getBehaviors()) {
			Input input = b.getInput();
			if (input instanceof Touch) {
				Array<Action> l = touchBehaviors
						.get(((Touch) input).getEvent());
				if (l == null) {
					l = new Array<Action>();
					touchBehaviors.put(((Touch) input).getEvent(), l);
				}
				l.add(b.getAction());
			}
		}
	}

	private void readActions(SceneElement element) {
		if (element.getActions() != null) {
			for (Action a : element.getActions()) {
				addAction((com.badlogic.gdx.scenes.scene2d.Action) Engine.factory
						.getElement(a));
			}
		}
	}

	private void readRenderer(SceneElement element) {
		renderer = Engine.factory.getElement(element.getRenderer());
		this.setWidth(renderer.getWidth());
		this.setHeight(renderer.getHeight());
	}

	private void readTransformation(SceneElement sceneElement) {
		Transformation t = sceneElement.getTransformation();
		if (t != null) {
			Color c = t.getColor();
			if (c != null) {
				this.getColor().set(c.getR(), c.getG(), c.getB(), c.getA());
			}
			this.setX(t.getX());
			this.setY(t.getY());
			this.setRotation(t.getRotation());
			this.setScaleX(t.getScaleX());
			this.setScaleY(t.getScaleY());
		}
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		batch.setColor(this.getColor());
		renderer.draw(batch, parentAlpha);
	}

	@Override
	public void free() {
		super.free();
		if (renderer != null) {
			renderer.free();
			renderer = null;
		}

		touchBehaviors.clear();

		// Setting actor to default
		this.setPosition(0, 0);
		this.getColor().set(1.0f, 1.0f, 1.0f, 1.0f);
		this.setRotation(0.0f);
		this.setScale(1.0f);

		for (com.badlogic.gdx.scenes.scene2d.Action a : this.getActions()) {
			if (a instanceof AbstractAction) {
				((AbstractAction) a).free();
			}
		}
		clearActions();

		// Clear children
		for (Actor a : this.getChildren()) {
			if (a instanceof AbstractActor) {
				((AbstractActor) a).free();
			}
		}
		clearChildren();
	}

	public Array<Action> getActions(Event event) {
		return touchBehaviors.get(event);
	}

}
