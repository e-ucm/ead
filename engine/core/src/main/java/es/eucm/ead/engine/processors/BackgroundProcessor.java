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
package es.eucm.ead.engine.processors;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.BackgroundComponent;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.Background;
import es.eucm.ead.schema.components.Shader;
import es.eucm.ead.schema.data.Parameter;

public class BackgroundProcessor extends ComponentProcessor<Background> {

	private final EntitiesLoader entitiesLoader;

	public BackgroundProcessor(GameLoop gameLoop, EntitiesLoader entitiesLoader) {
		super(gameLoop);
		this.entitiesLoader = entitiesLoader;
	}

	@Override
	public Component getComponent(Background background) {
		BackgroundComponent backgroundComponent = gameLoop
				.createComponent(BackgroundComponent.class);

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(background
				.getEntity());
		engineEntity.getGroup().setTransform(false);
		backgroundComponent.setEngineEntity(engineEntity);
		backgroundComponent.setGameLoop(gameLoop);

		return backgroundComponent;
	}
}
