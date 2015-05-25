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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.LibraryManager;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.assets.Sound;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Reference;
import es.eucm.ead.schema.components.controls.ImageButton;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.*;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * <p>
 * Imports a repository element to the current scene.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0] </strong>
 * <em>{@link es.eucm.ead.schema.editor.components.repo.RepoElement}</em> The
 * {@link es.eucm.ead.schema.editor.components.repo.RepoElement} already
 * downloaded to the local library.</dd>
 * </dl>
 * </p>
 * 
 */
public class ImportRepoElement extends ModelAction {

	private AddSceneElement addSceneElement;

	public ImportRepoElement() {
		super(true, false, RepoElement.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		addSceneElement = controller.getActions().getAction(
				AddSceneElement.class);
	}

	@Override
	public Command perform(Object... args) {
		RepoElement elem = (RepoElement) args[0];
		LibraryManager libraryManager = controller.getLibraryManager();

		FileHandle contentsFolder = libraryManager
				.getRepoElementContentsFolder(elem);
		FileHandle entityFile = contentsFolder
				.child(ModelStructure.ENTITY_FILE);

		FileHandle repoElementProjectFolder = libraryManager
				.getRepoElementProjectFolder(elem);

		if (!repoElementProjectFolder.isDirectory()) {
			contentsFolder.copyTo(repoElementProjectFolder);
			repoElementProjectFolder.child(ModelStructure.ENTITY_FILE).delete();
		}
		ModelEntity entity = controller.getEditorGameAssets().fromJson(
				ModelEntity.class, entityFile);

		String prefixPath = repoElementProjectFolder.path().substring(
				controller.getEditorGameAssets().getLoadingPath().length());
		if (!prefixPath.endsWith("/")) {
			prefixPath += "/";
		}
		addReferencesPrefix(entity, prefixPath);

		return addSceneElement.perform(entity);
	}

	private void addReferencesPrefix(ModelEntity entity, String prefix) {
		for (ModelComponent modelComponent : entity.getComponents()) {
			parseComponent(modelComponent, prefix);
		}
		for (ModelEntity modelEntity : entity.getChildren()) {
			addReferencesPrefix(modelEntity, prefix);
		}
	}

	private void parseComponent(ModelComponent component, String prefix) {
		if (component instanceof Sound) {
			Sound sound = (Sound) component;
			sound.setUri(prefix + sound.getUri());
		} else if (component instanceof ImageButton) {
			ImageButton imageButton = (ImageButton) component;
			imageButton.setImageDown(prefix + imageButton.getImageDown());
			imageButton.setImageUp(prefix + imageButton.getImageUp());
		} else if (component instanceof Image) {
			Image image = (Image) component;
			image.setUri(prefix + image.getUri());
		} else if (component instanceof SpineAnimation) {
			SpineAnimation spineAnimation = (SpineAnimation) component;
			spineAnimation.setUri(prefix + spineAnimation.getUri());
		} else if (component instanceof State) {
			State state = (State) component;
			parseComponent(state.getRenderer(), prefix);
		} else if (component instanceof Frames) {
			Frames frames = (Frames) component;
			for (Frame frame : frames.getFrames()) {
				parseComponent(frame.getRenderer(), prefix);
			}
		} else if (component instanceof States) {
			States states = (States) component;
			for (State state : states.getStates()) {
				parseComponent(state, prefix);
			}
		}
	}
}
