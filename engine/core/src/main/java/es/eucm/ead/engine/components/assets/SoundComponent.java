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
package es.eucm.ead.engine.components.assets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.assets.MediaResourcesLoader;

/**
 * Sound playback for elements
 */
public class SoundComponent extends Component implements Pool.Poolable {

	// exactly ONE of these will be active
	private Sound sound;
	private Music music;

	// only active if sound != null (and therefore, music == null)
	private long soundId;
	private long soundStart;

	private boolean started;
	private boolean finished;

	private es.eucm.ead.schema.assets.Sound config;

	public void setConfig(es.eucm.ead.schema.assets.Sound config) {
		this.config = config;
	}

	public es.eucm.ead.schema.assets.Sound getConfig() {
		return config;
	}

	public synchronized void setMusic(Music music) {
		this.music = music;
	}

	public synchronized void setSound(Sound sound) {
		this.sound = sound;
	}

	/**
	 * @return true if play has already been called.
	 */
	public synchronized boolean isStarted() {
		return started;
	}

	public synchronized boolean isLoaded() {
		return music != null || sound != null;
	}

	/**
	 * @return true if finished. Finished sounds can be removed & recycled.
	 */
	public synchronized boolean isFinished() {
		if (!finished) {
			// there is no API for LibGDX sound-effect-finished; assume 2s
			// duration
			if (sound != null) {
				if (!config.isLoop()
						&& TimeUtils.timeSinceMillis(soundStart) > 2000.0f) {
					finished = true;
				}
			} else if (music != null) {
				finished = !music.isPlaying();
			}
		}
		return finished;
	}

	/**
	 * Plays the sound at a given absolute volume.
	 * 
	 * @param volume
	 *            absolute volume.
	 */
	public synchronized void play(float volume) {
		if (sound != null) {
			soundId = sound.play();
			soundStart = TimeUtils.millis();
			sound.setVolume(soundId, volume);
			sound.setLooping(soundId, config.isLoop());
			started = true;
		} else if (music != null) {
			try {
				music.play();
				music.setVolume(volume);
				music.setLooping(config.isLoop());
				started = true;
			} catch (GdxRuntimeException gre) {
				// now debugging
				Gdx.app.log("Playing back music", config.getUri(), gre);
			}
		} else {
			throw new IllegalStateException(
					"SoundComponent contains neither sound nor music");
		}
	}

	/**
	 * Modifies the absolute volume of this sound. May do nothing (if sound has
	 * already finished).
	 * 
	 * @param volume
	 *            absolute volume.
	 */
	public synchronized void changeVolume(float volume) {
		if (!started || finished) {
			return;
		}

		if (sound != null) {
			sound.setVolume(soundId, volume);
		} else if (music != null) {
			music.setVolume(volume);
		}
	}

	/**
	 * Explicitly allow other effects to be started on this same component,
	 * replacing the currently-running effect. This ensures that all sound
	 * resources are properly freed.
	 * 
	 * @param other
	 *            sound to replace current sound with.
	 * @return
	 */
	@Override
	public synchronized boolean combine(Component other) {
		this.reset();
		SoundComponent otherSound = (SoundComponent) other;
		if (otherSound.sound != null) {
			this.sound = otherSound.sound;
			this.soundId = otherSound.soundId;
			this.soundStart = otherSound.soundStart;
		} else if (otherSound.music != null) {
			this.music = otherSound.music;
		}
		this.config = otherSound.config;
		this.finished = otherSound.finished;
		this.started = otherSound.started;
		return true;
	}

	@Override
	public synchronized void reset() {
		started = finished = false;
		config = null;
		if (sound != null) {
			sound.stop(soundId);
			sound = null;
		} else if (music != null) {
			music.stop();
			music = null;
		}
	}

	public synchronized void checkIfLoaded(GameAssets gameAssets) {
		if (config == null || config.getUri() == null) {
			return;
		}
		if (gameAssets.isLoaded(config.getUri())) {
			try {
				sound = gameAssets.get(config.getUri(),
						com.badlogic.gdx.audio.Sound.class);
			} catch (Exception e) {
				try {
					music = gameAssets.get(config.getUri(),
							com.badlogic.gdx.audio.Music.class);
				} catch (Exception e2) {

				}
			}
		}
	}
}
