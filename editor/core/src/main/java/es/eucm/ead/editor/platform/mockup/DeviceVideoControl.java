package es.eucm.ead.editor.platform.mockup;

import java.util.List;

public interface DeviceVideoControl {
	/* RECORDER */
	void prepareVideoAsynk();

	void stopPreviewAsynk();

	void startRecording();

	void stopRecording();

	boolean isRecording();

	/* PLAYER */
	void startPlaying(int videoID);

	boolean isPlaying();

	void setOnCompletionListener(CompletionListener listener);

	List<String> getQualities();

	interface CompletionListener {
		/**
		 * Fired when the video has completed.
		 */
		public void onCompletion();
	}
}
