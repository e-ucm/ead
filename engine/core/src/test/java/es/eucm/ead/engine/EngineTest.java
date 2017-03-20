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
package es.eucm.ead.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import es.eucm.ead.builder.DemoBuilder;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.expressions.operators.OperationsFactory;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockComponentProcessor;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.mock.MockImageUtils;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.engine.systems.gamestatepersistence.PersistentGameStateSystem;
import es.eucm.ead.engine.systems.GleanerSystem;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Map.Entry;

/**
 * Created by angel on 24/06/14.
 */
public class EngineTest extends DemoBuilder {

	private static Vector2 tmp = new Vector2();

	protected Accessor accessor;

	protected OperationsFactory operationsFactory;

	protected GameLoop gameLoop;

	protected GleanerSystemForTest gleanerSystem;

	protected GameAssets gameAssets;

	protected VariablesManager variablesManager;

	protected PersistentGameStateSystem persistentGameStateSystem;

	protected ComponentLoader componentLoader;

	protected DefaultGameView gameView;

	protected EntitiesLoader entitiesLoader;

	protected Stage stage;

	@BeforeClass
	public static void initStatics() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		gameAssets = new GameAssets(new MockFiles(), new MockImageUtils());
		gameLoop = new GameLoop() {
			@Override
			public void update(float deltaTime) {
				super.update(deltaTime);
				stage.draw();
			}
		};
		gleanerSystem = new GleanerSystemForTest(gameLoop);
		gameView = new DefaultGameView(gameLoop, gleanerSystem);
		stage = new Stage();
		stage.addActor(gameView);
		accessor = new Accessor();
		operationsFactory = new OperationsFactory(gameLoop, accessor, gameView);
		variablesManager = new VariablesManager(accessor, operationsFactory);
		componentLoader = new ComponentLoader(gameAssets, variablesManager);
		persistentGameStateSystem = new PersistentGameStateSystem(gameLoop,
				variablesManager);
		accessor.setComponentLoader(componentLoader);
		entitiesLoader = new EntitiesLoader(gameLoop, gameAssets,
				componentLoader);

		// Mock initialization
		componentLoader.registerComponentProcessor(MockModelComponent.class,
				new MockComponentProcessor(gameLoop));
		gameAssets.addClassTag("mockeffect", MockEffect.class);
		gameAssets.addClassTag("mockcomponent", MockModelComponent.class);
		doBuild();
		prepareEngine();
		for (Entry<String, ModelEntity> entry : entities.entrySet()) {
			gameAssets.addAsset(entry.getKey(), Object.class, entry.getValue());
		}

		if (gameAssets.isLoaded(ModelStructure.GAME_FILE, Object.class)) {
			ModelEntity game = (ModelEntity) gameAssets.get(
					ModelStructure.GAME_FILE, Object.class);

			if (game != null) {
				GameLoader gameLoader = new GameLoader(gameAssets,
						entitiesLoader);
				gameLoader.loaded(ModelStructure.GAME_FILE, game);
				// Load first scene
				update(0);
				loadAssets();
				update(0);
				loadAssets();
			}
		}
	}

	public void prepareEngine() {
	}

	public void update(float delta) {
		((MockApplication) Gdx.app).act();
		stage.act(delta);
		gameLoop.update(delta);
	}

	public void loadAssets() {
		while (!gameAssets.update())
			;
	}

	public Actor hit(float stageX, float stageY) {
		return stage.hit(stageX, stageY, true);
	}

	@Override
	protected void doBuild() {

	}

	@After
	public void cleanup() {
		if (gleanerSystem != null) {
			gleanerSystem.cleanup();
		}
	}

	public EngineEntity entityAtPosition(float stageX, float stageY) {
		Actor actor = hit(stageX, stageY);
		return EngineUtils.getActorEntity(actor);
	}

	public ModelEntity modelEntityAtPosition(float stageX, float stageY) {
		return entityAtPosition(stageX, stageY).getModelEntity();
	}
}
