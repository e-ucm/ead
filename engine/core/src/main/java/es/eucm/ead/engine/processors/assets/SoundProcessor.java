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

import ashley.core.Component;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.assets.SoundComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.assets.Sound;

public class SoundProcessor extends ComponentProcessor<Sound> {
	protected GameAssets gameAssets;

	private static final long MAX_COMPRESSED_SOUND_SIZE = 15 * 1024;

	public SoundProcessor(GameLoop engine, GameAssets gameAssets) {
		super(engine);
		this.gameAssets = gameAssets;
	}

	@Override
	public Component getComponent(Sound sound) {
		final SoundComponent soundComponent = createComponent();

		// files over 150k should be streamed as music; asume ~ 10x compression
		if (gameAssets.resolve(sound.getUri()).length() < MAX_COMPRESSED_SOUND_SIZE) {
			gameAssets.get(sound.getUri(), com.badlogic.gdx.audio.Sound.class,
					new AssetLoadedCallback<com.badlogic.gdx.audio.Sound>() {
						@Override
						public void loaded(String fileName,
								com.badlogic.gdx.audio.Sound asset) {
							soundComponent.setSound(asset);
						}
					});
		} else {
			gameAssets.get(sound.getUri(), com.badlogic.gdx.audio.Music.class,
					new AssetLoadedCallback<com.badlogic.gdx.audio.Music>() {
						@Override
						public void loaded(String fileName,
								com.badlogic.gdx.audio.Music asset) {
							soundComponent.setMusic(asset);
						}
					});
		}

		soundComponent.setConfig(sound);
		return soundComponent;
	}

	protected SoundComponent createComponent() {
		return gameLoop.createComponent(SoundComponent.class);
	}
}
