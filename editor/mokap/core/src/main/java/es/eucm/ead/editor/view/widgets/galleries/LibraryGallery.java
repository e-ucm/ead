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
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.AddLibraryReference;
import es.eucm.ead.editor.control.workers.LoadLibraryEntities;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.SearchView;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;

/**
 * A gallery showing the library elements
 */
public class LibraryGallery extends ThumbnailsGallery implements
		WorkerListener, ModelView {

	private Controller controller;

	public LibraryGallery(float rows, int columns, Controller controller) {
		super(rows, columns, controller.getApplicationAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N(), controller
				.getApplicationAssets().getSkin().get(GalleryStyle.class),
				SkinConstants.IC_SEARCH);
		this.controller = controller;
	}

	@Override
	public void prepare() {
		controller.action(ExecuteWorker.class, LoadLibraryEntities.class, this,
				controller.getPlatform().getLibraryFolder());
	}

	@Override
	public void release() {
		controller.getWorkerExecutor().cancel(LoadLibraryEntities.class, this);
	}

	@Override
	protected void prepareAddButton(Actor actor) {
		WidgetBuilder.actionOnClick(actor, ChangeView.class, SearchView.class);
	}

	@Override
	protected void prepareGalleryItem(Actor actor, Object id) {
		WidgetBuilder.actionsOnClick(actor, new Class[] {
				AddLibraryReference.class, ChangeView.class }, new Object[][] {
				new Object[] { id }, new Object[] { SceneView.class } });
	}

	@Override
	public void start() {
		clear();
	}

	@Override
	public void result(Object... results) {
		addTile(results[0], (String) results[1], (String) results[2]);
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

}
