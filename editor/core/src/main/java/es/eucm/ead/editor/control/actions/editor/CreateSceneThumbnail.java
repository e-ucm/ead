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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.PixmapsToFile;
import es.eucm.ead.editor.control.workers.StepsWorker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

/**
 * Creates a thumbnail for a {@link ModelEntity}.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link ModelEntity} or {@link Group}</em>
 * model entity for the thumbnail, or a group containing the model entity for
 * the thumbnail</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> width of the thumbnail</dd>
 * <dd><strong>args[2]</strong> <em>{@link Integer}</em> height for the
 * <dd><strong>args[3]</strong> <em>{@link Scaling}</em> Optional. You can also
 * pass the {@link Scaling} that the resulting thumbnail should have. If no
 * scaling is specified, {@link Scaling#stretch} will be used.
 * </dl>
 */
public class CreateSceneThumbnail extends EditorAction {

	public static final int STEPS = 4;

	private Batch batch;

	private GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	private EditorGameAssets assets;

	private Model model;

	public CreateSceneThumbnail() {
		super(true, false, new Class[] { ModelEntity.class },
				new Class[] { Group.class });
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		model = controller.getModel();
		entitiesLoader = controller.getEngine().getEntitiesLoader();
		gameLoop = controller.getEngine().getGameLoop();
		assets = controller.getEditorGameAssets();
		batch = controller.getPlatform().getBatch();
	}

	@Override
	public void perform(Object... args) {
		ModelEntity scene;
		EngineEntity sceneEngineEntity = null;
		Group sceneGroup;

		if (args[0] instanceof ModelEntity) {
			scene = (ModelEntity) args[0];
			sceneEngineEntity = entitiesLoader.toEngineEntity(scene);
			sceneGroup = sceneEngineEntity.getGroup();
		} else {
			sceneGroup = (Group) args[0];
			scene = Q.getModelEntity(sceneGroup);
		}
		int width = (int) (Gdx.graphics.getHeight() - Gdx.graphics.getDensity() * 56);
		int height = (int) (Gdx.graphics.getHeight() / 2.15f);

		FrameBuffer frameBuffer = new FrameBuffer(Format.RGB888, width, height,
				false);
		frameBuffer.begin();

		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sceneGroup.draw(batch, 1.0f);
		batch.end();
		frameBuffer.end();

		if (sceneEngineEntity != null) {
			gameLoop.removeEntity(sceneEngineEntity);
		}

		FileHandle thumbnailsFolder = assets
				.resolveProject(ModelStructure.THUMBNAILS_PATH);
		thumbnailsFolder.mkdirs();

		String thumbnailPath = Q.getThumbnailPath(model.getIdFor(scene));

		Thumbnail thumbnail = Q.getComponent(scene, Thumbnail.class);
		thumbnail.setPath(thumbnailPath);

		controller.action(ExecuteWorker.class, StepsWorker.class,
				new CreateThumbnailWorkerListener(frameBuffer, thumbnailPath),
				STEPS, assets);
	}

	public class CreateThumbnailWorkerListener implements WorkerListener,
			BackgroundTaskListener<String> {

		private FrameBuffer frameBuffer;

		private String thumbnailPath;

		private Pixmap[] pixmaps;

		public CreateThumbnailWorkerListener(FrameBuffer frameBuffer,
				String thumbnailPath) {
			this.frameBuffer = frameBuffer;
			this.thumbnailPath = thumbnailPath;
		}

		// Worker listener
		@Override
		public void start() {
			pixmaps = new Pixmap[STEPS];
		}

		@Override
		public void result(Object... results) {
			int step = (Integer) results[0];
			Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			batch.draw(frameBuffer.getColorBufferTexture(), 0, 0);
			batch.end();

			int width = frameBuffer.getWidth() / STEPS;
			pixmaps[step] = ScreenUtils.getFrameBufferPixmap(step * width, 0,
					width, frameBuffer.getHeight());
		}

		@Override
		public void done() {
			frameBuffer.dispose();
			controller.getBackgroundExecutor().submit(
					new PixmapsToFile(pixmaps,
							assets.resolveProject(thumbnailPath)), this);
		}

		// Background listener

		@Override
		public void done(BackgroundExecutor backgroundExecutor, String result) {
			if (assets.isLoaded(thumbnailPath, Texture.class)) {
				assets.unload(thumbnailPath);
			}
			assets.load(thumbnailPath, Texture.class);
		}

		@Override
		public void error(Throwable ex) {
		}

		@Override
		public void cancelled() {
		}
	}
}
