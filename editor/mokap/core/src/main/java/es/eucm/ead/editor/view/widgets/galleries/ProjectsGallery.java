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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.*;
import es.eucm.ead.editor.control.actions.model.DeleteProject;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.workers.LoadProjects;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class ProjectsGallery extends ContextMenuGallery implements
		WorkerListener, BackgroundTaskListener<Object[]>, ModelView {

	private Controller controller;

	private ResourceListener resourceListener = new ResourceListener();

	private ProjectListener projectListener = new ProjectListener();

	private final Array<String> projectNames = new Array<String>();

	private Label noProjects;

	public ProjectsGallery(float rows, int columns, Controller control) {
		super(rows, columns, control.getApplicationAssets(), control,
				SkinConstants.IC_ADD_MOKAP);
		this.controller = control;

		Button edit = WidgetBuilder.button(SkinConstants.IC_EDIT,
				i18N.m("edit"), SkinConstants.STYLE_CONTEXT, OpenProject.class);

		Button clone = WidgetBuilder.button(SkinConstants.IC_CLONE,
				i18N.m("clone"), SkinConstants.STYLE_CONTEXT,
				CloneProject.class, this, projectNames);

		Button delete = WidgetBuilder.button(SkinConstants.IC_DELETE,
				i18N.m("delete"), SkinConstants.STYLE_CONTEXT);

		float time = 10.0f;
		// Order matters here. Making ShowToast execute first adds a command
		// listener necessary for the correct execution of Delete Project
		WidgetBuilder.actionsOnClick(
				delete,
				new Class[] { ShowToast.class, DeleteProject.class },
				new Object[][] {
						new Object[] {
								controller.getApplicationAssets().getI18N()
										.m("project.deleted"), time },
						new Object[] { time } });

		Button action = getActionButton();
		action.setTransform(true);
		action.setOrigin(action.getWidth() * 0.5f, action.getHeight() * 0.5f);

		setContextMenu(edit, clone, delete);

		noProjects = new Label(i18N.m("no.mokaps"), skin);
	}

	@Override
	public Cell addTile(Object id, String title, String thumbnailPath) {
		String tileId = id.toString();
		if (tileId.endsWith(ModelStructure.GAME_FILE)) {
			tileId = tileId.substring(0, tileId.length()
					- ModelStructure.GAME_FILE.length());
		}
		projectNames.add(title);
		return super.addTile(tileId, title, thumbnailPath);
	}

	@Override
	public void prepare() {
		controller.getModel().addResourceListener(projectListener);
		controller.getModel().addSelectionListener(resourceListener);
	}

	@Override
	public void release() {
		controller.getModel().removeResourceListener(projectListener);
		controller.getModel().removeSelectionListener(resourceListener);
	}

	/**
	 * Loads the projects in the gallery
	 */
	@Override
	public void loadContents(String search) {
		clear();
		controller.action(ExecuteWorker.class, LoadProjects.class, this);
	}

	@Override
	protected void prepareActionButton(Actor actor) {
		WidgetBuilder.actionOnClick(actor, AddProject.class);
	}

	@Override
	protected void prepareGalleryItem(Actor actor, Object id) {
		WidgetBuilder.actionOnClick(actor, OpenProject.class, id);
	}

	@Override
	public void tileLongPressed(String tileName) {
		controller.action(SetSelection.class, null, Selection.RESOURCE,
				tileName);
	}

	@Override
	public void contextMenuHidden() {
		controller.action(SetSelection.class, null, Selection.RESOURCE);
	}

	// Worker listener

	@Override
	public void start() {
	}

	@Override
	public void result(Object... results) {
		addTile(results[0], (String) results[1], (String) results[2]);
		animateActionButton(false);
	}

	@Override
	public void done() {
		animateActionButton(true);
	}

	@Override
	public void error(Throwable ex) {
	}

	@Override
	public void cancelled() {
	}

	// Background listener

	@Override
	public void done(BackgroundExecutor backgroundExecutor, Object[] result) {
		result(result);

	}

	private void readResource() {
		String resource = (String) controller.getModel().getSelection()
				.getSingle(Selection.RESOURCE);
		gallery.uncheckAll();
		if (resource != null) {
			Cell cell = (Cell) EngineUtils.getDirectChild(gallery.getGrid(),
					findActor(resource));
			if (cell != null) {
				cell.checked(true);
			}
		}
	}

	private void addProject(String id) {
		if (!id.endsWith("/")) {
			id += "/";
		}
		final String projectPath = id;
		controller.getEditorGameAssets().get(
				projectPath + ModelStructure.GAME_FILE, Object.class,
				new AssetLoadedCallback<Object>() {
					@Override
					public void loaded(String fileName, Object asset) {
						ModelEntity game = (ModelEntity) asset;
						addTile(fileName,
								Q.getTitle(game),
								projectPath
										+ Q.getThumbnailPath(Q.getComponent(
												game, GameData.class)
												.getInitialScene()));
					}

					@Override
					public void error(String fileName, Class type,
							Throwable exception) {
						Gdx.app.error("ProjectsGallery", "Invalid game in "
								+ fileName);
					}
				});
	}

	private void removeProject(final String id) {
		String gameJson = id;
		if (!gameJson.endsWith(ModelStructure.GAME_FILE)) {
			if (!gameJson.endsWith("/")) {
				gameJson += "/";
			}
			gameJson += ModelStructure.GAME_FILE;
		}
		controller.getEditorGameAssets().get(gameJson, Object.class,
				new AssetLoadedCallback<Object>() {
					@Override
					public void loaded(String fileName, Object asset) {
						ModelEntity game = (ModelEntity) asset;
						projectNames.removeValue(Q.getTitle(game), false);
						Actor actor = findActor(id);
						if (actor != null) {
							actor.getParent().remove();
						}
					}

					@Override
					public void error(String fileName, Class type,
							Throwable exception) {
						Gdx.app.error("ProjectGallery", "Invalid game in "
								+ fileName);
					}
				});
	}

	class ProjectListener implements ModelListener<ResourceEvent> {

		@Override
		public void modelChanged(ResourceEvent event) {
			if (event.getCategory() == ResourceCategory.GAME) {
				switch (event.getType()) {
				case ADDED:
					addProject(event.getId());
					animateActionButton(false);
					break;
				case REMOVED:
					removeProject(event.getId());
					animateActionButton(true);
					break;
				}
			}
		}
	}

	class ResourceListener implements SelectionListener {

		@Override
		public boolean listenToContext(String contextId) {
			return Selection.RESOURCE.equals(contextId);
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			if (event.getType() == Type.FOCUSED) {
				readResource();
			}
		}
	}

	private void animateActionButton(boolean animate) {
		getActionButton().clearActions();
		getActionButton().setScale(1f);
		getActionButton().setRotation(0);
		if (animate && gallery.getGrid().getChildren().size == 0) {
			getActionButton().addAction(
					Actions.forever(Actions.parallel(Actions.sequence(
							Actions.scaleTo(1.5f, 1.5f, 0.6f),
							Actions.scaleTo(1f, 1f, 0.6f)), Actions.sequence(
							Actions.rotateBy(45f, 0.3f),
							Actions.rotateBy(-90f, 0.6f),
							Actions.rotateBy(45f, 0.3f)), Actions.delay(3f))));
			noProjects.setPosition(Math.round(getWidth() * 0.5f
					- noProjects.getWidth() * 0.5f), getHeight()
					- WidgetBuilder.dpToPixels(48));
			addActor(noProjects);
		} else {
			noProjects.remove();
		}
	}
}
