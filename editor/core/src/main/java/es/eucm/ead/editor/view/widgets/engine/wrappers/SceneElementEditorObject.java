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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.ShapeDrawable;
import es.eucm.ead.editor.view.widgets.engine.wrappers.transformer.SelectedOverlay;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Color;
import es.eucm.ead.schema.components.Transformation;

import static es.eucm.ead.editor.model.FieldNames.Y;

public class SceneElementEditorObject extends SceneElementEngineObject
		implements FieldListener, ShapeDrawable {

	private EditorGameLoop editorGameLoop;

	private Model model;

	private com.badlogic.gdx.graphics.Color borderColor = com.badlogic.gdx.graphics.Color.PINK;

	private static com.badlogic.gdx.graphics.Color polygonalBorderColor = com.badlogic.gdx.graphics.Color.GREEN;

	private static float polygonalBorderDotSize = 6f;

	private SelectedOverlay selectionOverlay;

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

	public void setSelectionOverlay(SelectedOverlay selectionOverlay) {
		this.selectionOverlay = selectionOverlay;
	}

	public void setBorderColor(com.badlogic.gdx.graphics.Color color) {
		this.borderColor = color;
	}

	/**
	 * Draws the polygon and its polygonal border.
	 * 
	 * @param sr
	 *            a ShapeRenderer
	 */
	public void drawShapes(ShapeRenderer sr) {
		if (editorGameLoop.isPlaying()) {
			return;
		}

		sr.begin(ShapeRenderer.ShapeType.Line);

		// border
		sr.setColor(borderColor);
		sr.rect(0, 0, getWidth(), getHeight());

		if (selectionOverlay != null) {
			// polygon
			sr.setColor(polygonalBorderColor);
			for (Polygon p : collisionPolygons) {
				sr.polygon(p.getVertices());
			}
			sr.end();

			float w = polygonalBorderDotSize / getScaleX();
			float h = polygonalBorderDotSize / getScaleY();
			sr.begin(ShapeRenderer.ShapeType.Filled);
			for (Polygon p : collisionPolygons) {
				float v[] = p.getVertices();
				for (int i = 0; i < v.length; i += 2) {
					sr.ellipse(v[i] - (w / 2), v[i + 1] - (h / 2), w, h);
				}
			}
		}
		sr.end();
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
			break;
		}
		return listenTo;
	}
}
