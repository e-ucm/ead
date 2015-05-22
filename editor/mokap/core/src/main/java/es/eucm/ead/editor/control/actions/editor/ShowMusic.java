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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.control.actions.model.scene.AddComponent;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.Modal;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.SoundsView;
import es.eucm.ead.editor.view.widgets.Slider;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.modals.ModalContainer;
import es.eucm.ead.editor.view.widgets.selectors.Selector;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;

public class ShowMusic extends EditorAction implements
		Selector.SelectorListener<String> {

	private Actor soundSelector;
	private Controller controller;
	private I18N i18N;
	private PlaySound playSound;
	private ModelEntity scene;

	private TextButton soundName;
	private Slider slider;

	private Button delete;
	private MusicModal modal;

	public ShowMusic() {
		super(true, false);
	}

	@Override
	public void initialize(final Controller control) {
		super.initialize(control);
		controller = control;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		i18N = applicationAssets.getI18N();
		Skin skin = applicationAssets.getSkin();

		modal = new MusicModal();
		modal.background(skin
				.getDrawable(SkinConstants.DRAWABLE_SEMI_TRANSPARENT));
		final LinearLayout list = modal.list;
		list.background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
		float pad = WidgetBuilder.dpToPixels(16);
		list.pad(pad);

		Label title = new Label(i18N.m("background.music"), skin);

		delete = WidgetBuilder.icon(SkinConstants.IC_DELETE,
				SkinConstants.STYLE_EDITION);
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				removeComponent();
				controller.getViews().hideModal();
			}
		});
		LinearLayout titleList = new LinearLayout(true);
		titleList.add(title);
		titleList.addSpace();
		titleList.add(delete);

		list.add(titleList).margin(0, pad, pad, pad).expandX();

		soundName = new TextButton(i18N.m("sound"), applicationAssets.getSkin());
		soundName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Views views = controller.getViews();
				float duration = 0.57f;
				SoundsView builder = views.getBuilder(SoundsView.class);
				soundSelector = builder.getView(ShowMusic.this);
				soundSelector.setX(Gdx.graphics.getWidth());
				soundSelector.addAction(Actions.moveTo(0, 0, duration,
						Interpolation.exp5Out));
				modal.addActor(soundSelector);
				views.getViewsContainer().addAction(
						Actions.delay(duration, Actions.visible(false)));

			}
		});
		slider = new Slider(0, 1, .05f, false, skin);
		list.add(soundName).expandX();
		LinearLayout volumeLayout = new LinearLayout(true);
		volumeLayout.add(WidgetBuilder.image(SkinConstants.IC_VOLUME,
				SkinConstants.COLOR_GRAY));
		volumeLayout.add(slider).expandX().marginLeft(pad);
		list.add(volumeLayout).expandX().margin(pad);
		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, playSound, FieldName.VOLUME,
						slider.getValue());
			}
		});

		TextButton.TextButtonStyle textButtonStyle = skin.get(
				SkinConstants.STYLE_DIALOG, TextButton.TextButtonStyle.class);
		ClickListener hideModalListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.getViews().hideModal();
			}
		};

		TextButton ok = WidgetBuilder.dialogButton(i18N.m("ok"),
				textButtonStyle);
		ok.addListener(hideModalListener);
		list.add(ok).right();
	}

	private void removeComponent() {
		ModelComponent component = Q
				.getComponentById(scene, ComponentIds.MUSIC);
		if (component != null) {
			controller.action(RemoveFromArray.class, scene,
					scene.getComponents(), component);
		}
	}

	@Override
	public void perform(Object... args) {
		controller.getCommands().pushStack();
		scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		ModelComponent component = Q
				.getComponentById(scene, ComponentIds.MUSIC);
		if (component != null) {
			Behavior behavior = (Behavior) component;
			playSound = (PlaySound) behavior.getEffects().first();
		} else {
			newComponent();
		}

		read();

		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				controller.getViews().showModal(modal, 0, 0);
			}
		});

	}

	private void newComponent() {
		Behavior behavior = new Behavior();
		behavior.setId(ComponentIds.MUSIC);
		behavior.setEvent(new Init());
		playSound = new PlaySound();
		behavior.getEffects().add(playSound);
		playSound.setUri("");
		playSound.setLoop(true);
		playSound.setVolume(1f);
		controller.action(AddComponent.class, scene, behavior);
	}

	private void read() {
		String uri = playSound.getUri();
		if (uri == null || uri.isEmpty()) {
			uri = i18N.m("sound");
			delete.setVisible(false);
		} else {
			delete.setVisible(true);
		}
		setSoundName(uri);
		slider.setValue(playSound.getVolume());
	}

	private void setSoundName(String uri) {
		String name = ProjectUtils.getFileName(uri);
		soundName.setText(name);
		soundName.setUserObject(uri);
	}

	private class MusicModal extends Table implements Modal {

		private LinearLayout list;

		public MusicModal() {
			setFillParent(true);
			list = new LinearLayout(false);
			add(list);
		}

		@Override
		public void show(Views views) {
			validate();
			clearActions();
			getColor().a = 0.0f;
			addAction(Actions.alpha(1.0f, 0.25f));
			float y = list.getY();
			list.setY(Gdx.graphics.getHeight());
			list.clearActions();
			list.addAction(Actions2.moveToY(y, 0.33f, Interpolation.exp5Out));
		}

		@Override
		public void hide(Runnable runnable) {
			clearActions();
			addAction(Actions.alpha(0.0f, 0.25f));
			list.addAction(Actions.sequence(Actions2.moveToY(
					Gdx.graphics.getHeight(), 0.33f, Interpolation.exp5Out),
					Actions.run(runnable)));

			if (scene != null && playSound != null) {
				String uri = playSound.getUri();
				if (uri == null || uri.isEmpty()) {
					removeComponent();
				}
			}
			controller.getCommands().popStack(false);
		}

		@Override
		public boolean hideAlways() {
			return false;
		}

	}

	@Override
	public void selected(String selected) {
		controller.action(SetField.class, playSound, FieldName.URI, selected);
		read();
		hideSelector(soundSelector);
	}

	@Override
	public void cancelled() {
		hideSelector(soundSelector);
	}

	private void hideSelector(Actor actor) {
		controller.getViews().getViewsContainer().setVisible(true);
		actor.addAction(Actions.sequence(Actions.moveTo(
				Gdx.graphics.getWidth(), 0, 0.57f, Interpolation.exp5Out),
				Actions.removeActor()));
	}
}
