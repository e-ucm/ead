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
package es.eucm.ead.editor.assets;

import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.components.Note;
import es.eucm.ead.schema.game.GameMetadata;

/**
 * Loads files corresponding to {@link es.eucm.ead.schema.game.GameMetadata}
 * Created by Javier Torrente on 9/03/14.
 */
public class GameMetadataLoader extends LoaderWithModelAccess<GameMetadata> {

	public GameMetadataLoader(ProjectAssets assets) {
		super(assets, GameMetadata.class);

	}

	@Override
	protected void fillInDefaultValuesInContentLoaded(GameMetadata object,
			String fileName, LoaderParametersWithModel<GameMetadata> parameter) {
		// Note in GameMetadata cannot be null
		if (object.getNotes() == null) {
			object.setNotes(new Note());
		}

		// Now, check if scene order must be set with default values (scene ids
		// in the order they've been loaded)
		if (object.getSceneorder().size() < parameter.getScenes().size()) {
			for (String sceneId : parameter.getScenes().keySet()) {
				if (!object.getSceneorder().contains(sceneId)) {
					object.getSceneorder().add(sceneId);
				}
			}
		}
	}
}
