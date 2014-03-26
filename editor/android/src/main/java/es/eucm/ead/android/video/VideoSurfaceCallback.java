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
package es.eucm.ead.android.video;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.android.platform.DeviceVideoControl;
import es.eucm.ead.android.platform.DeviceVideoControl.RecordingListener;

public class VideoSurfaceCallback implements SurfaceHolder.Callback {

	/**
	 * 3 minutes.
	 */
	private static final int MAX_RECORDING_DURATION = 180000;
	private static final long VIDEO_PREVIEW_TIME = 1100;
	private static final String VIDEO_THUMBNAIL_ID = "videothumbnail.jpg";
	private static final String VIDEO_ID = "video.mp4";
	private static final String VIDEO_LOGTAG = "Video";

	private final VideoSurface videoSurface;

	private CamcorderProfile mRecorderProfile;
	private MediaRecorder recorder;
	private Array<String> qualities;
	private String currentProfile;
	private SurfaceHolder holder;
	private String auxVideoPath;
	private Camera camera;
	boolean recording;

	public VideoSurfaceCallback(VideoSurface videoSurface) {
		this.videoSurface = videoSurface;
		setUpSupportedProfiles();
		this.recording = false;
	}

	private void setUpSupportedProfiles() {
		this.qualities = new Array<String>(false, 3);
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
			this.qualities.add(DeviceVideoControl.P1080);
		}
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
			this.qualities.add(DeviceVideoControl.P720);
		}
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
			this.qualities.add(DeviceVideoControl.P480);
		}
		this.currentProfile = this.qualities.size == 0 ? "" : this.qualities
				.first();
	}

	private CamcorderProfile getProfile() {
		CamcorderProfile prof = null;
		if (this.currentProfile.equals(DeviceVideoControl.P480)) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		} else if (this.currentProfile.equals(DeviceVideoControl.P720)) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		} else if (this.currentProfile.equals(DeviceVideoControl.P1080)) {
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
		} else {
			Gdx.app.log(VIDEO_LOGTAG,
					"Current profile is inconsistent, this should never happen! "
							+ this.currentProfile);
			prof = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
		}
		return prof;
	}

	@Override
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
			if (this.camera == null) {
				Gdx.app.error(VIDEO_LOGTAG, "Error opening camera");
				return;
			}
		}

		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {

			boolean supported = false;
			final Camera.Parameters parameters = this.camera.getParameters();
			int profileHeight = this.mRecorderProfile.videoFrameHeight;
			int profileWidth = this.mRecorderProfile.videoFrameWidth;
			final List<Size> suppPrevs = parameters.getSupportedPreviewSizes();
			for (final Size currSize : suppPrevs) {
				if (currSize.width == profileWidth
						&& currSize.height == profileHeight) {
					supported = true;
					break;
				}
			}
			if (supported) {
				parameters.setPreviewSize(profileWidth, profileHeight);
				this.camera.setParameters(parameters);
				Gdx.app.log(VIDEO_LOGTAG, "setting preview size: "
						+ profileWidth + "x" + profileHeight);
			} else {
				final float profileAspectRatio = profileWidth
						/ Float.valueOf(profileHeight);
				for (final Size currSize : suppPrevs) {
					final float currAspectRatio = currSize.width
							/ Float.valueOf(currSize.height);
					if (currAspectRatio == profileAspectRatio) {
						profileWidth = currSize.width;
						profileHeight = currSize.height;
						parameters.setPreviewSize(profileWidth, profileHeight);
						this.camera.setParameters(parameters);
						Gdx.app.log(VIDEO_LOGTAG,
								"Another aspect ratio found: " + profileWidth
										+ "x" + profileHeight);
						break;
					}
				}
			}

			setUpPreviewAspectRatio(profileWidth, profileHeight);
			this.camera.setPreviewDisplay(holder);
			this.camera.startPreview();
		} catch (final IOException e) {
			Gdx.app.log(VIDEO_LOGTAG, "Setting preview failed!", e);
		}
		Gdx.app.log(VIDEO_LOGTAG, "surfaceCreated");
	}

	private void setUpPreviewAspectRatio(int width, int height) {
		// Get the SurfaceView layout parameters
		final android.view.ViewGroup.LayoutParams params = this.videoSurface
				.getLayoutParams();

		float viewportWidth = width;
		float viewportHeight = height;
		final float viewPortAspect = viewportWidth / viewportHeight;
		final float physicalWidth = Gdx.graphics.getWidth();
		final float physicalHeight = Gdx.graphics.getHeight();
		final float physicalAspect = physicalWidth / physicalHeight;

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

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Gdx.app.log(VIDEO_LOGTAG, "surfaceChanged, " + width + "x" + height);
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (holder.getSurface() == null) {
			// preview surface does not exist
			Gdx.app.error(VIDEO_LOGTAG, "Preview surface does not exist");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Gdx.app.log(VIDEO_LOGTAG, "surfaceDestroyed");

		releaseRecorder();

		if (this.camera != null) {
			// Release the camera for other applications
			this.camera.release();
			this.camera = null;
		}
	}

	private void releaseRecorder() {
		if (this.recorder != null) {
			if (this.recording) {
				this.recorder.stop();
				this.recording = false;
			}
			this.recorder.release();
			this.recorder = null;
		}
	}

	public void startRecording(String path, RecordingListener listener) {
		if (this.recording) {
			if (listener != null) {
				listener.onVideoStartedRecording(false);
			}
			waitPreviewTime();
			return;
		}
		if (!prepareMediaRecorder(path)) {
			Gdx.app.error(VIDEO_LOGTAG,
					"PrepareMediaRecorder() failed!\n - Try again -");
			this.recording = false;
			if (listener != null) {
				listener.onVideoStartedRecording(false);
			}
			waitPreviewTime();
			return;
		}

		try {
			this.recorder.start();
			this.recording = true;
			Gdx.app.log(VIDEO_LOGTAG, "Recording Started");
		} catch (final Exception ex) {
			this.recording = false;
			Gdx.app.error(VIDEO_LOGTAG,
					"Exception trying to start recording, try again!", ex);
		}
		if (listener != null) {
			listener.onVideoStartedRecording(this.recording);
		}
		waitPreviewTime();
	}

	private boolean prepareMediaRecorder(String path) {

		this.camera.unlock();
		try {
			this.recorder.setCamera(this.camera);
		} catch (final Exception ex) {
			Gdx.app.error(VIDEO_LOGTAG, "Setting camera failed!", ex);
			return false;
		}
		this.recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		this.recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		this.recorder.setProfile(this.mRecorderProfile);

		final FileHandle rootPathHandle = Gdx.files.absolute(path);
		if (!rootPathHandle.exists()) {
			rootPathHandle.mkdirs();
		}
		final int id = 1 + rootPathHandle.list().length;
		this.auxVideoPath = path + File.separator + id + File.separator;
		final FileHandle videoPathHandle = Gdx.files
				.absolute(this.auxVideoPath);
		if (!videoPathHandle.exists()) {
			videoPathHandle.mkdirs();
		}
		this.recorder.setOutputFile(this.auxVideoPath + VIDEO_ID);
		this.recorder.setMaxDuration(MAX_RECORDING_DURATION);

		this.recorder.setPreviewDisplay(this.holder.getSurface());

		try {
			this.recorder.prepare();
		} catch (final IllegalStateException ille) {
			Gdx.app.error(VIDEO_LOGTAG,
					"Illegal State Exception preparing recorder!", ille);
			releaseRecorder();
			return false;
		} catch (final IOException ioex) {
			Gdx.app.error(VIDEO_LOGTAG, "IO Exception preparing recorder!",
					ioex);
			releaseRecorder();
			return false;
		}
		return true;
	}

	private Camera getCameraInstance() {
		Camera cam = null;
		try {
			// Attempt to get a Camera instance
			cam = Camera.open(CameraInfo.CAMERA_FACING_BACK);
		} catch (final Exception ex) {
			// Camera is not available (in use or does not exist)
			Gdx.app.error(VIDEO_LOGTAG, "Exception opening camera!", ex);
		}
		// Returns null if camera is unavailable
		return cam;
	}

	public void stopRecording(DeviceVideoControl.RecordingListener listener) {
		if (!this.recording && listener != null) {
			listener.onVideoFinishedRecording(false);
			waitPreviewTime();
			return;
		}
		// Stop recording and release camera
		try {
			this.recorder.stop();
		} catch (final Exception ex) {
			Gdx.app.error(VIDEO_LOGTAG, "Stop failed!", ex);
			this.recording = false;
			if (listener != null) {
				listener.onVideoFinishedRecording(false);
			}
			waitPreviewTime();
			return;
		}
		final String thumbPath = this.auxVideoPath;

		final String miniKingPath = thumbPath + VIDEO_THUMBNAIL_ID;

		OutputStream thumbnailFos = null;
		try {
			// MINI_KIND Thumbnail
			Bitmap bmMiniKind = ThumbnailUtils.createVideoThumbnail(
					this.auxVideoPath + VIDEO_ID, Thumbnails.MINI_KIND);

			if (bmMiniKind == null) {
				Gdx.app.error(VIDEO_LOGTAG,
						"Video corrupt or format not supported! (MINI_KIND)");
				this.recording = false;
				if (listener != null) {
					listener.onVideoFinishedRecording(false);
				}
				waitPreviewTime();
				return;
			}

			thumbnailFos = new FileOutputStream(new File(miniKingPath));
			bmMiniKind.compress(Bitmap.CompressFormat.JPEG, 90, thumbnailFos);
			thumbnailFos.flush();

			if (bmMiniKind != null) {
				bmMiniKind.recycle();
				bmMiniKind = null;
			}

			if (listener != null) {
				listener.onVideoFinishedRecording(true);
			}
			Gdx.app.log(VIDEO_LOGTAG,
					"Recording stopped, video thumbnail saved!");
		} catch (final FileNotFoundException fnfex) {
			if (listener != null) {
				listener.onVideoFinishedRecording(false);
			}
			Gdx.app.error("Picture",
					"File not found creating the video thumbnail", fnfex);
		} catch (final IOException ioex) {
			// Something went wrong creating the video thumbnail
			if (listener != null) {
				listener.onVideoFinishedRecording(false);
			}
			Gdx.app.error(VIDEO_LOGTAG,
					"Something went wrong creating the Video Thumbnail", ioex);
		} finally {
			close(thumbnailFos);
		}
		waitPreviewTime();
		this.recording = false;
	}

	private void waitPreviewTime() {
		try {
			Thread.sleep(VIDEO_PREVIEW_TIME);
		} catch (final InterruptedException ie) {
			Gdx.app.log(VIDEO_LOGTAG,
					"Picture thread interrupted while sleeping!", ie);
		}
	}

	private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (final Exception ex) {
				Gdx.app.log(
						VIDEO_LOGTAG,
						"Something went wrong closing the stream "
								+ closeable.toString(), ex);
				// Ignore ...
				// any significant errors should already have been
				// reported via an IOException from the final flush.
			}
			closeable = null;
		}
	}

	public boolean isRecording() {
		return this.recording;
	}

	public void setRecordingProfile(String profile) {
		this.currentProfile = profile;
	}

	public String getCurrentProfile() {
		return this.currentProfile;
	}

	public Array<String> getQualities() {
		return this.qualities;
	}
}
