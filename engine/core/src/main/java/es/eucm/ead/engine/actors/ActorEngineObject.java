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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.EngineObject;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.effects.EffectEngineObject;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.effects.Effect;

public abstract class ActorEngineObject<T> extends WidgetGroup implements
		EngineObject<T> {

	protected GameLoop gameLoop;

	protected T element;

	protected float accTime;

	public GameLoop getGameLoop() {
		return gameLoop;
	}

	public void setGameLoop(GameLoop gameLoop) {
		this.gameLoop = gameLoop;
	}

	public void setSchema(T schemaObject) {
		this.element = schemaObject;
		initialize(schemaObject);
	}

	public T getSchema() {
		return element;
	}

	public void dispose() {
		clearListeners();

		// Setting actor to default
		this.setPosition(0, 0);
		this.getColor().set(1.0f, 1.0f, 1.0f, 1.0f);
		this.setRotation(0.0f);
		this.setScale(1.0f);

		for (Action a : this.getActions()) {
			if (a instanceof EffectEngineObject) {
				((EffectEngineObject) a).dispose();
			}
		}
		clearActions();

		// Clear children
		for (Actor a : this.getChildren()) {
			if (a instanceof ActorEngineObject) {
				((ActorEngineObject) a).dispose();
			}
		}
		clearChildren();
		gameLoop.getAssets().free(this);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		accTime += delta;
	}

	/**
	 * @param sceneElement
	 *            the target scene element
	 * @return Returns the actor that wraps the given scene element
	 */
	public Actor getSceneElement(SceneElement sceneElement) {
		if (sceneElement == element) {
			return this;
		}
		for (Actor a : this.getChildren()) {
			if (a instanceof ActorEngineObject) {
				Actor actor = ((ActorEngineObject) a)
						.getSceneElement(sceneElement);
				if (actor != null) {
					return actor;
				}
			}
		}
		return null;
	}

	/**
	 * Adds an schema effect to the actor
	 * 
	 * @param effect
	 *            the effect schema
	 */
	public void addEffect(Effect effect) {
		addAction((Action) gameLoop.getAssets().getEngineObject(effect));
	}

	/**
	 * Adds an scene element to this actor
	 * 
	 * @param sceneElement
	 *            the schema object representing the scene element
	 */
	public void addActor(SceneElement sceneElement) {
		SceneElementEngineObject sceneElementActor = gameLoop.getAssets()
				.getEngineObject(sceneElement);
		addActor(sceneElementActor);
	}

	/**
	 * Returns all the scene elements tagged with the given tag.
	 * 
	 * @param tag
	 *            the tag
	 * @return the list of scene elements. It is null when no scene element has
	 *         the given tag
	 */
	public Array<SceneElementEngineObject> findByTag(String tag) {
		Array<SceneElementEngineObject> tags = new Array<SceneElementEngineObject>();
		findByTag(tags, tag);
		return tags;
	}

	/**
	 * Recursive find by tags. This method must be override for extending
	 * classes, to add the proper handling of the tags
	 * 
	 * @param actors
	 *            the list with the actors tagged with the given tag. This list
	 *            must be modified inside the method, adding the actors tagged
	 *            with the tag
	 * @param tag
	 *            the tag to look for
	 */
	protected void findByTag(Array<SceneElementEngineObject> actors, String tag) {
		for (Actor actor : getChildren()) {
			if (actor instanceof ActorEngineObject) {
				((ActorEngineObject) actor).findByTag(actors, tag);
			}
		}
	}
}
