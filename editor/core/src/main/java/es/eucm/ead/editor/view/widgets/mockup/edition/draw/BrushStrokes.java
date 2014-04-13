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

import java.io.File;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.ead.GameStructure;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Transformation;

/**
 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them to
 * a texture and manages the necessary {@link Pixmap pixmaps} to perform
 * undo/redo actions, erase and save it as a {@link SceneElement}
 */
public class BrushStrokes extends Widget implements Disposable {

	public enum Mode {
		DRAW, ERASE, NONE
	}

	private final Controller controller;
	private final MeshHelper mesh;
	private FileHandle savePath;
	private Mode mode;

	/**
	 * Wrapper around {@link MeshHelper}. A widget that draws lines renders them
	 * to a texture and manages the necessary {@link Pixmap pixmaps} to perform
	 * undo/redo actions, erase and save it as a {@link SceneElement}
	 */
	public BrushStrokes(Actor scaledView, Controller control) {
		this.controller = control;
		this.mesh = new MeshHelper(scaledView, control);
		activateMode(Mode.NONE);
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
		} else {
			removeCaptureListener(drawListener);
			removeCaptureListener(eraseListener);
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
		String savingPath = this.controller.getLoadingPath() + File.separator
				+ GameStructure.IMAGES_FOLDER;
		ApplicationAssets gameAssets = this.controller.getApplicationAssets();
		FileHandle savingDir = gameAssets.absolute(savingPath);
		if (!savingDir.exists()) {
			savingDir.mkdirs();
		}
		String name = gameAssets.getI18N().m("element");
		savingPath += name;
		FileHandle savingImage = null;
		int i = 0;
		do {
			savingImage = gameAssets.absolute(savingPath + (++i) + ".png");
		} while (savingImage.exists());

		this.mesh.save(this.savePath = savingImage);

		return true;
	}

	/**
	 * Creates a {@link SceneElement}. This method should only be invoked if the
	 * return value of the {@link #save()} method was true.
	 */
	public void createSceneElement() {
		SceneElement savedElement = this.controller.getTemplates()
				.createSceneElement(this.savePath.path());
		Transformation transform = savedElement.getTransformation();
		transform.setScaleX(mesh.getScaleX());
		transform.setScaleY(mesh.getScaleY());
		transform.setX(transform.getOriginX() * (transform.getScaleX() - 1));
		transform.setY(transform.getOriginY());
		this.controller.action(AddSceneElement.class, savedElement);
	}

	/**
	 * Clears undo/redo history and invokes {@link MeshHelper#release()}.
	 */
	public void release() {
		Commands commands = this.controller.getCommands();
		commands.getUndoHistory().clear();
		commands.getRedoHistory().clear();
		this.mesh.release();
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
		} else {
			this.mesh.setDrawRadius(radius);
			this.mesh.setEraseRadius(radius);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		Pixmap.setBlending(visible ? Blending.None : Blending.SourceOver);
		super.setVisible(visible);
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
				mesh.drawInput(event.getStageX(), event.getStageY());
			}
			return true;
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if (pointer == 0) {
				mesh.drawInput(event.getStageX(), event.getStageY());
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