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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.effects.EffectEngineObject;
import es.eucm.ead.engine.renderers.RendererEngineObject;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Trigger;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.renderers.Renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneElementEngineObject extends ActorEngineObject<SceneElement> {

	protected RendererEngineObject<?> renderer;

	private Map<Trigger, List<Effect>> behaviors;

	public SceneElementEngineObject() {
		behaviors = new HashMap<Trigger, List<Effect>>();
	}

	@Override
	public void initialize(SceneElement schemaObject) {
		readTransformation(element);
		setRenderer(element.getRenderer());
		readEffects(element);
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
			addActor(e);
		}
	}

	private void readBehaviors(SceneElement element) {
		for (Behavior b : element.getBehaviors()) {
			addBehavior(b);
		}
	}

	private void readEffects(SceneElement element) {
		if (element.getEffects() != null) {
			for (Effect a : element.getEffects()) {
				addAction((Action) gameLoop.getAssets().getEngineObject(a));
			}
		}
	}

	@Override
	protected void findByTag(Array<SceneElementEngineObject> actors, String tag) {
		if (element.getTags().contains(tag)) {
			actors.add(this);
		}
		super.findByTag(actors, tag);
	}

	/**
	 * This method is either used by
	 * {@link #initialize(es.eucm.ead.schema.actors.SceneElement)} to setup the
	 * default renderer and also by effect Change renderer
	 * {@link es.eucm.ead.engine.effects.ChangeRendererEngineObject} The
	 * renderer will only be changed if newRenderer is different from the
	 * current one (!newRenderer.equals(renderer))
	 * 
	 * @param newRenderer
	 *            The new renderer to be setup. It can either by a new Renderer
	 *            or element.getRenderer()
	 * @return True if the renderer actually changed. False otherwise
	 */
	public boolean setRenderer(Renderer newRenderer) {
		boolean rendererChanged = false;
		// Empties have no renderer
		if (newRenderer != null) {
			renderer = gameLoop.getAssets().getEngineObject(newRenderer);
			this.setWidth(renderer.getWidth());
			this.setHeight(renderer.getHeight());
			rendererChanged = true;

		} else {
			if (renderer != null) {
				rendererChanged = true;
			}
			renderer = null;
			this.setWidth(0);
			this.setHeight(0);
		}
		return rendererChanged;
	}

	/**
	 * This method just restores the original renderer this scene element used
	 * to have. To be invoked by
	 * {@link es.eucm.ead.engine.effects.ChangeRendererEngineObject}
	 * 
	 * @return True if the renderer actually changed, false otherwise
	 */
	public boolean restoreInitialRenderer() {
		// Gets back to the original renderer this scene element had
		return setRenderer(element.getRenderer());
	}

	public RendererEngineObject getRenderer() {
		return renderer;
	}

	protected void readTransformation(SceneElement sceneElement) {
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
		super.drawChildren(batch, parentAlpha);
	}

	/**
	 * Adds a new behavior to this actor
	 * 
	 * @param behavior
	 *            the new behavior
	 */
	public void addBehavior(Behavior behavior) {
		addBehavior(behavior.getTrigger(), behavior.getEffect());
	}

	/**
	 * Adds a new behavior to this actor
	 * 
	 * @param trigger
	 *            the behavior's trigger
	 * @param effect
	 *            the behavior's effect
	 */
	private void addBehavior(Trigger trigger, Effect effect) {
		List<Effect> effects = behaviors.get(trigger);
		if (effects == null) {
			effects = new ArrayList<Effect>();
			behaviors.put(trigger, effects);
			// Only register if it's not already registered
			gameLoop.registerForTrigger(this, trigger);
		}
		effects.add(effect);
	}

	/**
	 * 
	 * @return the current behaviors of this actor
	 */
	public Map<Trigger, List<Effect>> getBehaviors() {
		return behaviors;
	}

	/**
	 * Processes a trigger, most probably executing an effect. This method is
	 * usually called from an {@link es.eucm.ead.engine.triggers.TriggerSource}
	 * 
	 * @param trigger
	 *            the trigger
	 * @return Return if there was an effect associated to the trigger
	 */
	public boolean process(Trigger trigger) {
		List<Effect> effects = behaviors.get(trigger);
		if (effects != null) {
			for (Effect a : effects) {
				EffectEngineObject effect = gameLoop.getAssets()
						.getEngineObject(a);
				effect.setTrigger(trigger);
				addAction(effect);
			}
			return true;
		} else {
			Gdx.app.error("SceneElement", "No effect for event " + trigger);
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

		gameLoop.unregisterForAllTriggers(this);
		behaviors.clear();
	}

	@Override
	// SceneElementActor overrides act just to propogate the call to its
	// renderer. This is necessary to implement renderers that depend on time
	public void act(float delta) {
		super.act(delta);
		if (renderer != null)
			renderer.act(delta);
	}
}
