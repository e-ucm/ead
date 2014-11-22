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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.selectors.SceneSelector;
import es.eucm.ead.editor.view.widgets.selectors.Selector.SelectorListener;
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

	private Tile tile;

	private TextureDrawable thumbnail;

	private String sceneId;

	private String thumbnailPath;

	private GoScene goScene;

	public LinkEditor(Controller controller) {
		super(SkinConstants.IC_LINK, controller.getApplicationAssets()
				.getI18N().m("link"), "", controller);

		sceneSelector = new SceneSelector(controller);
		sceneSelector.setBounds(Gdx.graphics.getWidth(), 0,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	protected void buildContent() {
		tile = new Tile(controller.getApplicationAssets().getSkin());
		tile.setBackground(new Image(thumbnail = new TextureDrawable()));
		add(tile).expandX();
		tile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneSelector.prepare(LinkEditor.this, sceneId);
				sceneSelector.addAction(Actions.moveTo(0, 0, 0.57f,
						Interpolation.exp5Out));
				controller.getViews().addToModalsContainer(sceneSelector);
			}
		});
	}

	@Override
	protected float getPrefHeight(Actor a) {
		if (a == tile) {
			return getHeight() / 2.38f;
		} else {
			return super.getPrefHeight(a);
		}
	}

	private void hideSceneSelector() {
		sceneSelector.addAction(Actions.sequence(Actions.moveTo(
				Gdx.graphics.getWidth(), 0, 0.57f, Interpolation.exp5Out),
				Actions.removeActor()));
	}

	@Override
	protected void read(Behavior component) {
		goScene = (GoScene) component.getEffects().get(0);
		sceneId = goScene.getSceneId();
		if (sceneId == null) {
			tile.setText(i18N.m("scene.none_selected"));
			thumbnail.setTexture(null);
		} else {
			setSceneId(sceneId);
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
	}

	@Override
	protected Behavior buildNewComponent() {
		Behavior behavior = new Behavior();
		behavior.setEvent(new Touch());
		GoScene goScene = new GoScene();
		goScene.setTransition(Transition.FADE_IN);
		goScene.setDuration(1.0f);
		behavior.getEffects().add(goScene);
		return behavior;
	}

	@Override
	public void selected(String selected) {
		setSceneId(selected);
		hideSceneSelector();
	}

	@Override
	public void cancelled() {
		hideSceneSelector();
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		thumbnail.setTexture(asset);
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
	}

	@Override
	public void unloaded(String fileName, Assets assets) {
		thumbnail.setTexture(null);
	}
}
