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
package es.eucm.ead.editor.actions;

import com.badlogic.gdx.graphics.Texture;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.actions.AddSceneElement;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.Image;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddSceneElementTest extends EditorActionTest {
	@Override
	protected String getEditorAction() {
		return AddSceneElement.NAME;
	}

	@Test
	public void testAddSceneElement() throws URISyntaxException {
		openEmpty();
		URL url = ClassLoader.getSystemResource("blank.png");
		File image = new File(url.toURI());
		platform.pushPath(image.getAbsolutePath());

		Scene scene = controller.getModel().getEditScene();
		int size = scene.getChildren().size();
		controller.action(action);
		assertEquals(scene.getChildren().size(), size + 1);
		SceneElement sceneElement = scene.getChildren().get(0);
		assertEquals(sceneElement.getRenderer().getClass(), Image.class);
		String newPath = ((Image) sceneElement.getRenderer()).getUri();
		assertTrue(newPath.startsWith(ProjectAssets.IMAGES_FOLDER + "blank"));

		controller.getProjectAssets().finishLoading();
		assertTrue(controller.getProjectAssets().isLoaded(newPath,
				Texture.class));
	}
}
