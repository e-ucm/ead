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

import static es.eucm.ead.editor.model.FieldNames.Y;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
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

public class SceneElementEditorObject extends SceneElementEngineObject
		implements FieldListener {

	private static final String ELEMENT_TAG = "SceneElementEditorObject";

	private Drawable border;

	private EditorGameLoop editorGameLoop;

	private Model model;

	private com.badlogic.gdx.graphics.Color borderColor = com.badlogic.gdx.graphics.Color.PINK;

	private com.badlogic.gdx.graphics.Color polygonalBorderColor = com.badlogic.gdx.graphics.Color.GREEN;

	/**
	 * Used to render to a {@link Texture texture} the polygon (just once, no in
	 * every frame).
	 */
	private FrameBuffer fbo;
	/**
	 * Used to know if the {@link SceneElementEditorObject element} needs to
	 * draw the polygon {@link Texture texture}.
	 */
	private boolean selected;
	/**
	 * Used to draw the {@link Texture texture} rendered with the
	 * {@link FrameBuffer fbo}.
	 */
	private TextureRegion polygonsTex;

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
		// The FrameBuffer will render the polygons to a Texture.
		// The FrameBuffer needs to have the size of the whole
		// screen in order to display the maximum Texture quality.
		this.fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), false);
		// We draw that texture with this TextureRegion
		// This TextureRegion actually draws only the rectangle containing the
		// source image.
		this.polygonsTex = new TextureRegion(fbo.getColorBufferTexture(),
				(int) renderer.getWidth(), (int) renderer.getHeight());
		// We need to flip the texture because the FrameBuffer uses OpenGL
		// coordinates(origin is top-left)
		this.polygonsTex.flip(false, true);
		// The actual polygon-rendering is done only once (here is where the
		// optimization takes place)
		drawDetailedBorderToTexture();
	}

	/**
	 * Frees the resources used by this engine object. It usually returns all
	 * poolable instances and itself to {@link es.eucm.ead.engine.GameAssets}
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (this.fbo != null) {
			this.fbo.dispose();
			this.fbo = null;
		}
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		if (!editorGameLoop.isPlaying()) {
			// Draw the polygons-texture if this element is selected.
			if (this.selected) {
				batch.draw(this.polygonsTex, 0, 0);
			}
			drawBorder(batch);
		}
	}

	/**
	 * This function renders all the polygons to the {@link #polygonsTex}. Note
	 * that only needs to be called once.
	 */
	private void drawDetailedBorderToTexture() {
		this.fbo.begin();
		// Here we could change the rendering logic.
		// For instance we could use OpenGL Triangle_Strip or another renderer
		// different from the ShapeRenderer if we want prettier results.
		boolean success = renderPolygonShapes(this.polygonalBorderColor);
		this.fbo.end();
		if (!success) {
			// If there are no polygons available to be drawn we should free our
			// resources.
			this.polygonsTex = null;
			this.selected = false;
			this.fbo.dispose();
			this.fbo = null;
		}
	}

	public void setBorderColor(com.badlogic.gdx.graphics.Color color) {
		this.borderColor = color;
		// We make sure we don't draw the polygon-texture if we "lose focus"
		if (this.polygonsTex != null) {
			this.selected = color == com.badlogic.gdx.graphics.Color.WHITE;
		}
	}

	protected void drawBorder(Batch batch) {
		batch.setColor(borderColor);
		border.draw(batch, 0, 0, getWidth(), getHeight());
		batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
	}

	/**
	 * @param color
	 * @return true if any polygon was rendered, false otherwise
	 */
	private boolean renderPolygonShapes(com.badlogic.gdx.graphics.Color color) {

		// We calculate the maximum number of vertices that will be drawn
		// to instantiate the ShapeRenderer plus 25 just to be sure the
		// ShapeRenderer doesn't get flushed
		// because he has "space problems" and the performance is being affected
		// while at the same time saving memory and avoiding unnecessary GC
		// calls.
		// +25 is an acceptable amount considering that by not providing any
		// maxVertices argument
		// the ShapeRenderer uses 5000 (default).
		final int initialVertices = 25;
		int totalVertices = initialVertices;
		if (this.collisionPolygons.size == 0) {
			// We don't need to render an empty texture if there are 0 polygons.
			Gdx.app.log(ELEMENT_TAG,
					"There are 0 polygons, no need to draw anything.");
			return false;
		}
		for (Polygon polygon : this.collisionPolygons) {
			totalVertices += polygon.getVertices().length;
		}
		if (totalVertices == initialVertices) {
			// We don't need to render an empty texture if there are 0 vertices.
			Gdx.app.log(ELEMENT_TAG, "There are no vertices aviable.");
			return false;
		}
		ShapeRenderer shapeRenderer = new ShapeRenderer(totalVertices);
		shapeRenderer.setColor(color);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (Polygon polygon : this.collisionPolygons) {
			shapeRenderer.polygon(polygon.getVertices());
		}
		shapeRenderer.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for (Polygon polygon : this.collisionPolygons) {
			float vertices[] = polygon.getVertices();
			final int maxVertices = vertices.length;
			for (int i = 0; i < maxVertices; i += 2) {
				shapeRenderer.circle(vertices[i], vertices[i + 1], 2);
			}
		}
		shapeRenderer.end();
		// Remember to get rid of the ShapeRenderer since we no longer need it.
		shapeRenderer.dispose();
		shapeRenderer = null;
		return true;
	}

	@Override
	public void act(float delta) {
		if (editorGameLoop.isPlaying()) {
			super.act(delta);
		}
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
