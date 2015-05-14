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

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.spine.SkeletonData;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.components.renderers.SpineActor;
import es.eucm.ead.schema.renderers.SpineAnimation;

public class SpineProcessor extends RendererProcessor<SpineAnimation> {

	public SpineProcessor(GameLoop gameLoop, GameAssets gameAssets) {
		super(gameLoop, gameAssets);
	}

	@Override
	public Component getComponent(final SpineAnimation spineAnimation) {
		String baseUri = spineAnimation.getUri();
		final SpineActor actor = createActor();
		gameAssets.get(baseUri, SkeletonData.class,
				new AssetLoadedCallback<SkeletonData>() {
					@Override
					public void loaded(String fileName, SkeletonData asset) {
						actor.setSkeleton(asset);
						actor.setState(spineAnimation.getInitialState());
					}

					@Override
					public void error(String fileName, Class type,
							Throwable exception) {
						Gdx.app.error("SpineAnimationProcessor",
								"Impossible to load animation", exception);
					}
				});
		RendererComponent rendererComponent = gameLoop
				.createComponent(RendererComponent.class);
		rendererComponent.setRenderer(actor);
		return rendererComponent;
	}

	protected SpineActor createActor() {
		return new SpineActor();
	}
}
