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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.ead.GameStructure;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action.ActionListener;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Transformation;

/**
 * A widget that draws lines renders them to a texture and manages the necessary
 * {@link Pixmap pixmaps} to perform undo/redo actions, erase and save it as a
 * {@link SceneElement}
 */
public class BrushStrokes extends Widget implements Disposable {

	private final Controller controller;
	private final MeshHelper mesh;
	private String savePath;

	/**
	 * A widget that draws lines renders them to a texture and manages the
	 * necessary {@link Pixmap pixmaps} to perform undo/redo actions, erase and
	 * save it as a {@link SceneElement}
	 */
	public BrushStrokes(Actor scaledView, Controller control) {
		this.controller = control;
		this.mesh = new MeshHelper(scaledView);
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					mesh.input(event.getStageX(), event.getStageY());
				}
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (pointer == 0) {
					mesh.input(event.getStageX(), event.getStageY());
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					mesh.touchUp(event.getStageX(), event.getStageY());
					controller.command(mesh.getDrawLineCommand());
				}
			}
		});
		Actions actions = controller.getActions();
		actions.addActionListener(Undo.class, new ActionListener() {
			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (!enable) {
					mesh.release(mesh.getUndoPixmaps());
				}
			}
		});
		actions.addActionListener(Redo.class, new ActionListener() {
			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (!enable) {
					mesh.release(mesh.getRedoPixmaps());
				}
			}
		});
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

	public void delete(int x, int y, int radius) {
		// TODO
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

		this.mesh.save(savingImage);

		this.savePath = File.separator + GameStructure.IMAGES_FOLDER + name + i
				+ ".png";
		return true;
	}

	/**
	 * Creates a {@link SceneElement}. This method should only be invoked if the
	 * return value of the {@link #save()} method was true.
	 */
	public void createSceneElement() {
		SceneElement savedElement = this.controller.getTemplates()
				.createSceneElement(this.savePath);
		Transformation transform = savedElement.getTransformation();
		transform.setScaleX(1 / getParent().getScaleX());
		transform.setScaleY(-1 / getParent().getScaleY());
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
	 * Calls {@link MeshHelper#setRadius(float)}.
	 * 
	 * @param radius
	 */
	public void setRadius(float radius) {
		this.mesh.setRadius(radius);
	}

	/**
	 * Calls {@link MeshHelper#setMaxRadius(float)}.
	 * 
	 * @param maxRadius
	 */
	public void setMaxRadius(float maxRadius) {
		this.mesh.setMaxRadius(maxRadius);
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
}