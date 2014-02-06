/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.platform;

import com.badlogic.gdx.Files;

import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.assets.loaders.ProjectLoader;
import es.eucm.ead.editor.view.widgets.engine.wrappers.SceneElementEditorObject;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.schema.actors.SceneElement;

public class EditorFactory extends Assets {

	public EditorFactory(Files files) {
		super(files);
		setExtraBindings();
	}

	private void setExtraBindings() {
		bind("sceneelement", SceneElement.class, SceneElementEditorObject.class);
	}

	@Override
	protected void setLoaders() {
		super.setLoaders();
		setLoader(Project.class, new ProjectLoader(this));
	}
}
