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
package es.eucm.ead.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.triggers.TimeSource;
import es.eucm.ead.engine.triggers.TriggerSource;
import es.eucm.ead.schema.behaviors.Time;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Trigger;

import java.util.LinkedHashMap;
import java.util.Map;

public class EngineStage extends Stage implements TriggerSource {

	private Group ui;

	private Group scene;

	private int gameWidth;

	private int gameHeight;

	private Map<Class<?>, TriggerSource> triggerSources;

	public EngineStage(int width, int height, boolean keepAspectRatio) {
		super(width, height, keepAspectRatio);
		triggerSources = new LinkedHashMap<Class<?>, TriggerSource>();
		ui = new Group();
		scene = new Group();
		this.addActor(scene);
		this.addActor(ui);
		initUI();
		registerTriggerProducers();
	}

	protected void registerTriggerProducers() {
		registerTriggerSource(Touch.class, (TriggerSource) Engine.engine
				.getEventListener());
		registerTriggerSource(Time.class, new TimeSource());
	}

	private void initUI() {
	}

	public void addUi(Actor a) {
		ui.addActor(a);
	}

	public void setScene(Actor s) {
		scene.clear();
		scene.addActor(s);
	}

	public void setGameSize(int width, int height) {
		this.gameWidth = width;
		this.gameHeight = height;
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int windowWidth, int windowHeight) {
		this.setViewport(gameWidth, gameHeight, false, 0, 0, windowWidth,
				windowHeight);
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	/**
	 * Registers a trigger source
	 * 
	 * @param clazz
	 *            the class of the event
	 * @param triggerSource
	 *            a source of triggers
	 */
	public void registerTriggerSource(Class<? extends Trigger> clazz,
			TriggerSource triggerSource) {
		if (triggerSources.containsKey(clazz)) {
			Gdx.app.log("EngineStage", clazz
					+ " has already a trigger source. It'll be overwritten.");
		}
		triggerSources.put(clazz, triggerSource);
	}

	@Override
	public void act(float delta) {
		for (TriggerSource triggerSource : triggerSources.values()) {
			triggerSource.act(delta);
		}
		super.act(delta);
	}

	@Override
	public void registerForTrigger(SceneElementActor actor, Trigger event) {
		TriggerSource triggerSource = triggerSources.get(event.getClass());
		if (triggerSource == null) {
			Gdx.app.error("EngineState", "No trigger source found for class "
					+ event.getClass());
		} else {
			triggerSource.registerForTrigger(actor, event);
		}
	}

	@Override
	public void unregisterForAllTriggers(SceneElementActor actor) {
		for (TriggerSource triggerSource : triggerSources.values()) {
			triggerSource.unregisterForAllTriggers(actor);
		}
	}
}
