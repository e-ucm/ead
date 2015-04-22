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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.CreateSceneThumbnail;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.Switch;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.selectors.SceneSelector;
import es.eucm.ead.editor.view.widgets.selectors.Selector.SelectorListener;
import es.eucm.ead.editor.view.widgets.selectors.TransitionSelector;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.Assets.AssetLoadingListener;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.GoScene.Transition;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class LinkEditor extends ComponentEditor<Behavior> implements
		SelectorListener<String>, AssetLoadedCallback<Texture>,
		AssetLoadingListener<Texture> {

	private SceneSelector sceneSelector;

	private Switch previousScene;

	private Label nextSceneLabel;

	private Tile tile;

	private TextButton transitionName;

	private TextureDrawable thumbnail;

	private String sceneId;

	private Transition transition;

	private String thumbnailPath;

	private GoScene goScene;

	private TransitionSelector transitionSelector;

	private SelectBox<String> duration;

	public LinkEditor(Controller controller) {
		super(SkinConstants.IC_LINK, controller.getApplicationAssets()
				.getI18N().m("link"), ComponentIds.LINK, controller);

		sceneSelector = new SceneSelector(controller);
		transitionSelector = new TransitionSelector(controller);
		sceneSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		transitionSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	protected void buildContent() {
		I18N i18N = controller.getApplicationAssets().getI18N();
		Skin skin = controller.getApplicationAssets().getSkin();

		tile = new Tile(skin) {
			@Override
			public float getPrefHeight() {
				return LinkEditor.this.getHeight() / 2.38f;
			}
		};
		tile.setBackground(new Image(thumbnail = new TextureDrawable()));

		list.add(tile).expandX();

		tile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneSelector.prepare(LinkEditor.this, sceneId);
				sceneSelector.addAction(Actions.moveTo(0, 0, 0.57f,
						Interpolation.exp5Out));
				controller.getViews().addToModalsContainer(sceneSelector);
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
					sceneId = pickNextScene();
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
		list.add(previousSceneRow).expandX()
				.margin(WidgetBuilder.dpToPixels(8));

		Table transitionHeader = new Table();
		transitionHeader.pad(WidgetBuilder.dpToPixels(8), 0,
				WidgetBuilder.dpToPixels(8), 0);
		transitionHeader.add(WidgetBuilder.label(i18N.m("transition"),
				SkinConstants.STYLE_EDITION));
		list.add(transitionHeader);

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
				.margin(WidgetBuilder.dpToPixels(16), 0, 0, 0);
		velTable.add(duration).expandX()
				.margin(0, 0, WidgetBuilder.dpToPixels(16), 0);

		list.add(velTable).expandX();

		transition = Transition.FADE_IN;
		transitionName = new TextButton(i18N.m(transition.toString()),
				controller.getApplicationAssets().getSkin());
		transitionName.getLabel().setEllipsis(true);
		transitionName.getLabelCell().width(0);
		list.add(transitionName).expandX();
		final TransitionSelectorListener transitionSelectorListener = new TransitionSelectorListener();
		transitionName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				transitionSelector.addAction(Actions.moveTo(0, 0, 0.57f,
						Interpolation.exp5Out));
				controller.getViews().addToModalsContainer(transitionSelector);
				transitionSelector.prepare(transitionSelectorListener,
						transition.toString(), sceneId);
			}
		});

	}

	private void hideSelector(Actor actor) {
		actor.addAction(Actions.sequence(Actions.moveTo(
				Gdx.graphics.getWidth(), 0, 0.57f, Interpolation.exp5Out),
				Actions.removeActor()));
	}

	@Override
	protected void read(ModelEntity entity, Behavior component) {
		goScene = (GoScene) component.getEffects().get(0);
		sceneId = goScene.getSceneId();
		transition = goScene.getTransition();
		if (transition == null) {
			transition = Transition.FADE_IN;
			transitionName.setText(i18N.m(transition.toString()));
		}
		updateDurationSlider(goScene.getDuration());
		updateScenePreview(sceneId);
		updateTransitionName();
	}

	private void updateDurationSlider(float time) {
		if (time <= .5f) {
			duration.setSelected(i18N.m("fast"));
		} else if (time <= .8f) {
			duration.setSelected(i18N.m("normal"));
		} else {
			duration.setSelected(i18N.m("slow"));
		}
	}

	private float getDuration() {
		if (duration.getSelected().equals(i18N.m("fast"))) {
			return .4f;
		} else if (duration.getSelected().equals(i18N.m("normal"))) {
			return .8f;
		} else {
			return 1.6f;
		}
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
			this.thumbnailPath = Q.getThumbnailPath(scene);

			Q.getThumbnailTexture(scene, this);
		}
		invalidateHierarchy();
		Gdx.graphics.requestRendering();
	}

	private void updateTransitionName() {
		transitionName.setText(i18N.m(transition.toString()));
	}

	@Override
	protected Behavior buildNewComponent() {
		Behavior behavior = new Behavior();
		behavior.setEvent(new Touch());
		GoScene goScene = new GoScene();
		goScene.setTransition(transition);
		goScene.setSceneId(pickNextScene());
		goScene.setDuration(getDuration());
		behavior.getEffects().add(goScene);
		return behavior;
	}

	private String pickNextScene() {
		String currentSceneId = (String) controller.getModel().getSelection()
				.getSingle(Selection.MOKAP_RESOURCE);
		for (String key : controller.getModel()
				.getResources(ResourceCategory.SCENE).keySet()) {
			if (!currentSceneId.equals(key)) {
				return key;
			}
		}
		return null;
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

	public String getTooltip() {
		return controller.getApplicationAssets().getI18N().m("link");
	}

	@Override
	public void prepare() {
		super.prepare();
		// Generate thumbnail to correct display transition
		ModelEntity scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		if (scene != null) {
			controller.action(CreateSceneThumbnail.class, scene);
		}
		controller.getEditorGameAssets().addAssetListener(this);
	}

	@Override
	public void release() {
		super.release();
		controller.getEditorGameAssets().removeAssetListener(this);
	}

	@Override
	public boolean listenTo(String fileName) {
		return fileName.equals(thumbnailPath);
	}

	@Override
	public void loaded(String fileName, Texture asset, Assets assets) {
		thumbnail.setTexture(asset);
	}

	@Override
	public void unloaded(String fileName, Assets assets) {
		thumbnail.setTexture(null);
	}

	private class TransitionSelectorListener implements
			SelectorListener<String> {

		@Override
		public void selected(String selected) {

			transition = Transition.fromValue(selected);
			updateTransitionName();

			controller.action(SetField.class, goScene, FieldName.TRANSITION,
					Transition.fromValue(selected));
			hideSelector(transitionSelector);

		}

		@Override
		public void cancelled() {
			hideSelector(transitionSelector);
		}
	}
}