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
package es.eucm.ead.editor.control.engine;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.processors.EditorEmptyRendererProcessor;
import es.eucm.ead.editor.processors.EditorFramesProcessor;
import es.eucm.ead.editor.processors.EditorReferenceProcessor;
import es.eucm.ead.engine.*;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.effects.GoSceneExecutor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.Reference;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.renderers.EmptyRenderer;
import es.eucm.ead.schema.renderers.Frames;

public class MobileEngineInitializer extends DefaultEngineInitializer {

	private Controller controller;

	public MobileEngineInitializer(Controller controller) {
		this.controller = controller;
	}

	@Override
	protected void registerSystems(final GameAssets gameAssets,
			final GameLoop gameLoop, final EntitiesLoader entitiesLoader,
			final GameView gameView, final VariablesManager variablesManager) {
		super.registerSystems(gameAssets, gameLoop, entitiesLoader, gameView,
				variablesManager);
		EffectsSystem effectsSystem = gameLoop.getSystem(EffectsSystem.class);
		// Tell GoSceneExecutor to configure TransitionManager to use viewport
		// to calculate widths and heights (editor mode)
		effectsSystem.registerEffectExecutor(GoScene.class,
				new GoSceneExecutor(entitiesLoader, gameView, true));
	}

	@Override
	protected void registerComponents(ComponentLoader componentLoader,
			GameAssets gameAssets, GameLoop gameLoop,
			VariablesManager variablesManager, EntitiesLoader entitiesLoader) {
		super.registerComponents(componentLoader, gameAssets, gameLoop,
				variablesManager, entitiesLoader);
		componentLoader.registerComponentProcessor(EmptyRenderer.class,
				new EditorEmptyRendererProcessor(controller.getEngine(),
						gameLoop, controller.getApplicationAssets()));
		componentLoader
				.registerComponentProcessor(Frames.class,
						new EditorFramesProcessor(gameLoop, gameAssets,
								componentLoader));
		componentLoader.registerComponentProcessor(Reference.class,
				new EditorReferenceProcessor(gameLoop, gameAssets,
						entitiesLoader, controller.getPlatform()));
	}

}
