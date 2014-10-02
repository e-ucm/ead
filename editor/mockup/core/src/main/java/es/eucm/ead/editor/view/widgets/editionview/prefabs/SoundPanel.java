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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.ComponentId;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.actions.irreversibles.scene.AddBehaviorPrefab;
import es.eucm.ead.editor.control.actions.irreversibles.scene.ChangeBehaviorEffect;
import es.eucm.ead.editor.control.actions.irreversibles.scene.sound.ChangeSound;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.effects.PlaySound;

public class SoundPanel extends PrefabComponentPanel implements
		FileChooserListener {

	private static final String[] supportedExtensions = { ".mp3", ".ogg",
			".wav" };
	private static final float SUPPORTED_EXTENSIONS_TIMEOUT = 3f;

	private static final boolean DEFAULT_LOOP = false;
	private static final float DEFAULT_VOLUME = .5F;
	private static final float SPACE = 30F;

	private Label musicLabel;
	private PlaySound sound;
	private Slider volume;
	private CheckBox loop;

	public SoundPanel(final Controller controller) {
		super("sound80x80", "edition.sound", ComponentId.PREFAB_SOUND,
				controller);

		Table table = new Table();
		table.pad(SPACE).defaults().space(SPACE);

		final Button musicButton = new Button(skin);

		ChangeListener listener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				if (listenerActor == musicButton) {
					Platform platform = controller.getPlatform();
					((MockupPlatform) platform).askForAudio(controller,
							SoundPanel.this);
				} else if (listenerActor == volume) {
					if (sound != null) {
						updateVolume();
					}
				} else if (listenerActor == loop) {
					if (sound != null) {
						updateLoop();
					}
				}
			}
		};

		musicButton.addListener(listener);
		musicButton.pad(SPACE);
		musicLabel = new Label("", skin);
		musicButton.add(musicLabel).padRight(SPACE);
		Image image = new Image(skin.getDrawable("sound80x80"));
		image.setColor(Color.BLACK);
		image.setScaling(Scaling.fit);
		musicButton.add(image);
		table.add(musicButton);
		table.row();
		table.add(
				volume = new Slider(0f, 1f, .1f, false, skin,
						"white-horizontal")).expandX().fillX();
		table.row();
		table.add(loop = new CheckBox(i18n.m("sound.loop"), skin));
		volume.setDisabled(true);
		loop.setDisabled(true);
		volume.addListener(listener);
		loop.addListener(listener);

		panel.add(table).expand().fill();
	}

	/**
	 * Resets the panel to the default values.
	 */
	private void emptyPanel() {
		volume.setDisabled(true);
		loop.setDisabled(true);

		musicLabel.setText(i18n.m("edition.chooseSound"));
		volume.setValue(DEFAULT_VOLUME);
		loop.setChecked(DEFAULT_LOOP);
	}

	@Override
	public void hidePanel() {
		emptyPanel();
		super.hidePanel();
	}

	@Override
	protected void actualizePanel() {
		if (component == null) {
			emptyPanel();
			return;
		} else {
			if (sound == null) {
				sound = (PlaySound) ((Behavior) component).getEffects().first();
			}
		}

		String uri = sound.getUri();
		if (uri == null) {
			return;
		}
		int lastIndexOf = uri.lastIndexOf("/");
		musicLabel
				.setText(lastIndexOf == -1 ? uri : uri.substring(lastIndexOf));

		volume.setValue(sound.getVolume());
		loop.setChecked(sound.isLoop());

		volume.setDisabled(false);
		loop.setDisabled(false);
	}

	private void updatePath(String path) {
		sound.setUri(path);
	}

	private void updateLoop() {
		if (!loop.isDisabled()) {
			sound.setLoop(loop.isChecked());
			controller.action(ChangeSound.class, sound, loop.isChecked());
		}
	}

	private void updateVolume() {
		if (!volume.isDisabled()) {
			controller.action(ChangeSound.class, sound, volume.getValue());
		}
	}

	@Override
	public void fileChosen(String path) {
		if (path != null) {
			if (hasSupportedExtension(path)) {
				EditorGameAssets editorGameAssets = controller
						.getEditorGameAssets();
				if (!path.startsWith(editorGameAssets.getLoadingPath())) {
					path = editorGameAssets.copyToProjectIfNeeded(path,
							Music.class);
				}

				if (component == null) {
					component = new Behavior();
					controller.action(AddBehaviorPrefab.class, component,
							componentId);
				}

				if (((Behavior) component).getEffects().size > 0) {
					sound = (PlaySound) ((Behavior) component).getEffects()
							.first();
				} else {
					sound = new PlaySound();
					sound.setVolume(DEFAULT_VOLUME);
					sound.setLoop(false);
				}

				controller.action(ChangeBehaviorEffect.class, component, sound);

				setUsed(true);
				updatePath(path);
				updateVolume();
				updateLoop();
				actualizePanel();
			} else if (!path.isEmpty()) {
				((MockupViews) controller.getViews()).getToasts()
						.showNotification(
								i18n.m("sound.supportedExtensions") + ": "
										+ getSupportedExtensions(),
								SUPPORTED_EXTENSIONS_TIMEOUT);
			}
		}
	}

	private boolean hasSupportedExtension(String path) {
		for (int i = 0, n = supportedExtensions.length; i < n; ++i) {
			if (path.endsWith(supportedExtensions[i])) {
				return true;
			}
		}
		return false;
	}

	private String getSupportedExtensions() {
		String ret = "";
		for (int i = 0, n = supportedExtensions.length; i < n; ++i) {
			ret += supportedExtensions[i];
			if (i < n - 1) {
				ret += ", ";
			}
		}
		return ret;
	}

}
