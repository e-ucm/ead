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
package es.eucm.ead.engine.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.effects.video.VLCPlayer;

public class VideoEngineObject extends AbstractVideoEngineObject {

	private static VLCPlayer vlcPlayer;

	@Override
	protected void play(String uri, boolean skippable) {
		if (vlcPlayer == null) {
			vlcPlayer = new VLCPlayer();
		}
		FileHandle fh = gameLoop.getAssets().resolve(uri);
		if (fh.exists()) {
			vlcPlayer.play(this, fh, skippable);
		} else {
			Gdx.app.error("VideoAction", "Video file '" + uri
					+ "' doesn't exist.");
		}
	}

	/**
	 * Makes sure that if vlc was initialized, then all native resources it
	 * loaded are disposed. Should be invoked only as a consequence of a
	 * dispose() or exit() See VLCPlayer.release for more details.
	 */
	public static void release() {
		if (vlcPlayer != null) {
			Gdx.app.log(
					"VideoAction",
					"The VLC Component was created. Trying to release its resources (invoking VLCPLayer.release()...");
			vlcPlayer.release();
		} else {
			Gdx.app.log("VideoAction",
					"The VLC Component was not created. No resources to release");
		}
	}
}
