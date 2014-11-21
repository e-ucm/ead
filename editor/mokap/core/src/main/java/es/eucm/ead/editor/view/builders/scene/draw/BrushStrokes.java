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
package es.eucm.ead.editor.view.builders.scene.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.utils.GeometryUtils;
import es.eucm.ead.editor.view.builders.scene.SceneEditor;
import es.eucm.ead.editor.view.builders.scene.draw.MeshHelper.PixmapRegion;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them to
 * a texture and manages the necessary {@link Pixmap pixmaps} to perform
 * undo/redo actions, erase and save it as a {@link ModelEntity}
 */
public class BrushStrokes extends AbstractWidget {

	private static final int MAX_COMMANDS = 50;
	private static final Vector2 TEMP = new Vector2();

	private static final float MAX_RADIUS_CM = .5F;
	private static final float INITIAL_RADIUS_CM = MAX_RADIUS_CM * .5F;
	private static final Color INITIAL_COLOR = Color.GREEN;
	private static final Mode INITIAL_MODE = Mode.DRAW;

	private final Vector2 resultOrigin = new Vector2(),
			resultSize = new Vector2();
	private final Array<Actor> actors = new Array<Actor>(1);

	public enum Mode {
		DRAW, ERASE
	}

	private ModelEntity toEdit;
	private FileHandle savePath;

	private final Controller controller;
	private final MeshHelper mesh;
	private Group container;
	private float targetX, targetY;
	private SceneEditor sceneEditor;

	/**
	 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them
	 * to a texture and manages the necessary {@link Pixmap pixmaps} to perform
	 * undo/redo actions, erase and save it as a {@link ModelEntity}
	 */
	public BrushStrokes(Controller control, Group container,
			SceneEditor sceneEditor) {
		this.container = container;
		this.mesh = new MeshHelper(this, control);
		mesh.setColor(INITIAL_COLOR);
		mesh.setMaxDrawRadius(getMaxRadius());
		mesh.setRadius(getInitialRadius());
		this.controller = control;
		this.sceneEditor = sceneEditor;
		addCaptureListener(drawListener);
		setMode(INITIAL_MODE);
	}

	public float getMaxRadius() {
		return cmToXPixels(MAX_RADIUS_CM);
	}

	public float getInitialRadius() {
		return cmToXPixels(INITIAL_RADIUS_CM);
	}

	public Color getInitialColor() {
		return INITIAL_COLOR;
	}

	/**
	 * Sets the behavior of this widget.
	 * 
	 * @param mode
	 */
	public void setMode(Mode mode) {
		mesh.setErasing(mode == Mode.ERASE);
		fireModeChanged(mode);
	}

	private void fireModeChanged(Mode mode) {
		ModeEvent event = Pools.obtain(ModeEvent.class);
		event.mode = mode;
		fire(event);
		Pools.free(mode);
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		batch.setColor(Color.WHITE);
		this.mesh.draw(batch, parentAlpha);
	}

	/**
	 * Attempts to save the contents of the {@link #mesh} to a file located in
	 * the {@link GameStructure#IMAGES_FOLDER}.
	 * 
	 * @return true if everything went OK.
	 */
	public boolean save() {
		if (!this.mesh.hasSomethingToSave())
			return false;

		// Get a correct image name
		String savingPath = GameStructure.IMAGES_FOLDER;
		I18N i18n = this.controller.getApplicationAssets().getI18N();
		EditorGameAssets gameAssets = this.controller.getEditorGameAssets();
		FileHandle savingDir = gameAssets.resolve(savingPath);
		if (!savingDir.exists()) {
			savingDir.mkdirs();
		}
		String name = i18n.m("element");
		savingPath += name;
		FileHandle savingImage = null;
		int i = 0;
		do {
			savingImage = gameAssets.resolve(savingPath + (++i) + ".png");
		} while (savingImage.exists());

		PixmapRegion currentPixmap = mesh.save(this.savePath = savingImage);
		Pixmap pixmap = currentPixmap.pixmap;

		stageToLocalCoordinates(TEMP.set(currentPixmap.x + pixmap.getWidth()
				* .5f, currentPixmap.y + pixmap.getHeight() * .5f));
		targetX = TEMP.x;
		targetY = TEMP.y;

		return true;
	}

	/**
	 * Creates a {@link ModelEntity}. This method should only be invoked if the
	 * return value of the {@link #save()} method was true.
	 */
	public ModelEntity createSceneElement() {
		ModelEntity savedElement = controller.getTemplates()
				.createSceneElement(savePath.path(), targetX, targetY);
		return savedElement;
	}

	public void show() {
		show(null);
	}

	public void show(ModelEntity imageEntity) {
		if (!hasParent()) {
			container.addActor(this);
			Pixmap.setBlending(Blending.None);
			controller.getCommands().pushStack(MAX_COMMANDS);
			setBounds(0, 0, container.getWidth(), container.getHeight());
			mesh.initializeRenderingResources();

			if (imageEntity != null) {
				toEdit = imageEntity;
				Gdx.app.postRunnable(editImage);
			}
		}
	}

	private Runnable editImage = new Runnable() {

		@Override
		public void run() {
			EngineEntity engineEntity = controller.getEngine()
					.getEntitiesLoader().toEngineEntity(toEdit);
			Group engineGroup = engineEntity.getGroup();
			controller.getEngine().getGameLoop().removeEntity(engineEntity);

			addActor(engineGroup);

			actors.clear();
			actors.add(engineGroup);
			GeometryUtils.calculateBounds(actors, resultOrigin, resultSize);

			engineGroup.remove();

			mesh.show(engineGroup, resultOrigin, resultSize);
		}
	};

	/**
	 * Clears undo/redo history and invokes {@link MeshHelper#release()}.
	 * 
	 * @param release
	 */
	public void hide(boolean release) {
		if (hasParent()) {
			Pixmap.setBlending(Blending.SourceOver);
			remove();
			if (release) {
				release();
			}
			controller.getCommands().popStack(false);
		}
	}

	public void release() {
		mesh.release();
	}

	/**
	 * 
	 * @param value
	 *            a value between 0 and 1.
	 */
	public void setRadius(float value) {
		this.mesh.setRadius(value * getMaxRadius());
	}

	/**
	 * Calls {@link MeshHelper#setColor(Color)}.
	 * 
	 * @param color
	 */
	@Override
	public void setColor(Color color) {
		this.mesh.setColor(color);
	}

	private final InputListener drawListener = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (pointer == 0) {
				event.stop();
				mesh.touchDown(x, y);
				sceneEditor.enterFullScreen();
			}
			return true;
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if (pointer == 0) {
				mesh.touchDragged(x, y);
			}
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			if (pointer == 0) {
				mesh.touchUp(x, y);
				sceneEditor.exitFullscreen();
			}
		}
	};

	/**
	 * Base class to listen to {@link ModeEvent}s produced by
	 * {@link BrushStrokes}.
	 */
	public static class ModeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof ModeEvent) {
				modeChanged((ModeEvent) event);
			}
			return true;
		}

		/**
		 * The mode has changed.
		 */
		public void modeChanged(ModeEvent event) {

		}
	}

	public static class ModeEvent extends Event {

		private Mode mode;

		/**
		 * 
		 * @return the current mode.
		 */
		public Mode getMode() {
			return mode;
		}

		@Override
		public void reset() {
			super.reset();
			this.mode = null;
		}
	}
}
