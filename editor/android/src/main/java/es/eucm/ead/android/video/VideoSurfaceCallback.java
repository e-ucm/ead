package es.eucm.ead.android.video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.SurfaceHolder;

import com.badlogic.gdx.Gdx;

public class VideoSurfaceCallback implements SurfaceHolder.Callback {

	private static final String LOGTAG = "Video";
	private static final String P1080 = "1080p";
	private static final String P720 = "720p";
	private static final String P480 = "480p";
	private static final int MAX_RECORDING_DURATION = 120000;

	private final VideoSurface videoSurface;

	private CamcorderProfile mRecorderProfile;
	private MediaRecorder recorder;
	private List<String> qualities;
	private SurfaceHolder holder;
	private String auxVideoPath;
	private String rootPath;
	private Camera camera;
	boolean recording;

	public VideoSurfaceCallback(VideoSurface videoSurface) {
		this.videoSurface = videoSurface;
		setUpSupportedProfiles();
		this.recording = false;

	}

	public List<String> setUpSupportedProfiles() {
		this.qualities = new ArrayList<String>();
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
			this.qualities.add(P480);
		}
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
			this.qualities.add(P720);
		}
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
			this.qualities.add(P1080);
		}
		return this.qualities;
	}

	private CamcorderProfile getProfile() {
		CamcorderProfile prof = null;
		if (VideoScreen.QUALITY.equals(P480)) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		} else if (VideoScreen.QUALITY.equals(P720)) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		} else if (VideoScreen.QUALITY.equals(P1080)) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
		}

		if (prof == null) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
		}
		return prof;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		this.mRecorderProfile = getProfile();

		if (this.holder == null) {
			this.holder = holder;
		}

		if (this.recorder == null) {
			this.recorder = new MediaRecorder();
		}

		if (this.camera == null) {
			this.camera = getCameraInstance();
		}

		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {

			boolean supported = false;
			Camera.Parameters parameters = this.camera.getParameters();
			int h = this.mRecorderProfile.videoFrameHeight;
			int w = this.mRecorderProfile.videoFrameWidth;
			List<Size> suppPrevs = parameters.getSupportedPreviewSizes();
			for (Size s : suppPrevs) {
				if (s.width == w && s.height == h) {
					supported = true;
					break;
				}
			}
			if (supported) {
				parameters.setPreviewSize(w, h);
				this.camera.setParameters(parameters);
				Gdx.app.log(LOGTAG, "setting: " + w + "x" + h);
			} else {

				float ratio = w / Float.valueOf(h);
				for (Size s : suppPrevs) {
					float ratioS = s.width / Float.valueOf(s.height);
					if (ratioS == ratio) {
						Gdx.app.log(LOGTAG, "Another ratio found: " + s.width
								+ ", " + s.height);
						parameters.setPreviewSize(s.width, s.height);
						this.camera.setParameters(parameters);
						break;
					}
				}
			}

			setUpPreviewAspectRatio(w, h);
			this.camera.setPreviewDisplay(holder);
			this.camera.startPreview();
		} catch (IOException e) {
			Gdx.app.log(LOGTAG, "Setting previeww failed!", e);
		}
		Gdx.app.log(LOGTAG, "surfaceCreated");
	}

	private void setUpPreviewAspectRatio(int width, int height) {

		// Get the SurfaceView layout parameters
		android.view.ViewGroup.LayoutParams params = this.videoSurface
				.getLayoutParams();

		float viewportWidth = width;
		float viewportHeight = height;
		float viewPortAspect = viewportWidth / viewportHeight;
		float physicalWidth = Gdx.graphics.getWidth();
		float physicalHeight = Gdx.graphics.getHeight();
		float physicalAspect = physicalWidth / physicalHeight;

		if (physicalAspect < viewPortAspect) {
			viewportHeight = viewportHeight * (physicalWidth / viewportWidth);
			viewportWidth = physicalWidth;
		} else {
			viewportWidth = viewportWidth * (physicalHeight / viewportHeight);
			viewportHeight = physicalHeight;
		}

		params.width = (int) viewportWidth;
		params.height = (int) viewportHeight;

		this.videoSurface.setLayoutParams(params);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Gdx.app.log(LOGTAG, "surfaceChanged, width: " + width + ", height: "
				+ height);
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (holder.getSurface() == null) {
			// preview surface does not exist
			Gdx.app.error(LOGTAG, "Preview surface does not exist");
			return;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Gdx.app.log(LOGTAG, "surfaceDestroyed");

		if (this.recorder != null) {
			if (this.recording) {
				this.recorder.stop();
				this.recording = false;
			}
			this.recorder.release();
			this.recorder = null;
		}

		if (camera != null) {
			this.camera.release(); // release the camera for other applications
			this.camera = null;
		}
	}

	public void startRecording() {
		this.recording = true;

		if (!prepareMediaRecorder()) {
			Gdx.app.error(LOGTAG,
					"PrepareMediaRecorder() failed!\n - Try again -");
			return;
		}

		try {
			this.recorder.start();
			this.recording = true;
			Gdx.app.log(LOGTAG, "Recording Started");
		} catch (Exception ex) {
			Gdx.app.error(LOGTAG,
					"Exception trying to start recording, try again!", ex);
			this.recording = false;
		}
	}

	private boolean prepareMediaRecorder() {

		this.camera.unlock();
		this.recorder.setCamera(this.camera);

		this.recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		this.recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		this.recorder.setProfile(this.mRecorderProfile);

		int id = FileHandler.getVideoID() + 1;
		this.auxVideoPath = rootPath + id + ".mp4";
		this.recorder.setOutputFile(auxVideoPath);
		this.recorder.setMaxDuration(MAX_RECORDING_DURATION);

		this.recorder.setPreviewDisplay(this.holder.getSurface());

		try {
			this.recorder.prepare();
		} catch (IllegalStateException ille) {
			Gdx.app.error(LOGTAG,
					"Illegal State Exception preparing recorder!", ille);
			surfaceDestroyed(null);
			return false;
		} catch (IOException ioex) {
			Gdx.app.error(LOGTAG, "IOException preparing recorder!", ioex);
			surfaceDestroyed(null);
			return false;
		}
		return true;
	}

	private Camera getCameraInstance() {
		Camera cam = null;
		try {
			// attempt to get a Camera instance
			cam = Camera.open(CameraInfo.CAMERA_FACING_BACK);
		} catch (Exception ex) {
			// Camera is not available (in use or does not exist)
			Gdx.app.error(LOGTAG, "Exception opening camera!", ex);
		}
		// returns null if camera is unavailable
		return cam;
	}

	public void stopRecording() {
		// stop recording and release camera
		this.recorder.stop();

		int vidID = FileHandler.getVideoID() + 1;
		String num = String.valueOf(vidID) + ".jpg";

		String thumbPath = FileHandler.getVideoThumbnailsFileHandle().file()
				.getAbsolutePath()
				+ File.separator;

		String MINI_KIND = thumbPath + "VideoThumbnail" + num;

		try {
			OutputStream fos = null;

			// MINI_KIND Thumbnail 96x96
			Bitmap bmMiniKind = ThumbnailUtils.createVideoThumbnail(
					this.auxVideoPath, Thumbnails.MINI_KIND);

			if (bmMiniKind == null) {
				Gdx.app.error(LOGTAG,
						"Video corrupt or format not supported! (MINI_KIND)");
				return;
			}

			fos = new FileOutputStream(new File(MINI_KIND));
			bmMiniKind.compress(Bitmap.CompressFormat.JPEG, 95, fos);
			fos.flush();
			fos.close();
			if (bmMiniKind != null) {
				bmMiniKind.recycle();
				bmMiniKind = null;
			}
			fos = null;

			Gdx.app.log(LOGTAG, "Recording Stopped");
		} catch (Exception ex) {
			// Something went wrong creating the Video Thumbnail
			Gdx.app.error(LOGTAG,
					"Something went wrong creating the Video Thumbnail", ex);
		}
		this.recording = false;
	}

	public boolean isRecording() {
		return this.recording;
	}
}
