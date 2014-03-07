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

import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;

import static es.eucm.ead.editor.model.FieldNames.Y;

public class SceneElementEditorObject extends SceneElementEngineObject
		implements FieldListener {

	private Drawable border;

	private EditorGameLoop editorGameLoop;

	private Model model;

	private com.badlogic.gdx.graphics.Color borderColor = com.badlogic.gdx.graphics.Color.PINK;

	@Override
	public void setGameLoop(GameLoop gameLoop) {
		super.setGameLoop(gameLoop);
		editorGameLoop = (EditorGameLoop) gameLoop;
		model = editorGameLoop.getController().getModel();
		addListener(editorGameLoop.getSelectionListener());
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
		border = skin.getDrawable("white-border");
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (!editorGameLoop.isPlaying()) {
			drawBorder(batch);
		}
	}

	public void setBorderColor(com.badlogic.gdx.graphics.Color color) {
		this.borderColor = color;
	}

	protected void drawBorder(Batch batch) {
		batch.setColor(borderColor);
		border.draw(batch, 0, 0, getWidth(), getHeight());
		batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
	}

	@Override
	public void act(float delta) {
		super.act(editorGameLoop.isPlaying() ? delta : 0);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		FieldNames fieldName = event.getField();
		if (FieldNames.X == fieldName) {
			setX((Float) event.getValue());
		} else if (Y == fieldName) {
			setY((Float) event.getValue());
		} else if (FieldNames.ROTATION == fieldName) {
			setRotation((Float) event.getValue());
		} else if (FieldNames.SCALE_X == fieldName) {
			setScaleX((Float) event.getValue());
		} else if (FieldNames.SCALE_Y == fieldName) {
			setScaleY((Float) event.getValue());
		} else if (FieldNames.ORIGIN_X == fieldName) {
			setOriginX((Float) event.getValue());
		} else if (FieldNames.ORIGIN_Y == fieldName) {
			setOriginY((Float) event.getValue());
		}
	}

	@Override
	public boolean listenToField(FieldNames fieldName) {
		boolean listenTo = false;
		switch (fieldName) {
		case X:
		case Y:
		case ORIGIN_X:
		case ORIGIN_Y:
		case SCALE_Y:
		case SCALE_X:
		case ROTATION:
			listenTo = true;
			break;
		default:
			listenTo = false;
		}
		return listenTo;
	}
}
