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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.workers.LoadFiles;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.schemax.ModelStructure;

public class ProjectResourcesGallery extends ThumbnailsGallery implements
		WorkerListener, ModelView {

	private Array<String> loadedThumbnails = new Array<String>();
	private Controller controller;

	public ProjectResourcesGallery(float rows, int columns,
			Controller controller) {
		super(rows, columns, controller.getApplicationAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N(), controller
				.getApplicationAssets().getSkin().get(GalleryStyle.class));
		this.controller = controller;
	}

	@Override
	public void prepare() {
		clear();
		controller.action(ExecuteWorker.class, LoadFiles.class, this,
				controller.getEditorGameAssets().getLoadingPath()
						+ ModelStructure.IMAGES_FOLDER);
	}

	@Override
	public void loadContents(String search) {
		prepare();
	}

	@Override
	public void release() {
		for (String loadedThumbnail : loadedThumbnails) {
			if (assets.isLoaded(loadedThumbnail, Texture.class)) {
				assets.unload(loadedThumbnail);
			}
		}
	}

	@Override
	protected void prepareActionButton(Actor actor) {
	}

	@Override
	protected void prepareGalleryItem(Actor actor, final Object elemPath) {
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				addElementAndChangeView(elemPath.toString());
			}
		});
	}

	@Override
	public void start() {
		clear();
	}

	@Override
	public void result(Object... results) {
		String path = (String) results[0];
		loadedThumbnails.add(path);
		addTile(path, (String) results[1], (String) results[2]);
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

	private void addElementAndChangeView(String elemPath) {
		controller.action(AddSceneElement.class, controller.getTemplates()
				.createSceneElement(elemPath, false));
		controller.action(ChangeView.class, SceneView.class);
	}
}
