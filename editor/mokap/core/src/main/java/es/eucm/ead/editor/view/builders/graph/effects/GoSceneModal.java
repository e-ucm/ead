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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.commander.Commander;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.ScrollPane;
import es.eucm.ead.editor.view.widgets.Switch;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.selectors.SceneSelector;
import es.eucm.ead.editor.view.widgets.selectors.Selector;
import es.eucm.ead.editor.view.widgets.selectors.TransitionSelector;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;
import es.eucm.gdx.widgets.layouts.LinearLayout;

public class GoSceneModal extends EffectModal<GoScene> implements
		Selector.SelectorListener<String>, Assets.AssetLoadedCallback<Texture> {

	private Controller controller;
	private SceneSelector sceneSelector;

	private Switch previousScene;

	private Label nextSceneLabel;

	private LinearLayout list;

	private Tile tile;

	private TextButton transitionName;

	private TextureDrawable thumbnail;

	private String sceneId;

	private GoScene.Transition transition;

	private GoScene goScene;

	private TransitionSelector transitionSelector;

	private SelectBox<String> duration;
	private I18N i18N;

	public GoSceneModal(GoSceneNodeBuilder nodeBuilder, Controller controller,
			Commander commander, Skin skin, I18N i18N) {
		super(nodeBuilder, commander, skin, i18N);
		sceneSelector = new SceneSelector(controller);
		transitionSelector = new TransitionSelector(controller);
		sceneSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		transitionSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.controller = controller;
	}

	@Override
	protected void updateEditor(GoScene effect) {
		goScene = effect;
		sceneId = goScene.getSceneId();
		transition = goScene.getTransition();
		if (transition == null) {
			transition = GoScene.Transition.FADE_IN;
			transitionName.setText(i18N.m(transition.toString()));
		}
		updateDurationSlider(goScene.getDuration());
		updateScenePreview(sceneId);
		updateTransitionName();
	}

	@Override
	protected Actor buildEditor(Skin skin, I18N i18n) {
		this.i18N = i18n;

		list = new LinearLayout(false);
		float pad = WidgetBuilder.dpToPixels(16);
		list.pad(pad);

		list.add(new Label(i18N.m("link"), skin)).marginBottom(pad);

		tile = new Tile(skin);
		tile.setBackground(new Image(thumbnail = new TextureDrawable()));

		list.add(tile).expandX().marginBottom(pad);

		tile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneSelector.prepare(GoSceneModal.this, sceneId);
				float duration = 0.57f;
				sceneSelector.addAction(Actions.moveTo(0, 0, duration,
						Interpolation.exp5Out));
				Views views = controller.getViews();
				getParent().addActor(sceneSelector);
				views.getViewsContainer().addAction(
						Actions.delay(duration, Actions.visible(false)));
			}

		});

		previousScene = new Switch(skin);
		previousScene.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String sceneId;
				if (previousScene.isChecked()) {
					sceneId = null;
				} else {
					sceneId = ((GoSceneNodeBuilder) nodeBuilder)
							.pickNextScene();
				}
				updateScenePreview(sceneId);
				controller.action(SetField.class, goScene, FieldName.SCENE_ID,
						sceneId);
			}
		});

		nextSceneLabel = new Label(i18N.m("go.previous.scene"), skin,
				SkinConstants.STYLE_CONTEXT);

		LinearLayout previousSceneRow = new LinearLayout(true);
		previousSceneRow.add(nextSceneLabel);
		previousSceneRow.addSpace();
		previousSceneRow.add(previousScene);
		list.add(previousSceneRow).expandX().marginBottom(pad);

		list.add(
				WidgetBuilder.label(i18N.m("transition"),
						SkinConstants.STYLE_EDITION)).marginBottom(pad);

		LinearLayout velTable = new LinearLayout(true);

		Array<String> speedOptions = new Array<String>();
		speedOptions.add(i18N.m("fast"));
		speedOptions.add(i18N.m("normal"));
		speedOptions.add(i18N.m("slow"));
		duration = new SelectBox<String>(skin);
		duration.setItems(speedOptions);
		duration.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float duration = getDuration();
				controller.action(SetField.class, goScene, FieldName.DURATION,
						duration);
			}
		});
		velTable.add(new Label(i18N.m("speed") + ":", skin)).expandX()
				.margin(pad, 0, 0, 0);
		velTable.add(duration).expandX().margin(0, 0, pad, 0);

		transition = GoScene.Transition.FADE_IN;
		transitionName = new TextButton(i18N.m(transition.toString()), skin);
		transitionName.getLabel().setEllipsis(true);
		transitionName.getLabelCell().width(0);
		list.add(transitionName).marginBottom(pad).expandX();

		list.add(velTable).expandX();
		final TransitionSelectorListener transitionSelectorListener = new TransitionSelectorListener();
		transitionName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				float duration = 0.57f;
				transitionSelector.addAction(Actions.moveTo(0, 0, duration,
						Interpolation.exp5Out));
				Views views = controller.getViews();

				getParent().addActor(transitionSelector);
				transitionSelector.prepare(transitionSelectorListener,
						transition.toString(), sceneId);
				views.getViewsContainer().addAction(
						Actions.delay(duration, Actions.visible(false)));
			}
		});

		ScrollPane scroll = new ScrollPane(list);
		return scroll;
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight() * .9f;
	}

	private void updateDurationSlider(float time) {
		duration.setSelected(Q.getSpeedTag(time));
	}

	private float getDuration() {
		return Q.getSpeed(duration.getSelected());
	}

	private void hideSelector(Actor actor) {
		controller.getViews().getViewsContainer().setVisible(true);
		actor.addAction(Actions.sequence(Actions.moveTo(
				Gdx.graphics.getWidth(), 0, 0.57f, Interpolation.exp5Out),
				Actions.removeActor()));
	}

	private void updateTransitionName() {
		transitionName.setText(i18N.m(transition.toString()));
	}

	private void updateScenePreview(String sceneId) {
		this.sceneId = sceneId;
		if (sceneId == null) {
			tile.setVisible(false);
			thumbnail.setTexture(null);
			previousScene.setChecked(true);
		} else {
			previousScene.setChecked(false);
			tile.setVisible(true);

			ModelEntity scene = (ModelEntity) controller.getModel()
					.getResource(sceneId, ResourceCategory.SCENE).getObject();

			tile.setText(i18N.m("go.to", Q.getTitle(scene, i18N.m("untitled"))));

			Q.getThumbnailTexture(scene, this);
		}
		list.invalidateHierarchy();
		Gdx.graphics.requestRendering();
	}

	@Override
	public void selected(String selected) {
		controller
				.action(SetField.class, goScene, FieldName.SCENE_ID, selected);
		updateScenePreview(selected);
		hideSelector(sceneSelector);
	}

	@Override
	public void cancelled() {
		hideSelector(sceneSelector);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		thumbnail.setTexture(asset);
	}

	@Override
	public void error(String fileName, Class type, Throwable exception) {

	}

	private class TransitionSelectorListener implements
			Selector.SelectorListener<String> {

		@Override
		public void selected(String selected) {

			transition = GoScene.Transition.fromValue(selected);
			updateTransitionName();

			controller.action(SetField.class, goScene, FieldName.TRANSITION,
					transition);
			hideSelector(transitionSelector);

		}

		@Override
		public void cancelled() {
			hideSelector(transitionSelector);
		}
	}
}
