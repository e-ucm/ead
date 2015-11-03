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
package es.eucm.ead.engine.tests.systems;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.GleanerSystemForTest;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.gleaner.effects.LogTraceExecutor;
import es.eucm.ead.engine.gleaner.processors.SettingsProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.processors.renderers.EmptyRendererProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.gleaner.components.GleanerLocalStorage;
import es.eucm.ead.schema.gleaner.components.GleanerSettings;
import es.eucm.ead.schema.gleaner.effects.LogTrace;
import es.eucm.ead.schema.renderers.EmptyRenderer;
import es.eucm.gleaner.tracker.C;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by jtorrente on 01/11/2015.
 */
public class GleanerSystemTest extends EngineTest {

	private GleanerSettings gleanerSettings;

	@Before
	public void init() {
		componentLoader.registerComponentProcessor(GleanerSettings.class,
				new SettingsProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Behavior.class,
				new BehaviorsProcessor(gameLoop));
		EffectsSystem effectsSystem = new EffectsSystem(gameLoop,
				variablesManager, gameAssets, gleanerSystem);
		gameLoop.addSystem(effectsSystem);
		effectsSystem.registerEffectExecutor(LogTrace.class,
				new LogTraceExecutor(gleanerSystem));

		gleanerSettings = new GleanerSettings();
		GleanerLocalStorage gleanerLocalStorage = new GleanerLocalStorage();
		gleanerLocalStorage.setFilePrefix("test");
		gleanerSettings.setStorage(gleanerLocalStorage);

		ModelEntity modelEntity = new ModelEntity();
		modelEntity.getComponents().add(gleanerSettings);
		gameLoop.addSystem(gleanerSystem);
		gameLoop.addEntity(entitiesLoader.toEngineEntity(modelEntity));
		gameLoop.update(0);
	}

	@Test
	public void testDefaultSettings() {
		// Zone is not logged by default
		traces(0, C.SCREEN, C.ZONE, C.CHOICE, C.VAR, C.CLICK);
		checkLocalStorage(5, C.SCREEN, C.CHOICE, C.VAR, C.CLICK);
	}

	@Test
	public void testPressClick() {
		ClickListener clickListener = null;
		for (int i = 0; i < gameView.getListeners().size; i++) {
			EventListener listener = gameView.getListeners().get(i);
			System.out.println(listener.getClass());
			if (ClickListener.class.isAssignableFrom(listener.getClass())) {
				clickListener = (ClickListener) listener;
				break;
			}
		}
		if (clickListener == null) {
			fail("Cannot inject clicks");
		}

		componentLoader.registerComponentProcessor(EmptyRenderer.class,
				new EmptyRendererProcessor(gameLoop, gameAssets));
		ModelEntity entity = entity(20F, 20F).getLastEntity();
		entity.setName("entity1");
		emptyRectangle(50, 50, true);
		entity(10F, 10F).emptyRectangle(20, 20, true);
		EngineEntity engineEntity = entitiesLoader.toEngineEntity(entity);
		gameLoop.addEntity(engineEntity);

		InputEvent event = new InputEvent();
		event.setStageX(10);
		event.setStageY(10);
		clickListener.touchUp(event, 5, 5, 0, 0);
		gameLoop.update(30);
		checkLocalStorage(2, "click");
		clickListener.touchDown(event, 10, 10, 0, 0);
		gameLoop.update(30);
		checkLocalStorage(3, "press");
		event.setTarget(engineEntity.getGroup().getChildren().get(0));
		clickListener.touchDown(event, 30, 30, 0, 0);
		gameLoop.update(30);
		checkLocalStorage(4, "entity1");
	}

	@Test
	public void testEffect() {
		gleanerSettings.setEffect(true);
		LogTrace logTrace = new LogTrace();
		logTrace.setTag("custom");
		logTrace.getValues().add("val1");
		logTrace.getValues().add(13F);
		logTrace.getValues().add(true);

		Behavior behavior = new Behavior();
		behavior.setEvent(new Init());
		behavior.getEffects().add(logTrace);
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.getComponents().add(behavior);

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(modelEntity);
		gameLoop.addEntity(engineEntity);
		gameLoop.update(0);
		checkLocalStorage(1, "new session");
		gameLoop.update(30); // By default, traces are flushed every 3 seconds
		checkLocalStorage(3, "custom", "effect");
	}

	private void traces(float seconds, String... types) {
		for (String type : types) {
			if (type.equals(C.SCREEN)) {
				gleanerSystem.screen("testScreenId");
			} else if (type.equals(C.ZONE)) {
				gleanerSystem.zone("testZoneId");
			} else if (type.equals(C.CHOICE)) {
				gleanerSystem.choice("testChoiceId", "testOptionId");
			} else if (type.equals(C.CLICK)) {
				gleanerSystem.click(0, 0, "testClickTarget");
			} else if (type.equals(C.VAR)) {
				gleanerSystem.var("testVarName", "testValue");
			}
		}
		gameLoop.update(seconds);
	}

	private void checkLocalStorage(final int expectedNumberOfLines,
			final String... piecesOfContent) {
		gleanerSystem.setListener(new GleanerSystemForTest.DataSentListener() {
			@Override
			public void dataSent(String data) {
				String contents = gleanerSystem.getGleanerFile().readString();

				for (String line : contents.split("\\n")) {
					System.out.println(line);
				}
				assertEquals(expectedNumberOfLines,
						contents.split("\\n").length);
				for (String pieceOfContent : piecesOfContent) {
					assertTrue(contents.contains(pieceOfContent));
				}
			}
		});
	}
}
