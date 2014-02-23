package es.eucm.ead.android.video;

import java.util.List;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoSurface extends SurfaceView {

	private final VideoSurfaceCallback callback;

	public VideoSurface(Context context) {
		super(context);
		this.callback = new VideoSurfaceCallback(this);

		// We're implementing the Callback interface and want to get notified
		// about certain surface events.
		SurfaceHolder sh = getHolder();
		sh.addCallback(this.callback);
		// We're changing the surface to a PUSH surface, meaning we're receiving
		// all buffer data from another component - the camera, in this case.
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void startRecording() {
		this.callback.startRecording();
	}

	public void stopRecording() {
		this.callback.stopRecording();
	}

	public boolean isRecording() {
		return this.callback.isRecording();
	}

	public List<String> getQualities() {
		return this.callback.setUpSupportedProfiles();
	}
}