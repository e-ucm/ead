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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import java.io.File;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.DeleteProject;
import es.eucm.ead.editor.control.actions.editor.DeleteProject.DeleteProjectListener;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.builders.mockup.menu.ProjectScreen;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * The gallery that will display our projects. Has a top tool bar and a gallery
 * grid.
 */
public class ProjectGallery extends BaseGallery<ProjectButton> implements
		PreferenceListener {

	private static final String ADD_PROJECT_BUTTON = "ic_new";
	private static final String PROJECT_FILE_ENDING = "game.json";
	private static final String PROJECTS = "ProjectGallery";
	private static final String IC_GO_BACK = "ic_goback";

	/**
	 * The element that is being deleted when the user chooses to delete
	 * elements.
	 */
	private ProjectButton deletingEntity;
	/**
	 * If true next time we show this view the gallery elements will be updated.
	 */
	private boolean needsUpdate;

	@Override
	public void initialize(Controller controller) {
		controller.getPreferences().addPreferenceListener(
				Preferences.RECENT_GAMES, this);
		this.needsUpdate = true;
		super.initialize(controller);
	}

	@Override
	protected Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller) {
		final Button backButton = new ToolbarButton(viewport, skin, IC_GO_BACK);
		backButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, InitialScreen.class));
		return backButton;
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ProjectButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		if (!this.needsUpdate) {
			Gdx.app.log(PROJECTS,
					"Projects are already loaded and nothing has changed!");
			return false;
		}

		final FileHandle projectsRoot = InitialScreen.MOCKUP_PROJECT_FILE;
		if (!projectsRoot.exists()) {
			projectsRoot.mkdirs();
		}

		elements.clear();
		final String projectEnding = File.separator + PROJECT_FILE_ENDING;
		final EditorGameAssets assets = controller.getEditorGameAssets();
		for (final FileHandle project : projectsRoot.list()) {
			if (project.isDirectory()) {
				final String rootProjectJsonPath = project.file()
						.getAbsolutePath();
				final String projectJsonPath = rootProjectJsonPath
						+ projectEnding;
				final FileHandle projectJsonFile = assets
						.absolute(projectJsonPath);
				if (projectJsonFile.exists()) {
					final ModelEntity proj = assets.fromJson(ModelEntity.class,
							projectJsonFile);
					elements.add(new ProjectButton(viewport, i18n, proj, skin,
							projectJsonFile.lastModified(), rootProjectJsonPath));
				}
			}
		}
		this.needsUpdate = false;
		Gdx.app.log(PROJECTS, "Projects loaded successfully!");
		return true;
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (Preferences.RECENT_GAMES.equals(preferenceName)) {
			Gdx.app.log(
					PROJECTS,
					"Recent projects changed, project gallery will be reloaded on demand (next time needed)!");
			this.needsUpdate = true;
		}
	}

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, final I18N i18n,
			Skin skin, final Controller controller) {
		final ModelEntity newGame = controller.getTemplates().createGame("",
				"", InitialScreen.DEFAULT_GAME_WIDTH,
				InitialScreen.DEFAULT_GAME_HEIGHT);
		final IconButton addProjectButton = new IconButton(viewport, skin,
				ADD_PROJECT_BUTTON);
		addProjectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(
						NewGame.class,
						InitialScreen.MOCKUP_PROJECT_FILE.file()
								.getAbsolutePath()
								+ File.separator
								+ i18n.m("project.untitled"), newGame);
				controller.action(ChangeView.class, ProjectScreen.class);
			}
		});
		addProjectButton.setPrefWidth(0.15f);
		return addProjectButton;
	}

	@Override
	protected void addSortingsAndComparators(Array<String> shortings,
			ObjectMap<String, Comparator<ProjectButton>> comparators, I18N i18n) {

		final String newer = i18n.m("general.gallery.more"), older = i18n
				.m("general.gallery.less");
		shortings.add(older);
		shortings.add(newer);
		comparators.put(newer, new Comparator<ProjectButton>() {
			@Override
			public int compare(ProjectButton o1, ProjectButton o2) {
				if (o1.getLastModified() == o2.getLastModified())
					return 0;
				return o1.getLastModified() > o2.getLastModified() ? 1 : -1;
			}

		});
		comparators.put(older, new Comparator<ProjectButton>() {
			@Override
			public int compare(ProjectButton o1, ProjectButton o2) {
				if (o1.getLastModified() == o2.getLastModified())
					return 0;
				return o1.getLastModified() > o2.getLastModified() ? -1 : 1;
			}
		});
	}

	@Override
	protected void entityClicked(InputEvent event, ProjectButton target,
			Controller controller, I18N i18n) {
		controller.action(OpenGame.class, target.getPathToJson());

		controller.action(ChangeView.class, ProjectScreen.class);
	}

	@Override
	protected void entityDeleted(ProjectButton entity, Controller controller) {
		this.deletingEntity = entity;
		controller.action(DeleteProject.class,
				entity.getPathToJson().replace(PROJECT_FILE_ENDING, ""),
				this.onProjectDeleted);
	}

	private final DeleteProjectListener onProjectDeleted = new DeleteProjectListener() {
		@Override
		public void projectDeleted(boolean succeed) {
			if (succeed)
				onEntityDeleted(ProjectGallery.this.deletingEntity);
		}
	};

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.project-gallery");
	}
}
