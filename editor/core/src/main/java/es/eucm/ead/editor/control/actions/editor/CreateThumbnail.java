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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ScreenUtils;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Creates a thumbnail for a {@link ModelEntity}.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> Project relative path for the
 * thumbnail</dd>
 * <dd><strong>args[1]</strong> <em>{@link ModelEntity}</em> model entity for</dd>
 * <dd><strong>args[2]</strong> <em>{@link Integer}</em> width of the thumbnail</dd>
 * <dd><strong>args[3]</strong> <em>{@link Integer}</em> height for the
 * thumbnail</dd>
 * </dl>
 */
public class CreateThumbnail extends EditorAction {

	private Batch batch;

	private GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	private EditorGameAssets editorGameAssets;

	public CreateThumbnail() {
		super(true, false, String.class, ModelEntity.class, Integer.class,
				Integer.class);
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
		String dstPath = (String) args[0];
		ModelEntity modelEntity = (ModelEntity) args[1];
		int width = (Integer) args[2];
		int height = (Integer) args[3];

		Gdx.gl.glViewport(0, 0, width, height);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		EngineEntity engineEntity = entitiesLoader.toEngineEntity(modelEntity);
		editorGameAssets.finishLoading();
		batch.begin();

		// Prepare group transformations, so thumbnail is centered
		Group group = engineEntity.getGroup();
		group.setPosition(0, 0);
		group.setSize(width, height);
		group.setScale(1, -1);
		group.setOrigin(width / 2.0f, height / 2.0f);

		group.draw(batch, 1.0f);

		batch.end();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, width, height);
		FileHandle dstFh = editorGameAssets.resolve(dstPath);
		PixmapIO.writePNG(dstFh, pixmap);
		pixmap.dispose();

		gameLoop.removeEntity(engineEntity);
	}
}
