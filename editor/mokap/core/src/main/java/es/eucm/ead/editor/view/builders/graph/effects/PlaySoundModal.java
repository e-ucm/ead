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
package es.eucm.ead.editor.view.builders.graph.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.commander.Commander;
import es.eucm.commander.actions.SetField;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.SoundsView;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.selectors.Selector;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schemax.FieldName;
import es.eucm.gdx.widgets.layouts.LinearLayout;

public class PlaySoundModal extends EffectModal<PlaySound> implements
		Selector.SelectorListener<String> {

	private String soundString;
	private Actor soundSelector;
	private PlaySound sound;
	private TextButton soundName;
	private Slider slider;
	private Controller controller;

	public PlaySoundModal(PlaySoundNodeBuilder nodeBuilder,
			Controller controller, Commander commander, Skin skin, I18N i18N) {
		super(nodeBuilder, commander, skin, i18N);
		this.controller = controller;
	}

	@Override
	protected void updateEditor(PlaySound component) {
		sound = effect;
		String uri = sound.getUri();
		if (uri == null || uri.isEmpty()) {
			uri = soundString;
		}
		setSoundName(uri);
		slider.setValue(sound.getVolume());
	}

	private void setSoundName(String uri) {
		String name = ProjectUtils.getFileName(uri);
		soundName.setText(name);
		soundName.setUserObject(uri);
	}

	@Override
	protected Actor buildEditor(Skin skin, final I18N i18N) {
		soundString = i18N.m("sound");

		LinearLayout list = new LinearLayout(false);
		float pad = WidgetBuilder.dpToPixels(16);
		list.pad(pad);

		Label title = new Label(soundString, skin);

		list.add(title).marginBottom(pad).marginTop(pad);

		soundName = new TextButton(soundString, skin);
		soundName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Views views = controller.getViews();
				float duration = 0.57f;
				SoundsView builder = views.getBuilder(SoundsView.class);
				soundSelector = builder.getView(PlaySoundModal.this);
				soundSelector.setX(Gdx.graphics.getWidth());
				soundSelector.addAction(Actions.moveTo(0, 0, duration,
						Interpolation.exp5Out));
				getParent().addActor(soundSelector);
				views.getViewsContainer().addAction(
						Actions.delay(duration, Actions.visible(false)));

			}
		});
		slider = new Slider(0, 1, .05f, false, skin);
		list.add(soundName).marginBottom(pad);
		LinearLayout volumeLayout = new LinearLayout(true);
		volumeLayout.add(WidgetBuilder.image(SkinConstants.IC_VOLUME,
				SkinConstants.COLOR_GRAY));
		volumeLayout.add(slider).expandX();
		list.add(volumeLayout).expandX();
		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				commander.perform(SetField.class, sound, FieldName.VOLUME,
						slider.getValue());
			}
		});

		return list;
	}

	private void hideSelector(Actor actor) {
		controller.getViews().getViewsContainer().setVisible(true);
		actor.addAction(Actions.sequence(Actions.moveTo(
				Gdx.graphics.getWidth(), 0, 0.57f, Interpolation.exp5Out),
				Actions.removeActor()));
	}

	@Override
	public void selected(String selected) {
		commander.perform(SetField.class, sound, FieldName.URI, selected);
		updateEditor(sound);
		hideSelector(soundSelector);
	}

	@Override
	public void cancelled() {
		hideSelector(soundSelector);
	}
}
