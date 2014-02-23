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
package es.eucm.ead.android.video;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.android.EditorActivity;
import es.eucm.ead.editor.platform.mockup.DeviceVideoControl;

public class AndroidDeviceVideoController implements DeviceVideoControl {

	private static final String LOGTAG = "Video";

	private final Runnable mPrepareVideoAsynkRunnable;
	private final Runnable mStopPreviewAsynkRunnable;
	private final RelativeLayout previewLayout;
	private final LayoutParams mLayoutParams;
	private final VideoSurface videoSurface;
	private final EditorActivity activity;
	private final MiVideoPlayer mPlayer;

	private boolean playing;
	private int videoID;

	public AndroidDeviceVideoController(EditorActivity activity) {
		this.previewLayout = new RelativeLayout(activity);
		this.activity = activity;
		this.mPlayer = new MiVideoPlayer();
		this.videoSurface = new VideoSurface(activity);
		this.mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.mPrepareVideoAsynkRunnable = new Runnable() {
			@Override
			public void run() {
				prepareVideo();
			}
		};
		this.mStopPreviewAsynkRunnable = new Runnable() {
			@Override
			public void run() {
				stopRemoveViewFromParent();
			}
		};
		final RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		videoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.previewLayout.setGravity(Gravity.CENTER);
		this.previewLayout.addView(this.videoSurface, videoParams);
	}

	/* RECORDER */
	@Override
	public void prepareVideoAsynk() {
		Gdx.app.log(LOGTAG, "prepareVideoAsynk");
		this.activity.post(this.mPrepareVideoAsynkRunnable);
	}

	@Override
	public void stopPreviewAsynk() {
		Gdx.app.log(LOGTAG, "stopPreviewAsynk");
		this.activity.post(this.mStopPreviewAsynkRunnable);
	}

	@Override
	public void startRecording(String path) {
		Gdx.app.log(LOGTAG, "startRecording " + path);
		this.videoSurface.startRecording(path);
	}

	@Override
	public void stopRecording() {
		Gdx.app.log(LOGTAG, "stopRecording");
		this.videoSurface.stopRecording();
	}

	@Override
	public boolean isRecording() {
		return this.videoSurface.isRecording();
	}

	private synchronized void prepareVideo() {
		this.activity.addContentView(this.previewLayout, this.mLayoutParams);
	}

	private synchronized void stopRemoveViewFromParent() {
		// stop previewing.
		ViewParent parentView = this.previewLayout.getParent();
		if (parentView instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) parentView;
			viewGroup.removeView(this.previewLayout);
		}
		if (isRecording()) {
			stopRecording();
		}
		if (isPlaying()) {
			this.mPlayer.stopAndRemoveView();
		}
	}

	/* PLAYER */
	@Override
	public void startPlaying(int vidID) {
		Gdx.app.log(LOGTAG, "startPlaying " + vidID);
		this.videoID = vidID;
		this.playing = true;
		this.activity.post(this.mPlayer);
	}

	private class MiVideoPlayer implements Runnable {

		private final VideoView playingVideoView;
		private final RelativeLayout layout;
		private final String rootPath;

		private CompletionListener mListener;

		public MiVideoPlayer() {
			this.playingVideoView = new VideoView(
					AndroidDeviceVideoController.this.activity);
			this.layout = new RelativeLayout(
					AndroidDeviceVideoController.this.activity);
			final RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			videoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			this.layout.addView(playingVideoView, videoParams);
			this.playingVideoView
					.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer arg0) {
							stopAndRemoveView();
							if (MiVideoPlayer.this.mListener != null) {
								MiVideoPlayer.this.mListener.onCompletion();
							}
						}
					});
			this.rootPath = "file://"
					+ Environment.getExternalStorageDirectory()
					+ "/Slideshow/Videos/Video";
		}

		@Override
		public void run() {
			this.playingVideoView.setVideoURI(Uri.parse(this.rootPath
					+ AndroidDeviceVideoController.this.videoID + ".mp4"));
			AndroidDeviceVideoController.this.activity.addContentView(
					this.layout,
					AndroidDeviceVideoController.this.mLayoutParams);
			this.playingVideoView.start();
		}

		private boolean isPlaying() {
			return this.playingVideoView.isPlaying();
		}

		private void stopAndRemoveView() {
			this.playingVideoView.stopPlayback();
			ViewParent parentView = this.layout.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(this.layout);
			}
			AndroidDeviceVideoController.this.playing = false;
		}

		private void setOnCompletionListener(CompletionListener listener) {
			this.mListener = listener;
		}
	}

	@Override
	public boolean isPlaying() {
		return this.playing || this.mPlayer.isPlaying();
	}

	@Override
	public void setOnCompletionListener(CompletionListener listener) {
		this.mPlayer.setOnCompletionListener(listener);
	}

	@Override
	public Array<String> getQualities() {
		return AndroidDeviceVideoController.this.videoSurface.getQualities();
	}

	@Override
	public void setRecordingProfile(String profile) {
		Gdx.app.log(LOGTAG, "setRecordingProfile " + profile);
		this.videoSurface.setRecordingProfile(profile);
	}

	@Override
	public String getCurrentProfile() {
		String currProf = this.videoSurface.getCurrentProfile();
		Gdx.app.log(LOGTAG, "getCurrentProfile " + currProf);
		return currProf;
	}
}