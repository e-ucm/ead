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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.actions.AbstractAction;
import es.eucm.ead.engine.renderers.AbstractRenderer;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Trigger;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneElementActor extends AbstractActor<SceneElement> {

	private AbstractRenderer<?> renderer;

	private Map<Trigger, List<Action>> behaviors;

	public SceneElementActor() {
		behaviors = new HashMap<Trigger, List<Action>>();
	}

	@Override
	public void initialize(SceneElement schemaObject) {
		readTransformation(element);
		readRenderer(element);
		readActions(element);
		readBehaviors(element);
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
			this.addActor((Actor) Engine.factory.getEngineObject(e));
		}
	}

	private void readBehaviors(SceneElement element) {
		for (Behavior b : element.getBehaviors()) {
			addBehavior(b);
		}
	}

	private void readActions(SceneElement element) {
		if (element.getActions() != null) {
			for (Action a : element.getActions()) {
				addAction((com.badlogic.gdx.scenes.scene2d.Action) Engine.factory
						.getEngineObject(a));
			}
		}
	}

	private void readRenderer(SceneElement element) {
		// Empties have no renderer
		if (element.getRenderer() != null) {
			renderer = Engine.factory.getEngineObject(element.getRenderer());
			this.setWidth(renderer.getWidth());
			this.setHeight(renderer.getHeight());
		} else {
			this.setWidth(0);
			this.setHeight(0);
		}
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
			this.setOrigin(t.getOriginX(), t.getOriginY());
		}
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		// Empties have no renderer
		if (renderer != null) {
			// Set alpha and color
			float alpha = this.getColor().a;
			this.getColor().a *= parentAlpha;
			batch.setColor(this.getColor());

			renderer.draw(batch);

			// Restore alpha
			this.getColor().a = alpha;
		}
	}

	/**
	 * Adds a new behavior to this actor
	 * 
	 * @param behavior
	 *            the new behavior
	 */
	public void addBehavior(Behavior behavior) {
		addBehavior(behavior.getTrigger(), behavior.getAction());
	}

	/**
	 * Adds a new behavior to this actor
	 * 
	 * @param trigger
	 *            the behavior's trigger
	 * @param action
	 *            the behavior's action
	 */
	private void addBehavior(Trigger trigger, Action action) {
		List<Action> actions = behaviors.get(trigger);
		if (actions == null) {
			actions = new ArrayList<Action>();
			behaviors.put(trigger, actions);
			// Only register if it's not already registered
			Engine.gameController.registerForTrigger(this, trigger);
		}
		actions.add(action);
	}

	/**
	 * 
	 * @return the current behaviors of this actor
	 */
	public Map<Trigger, List<Action>> getBehaviors() {
		return behaviors;
	}

	/**
	 * Processes a trigger, most probably executing an action. This method is
	 * usually called from an {@link es.eucm.ead.engine.triggers.TriggerSource}
	 * 
	 * @param trigger
	 *            the trigger
	 * @return Return if there was an action associated to the trigger
	 */
	public boolean process(Trigger trigger) {
		List<Action> actions = behaviors.get(trigger);
		if (actions != null) {
			for (Action a : actions) {
				AbstractAction action = Engine.factory.getEngineObject(a);
				action.setTrigger(trigger);
				addAction(action);
			}
			return true;
		} else {
			Gdx.app.error("SceneElementActor", "No action for event " + trigger);
			return false;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (renderer != null) {
			renderer.dispose();
			renderer = null;
		}

		Engine.gameController.unregisterForAllTriggers(this);
		behaviors.clear();
		clearListeners();

		// Setting actor to default
		this.setPosition(0, 0);
		this.getColor().set(1.0f, 1.0f, 1.0f, 1.0f);
		this.setRotation(0.0f);
		this.setScale(1.0f);

		for (com.badlogic.gdx.scenes.scene2d.Action a : this.getActions()) {
			if (a instanceof AbstractAction) {
				((AbstractAction) a).dispose();
			}
		}
		clearActions();

		// Clear children
		for (Actor a : this.getChildren()) {
			if (a instanceof AbstractActor) {
				((AbstractActor) a).dispose();
			}
		}
		clearChildren();
	}
}
