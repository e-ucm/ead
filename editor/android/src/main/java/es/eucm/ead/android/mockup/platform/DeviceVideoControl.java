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
package es.eucm.ead.android.mockup.platform;

import com.badlogic.gdx.utils.Array;

public interface DeviceVideoControl {
	/* RECORDER */
	String P1080 = "1080p";
	String P720 = "720p";
	String P480 = "480p";

	void prepareVideoAsynk();

	void stopPreviewAsynk();

	void startRecording(String path);

	void stopRecording();

	void setRecordingProfile(String profile);

	Array<String> getQualities();

	String getCurrentProfile();

	boolean isRecording();

	/* PLAYER */
	void startPlaying(int videoID);

	boolean isPlaying();

	void setOnCompletionListener(CompletionListener listener);

	interface CompletionListener {
		/**
		 * Fired when the video has completed.
		 */
		public void onCompletion();
	}
}
