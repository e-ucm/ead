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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.io.File;
import java.io.IOException;

public class CreateThumbnailTest extends EditorUITest {

	public void setUp() throws IOException {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
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

	public String createThumbnail(String id, Scaling scaling) {

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
		controller.getModel().putResource(id, ResourceCategory.SCENE, scene);
		return Q.getThumbnailPath(scene);
		/* return Q.getThumbnailPath(scene, 250, 250, scaling).getPath(); */
	}

	@Override
	protected void builUI(Group root) {
		try {
			setUp();
			final Table table = new Table(controller.getApplicationAssets()
					.getSkin()).debug();
			table.columnDefaults(1).expand();
			table.add("Scaling");
			table.add("Result");
			table.row();
			ScrollPane scroll = new ScrollPane(table);
			scroll.setFillParent(true);

			for (Scaling scaling : Scaling.values()) {

				final String scalingName = scaling.toString();
				String path = createThumbnail("scene_" + scalingName, scaling);
				controller.getEditorGameAssets().get(path, Texture.class,
						new AssetLoadedCallback<Texture>() {
							@Override
							public void loaded(String fileName, Texture asset) {
								com.badlogic.gdx.scenes.scene2d.ui.Image image = new com.badlogic.gdx.scenes.scene2d.ui.Image(
										asset);
								image.setScaling(Scaling.fit);
								table.add(scalingName);
								table.add(image);
								table.row();
							}
						});
			}

			root.addActor(scroll);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new LwjglApplication(new CreateThumbnailTest(),
				"Create Thumbnail test", 1480, 800);
	}

	@Override
	public void render() {
		super.render();
	}
}
