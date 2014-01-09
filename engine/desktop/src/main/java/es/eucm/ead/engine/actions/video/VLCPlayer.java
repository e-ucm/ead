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
package es.eucm.ead.engine.actions.video;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.actions.VideoAction;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VLCPlayer {

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	private VideoAction videoAction;

	private Canvas videoSurface;

	private Canvas gameSurface;

	private EmbeddedMediaPlayer mediaPlayer;

	private boolean skip;

	public VLCPlayer() {
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

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				Gdx.app.error("Video", "Video finished.");
				skip = true;
				end();
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				Gdx.app.error("Video", "An error ocurred while playing video.");
				skip = true;
				end();
			}
		});
	}

	/**
	 * Plays the video
	 * 
	 * @param videoAction
	 *            the action launching the video
	 * @param mlr
	 *            the location of the video
	 * @param skippable
	 *            if the video can be skipped
	 */
	public void play(VideoAction videoAction, String mlr, boolean skippable) {
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

}
