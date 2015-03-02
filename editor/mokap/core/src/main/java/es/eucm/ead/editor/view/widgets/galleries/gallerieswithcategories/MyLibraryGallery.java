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
package es.eucm.ead.editor.view.widgets.galleries.gallerieswithcategories;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.Play;
import es.eucm.ead.editor.control.workers.LoadLibraryEntities;
import es.eucm.ead.editor.control.workers.Worker;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;
import es.eucm.ead.schemax.ModelStructure;

public class MyLibraryGallery extends ThumbnailsGallery implements
		Worker.WorkerListener {

	protected Array<CategoryButton> categories;

	protected Controller controller;

	protected int columns;

	protected int count;

	public MyLibraryGallery(float rows, int columns, Controller controller) {
		super(rows, columns, controller.getApplicationAssets(), controller
				.getApplicationAssets().getSkin(), controller
				.getApplicationAssets().getI18N());

		this.controller = controller;
		this.categories = new Array<CategoryButton>();

		this.columns = columns;

		this.count = 0;

		searchEnabled = false;
	}

	public Actor addCategory(String name, String icon, String categoryName,
			Color color) {
		CategoryButton cat = new CategoryButton(icon, name, categoryName,
				color, skin);
		categories.add(cat);

		return cat;
	}

	protected String search;

	@Override
	public void loadContents(String search) {
		clear();
		count = 0;
		for (Actor button : categories) {
			gallery.addOriginal(button);
		}
		loadContent(search);
	}

	protected void loadContent(String search) {
		if (count < categories.size) {
			controller.action(ExecuteWorker.class, LoadLibraryEntities.class,
					this, categories.get(count).getCategoryName());
		}
	}

	protected void finishLoadContent() {
		controller.getWorkerExecutor().cancel(LoadLibraryEntities.class, this);
		done();
	}

	private void addCategoryLabel() {
		int toFill = columns - gallery.getGrid().getChildren().size % columns;
		for (int i = 0; i < toFill; i++) {
			gallery.addSpace();
		}
		CategoryButton aux = categories.get(count);
		Label cat = new Label(aux.getButtonText(), skin,
				SkinConstants.STYLE_BIG);
		gallery.addOriginal(cat);
		gallery.addSpace();
		Container<TextButton> more = new Container<TextButton>();
		TextButton button = new TextButton(i18N.m("more").toUpperCase(), skin,
				SkinConstants.STYLE_CATEGORY);
		Color color = aux.getColor();
		color.a = 1;
		button.setColor(color);
		more.setActor(button);
		for (EventListener listener : aux.getListeners()) {
			button.addListener(listener);
		}
		more.right();
		gallery.addOriginal(more);
	}

	@Override
	public void start() {

	}

	private int number = 0;

	@Override
	public void result(Object... results) {
		if (number < 3) {
			Object firstResult = results[0];
			if (!(firstResult instanceof SearchResponse)) {

				// If there are results and still has not shown any
				if (number == 0 && count < categories.size) {
					addCategoryLabel();
				}

				addResultTile(results);
				number++;
			}
		} else {
			finishLoadContent();
		}
	}

	protected void addResultTile(Object... results) {
		addTile(results[0], (String) results[1], (String) results[2]);
	}

	@Override
	public void done() {
		count++;
		number = 0;
		loadContent(search);
	}

	@Override
	public void error(Throwable ex) {

	}

	@Override
	public void cancelled() {

	}

	@Override
	protected void prepareActionButton(Actor actor) {

	}

	@Override
	protected void prepareGalleryItem(Actor actor, Object id) {
		if (id instanceof RepoElement) {
			RepoElement selected = (RepoElement) id;
			if (selected.getCategoryList()
					.contains(RepoCategories.MOKAPS, true)) {
				WidgetBuilder
						.actionsOnClick(
								actor,
								new Class[] { Play.class },
								new Object[][] {
										new Object[] { controller
												.getLibraryManager()
												.getRepoElementLibraryFolder(
														(RepoElement) id)
												.file().getAbsolutePath()
												+ "/"
												+ ModelStructure.CONTENTS_FOLDER },
										new Object[] {} });
			}
		}
	}

}