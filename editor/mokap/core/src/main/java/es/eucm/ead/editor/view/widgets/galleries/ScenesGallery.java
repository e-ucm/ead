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
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.AddScene;
import es.eucm.ead.editor.control.actions.model.scene.SetSelectedScene;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadingListener;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.Map.Entry;

public class ScenesGallery extends ThumbnailsGallery implements
		ModelListener<ResourceEvent>, ModelView, AssetLoadingListener<Texture> {

	protected Controller controller;

	public ScenesGallery(float rowHeight, int columns, Controller controller,
			String galleryStyle) {
		super(rowHeight, columns, controller.getEditorGameAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N(), galleryStyle);
		this.controller = controller;
	}

	public ScenesGallery(float rowHeight, int columns, Controller controller) {
		super(rowHeight, columns, controller.getEditorGameAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N());
		this.controller = controller;
	}

	@Override
	public void prepare() {
		clear();
		controller.getModel().addResourceListener(this);
		for (Entry<String, Resource> entry : controller.getModel()
				.getResources(ResourceCategory.SCENE).entrySet()) {
			addScene(entry.getKey(), (ModelEntity) entry.getValue().getObject());
		}
		controller.getEditorGameAssets().addAssetListener(this);
	}

	@Override
	public void release() {
		controller.getModel().removeResourceListener(this);
		controller.getEditorGameAssets().removeAssetListener(this);
	}

	@Override
	protected void prepareAddButton(Actor actor) {
		WidgetBuilder.actionOnClick(actor, AddScene.class);
	}

	@Override
	protected void prepareGalleryItem(Actor actor, String id) {
		WidgetBuilder.actionOnClick(actor, SetSelectedScene.class, id,
				controller.getModel().getResourceObject(id));
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
		}
	}

	private void addScene(String id, ModelEntity scene) {
		addTile(id, Q.getName(scene, ""), Q
				.getComponent(scene, Thumbnail.class).getPath());
	}

	private void removeScene(String id) {
		Actor tile = findActor(id);
		if (tile != null) {
			tile.remove();
		}
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
}
