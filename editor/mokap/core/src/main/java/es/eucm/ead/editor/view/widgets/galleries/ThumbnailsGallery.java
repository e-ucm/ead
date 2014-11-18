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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.Worker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;

/**
 * Created by angel on 18/11/14.
 */
public abstract class ThumbnailsGallery extends AbstractWidget implements
		WorkerListener, AssetLoadedCallback<Texture> {

	private Controller controller;

	private Assets assets;

	private Class<? extends Worker> workerClass;

	private ObjectMap<String, TextureDrawable> pendingTextures = new ObjectMap<String, TextureDrawable>();

	protected Gallery gallery;

	private Button add;

	/**
	 * @param workerClass
	 *            a worker that runs in the UI thread and give as results an id
	 *            {@link String}, a title {@link String}, a thumbnail path
	 *            {@link String}
	 */
	public ThumbnailsGallery(float rowHeight, int columns,
			Controller controller, Class<? extends Worker> workerClass,
			Assets assets) {
		addActor(gallery = new Gallery(rowHeight, columns));
		addActor(add = WidgetBuilder.button(SkinConstants.STYLE_ADD));
		prepareAddButton(add);
		this.controller = controller;
		this.workerClass = workerClass;
		this.assets = assets;
	}

	public void prepare() {
		controller.action(ExecuteWorker.class, workerClass, this);
	}

	@Override
	public void start() {
		gallery.clearChildren();
		pendingTextures.clear();
	}

	@Override
	public void layout() {
		setBounds(gallery, 0, 0, getWidth(), getHeight());
		float width = getPrefWidth(add);
		setBounds(add, getWidth() - width - WidgetBuilder.dpToPixels(32),
				WidgetBuilder.dpToPixels(32), width, getPrefHeight(add));
	}

	@Override
	public void result(Object... results) {
		String id = (String) results[0];
		String title = (String) results[1];
		String thumbnailPath = (String) results[2];

		Image image;
		if (thumbnailPath == null) {
			image = new Image(controller.getApplicationAssets().getSkin(),
					SkinConstants.DRAWABLE_LOGO);
			image.setScaling(Scaling.fit);
		} else {
			TextureDrawable thumbnail = new TextureDrawable();
			image = new Image(thumbnail);
			pendingTextures.put(thumbnailPath, thumbnail);
			assets.get(thumbnailPath, Texture.class, this);
		}
		title = title == null || "".equals(results[1]) ? controller
				.getApplicationAssets().getI18N().m("untitled")
				: (String) results[1];
		Tile tile = WidgetBuilder.tile(image, title);
		prepareGalleryItem(tile, id);
		gallery.add(tile);
	}

	protected abstract void prepareAddButton(Actor actor);

	protected abstract void prepareGalleryItem(Actor actor, String id);

	@Override
	public void done() {
	}

	@Override
	public void error(Throwable ex) {
	}

	@Override
	public void cancelled() {
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		TextureDrawable drawable = pendingTextures.get(fileName);
		drawable.setTexture(asset);
	}
}
