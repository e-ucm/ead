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
package es.eucm.ead.editor.view.builders.scene.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.SoundsView;
import es.eucm.ead.editor.view.widgets.Slider;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.selectors.Selector;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;

public class SoundEditor extends ComponentEditor<Behavior> implements
		Selector.SelectorListener<String> {

	private Actor soundSelector;
	private TextButton soundName;
	private boolean loop = false;
	private PlaySound playSound;
	private ModelEntity sceneElement;
	private Slider slider;

	public SoundEditor(Controller controller) {
		super(SkinConstants.IC_SOUND, controller.getApplicationAssets()
				.getI18N().m("sound"), ComponentIds.SOUND, controller);
	}

	@Override
	protected void buildContent() {

		soundName = new TextButton(i18N.m("sound"), controller
				.getApplicationAssets().getSkin());
		soundName.getLabel().setEllipsis(true);
		soundName.getLabelCell().width(Value.percentWidth(0.9f, list));
		soundName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Views views = controller.getViews();
				float duration = 0.57f;
				SoundsView builder = views.getBuilder(SoundsView.class);
				soundSelector = builder.getView(SoundEditor.this);
				soundSelector.setX(Gdx.graphics.getWidth());
				soundSelector.addAction(Actions.moveTo(0, 0, duration,
						Interpolation.exp5Out));
				views.addToModalsContainer(soundSelector);
				views.getViewsContainer().addAction(
						Actions.delay(duration, Actions.visible(false)));
			}
		});
		slider = new Slider(0, 1, .05f, false, skin);
		list.add(soundName).expandX();
		float pad = WidgetBuilder.dpToPixels(16);
		LinearLayout volumeLayout = new LinearLayout(true);
		volumeLayout.add(WidgetBuilder.image(SkinConstants.IC_VOLUME,
				SkinConstants.COLOR_GRAY));
		volumeLayout.add(slider).expandX().marginLeft(pad);
		list.add(volumeLayout).expandX().margin(pad, pad, pad, pad);
		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, playSound, FieldName.VOLUME,
						slider.getValue());
			}
		});
	}

	@Override
	protected void read(ModelEntity entity, Behavior component) {
		playSound = (PlaySound) component.getEffects().get(0);
		String uri = playSound.getUri();
		if (uri == null || uri.isEmpty()) {
			uri = i18N.m("sound");
		}
		setSoundName(uri);
		slider.setValue(playSound.getVolume());
	}

	@Override
	protected Behavior buildNewComponent() {
		Behavior behavior = new Behavior();
		behavior.setEvent(new Touch());
		playSound = new PlaySound();
		behavior.getEffects().add(playSound);
		playSound.setUri("");
		playSound.setLoop(loop);
		playSound.setVolume(1f);
		return behavior;
	}

	@Override
	public void prepare() {
		sceneElement = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		ModelComponent component = Q.getComponentById(sceneElement,
				ComponentIds.SOUND);
		if (component != null) {
			Behavior soundBehavior = (Behavior) component;
			playSound = (PlaySound) soundBehavior.getEffects().first();
			setSoundName(playSound.getUri());
		} else {
			playSound = null;
		}
		super.prepare();
	}

	public String getTooltip() {
		return i18N.m("sound");
	}

	@Override
	public void release() {
		if (sceneElement != null && playSound != null) {
			String uri = playSound.getUri();
			if (uri == null || uri.isEmpty()) {
				ModelComponent component = Q.getComponentById(sceneElement,
						ComponentIds.SOUND);
				if (component != null) {
					controller.action(RemoveFromArray.class, sceneElement,
							sceneElement.getComponents(), component);
				}
			}

		}
	}

	private void setSoundName(String uri) {
		String name = ProjectUtils.getFileName(uri);
		soundName.setText(name);
		soundName.setUserObject(uri);
	}

	@Override
	public void selected(String selected) {
		controller.action(SetField.class, playSound, FieldName.URI, selected);
		setSoundName(selected);
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