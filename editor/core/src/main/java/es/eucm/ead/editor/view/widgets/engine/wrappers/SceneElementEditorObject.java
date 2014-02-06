/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;

public class SceneElementEditorObject extends SceneElementEngineObject
		implements FieldListener {

	public static final Array<String> FIELDS = new Array<String>();

	static {
		FIELDS.add("x");
		FIELDS.add("y");
	}

	private Drawable border;

	private EditorGameLoop editorGameLoop;

	private Model model;

	@Override
	public void setGameLoop(GameLoop gameLoop) {
		super.setGameLoop(gameLoop);
		editorGameLoop = (EditorGameLoop) gameLoop;
		addListener(editorGameLoop.getDragListener());
		model = editorGameLoop.getController().getModel();
	}

	@Override
	public void setSchema(SceneElement schemaObject) {
		Transformation t = schemaObject.getTransformation();
		// Create default transformation if it is null
		if (t == null) {
			t = new Transformation();
			t.setColor(new Color());
			schemaObject.setTransformation(t);
		}
		// Re-target listener to new schema object
		model.retargetListener(
				element == null ? null : element.getTransformation(),
				schemaObject.getTransformation(), this);
		super.setSchema(schemaObject);
	}

	@Override
	public void initialize(SceneElement schemaObject) {
		super.initialize(schemaObject);
		Skin skin = editorGameLoop.getSkin();
		border = skin.getDrawable("rose-border");
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (!editorGameLoop.isPlaying()) {
			border.draw(batch, 0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public void act(float delta) {
		super.act(editorGameLoop.isPlaying() ? delta : 0);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		String fieldName = event.getField();
		if ("x".equals(fieldName)) {
			setX((Float) event.getValue());
		} else if ("y".equals(fieldName)) {
			setY((Float) event.getValue());
		}
	}

	@Override
	public boolean listenToField(String fieldName) {
		return FIELDS.contains(fieldName, false);
	}
}
