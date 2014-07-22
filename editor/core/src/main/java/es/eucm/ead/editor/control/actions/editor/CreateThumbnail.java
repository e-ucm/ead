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

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Creates a thumbnail for a {@link ModelEntity}.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link ModelEntity}</em> model entity for</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> width of the thumbnail</dd>
 * <dd><strong>args[2]</strong> <em>{@link Integer}</em> height for the
 * thumbnail</dd>
 * </dl>
 */
public class CreateThumbnail extends EditorAction {

	private Batch batch;

	private GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	private EditorGameAssets editorGameAssets;

	public CreateThumbnail() {
		super(true, false, ModelEntity.class, Integer.class, Integer.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		entitiesLoader = controller.getEngine().getEntitiesLoader();
		gameLoop = controller.getEngine().getGameLoop();
		editorGameAssets = controller.getEditorGameAssets();
		batch = controller.getPlatform().getBatch();
	}

	@Override
	public void perform(Object... args) {
		ModelEntity modelEntity = (ModelEntity) args[0];

		Thumbnail thumbnail = Q.getComponent(modelEntity, Thumbnail.class);
		if (thumbnail.getThumbnail() == null) {
			int width = (Integer) args[1];
			int height = (Integer) args[2];

			String thumbSavingPath = GameStructure.THUMBNAILS_PATH;
			FileHandle thumbSavingDir = editorGameAssets
					.resolve(thumbSavingPath);
			if (!thumbSavingDir.exists()) {
				thumbSavingDir.mkdirs();
			}
			String id = controller.getModel().getIdFor(modelEntity);
			FileHandle temp = editorGameAssets.resolve(id);
			thumbSavingPath += temp.nameWithoutExtension();
			FileHandle thumbSavingImage = editorGameAssets
					.resolve(thumbSavingPath + ".png");

			EngineEntity engineEntity = entitiesLoader
					.toEngineEntity(modelEntity);
			editorGameAssets.finishLoading();

			// Prepare group transformations, so thumbnail is centered
			Group group = engineEntity.getGroup();
			Vector2 scl = Scaling.fit.apply(group.getWidth(),
					group.getHeight(), width, height);
			group.setScale(scl.x / group.getWidth(), scl.y / group.getHeight());
			group.setPosition((width - scl.x) * .5f, (height - scl.y) * .5f);

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			group.draw(batch, 1.0f);
			batch.end();
			batch.flush();

			Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, width,
					height);

			// We must convert the OpenGL ES coordinates of the pixels (y-down)
			// to an y-up coordinate system before saving.
			final int w = pixmap.getWidth();
			final int h = pixmap.getHeight();
			final ByteBuffer pixels = pixmap.getPixels();
			byte[] lines = new byte[w * h * 4];
			final int numBytesPerLine = w * 4, height_index = h - 1;
			for (int i = 0; i < h; ++i) {
				pixels.position((height_index - i) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);

			PixmapIO.writePNG(thumbSavingImage, pixmap);
			pixmap.dispose();

			gameLoop.removeEntity(engineEntity);
			thumbnail.setThumbnail(thumbSavingImage.path());
		}
	}
}
