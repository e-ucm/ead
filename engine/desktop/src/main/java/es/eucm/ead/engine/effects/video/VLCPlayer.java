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
package es.eucm.ead.engine.effects.video;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.effects.VideoEngineObject;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class VLCPlayer {

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	private VideoEngineObject videoAction;

	private Canvas videoSurface;

	private Canvas gameSurface;

	private EmbeddedMediaPlayer mediaPlayer;

	private Map<String, String> tempVideos;

	private boolean skip;

	private boolean vlcNotInstalled;

	public VLCPlayer() {
		tempVideos = new HashMap<String, String>();

		// VLC initialization
		try {
			System.setProperty("jna.nosys", "true");
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
			videoSurface = mediaPlayerComponent.getVideoSurface();
			mediaPlayer = mediaPlayerComponent.getMediaPlayer();
			gameSurface = EngineDesktop.frame.getLwjglCanvas().getCanvas();

			mediaPlayer.setEnableKeyInputHandling(false);
			videoSurface.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						end();
					}
				}
			});

			mediaPlayer
					.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
						@Override
						public void finished(MediaPlayer mediaPlayer) {
							Gdx.app.debug("Video", "Video finished.");
							skip = true;
							end();
						}

						@Override
						public void error(MediaPlayer mediaPlayer) {
							Gdx.app.error("Video",
									"An error ocurred while playing video.");
							skip = true;
							end();
						}
					});
			vlcNotInstalled = false;
		} catch (Exception e) {
			Gdx.app.error(
					"VLCPlayer",
					"VLC is not installed. All video's in this game will be skipped.",
					e);
			vlcNotInstalled = true;
		}
	}

	/**
	 * Plays the video
	 * 
	 * @param videoAction
	 *            the action launching the video
	 * @param fh
	 *            file handle of the video file
	 * @param skippable
	 *            if the video can be skipped
	 */
	public void play(VideoEngineObject videoAction, FileHandle fh,
			boolean skippable) {
		if (vlcNotInstalled) {
			Gdx.app.error("VLCPlayer", "VLC not installed. Video " + fh.name()
					+ " was skipped.");
			return;
		}

		String mlr = copyVideoToTemp(fh);
		Gdx.app.debug("Video", "Playing video " + mlr);
		this.skip = !skippable;
		this.videoAction = videoAction;
		EngineDesktop.frame.add(mediaPlayerComponent);
		gameSurface.setVisible(false);
		videoSurface.requestFocus();
		mediaPlayer.playMedia(mlr);
	}

	/** Finish the video **/
	public void end() {
		if (skip) {
			Gdx.app.debug("Video", "Video ended.");
			mediaPlayerComponent.getMediaPlayer().stop();
			videoAction.end();
			gameSurface.requestFocus();
			gameSurface.setVisible(true);
		}
	}

	/**
	 * Extracts the video in the given file handle to a temp file
	 * 
	 * @param fileHandle
	 *            the video file handle
	 * @return the temp file path
	 */
	private String copyVideoToTemp(FileHandle fileHandle) {
		String destiny = null;
		// If file is internal means the video is inside the jar. To play it,
		// wee need to extract the video to a temp file. VLC can't deal with
		// InputStreams
		if (fileHandle.type() == FileType.Internal) {
			String name = fileHandle.path();
			destiny = tempVideos.get(name);
			if (destiny == null || !new File(destiny).exists()) {
				OutputStream os = null;
				InputStream is = null;
				try {
					is = ClassLoader.getSystemResourceAsStream(name);
					File tempFile = File.createTempFile("ead", "video");
					destiny = tempFile.getAbsolutePath();
					os = new FileOutputStream(tempFile);

					byte[] buffer = new byte[1024];
					int len;
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					tempVideos.put(name, destiny);
				} catch (IOException e) {
					Gdx.app.error("VLCPlayer",
							"Error copying video to temp file", e);
					return null;
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							Gdx.app.error("VLCPlayer",
									"Error closing input stream", e);
						}
					}
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							Gdx.app.error("VLCPlayer",
									"Error closing output stream", e);
						}
					}
				}
			}
			// If it's absolute, it is outside the jar
		} else if (fileHandle.type() == FileType.Absolute) {
			destiny = fileHandle.file().getAbsolutePath();
		}
		return destiny;
	}

	/**
	 * This method makes sure all resources initialized by VLC (e.g. native
	 * libraries loaded like OpenAL, audio and video streams, etc.), are cleaned
	 * up properly. This method should ONLY be invoked by VideoAction, who
	 * controls video play, as a reaction to exit() or dispose() being invoked
	 */
	public void release() {
		if (mediaPlayer != null) {
			Gdx.app.log("VLCPlayer",
					"The Media Player Component was created. Trying to release its resources...");
			mediaPlayerComponent.release();
		} else {
			Gdx.app.log("VLCPlayer",
					"The Media Player Component was not created. Nothing to cleanup then");
		}
	}
}
