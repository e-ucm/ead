package es.eucm.ead.editor.video;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.platform.mockup.DeviceVideoControl;

public class DesktopDeviceVideoController implements DeviceVideoControl {

	@Override
	public void startRecording() {		
		Gdx.app.log("Video", "startRecording()");
	}

	@Override
	public void stopRecording() {	
		Gdx.app.log("Video", "stopRecording()");	
	}

	@Override
	public void startPlaying(int id) {
		Gdx.app.log("Video", "startPlaying()");		
	}

	@Override
	public boolean isRecording() {
		Gdx.app.log("Video", "isRecording()");
		return false;
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
	public List<String> getQualities() {
		List<String> qualities = new ArrayList<String>();
		qualities.add("480p");
		qualities.add("720p");
		qualities.add("1080p");
		return qualities;
	}

}
