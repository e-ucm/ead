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
package es.eucm.ead.editor.platform;

import com.badlogic.gdx.utils.Array;

public interface DeviceVideoControl {
	/* RECORDER */
	/**
	 * Recording profile. Equals {@link CamcorderProfile#QUALITY_1080P}.
	 */
	String P1080 = "1080p";
	/**
	 * Recording profile. Equals {@link CamcorderProfile#QUALITY_720P}.
	 */
	String P720 = "720p";
	/**
	 * Recording profile. Equals {@link CamcorderProfile#QUALITY_480P}.
	 */
	String P480 = "480p";

	/**
	 * Prepares video preview asynchronously.
	 */
	void prepareVideoAsynk();

	/**
	 * Stops video preview asynchronously.
	 */
	void stopPreviewAsynk();

	/**
	 * Starts recording.
	 * 
	 * @param path
	 *            where the video will be saved.
	 * @param listener
	 *            optional (can be null) parameter that will be notified when
	 *            the device successfully started recording via
	 *            {@link RecordingListener#onVideoStartedRecording(boolean)}.
	 */
	void startRecording(String path, RecordingListener listener);

	/**
	 * Stops recording.
	 * 
	 * @param listener
	 *            optional (can be null) parameter that will be notified when
	 *            the device successfully finished recording via
	 *            {@link RecordingListener#onVideoFinishedRecording(boolean)}.
	 */
	void stopRecording(RecordingListener listener);

	/**
	 * Sets the profile that will be used to record the video.
	 * 
	 * @param profile
	 *            can be either {@link DeviceVideoControl#P480},
	 *            {@link DeviceVideoControl#P720} or
	 *            {@link DeviceVideoControl#P1080}.
	 */
	void setRecordingProfile(String profile);

	/**
	 * @return the available recording qualities.
	 */
	Array<String> getQualities();

	/**
	 * @return the profile used currently for recording.
	 */
	String getCurrentProfile();

	/**
	 * @return true if the device is recording, false otherwise.
	 */
	boolean isRecording();

	interface RecordingListener {

		/**
		 * Invoked when the device successfully started recording.
		 * 
		 * @param success
		 */
		void onVideoStartedRecording(boolean success);

		/**
		 * Invoked when the device successfully finished recording.
		 * 
		 * @param success
		 */
		void onVideoFinishedRecording(boolean success);
	}

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
