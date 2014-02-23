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
package es.eucm.ead.editor.video;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.platform.mockup.DeviceVideoControl;

public class DesktopDeviceVideoController implements DeviceVideoControl {

	private boolean recording = false;

	@Override
	public void startRecording(String path) {
		Gdx.app.log("Video", "startRecording() " + path);
		this.recording = true;
	}

	@Override
	public void stopRecording() {
		Gdx.app.log("Video", "stopRecording()");
		this.recording = false;
	}

	@Override
	public void startPlaying(int id) {
		Gdx.app.log("Video", "startPlaying()");
	}

	@Override
	public boolean isRecording() {
		Gdx.app.log("Video", "isRecording()");
		return this.recording;
	}

	@Override
	public boolean isPlaying() {
		Gdx.app.log("Video", "isPlaying()");
		return false;
	}

	@Override
	public void prepareVideoAsynk() {
		Gdx.app.log("Video", "prepareVideoAsynk()");
	}

	@Override
	public void stopPreviewAsynk() {
		Gdx.app.log("Video", "startRecording()");
	}

	@Override
	public void setOnCompletionListener(CompletionListener listener) {
		Gdx.app.log("Video", "setOnCompletionListener()");
	}

	@Override
	public Array<String> getQualities() {
		Gdx.app.log("Video", "getQualities()");
		Array<String> qualities = new Array<String>(false, 3);
		qualities.add(P480);
		qualities.add(P720);
		qualities.add(P1080);
		return qualities;
	}

	@Override
	public void setRecordingProfile(String profile) {
		Gdx.app.log("Video", "setRecordingProfile() " + profile);
	}

	@Override
	public String getCurrentProfile() {
		return P480;
	}

}
