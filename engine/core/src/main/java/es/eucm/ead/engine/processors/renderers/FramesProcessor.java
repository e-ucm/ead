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
package es.eucm.ead.engine.processors.renderers;

import ashley.core.PooledEngine;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.renderers.FramesAnimationComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;

public class FramesProcessor extends RendererProcessor<Frames> {

	private EntitiesLoader entitiesLoader;

	public FramesProcessor(PooledEngine engine, GameAssets gameAssets,
			EntitiesLoader entitiesLoader) {
		super(engine, gameAssets);
		this.entitiesLoader = entitiesLoader;
	}

	@Override
	public RendererComponent getComponent(Frames component) {
		FramesAnimationComponent frames = engine
				.createComponent(FramesAnimationComponent.class);
		for (Frame f : component.getFrames()) {
			RendererComponent renderer = (RendererComponent) entitiesLoader
					.getComponent(f.getRenderer());
			frames.addFrame(renderer, f.getTime());
		}
		return frames;
	}
}
