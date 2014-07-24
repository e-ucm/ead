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
package es.eucm.ead.editor.control.actions.editor;

import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.ViewEvent;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.JsonExtension;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Opens a game. Accepts one path (the path where the game is) as argument. If
 * no argument is passed along, the action uses {@link ChooseFile} to ask user
 * to select a folder in the file system
 */
public class OpenGame extends EditorAction implements FileChooserListener,
		AssetLoadedCallback<Object> {

	public OpenGame() {
		super(true, true, String.class);
	}

	@Override
	public boolean validate(Object... args) {
		return true;
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			controller.action(ChooseFile.class, true, this);
		} else {
			fileChosen(args[0].toString());
		}
	}

	@Override
	public void fileChosen(String path) {
		load(path);
	}

	/**
	 * Checks the folder selected for loading is correct and triggers the
	 * loading process (see {@link #doLoad(String, FileHandle)}) in consequence,
	 * or throws an {@link EditorActionException} if the game project is not
	 * valid.
	 * 
	 * @param gamePath
	 *            The full path of the project folder (e.g. /Users/a
	 *            User/eadgames/a game/)
	 */
	private void load(String gamePath) {
		if (gamePath != null) {
			FileHandle fileHandle = controller.getEditorGameAssets().absolute(
					gamePath);
			if (fileHandle.exists()) {
				doLoad(gamePath, fileHandle);
			} else {
				Gdx.app.error("OpenGame", "Invalid project folder: " + gamePath);
			}
		}
	}

	/**
	 * Does the actual loading. Iterates recursively through the {code
	 * fileHandle} provided, which is the game project folder to be loaded,
	 * scheduling all JSON files for loading through {@code EditorGameAssets}.
	 * 
	 * @param path
	 *            The full path of the game folder to be loaded, given as a
	 *            {@code String}.
	 * @param fileHandle
	 *            The game folder to be loaded, given as a {code FileHandle}.
	 */
	private void doLoad(String path, FileHandle fileHandle) {
		// Notify listeners that the current model is going to be unloaded
		controller.getModel().reset();
		controller.getModel().notify(
				new LoadEvent(LoadEvent.Type.UNLOADED, controller.getModel()));
		EditorGameAssets assets = controller.getEditorGameAssets();
		assets.setLoadingPath(path);
		controller.getPreferences().putString(Preferences.LAST_OPENED_GAME,
				path);
		loadAllJsonResources(fileHandle);
		assets.finishLoading();

		// Delete current command history
		if (!controller.getCommands().getCommandsStack().isEmpty()) {
			controller.getCommands().popStack(false);
		}
		controller.getCommands().pushStack();

		// Some checks before start editing
		checks(controller.getModel());

		controller.getModel().notify(
				new LoadEvent(LoadEvent.Type.LOADED, controller.getModel()));
		controller.action(AddRecentGame.class, path);
	}

	private void loadAllJsonResources(FileHandle fileHandle) {
		loadAllJsonResources(fileHandle, fileHandle);
	}

	private void loadAllJsonResources(FileHandle root, FileHandle folder) {
		for (FileHandle child : folder.list()) {
			if (child.isDirectory()) {
				loadAllJsonResources(root, child);
			} else if (JsonExtension.hasJsonExtension(child.extension())) {
				String path = child.path().substring(root.path().length() + 1);
				controller.getEditorGameAssets().get(path, Object.class, this);
			}
		}
	}

	@Override
	public void loaded(String fileName, Object asset) {
		controller.getModel().putResource(fileName, asset);
	}

	/**
	 * If we added a new piece of data, and old projects does not have it. This
	 * method tries to solve this problem.
	 * 
	 * @param model
	 */
	private void checks(Model model) {
		addParents(model);
		setEditionState(model);
		checkSceneMap(model);
	}

	private void addParents(Model model) {
		for (Entry<String, Object> entry : model.listNamedResources()) {
			if (entry.getValue() instanceof ModelEntity) {
				addParent((ModelEntity) entry.getValue(), null);
			}
		}
	}

	private void addParent(ModelEntity entity, ModelEntity parent) {
		Q.getComponent(entity, Parent.class).setParent(parent);
		for (ModelEntity child : entity.getChildren()) {
			addParent(child, entity);
		}
	}

	private void setEditionState(Model model) {
		final EditState editState = Q.getComponent(model.getGame(),
				EditState.class);
		if (editState.getView() != null) {
			try {
				Class viewClass = ClassReflection.forName(editState.getView());

				int i = 0;
				Object[] args = new Object[editState.getArguments().size + 1];
				args[i++] = viewClass;
				for (Object arg : editState.getArguments()) {
					args[i++] = arg;
				}

				controller.action(ChangeView.class, args);
			} catch (ReflectionException e) {
				Gdx.app.error("OpenGame",
						"Impossible to set view " + editState.getView());
			}
		}
		controller.getModel().addViewListener(new ModelListener<ViewEvent>() {
			@Override
			public void modelChanged(ViewEvent event) {
				editState.setView(event.getViewClass().getName());
				editState.getArguments().clear();
				editState.getArguments().addAll(event.getArgs());
			}
		});
	}

	/**
	 * Iterates through the scenes in the model and checks if there is a
	 * {@link Cell} in the {@link SceneMap} for that scene. If there isn't tries
	 * to add a new one.
	 * 
	 * @param model
	 */
	private void checkSceneMap(Model model) {
		SceneMap sceneMap = Q.getComponent(model.getGame(), SceneMap.class);
		Array<Cell> cells = sceneMap.getCells();

		Set<Entry<String, Object>> entrySet = model.getResources(
				ResourceCategory.SCENE).entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String sceneId = entry.getKey();
			if (Q.getCellFromId(sceneId, cells) == null) {

				Cell cell = Q.createCell(sceneId, sceneMap);
				if (cell != null) {
					cells.add(cell);
				} else {

					cell = new Cell();
					// There are no empty spaces in our map, let's automatically
					// create a new row of cells
					int rows = sceneMap.getRows();
					sceneMap.setRows(rows + 1);
					cell.setSceneId(sceneId);
					cell.setRow(rows);
					cell.setColumn(0);
					cells.add(cell);
				}
			}
		}
	}

}
