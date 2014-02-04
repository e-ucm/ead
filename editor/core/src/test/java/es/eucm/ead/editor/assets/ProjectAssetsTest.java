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

import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class ProjectAssetsTest {
	
	@Test
	public void testLoadProject(){
		MockApplication.initStatics();
		final ProjectAssets assets = new ProjectAssets(new MockFiles());
		assets.setLoadingPath("editor/emptyproject", true);
		assets.loadProject(new LoadedCallback() {
			@Override
			public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
				assertNotNull(assetManager.get(ProjectAssets.PROJECT_FILE, Project.class));
				assertNotNull(assetManager.get(ProjectAssets.GAME_FILE, Game.class));
				assertNotNull(assetManager.get(assets.convertSceneNameToPath("initial"), Scene.class));
			}
		});
		assets.finishLoading();
	}
}
