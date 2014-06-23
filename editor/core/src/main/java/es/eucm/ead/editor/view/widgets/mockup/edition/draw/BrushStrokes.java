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
package es.eucm.ead.editor.view.widgets.mockup.edition.draw;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them to
 * a texture and manages the necessary {@link Pixmap pixmaps} to perform
 * undo/redo actions, erase and save it as a {@link ModelEntity}
 */
public class BrushStrokes extends Widget implements Disposable {

	public enum Mode {
		DRAW, ERASE
	}

	private FileHandle savePath, thumbSavePath;
	private final Controller controller;
	private final MeshHelper mesh;
	private GroupEditor scaledView;
	private boolean needsRelease;
	private Mode mode;

	/**
	 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them
	 * to a texture and manages the necessary {@link Pixmap pixmaps} to perform
	 * undo/redo actions, erase and save it as a {@link ModelEntity}
	 */
	public BrushStrokes(GroupEditor scaledView, Controller control) {
		this.mesh = new MeshHelper(scaledView, control);
		this.scaledView = scaledView;
		this.controller = control;
		this.needsRelease = false;
		this.mode = null;
	}

	/**
	 * Sets the behavior of this widget.
	 * 
	 * @param mode
	 */
	public void activateMode(Mode mode) {
		if (this.mode == mode)
			return;
		if (mode == Mode.DRAW) {
			removeCaptureListener(eraseListener);
			addCaptureListener(drawListener);
		} else if (mode == Mode.ERASE) {
			removeCaptureListener(drawListener);
			addCaptureListener(eraseListener);
		}
		this.mode = mode;
	}

	@Override
	public void layout() {
		this.mesh.layout();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
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

		// Get a correct thumbnail name
		String thumbSavingPath = GameStructure.THUMBNAILS_PATH;
		FileHandle thumbSavingDir = gameAssets.resolve(thumbSavingPath);
		if (!thumbSavingDir.exists()) {
			thumbSavingDir.mkdirs();
		}

		thumbSavingPath += name;
		FileHandle thumbSavingImage = null;
		i = 0;
		do {
			thumbSavingImage = gameAssets.resolve(thumbSavingPath + (++i)
					+ ".png");
		} while (thumbSavingImage.exists());

		this.mesh.save(this.savePath = savingImage,
				this.thumbSavePath = thumbSavingImage);

		return true;
	}

	/**
	 * Creates a {@link ModelEntity}. This method should only be invoked if the
	 * return value of the {@link #save()} method was true.
	 */
	public void createSceneElement() {
		final ModelEntity savedElement = controller.getTemplates()
				.createSceneElement(savePath.path());
		Actor container = scaledView.getGroupEditorDragListener()
				.getContainer();
		Vector2 pos = scaledView.localToDescendantCoordinates(container,
				mesh.getPosition());
		savedElement.setX(pos.x);
		savedElement.setY(pos.y);

		savedElement.setScaleX(1 / container.getScaleX());
		savedElement.setScaleY(1 / container.getScaleY());

		Model.getComponent(savedElement, RepoElement.class).setThumbnail(
				thumbSavePath.name());
		controller.action(AddSceneElement.class, savedElement);
	}

	/**
	 * Clears undo/redo history and invokes {@link MeshHelper#release()}.
	 */
	public void release() {
		if (needsRelease) {
			this.mesh.release();
			needsRelease = false;
			controller.getCommands().popContext(false);
		}
	}

	/**
	 * Calls {@link MeshHelper#clear()}.
	 */
	public void clearMesh() {
		this.mesh.clear();
	}

	/**
	 * Calls {@link MeshHelper#setDrawRadius(float)} or
	 * {@link MeshHelper#setEraseRadius(float)} depending on the current mode.
	 * 
	 * @param radius
	 */
	public void setRadius(float radius) {
		if (mode == Mode.DRAW) {
			this.mesh.setDrawRadius(radius);
		} else if (mode == Mode.ERASE) {
			this.mesh.setEraseRadius(radius);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			this.needsRelease = true;
			Pixmap.setBlending(Blending.None);
			controller.getCommands().pushContext();
		} else
			Pixmap.setBlending(Blending.SourceOver);
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

	/**
	 * Calls {@link MeshHelper#dispose()}.
	 */
	@Override
	public void dispose() {
		this.mesh.dispose();
	}

	private final InputListener drawListener = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (pointer == 0) {
				mesh.drawTouchDown(event.getStageX(), event.getStageY());
			}
			return true;
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if (pointer == 0) {
				mesh.drawTouchDragged(event.getStageX(), event.getStageY());
			}
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			if (pointer == 0) {
				mesh.drawTouchUp(event.getStageX(), event.getStageY());
			}
		}
	};

	private final InputListener eraseListener = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (pointer == 0) {
				mesh.eraseTouchDown(event.getStageX(), event.getStageY());
			}
			return true;
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if (pointer == 0) {
				mesh.eraseTouchDragged(event.getStageX(), event.getStageY());
			}
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			if (pointer == 0) {
				mesh.eraseTouchUp(event.getStageX(), event.getStageY());
			}
		}
	};
}
