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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.AddRepoElementReference;
import es.eucm.ead.editor.control.workers.SearchRepo;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.RepoTile.RepoTileListener;
import es.eucm.ead.editor.view.widgets.ScrollPane;
import es.eucm.ead.editor.view.widgets.ScrollPane.ScrollPaneListener;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.RepoGallery;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;

/**
 * File view. A list with the children of a given file.
 */
public class SearchView implements ViewBuilder, BackListener, WorkerListener {

	private LinearLayout view;
	private RepoGallery repoGallery;
	private Controller controller;
	private TextField textField;

	private SearchResponse currentResponse;
	private boolean canSearch;

	@Override
	public void initialize(Controller control) {
		this.controller = control;
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		I18N i18N = assets.getI18N();
		view = new LinearLayout(false);
		view.add(buildToolbar(skin, i18N)).expandX();
		view.add(repoGallery = new RepoGallery(2.65f, 4, controller))
				.expand(true, true).top();

		repoGallery.addListener(new ScrollPaneListener() {
			@Override
			public void hitEdge(ScrollPane scrollPane, Edge edge) {
				if (canSearch && edge == Edge.BOTTOM) {
					search();
				}
			}
		});

		repoGallery.addListener(new RepoTileListener() {

			@Override
			public void clickedInLibrary(RepoTileEvent event) {
				controller.action(AddRepoElementReference.class,
						event.getRepoElement());
				controller.action(ChangeView.class, SceneView.class);
			}

		});
	}

	@Override
	public Actor getView(Object... args) {
		return view;
	}

	private void search() {
		canSearch = false;
		if (currentResponse != null) {
			controller.action(ExecuteWorker.class, SearchRepo.class, this,
					textField.getText(), currentResponse.getSearchCursor());
		} else {
			controller.action(ExecuteWorker.class, SearchRepo.class, this,
					textField.getText());
		}
	}

	@Override
	public void release(Controller controller) {
		controller.getWorkerExecutor().cancel(SearchRepo.class, this);
		currentResponse = null;
		canSearch = true;
	}

	private Actor buildToolbar(Skin skin, I18N i18N) {
		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);

		LinearLayout project = new LinearLayout(true);
		IconButton back = WidgetBuilder.toolbarIcon(SkinConstants.IC_GO, null);
		project.add(back);
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onBackPressed();
			}
		});
		IconButton search = WidgetBuilder.toolbarIcon(SkinConstants.IC_SEARCH,
				i18N.m("search"));
		project.add(search);
		textField = new TextField("", skin);
		textField.addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					repoGallery.clear();
					controller.action(ExecuteWorker.class, SearchRepo.class,
							SearchView.this, textField.getText());
					hideTextField();
					return true;
				}
				return false;
			}
		});
		project.add(textField).expandX();
		toolbar.addWidgets(project);
		return toolbar;
	}

	private void hideTextField() {
		Stage stage = textField.getStage();
		stage.unfocus(textField);
		stage.setKeyboardFocus(null);
	}

	@Override
	public boolean onBackPressed() {
		controller.action(ChangeView.class, ResourcesView.class);
		return true;
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		Object firstResult = results[0];
		if (!(firstResult instanceof SearchResponse)) {
			RepoElement elem = (RepoElement) firstResult;
			if (!controller.getLibraryManager().isMokap(elem)) {
				Pixmap repoThumbnail = (Pixmap) results[1];
				Texture thumbnailTex = new Texture(repoThumbnail);
				repoGallery.add(elem, repoThumbnail, thumbnailTex);
			}
		} else {
			currentResponse = (SearchResponse) firstResult;
		}
	}

	@Override
	public void done() {
		canSearch = true;
	}

	@Override
	public void error(Throwable ex) {

	}

	@Override
	public void cancelled() {
		canSearch = true;
	}
}
