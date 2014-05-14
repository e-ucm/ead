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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryEntity;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.components.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * A button displaying a {@link ModelEntity} (name, description, image...)
 */
public class ProjectButton extends GalleryEntity {

	private final long lastModified;
	private final String pathToJson;

	public ProjectButton(Vector2 viewport, I18N i18n, ModelEntity game,
			Skin skin) {
		super(Model.getComponent(game, Note.class), viewport, i18n, i18n
				.m("project"), null, skin, null);
		this.lastModified = 0;
		this.pathToJson = null;
	}

	public ProjectButton(Vector2 viewport, I18N i18n, ModelEntity game,
			Skin skin, Controller controller, Class<?> action, Object... args) {
		super(Model.getComponent(game, Note.class), viewport, i18n, i18n
				.m("project"), Model.getComponent(game, RepoElement.class),
				skin, controller, action, args);
		this.lastModified = 0;
		this.pathToJson = null;
	}

	public ProjectButton(Vector2 viewport, I18N i18n, ModelEntity game,
			Skin skin, long lastModified, String pathToJson) {
		super(Model.getComponent(game, Note.class), viewport, i18n, i18n
				.m("project"), Model.getComponent(game, RepoElement.class),
				skin, null);
		this.lastModified = lastModified;
		this.pathToJson = pathToJson;
	}

	/**
	 * Returns the last modified time in milliseconds for this file. Zero is
	 * returned if the file doesn't exist. Zero is returned for
	 * {@link FileType#Classpath} files. On Android, zero is returned for
	 * {@link FileType#Internal} files. On the desktop, zero is returned for
	 * {@link FileType#Internal} files on the classpath.
	 */
	public long getLastModified() {
		return this.lastModified;
	}

	/**
	 * @return the path to the .json file.
	 */
	public String getPathToJson() {
		return this.pathToJson;
	}
}
