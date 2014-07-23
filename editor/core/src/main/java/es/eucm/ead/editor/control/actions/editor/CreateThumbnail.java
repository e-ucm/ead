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
import com.badlogic.gdx.math.MathUtils;
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
import es.eucm.ead.schema.editor.components.GameData;
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
 * <dd><strong>args[3]</strong> <em>{@link Scaling}</em> Optional. You can also
 * pass the {@link Scaling} that the resulting thumbnail should have. If no
 * scaling is specified, {@link Scaling#stretch} will be used.
 * </dl>
 */
public class CreateThumbnail extends EditorAction {

	private Batch batch;

	private GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	private EditorGameAssets editorGameAssets;

	public CreateThumbnail() {
		super(true, false, new Class[] { ModelEntity.class, Integer.class,
				Integer.class }, new Class[] { ModelEntity.class,
				Integer.class, Integer.class, Scaling.class });
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
		int width = (Integer) args[1];
		int height = (Integer) args[2];
		Scaling scaling;
		if (args.length == 3) {
			scaling = Scaling.stretch;
		} else {
			scaling = (Scaling) args[3];
		}

		String thumbSavingPath = GameStructure.THUMBNAILS_PATH;
		FileHandle thumbSavingDir = editorGameAssets.resolve(thumbSavingPath);
		if (!thumbSavingDir.exists()) {
			thumbSavingDir.mkdirs();
		}
		String id = controller.getModel().getIdFor(modelEntity);
		FileHandle temp = editorGameAssets.resolve(id);
		thumbSavingPath += temp.nameWithoutExtension();
		FileHandle thumbSavingImage = editorGameAssets.resolve(thumbSavingPath
				+ ".png");

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(modelEntity);
		editorGameAssets.finishLoading();

		// Prepare group transformations, so thumbnail is centered
		Group group = engineEntity.getGroup();
		ModelEntity game = controller.getModel().getGame();
		GameData gameData;
		if (game != null) {
			gameData = Q.getComponent(game, GameData.class);
		} else {
			// Fix while testing via EditorUITest
			gameData = new GameData();
			gameData.setWidth(1280);
			gameData.setHeight(720);
		}

		float x, y, w, h;
		if (modelEntity.getChildren().size == 0
				&& modelEntity.getComponents().size == 0) {

			// The scene is completely empty, probably a new scene
			x = y = 0;
			w = width;
			h = height;
		} else {

			float currentWidth = group.getWidth() != 0 ? group.getWidth()
					: (gameData.getWidth() != 0 ? gameData.getWidth() : width);
			float currentHeight = group.getHeight() != 0 ? group.getHeight()
					: (gameData.getHeight() != 0 ? gameData.getHeight()
							: height);

			Vector2 scl = scaling.apply(currentWidth, currentHeight, width,
					height);
			w = scl.x;
			h = scl.y;
			x = Math.max((width - w) * .5f, 0f);
			y = Math.max((height - h) * .5f, 0f);
			group.setScale(w / currentWidth, h / currentHeight);
			group.setBounds(x, y, w, h);
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		group.draw(batch, 1.0f);
		batch.end();

		Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(MathUtils.round(x),
				MathUtils.round(y), MathUtils.round(w), MathUtils.round(h));

		// We must convert the OpenGL ES coordinates of the pixels (y-down)
		// to an y-up coordinate system before saving.
		int pixW = pixmap.getWidth();
		int pixH = pixmap.getHeight();
		final ByteBuffer pixels = pixmap.getPixels();
		int numBytesPerLine = pixW * 4, height_index = pixH - 1;
		byte[] lines = new byte[numBytesPerLine * pixH];
		for (int i = 0; i < pixH; ++i) {
			pixels.position((height_index - i) * numBytesPerLine);
			pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
		}
		pixels.clear();
		pixels.put(lines);

		PixmapIO.writePNG(thumbSavingImage, pixmap);
		pixmap.dispose();

		gameLoop.removeEntity(engineEntity);

		Thumbnail thumbnail = Q.getComponent(modelEntity, Thumbnail.class);
		thumbnail.setThumbnail(thumbSavingImage.path());
	}
}
