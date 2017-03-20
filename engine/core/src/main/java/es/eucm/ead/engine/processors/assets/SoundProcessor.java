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
package es.eucm.ead.engine.processors.assets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.assets.MediaResourcesLoader;
import es.eucm.ead.engine.components.assets.SoundComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.assets.Sound;

public class SoundProcessor extends ComponentProcessor<Sound> {
	public static final String LOG_TAG = "SoundProcessor";
	protected GameAssets gameAssets;

	public SoundProcessor(GameLoop engine, GameAssets gameAssets) {
		super(engine);
		this.gameAssets = gameAssets;
	}

	@Override
	public Component getComponent(Sound sound) {
		final SoundComponent soundComponent = createComponent();
		MediaResourcesLoader.loadAudio(sound.getUri(), gameAssets,
				new AssetLoadedCallback() {
					@Override
					public void loaded(String fileName, Object asset) {
						if (asset instanceof com.badlogic.gdx.audio.Sound) {
							soundComponent
									.setSound((com.badlogic.gdx.audio.Sound) asset);
						} else if (asset instanceof com.badlogic.gdx.audio.Music) {
							soundComponent
									.setMusic((com.badlogic.gdx.audio.Music) asset);
						}
					}

					@Override
					public void error(String fileName, Class type,
							Throwable exception) {
						if (type == com.badlogic.gdx.audio.Sound.class) {
							Gdx.app.error(LOG_TAG, "Impossible to play sound "
									+ fileName, exception);
						} else if (type == com.badlogic.gdx.audio.Music.class) {
							Gdx.app.error(LOG_TAG, "Impossible to play music "
									+ fileName, exception);
						}
					}
				});

		soundComponent.setConfig(sound);
		return soundComponent;
	}

	protected SoundComponent createComponent() {
		return gameLoop.createComponent(SoundComponent.class);
	}
}
