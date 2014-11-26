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
package es.eucm.ead.editor.view.builders.scene.interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.selectors.SceneSelector;
import es.eucm.ead.editor.view.widgets.selectors.Selector.SelectorListener;
import es.eucm.ead.editor.view.widgets.selectors.TransitionDrawable;
import es.eucm.ead.editor.view.widgets.selectors.TransitionSelector;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.Assets.AssetLoadingListener;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.GoScene.Transition;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class LinkEditor extends ComponentEditor<Behavior> implements
		SelectorListener<String>, AssetLoadedCallback<Texture>,
		AssetLoadingListener<Texture> {

	private SceneSelector sceneSelector;

	private Tile tile, transitionTile;

	private TextureDrawable thumbnail;

	private TransitionDrawable transitionPreview;

	private String sceneId;

	private Transition transition;

	private String thumbnailPath;

	private GoScene goScene;

	private TransitionSelector transitionSelector;

	private Slider duration;

	private AssetLoadedCallback<Texture> currentThumbnail = new AssetLoadedCallback<Texture>() {

		@Override
		public void loaded(String fileName, Texture asset) {
			transitionPreview.setCurrentTexture(asset);

		}
	};

	public LinkEditor(Controller controller) {
		super(SkinConstants.IC_LINK, controller.getApplicationAssets()
				.getI18N().m("link"), "_link", controller);

		sceneSelector = new SceneSelector(controller);
		transitionSelector = new TransitionSelector(controller);
		sceneSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		transitionSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	protected void buildContent() {

		tile = new Tile(controller.getApplicationAssets().getSkin()) {
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

		Table transitionHeader = new Table();
		transitionHeader.pad(WidgetBuilder.dpToPixels(8), 0,
				WidgetBuilder.dpToPixels(8), 0);
		transitionHeader.add(WidgetBuilder.label(i18N.m("transition"),
				SkinConstants.STYLE_EDITION));
		list.add(transitionHeader);
		Table table = new Table(skin);
		table.add(i18N.m("fast"));
		table.add(i18N.m("normal")).expand();
		table.add(i18N.m("slow"));
		list.add(table).expandX();
		duration = new Slider(0f, 2f, 1f, false, skin);
		duration.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float duration = getDuration();
				transitionPreview.setTransition(transition, duration);
				controller.action(SetField.class, goScene, FieldName.DURATION,
						duration);
			}
		});
		list.add(duration).expandX();

		transitionTile = new Tile(controller.getApplicationAssets().getSkin()) {
			@Override
			public float getPrefHeight() {
				return LinkEditor.this.getHeight() / 2.38f;
			}
		};
		transitionTile.setBackground(new Image(
				transitionPreview = new TransitionDrawable()) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				if (clipBegin(getX(), getY(), getWidth(), getHeight())) {
					super.draw(batch, parentAlpha);
					batch.flush();
					clipEnd();
				}
			}
		});
		transition = Transition.FADE_IN;
		transitionTile.setText(transition.toString());
		list.add(transitionTile).expandX();
		transitionTile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				transitionSelector.prepare(new SelectorListener<String>() {

					@Override
					public void selected(String selected) {

						transition = Transition.fromValue(selected);
						setTransition();

						controller.action(SetField.class, goScene,
								FieldName.TRANSITION,
								Transition.fromValue(selected));
						hideSelector(transitionSelector);

					}

					@Override
					public void cancelled() {
						hideSelector(transitionSelector);
					}
				}, transition.toString(), sceneId);
				transitionSelector.addAction(Actions.moveTo(0, 0, 0.57f,
						Interpolation.exp5Out));
				controller.getViews().addToModalsContainer(transitionSelector);
			}
		});

	}

	private void hideSelector(Actor actor) {
		actor.addAction(Actions.sequence(Actions.moveTo(
				Gdx.graphics.getWidth(), 0, 0.57f, Interpolation.exp5Out),
				Actions.removeActor()));
	}

	@Override
	protected void read(Behavior component) {
		goScene = (GoScene) component.getEffects().get(0);
		sceneId = goScene.getSceneId();
		transition = goScene.getTransition();
		if (transition == null) {
			transition = Transition.FADE_IN;
			transitionTile.setText(i18N.m(transition.toString()));
		}
		updateDurationSlider(goScene.getDuration());
		if (sceneId == null) {
			tile.setText(i18N.m("scene.none_selected"));
			thumbnail.setTexture(null);
		} else {
			setSceneId(sceneId);
		}
	}

	private void updateDurationSlider(float time) {
		if (time <= .5f) {
			duration.setValue(.0f);
		} else if (time <= .8f) {
			duration.setValue(1f);
		} else {
			duration.setValue(2f);
		}
	}

	private float getDuration() {
		if (duration.getValue() == 0f) {
			return .4f;
		} else if (duration.getValue() == 1f) {
			return .8f;
		} else {
			return 1.6f;
		}
	}

	private void setSceneId(String sceneId) {
		this.sceneId = sceneId;

		ModelEntity scene = (ModelEntity) controller.getModel()
				.getResource(sceneId, ResourceCategory.SCENE).getObject();

		tile.setText(Q.getName(scene, i18N.m("untitled")));
		this.thumbnailPath = Q.getThumbnailPath(scene);

		Q.getThumbnailTexture(scene, this);

		controller.action(SetField.class, goScene, FieldName.SCENE_ID, sceneId);

		setTransition();

	}

	private void setTransition() {
		ModelEntity selectedScene = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE);
		Q.getThumbnailTexture(selectedScene, currentThumbnail);
		transitionTile.setText(i18N.m(transition.toString()));
		transitionPreview.setTransition(transition, getDuration());
	}

	@Override
	protected Behavior buildNewComponent() {
		Behavior behavior = new Behavior();
		behavior.setEvent(new Touch());
		GoScene goScene = new GoScene();
		goScene.setTransition(transition);
		System.out.println("duration: " + getDuration());
		goScene.setDuration(getDuration());
		behavior.getEffects().add(goScene);
		return behavior;
	}

	@Override
	public void selected(String selected) {
		setSceneId(selected);
		hideSelector(sceneSelector);
	}

	@Override
	public void cancelled() {
		hideSelector(sceneSelector);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		thumbnail.setTexture(asset);
		transitionPreview.setNextTexture(asset);
	}

	public String getTooltip() {
		return controller.getApplicationAssets().getI18N().m("link");
	}

	@Override
	public void prepare() {
		super.prepare();
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
		transitionPreview.setNextTexture(asset);
	}

	@Override
	public void unloaded(String fileName, Assets assets) {
		thumbnail.setTexture(null);
	}
}