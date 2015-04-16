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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.ChangeSceneBackgroundShader;
import es.eucm.ead.editor.control.workers.LoadShaders;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;

public class BackgroundShadersGallery extends ThumbnailsGallery implements
		WorkerListener, ModelView {

	private Controller controller;

	public BackgroundShadersGallery(float rows, int columns,
			Controller controller) {
		super(rows, columns, controller.getApplicationAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N(), controller
				.getApplicationAssets().getSkin().get(GalleryStyle.class));
		this.controller = controller;
	}

	@Override
	public void prepare() {
		controller.action(ExecuteWorker.class, LoadShaders.class, this);
	}

	private void emptyTile() {
		Drawable drawable = controller.getApplicationAssets().getSkin()
				.get(SkinConstants.DRAWABLE_BLANK, Drawable.class);
		Tile tile = WidgetBuilder.tile(null, drawable);
		tile.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				addElementAndChangeView(null);
			}
		});
		gallery.add(tile);
	}

	@Override
	public void loadContents(String search) {
		prepare();
	}

	@Override
	public void release() {
	}

	@Override
	protected void prepareActionButton(Actor actor) {
	}

	@Override
	protected void prepareGalleryItem(Actor actor, final Object shaderPath) {
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				addElementAndChangeView(shaderPath.toString());
			}
		});
	}

	@Override
	public void start() {
		clear();
		emptyTile();
	}

	@Override
	public void result(Object... results) {
		String shaderPath = (String) results[0];
		addTile(shaderPath, null, (String) results[1]);
	}

	@Override
	public void done() {

	}

	@Override
	public void error(Throwable ex) {

	}

	@Override
	public void cancelled() {

	}

	private void addElementAndChangeView(String shaderPath) {
		controller.action(ChangeSceneBackgroundShader.class, shaderPath);
		controller.action(ChangeView.class, SceneView.class);
	}
}