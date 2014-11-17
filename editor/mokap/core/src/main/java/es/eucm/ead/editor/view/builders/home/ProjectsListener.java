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
package es.eucm.ead.editor.view.builders.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.control.workers.LoadProjects;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProjectsListener implements WorkerListener,
		AssetLoadedCallback<Texture> {

	private Controller controller;

	private AtomicBoolean started = new AtomicBoolean(false);

	private Gallery gallery;

	private ObjectMap<String, TextureDrawable> pendingTextures = new ObjectMap<String, TextureDrawable>();

	public ProjectsListener(Controller controller, Gallery gallery) {
		this.controller = controller;
		this.gallery = gallery;
	}

	public void prepare() {
		controller.action(ExecuteWorker.class, LoadProjects.class, this);
	}

	@Override
	public void start() {
		started.set(true);
		gallery.clearChildren();
		pendingTextures.clear();
	}

	@Override
	public void result(Object... results) {
		if (started.get()) {
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
			}
			String title = results[1] == null || "".equals(results[1]) ? controller
					.getApplicationAssets().getI18N().m("untitled")
					: (String) results[1];
			Tile tile = WidgetBuilder.tile(image, title);
			WidgetBuilder.actionOnClick(tile, OpenGame.class, results[0]);
			Gdx.app.postRunnable(new AddImage(gallery, tile));
			controller.getApplicationAssets().get(thumbnailPath, Texture.class,
					this);
		}
	}

	@Override
	public void done() {
		started.set(false);
	}

	@Override
	public void error(Throwable ex) {
		started.set(false);
	}

	@Override
	public void cancelled() {
		started.set(false);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		TextureDrawable drawable = pendingTextures.get(fileName);
		drawable.setTexture(asset);
	}

	public static class AddImage implements Runnable {

		private Gallery gallery;

		private Actor image;

		public AddImage(Gallery gallery, Actor image) {
			this.gallery = gallery;
			this.image = image;
		}

		@Override
		public void run() {
			gallery.add(image);
		}
	}
}
