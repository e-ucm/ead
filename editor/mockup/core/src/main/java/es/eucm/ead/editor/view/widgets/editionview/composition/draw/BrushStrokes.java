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
package es.eucm.ead.editor.view.widgets.editionview.composition.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.composition.draw.MeshHelper.PixmapRegion;
import es.eucm.ead.editor.view.widgets.groupeditor.Modifier;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them to
 * a texture and manages the necessary {@link Pixmap pixmaps} to perform
 * undo/redo actions, erase and save it as a {@link ModelEntity}
 */
public class BrushStrokes extends WidgetGroup {

	public static final int MAX_COMMANDS = 50;
	private static final Vector2 TEMP = new Vector2();
	private static final Color eraseColor = new Color(1F, 1F, 1F, 0F);

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
	private MockupSceneEditor sceneEditor;
	private Mode mode;
	private float targetX, targetY, scaleX, scaleY;

	/**
	 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them
	 * to a texture and manages the necessary {@link Pixmap pixmaps} to perform
	 * undo/redo actions, erase and save it as a {@link ModelEntity}
	 */
	public BrushStrokes(MockupSceneEditor scaledView, Controller control) {
		this.mesh = new MeshHelper(scaledView, control);
		this.sceneEditor = scaledView;
		this.controller = control;
		this.mode = null;
		((MockupController) controller).addDisposable(mesh);
		addCaptureListener(drawListener);
	}

	/**
	 * Sets the behavior of this widget.
	 * 
	 * @param mode
	 */
	public void setMode(Mode mode) {
		if (this.mode == mode)
			return;
		if (mode == Mode.ERASE) {
			mesh.setColor(eraseColor);
		}
		this.mode = mode;
	}

	@Override
	public void layout() {
		this.mesh.layout();
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
		Group container = sceneEditor.getContainer();

		container.stageToLocalCoordinates(TEMP.set(currentPixmap.x,
				currentPixmap.y));
		scaleX = container.getScaleX();
		scaleY = container.getScaleY();
		targetX = TEMP.x + pixmap.getWidth() * .5f / scaleX;
		targetY = TEMP.y + pixmap.getHeight() * .5f / scaleY;
		scaleX = 1 / scaleX;
		scaleY = 1 / scaleY;

		return true;
	}

	/**
	 * Creates a {@link ModelEntity}. This method should only be invoked if the
	 * return value of the {@link #save()} method was true.
	 */
	public ModelEntity createSceneElement() {
		ModelEntity savedElement = controller.getTemplates()
				.createSceneElement(savePath.path(), targetX, targetY);
		savedElement.setScaleX(scaleX);
		savedElement.setScaleY(scaleX);
		return savedElement;
	}

	public void show(ModelEntity selection) {
		if (!hasParent()) {
			sceneEditor.addActor(this);
			Pixmap.setBlending(Blending.None);
			controller.getCommands().pushStack(MAX_COMMANDS);
			setBounds(0f, 0f, sceneEditor.getWidth(), sceneEditor.getHeight());

			if (selection != null) {
				toEdit = selection;

				Gdx.app.postRunnable(editImage);
			}
		}
	}

	private Runnable editImage = new Runnable() {

		@Override
		public void run() {
			EngineEntity engineEntity = controller.getEngine()
					.getEntitiesLoader().toEngineEntity(toEdit);
			Group eG = engineEntity.getGroup();
			controller.getEngine().getGameLoop().removeEntity(engineEntity);

			Modifier modifier = sceneEditor.getModifier();

			sceneEditor.getContainer().addActor(eG);

			modifier.computeTransform(eG, sceneEditor);

			sceneEditor.addActor(eG);

			actors.clear();
			actors.add(eG);
			modifier.calculateBounds(actors, resultOrigin, resultSize);

			eG.remove();

			mesh.show(eG, resultOrigin, resultSize);
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
			clearMesh();
			if (release) {
				release();
			}
			controller.getCommands().popStack(false);
		}
	}

	/**
	 * Calls {@link MeshHelper#clear()}.
	 */
	private void clearMesh() {
		this.mesh.clear();
	}

	public void release() {
		mesh.release();
	}

	/**
	 * Calls {@link MeshHelper#setRadius(float)} or
	 * {@link MeshHelper#setEraseRadius(float)} depending on the current mode.
	 * 
	 * @param radius
	 */
	public void setRadius(float radius) {
		this.mesh.setRadius(radius);
	}

	/**
	 * Calls {@link MeshHelper#setMaxDrawRadius(float)}.
	 * 
	 * @param maxRadius
	 */
	public void setMaxDrawRadius(float maxRadius) {
		this.mesh.setMaxDrawRadius(maxRadius);
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
				mesh.touchDown(x, y);
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
			}
		}
	};
}
