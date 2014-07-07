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
package es.eucm.ead.editor.editorui;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.control.actions.editor.CreateThumbnail;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

import java.io.File;
import java.io.IOException;

public class CreateThumbnailTest extends EditorUITest {

	public void setUp() throws IOException {
		File file = File.createTempFile("createthumbnailtest", "folder");
		file.delete();
		file.mkdir();
		controller.getEditorGameAssets().setLoadingPath(file.getAbsolutePath());

		FileHandle map = controller.getEditorGameAssets().resolve("map.png");
		map.write(ClassLoader
				.getSystemResourceAsStream("cooldemo/images/map.png"), false);

		FileHandle bee = controller.getEditorGameAssets().resolve("bee.png");
		bee.write(ClassLoader
				.getSystemResourceAsStream("cooldemo/images/bee.png"), false);
	}

	public FileHandle createThumbnail() {
		ModelEntity scene = new ModelEntity();
		Image image = new Image();
		image.setUri("map.png");
		scene.getComponents().add(image);

		ModelEntity child = new ModelEntity();
		child.setX(200);
		child.setY(200);
		child.setRotation(78);
		image = new Image();
		image.setUri("bee.png");
		child.getComponents().add(image);
		scene.getChildren().add(child);

		controller.getEditorGameAssets().finishLoading();
		controller.action(CreateThumbnail.class, "thumbnail.png", scene, 800,
				600);
		return controller.getEditorGameAssets().resolve("thumbnail.png");
	}

	@Override
	protected void builUI(final Group root) {
		try {
			setUp();
			FileHandle fh = createThumbnail();

			controller.getEditorGameAssets().get(fh.path(), Texture.class,
					new AssetLoadedCallback<Texture>() {
						@Override
						public void loaded(String fileName, Texture asset) {
							com.badlogic.gdx.scenes.scene2d.ui.Image image = new com.badlogic.gdx.scenes.scene2d.ui.Image(
									asset);
							root.addActor(image);
						}
					});
			controller.getEditorGameAssets().finishLoading();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new LwjglApplication(new CreateThumbnailTest(), "Scene Editor test",
				800, 600);
	}
}
