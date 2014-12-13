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
package es.eucm.ead.editor.view.widgets.galleries;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.AddScene;
import es.eucm.ead.editor.control.actions.editor.CreateSceneThumbnail;
import es.eucm.ead.editor.control.actions.model.scene.SetEditedScene;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadingListener;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Comparator;
import java.util.Map.Entry;

public class ScenesGallery extends ThumbnailsGallery implements
		ModelListener<ResourceEvent>, ModelView, AssetLoadingListener<Texture>,
		Comparator<Actor> {

	protected Controller controller;

	private GameData gameData;

	private ImageButton initialSceneMarker;

	private GameDataListener gameDataListener = new GameDataListener();

	private Array<NameListener> nameListeners = new Array<NameListener>();

	public ScenesGallery(float rowHeight, int columns, Controller controller,
			String galleryStyle) {
		super(rowHeight, columns, controller.getEditorGameAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N(), galleryStyle);
		this.controller = controller;
		init();
	}

	public ScenesGallery(float rowHeight, int columns, Controller controller) {
		super(rowHeight, columns, controller.getEditorGameAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N());
		this.controller = controller;
		init();
	}

	private void init() {
		ImageButtonStyle style = new ImageButtonStyle(skin.get(
				SkinConstants.STYLE_MARKER, ImageButtonStyle.class));

		style.imageUp = skin.getDrawable(SkinConstants.IC_ONE);
		initialSceneMarker = new ImageButton(style);
		initialSceneMarker.pad(WidgetBuilder.dpToPixels(8));
	}

	@Override
	public void prepare() {
		clear();
		controller.getModel().addResourceListener(this);
		gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);

		for (Entry<String, Resource> entry : controller.getModel()
				.getResources(ResourceCategory.SCENE).entrySet()) {
			addScene(entry.getKey(), (ModelEntity) entry.getValue().getObject());
		}
		controller.getEditorGameAssets().addAssetListener(this);
		controller.getModel().addFieldListener(gameData, gameDataListener);

		readInitialScene();
	}

	@Override
	public void release() {
		controller.getModel().removeResourceListener(this);
		controller.getEditorGameAssets().removeAssetListener(this);
		controller.getModel().removeListener(gameData, gameDataListener);
		for (NameListener nameListener : nameListeners) {
			controller.getModel().removeListenerFromAllTargets(nameListener);
		}
		nameListeners.clear();
	}

	@Override
	protected void prepareAddButton(Actor actor) {
		WidgetBuilder.actionOnClick(actor, AddScene.class);
	}

	@Override
	protected void prepareGalleryItem(Actor actor, String id) {
		WidgetBuilder.actionOnClick(actor, SetEditedScene.class, id, controller
				.getModel().getResourceObject(id));
	}

	@Override
	public void modelChanged(ResourceEvent event) {
		if (event.getCategory() == ResourceCategory.SCENE) {
			switch (event.getType()) {
			case ADDED:
				addScene(event.getId(), (ModelEntity) event.getResource());
				break;
			case REMOVED:
				removeScene(event.getId());
				break;
			}
			readInitialScene();
		}
	}

	private void addScene(String id, ModelEntity scene) {
		String thumbnailPath = Q.getThumbnailPath(id);
		Cell cell = addTile(id, Q.getName(scene, ""), thumbnailPath);
		cell.setUserObject(scene);
		Tile tile = (Tile) cell.getActor();

		NameListener nameListener = new NameListener(tile);
		nameListeners.add(nameListener);
		controller.getModel().addFieldListener(
				Q.getComponent(scene, Documentation.class), nameListener);
		sortScenes();

		// Check if thumbnail exists. If not, create
		if (assets.resolve(thumbnailPath).exists()) {
			super.loadThumbnail(thumbnailPath);
		} else {
			controller.action(CreateSceneThumbnail.class, scene);
		}
	}

	@Override
	protected void loadThumbnail(String path) {
		// Do nothing
	}

	private void sortScenes() {
		gallery.getGrid().getChildren().sort(this);
		gallery.getGrid().invalidate();
	}

	private void removeScene(String id) {
		Actor tile = findActor(id);
		ModelEntity scene = null;
		if (tile instanceof Tile) {
			scene = (ModelEntity) tile.getParent().getUserObject();
			tile.getParent().remove();
		} else if (tile instanceof Cell) {
			tile.remove();
			scene = (ModelEntity) tile.getUserObject();
		}
		controller.getModel().removeListenersFrom(
				Q.getComponent(scene, Documentation.class));
		sortScenes();
	}

	private void readInitialScene() {
		Actor actor = findActor(gameData.getInitialScene());
		if (actor instanceof Tile) {
			((Tile) actor).setMarker(initialSceneMarker);
		} else if (actor instanceof Cell) {
			((Tile) ((Cell) actor).getActor()).setMarker(initialSceneMarker);
		}
		sortScenes();
	}

	@Override
	public boolean listenTo(String fileName) {
		return pendingTextures.containsKey(fileName);
	}

	@Override
	public void loaded(String fileName, Texture asset, Assets assets) {
		pendingTextures.get(fileName).setTexture(asset);
	}

	@Override
	public void unloaded(String fileName, Assets assets) {
		pendingTextures.get(fileName).setTexture(null);
	}

	@Override
	public int compare(Actor actor, Actor actor2) {
		String scene1 = Q.getDate((ModelEntity) actor.getUserObject());
		String scene2 = Q.getDate((ModelEntity) actor2.getUserObject());
		if (scene1 != null && scene2 != null) {
			return scene1.compareTo(scene2);
		} else {
			return scene1 != null ? 1 : scene2 != null ? -1 : 0;
		}
	}

	public class GameDataListener implements FieldListener {

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.INITIAL_SCENE.equals(fieldName);
		}

		@Override
		public void modelChanged(FieldEvent event) {
			readInitialScene();
		}
	}

	public static class NameListener implements FieldListener {

		private Tile tile;

		public NameListener(Tile tile) {
			this.tile = tile;
		}

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.NAME.equals(fieldName);
		}

		@Override
		public void modelChanged(FieldEvent event) {
			tile.setText((String) event.getValue());
		}
	}

}
