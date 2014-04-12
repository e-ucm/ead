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

import java.nio.ByteBuffer;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.view.EditorStage;

/**
 * Handles all the necessary data required to draw brush strokes, undo/redo and
 * delete them.
 */
public class MeshHelper implements Disposable {
	private static final String MESH_TAG = "MeshHelper";

	/**
	 * The maximum number of triangles. Increasing this value increases the
	 * length of the line avoiding the user to touchUp and start a new input
	 * process. But this will increase the memory usage and decrease the
	 * performance if the number of triangles is too high.
	 */
	private static final int MAX_TRIANGLES = 500;
	/**
	 * Used to decide when to draw a dot or to start drawing the brush stroke.
	 */
	private static final int MIN_VERTICES = 6;
	/**
	 * Defines the quality of the dot. The current amount is calculated via
	 * MAX_DOT_TRIANGLES * currentRadius / maxRadius.
	 */
	private static final int MAX_DOT_TRIANGLES = 15;
	/**
	 * Auxiliary constants cached in order to avoid per-frame calculation.
	 */
	private static final int MAX_VERTICES = MAX_TRIANGLES * 2 + 2,
			MAX_VERTICES_2 = MAX_VERTICES - 2;

	/**
	 * The lower this value is the higher is the accuracy of the brush stroke
	 * but the length of the line will also decrease requiring the user to
	 * touchUp and start a new input process.
	 */
	private static final float DASH_ACCURACY = 250;
	/**
	 * The lower this value is the higher is the accuracy of the brush stroke
	 * while changing direction but the length of the line will also decrease
	 * requiring the user to touchUp and start a new input process.
	 */
	private static final float CURVE_ACCURACY = MathUtils.PI / 10;

	/**
	 * Used by the {@link #drawLine} to perform correctly.
	 */
	private final Stack<PixmapRegion> undoPixmaps = new Stack<PixmapRegion>(),
			redoPixmaps = new Stack<PixmapRegion>();
	/**
	 * Used to convert from local to {@link EditorStage} coordinates and vice
	 * versa.
	 */
	private final Vector2 unprojectedVertex = new Vector2();
	/**
	 * Performs the undo/redo encapsulation.
	 */
	private final Command drawLine = new DrawLineCommand();

	/**
	 * Used to convert from LocalToStageCoordinates and vice versa.
	 */
	private final Vector2 temp = new Vector2();
	/**
	 * This defines the local coordinates, and the bounds to clamp via
	 * {@link #clampTotalBounds()}
	 */
	private final Actor scaledView;
	/**
	 * Used to clear the {@link #frameBuffer} contents, also used while
	 * undo/redo is performed.
	 */
	private final PixmapRegion flusher = new PixmapRegion(null, 0, 0);
	/**
	 * {@link Camera#combined} matrix passed to the {@link #meshShader}.
	 */
	private final Matrix4 combinedMatrix = new Matrix4();
	private final float[] lineVertices;
	/**
	 * The number of vertices from {@link #lineVertices} passed to the
	 * {@link #mesh}.
	 */
	private int vertexIndex = 0;
	/**
	 * Used to decide if we should render via {@link GL20#GL_TRIANGLE_STRIP} for
	 * a brush stroke or via {@link GL20#GL_TRIANGLE_FAN} for a dot (if the user
	 * doesn't drag enough to render a line).
	 */
	private int primitiveType;

	private float drawRadius = 20f, maxDrawRadius = drawRadius * 2f,
			eraseRadius = drawRadius;
	/**
	 * Used to define the {@link Color} of the brush stroke.
	 */
	private float r = 1f, g = 1f, b = 0f, a = 1f;
	/**
	 * Used to decide the boundaries of the final saved image.
	 */
	private float minX, minY, maxX, maxY;
	/**
	 * Those values define the bounds of {@link #minX}, {@link #minY},
	 * {@link #maxX} and {@link #maxY}. Used in {@link #clampTotalBounds()}.
	 */
	private float clampMinX, clampMinY, clampMaxX, clampMaxY;
	/**
	 * Defines the scale with which the {@link #showingTexRegion} should be
	 * drawn.
	 */
	private float scaleX, scaleY;
	/**
	 * Last input position, used to calculate distance-based optimizations.
	 */
	private float lastX, lastY;
	/**
	 * Used to perform the coordinate translation when the window is resized.
	 */
	private int prevParentX, prevParentY;
	/**
	 * If true, the {@link #combinedMatrix} will be recalculated next time is
	 * needed.
	 */
	private boolean recalculateMatrix;

	/**
	 * Used to know the previous view port of the default frame buffer.
	 */
	private Viewport stageViewport;

	private PixmapRegion currentModifiedPixmap;
	private TextureRegion showingTexRegion;
	private ShaderProgram meshShader;
	private FrameBuffer frameBuffer;
	private Mesh mesh;

	/**
	 * Handles all the necessary data required to draw brush strokes, undo/redo
	 * and delete them. The {@link #scaledView} parameter will be used to
	 * perform local to stage coordinates conversion in order to recalculate the
	 * new required data/positions.
	 */
	public MeshHelper(Actor scaledView) {
		this.lineVertices = new float[MAX_VERTICES];
		this.scaledView = scaledView;
		resetTotalBounds();
		createShader();
		createMesh();
	}

	/**
	 * Clears the contents of the current {@link #frameBuffer} by clearing it's
	 * {@link Texture} with the help of the {@link #flusher}.
	 */
	void clear() {
		if (this.showingTexRegion == null)
			return;

		Pixmap.setBlending(Blending.None);
		this.flusher.pixmap.fill();
		this.showingTexRegion.getTexture().draw(this.flusher.pixmap,
				this.flusher.x, this.flusher.y);
		Pixmap.setBlending(Blending.SourceOver);
	}

	/**
	 * Disposes all the {@link Stack}s, {@link #currentModifiedPixmap} and
	 * resets {@link #minX}, {@link #minY}, {@link #maxX} and {@link #maxY} via
	 * {@link #resetTotalBounds()}.
	 */
	public void release() {
		resetTotalBounds();
		release(this.redoPixmaps);
		release(this.undoPixmaps);
		if (this.currentModifiedPixmap != null) {
			this.currentModifiedPixmap.dispose();
			this.currentModifiedPixmap = null;
		}
	}

	/**
	 * @return true if the {@link #currentModifiedPixmap} has been modified and
	 *         has something to save.
	 */
	boolean hasSomethingToSave() {
		return this.currentModifiedPixmap != null
				&& this.currentModifiedPixmap.pixmap != null
				&& this.currentModifiedPixmap.pixmap != this.flusher.pixmap;
	}

	/**
	 * Disposes the contents of the {@link Stack} and clears the {@link Stack}.
	 * 
	 * @param pixmaps
	 */
	void release(Stack<PixmapRegion> pixmaps) {
		if (pixmaps.isEmpty())
			return;
		for (PixmapRegion pixmap : pixmaps) {
			pixmap.dispose();
			pixmap = null;
		}
		pixmaps.clear();
	}

	/**
	 * Computes and caches any information needed for drawing.
	 */
	void layout() {
		reinitializeRenderingResources();
	}

	/**
	 * Saves the minimum amount of pixels that encapsulates the drawn image.
	 */
	void save(FileHandle file) {
		PixmapIO.writePNG(file, this.currentModifiedPixmap.pixmap);
	}

	/**
	 * Returns a portion of the default {@link #frameBuffer} contents specified
	 * by x, y, width and height as a {@link Pixmap} with the same dimensions.
	 * Always has RGBA8888 {@link Format}.
	 * 
	 * @param x
	 *            the x position of the {@link #frameBuffer} contents to capture
	 * @param y
	 *            the y position of the {@link #frameBuffer} contents to capture
	 * @param w
	 *            the width of the {@link #frameBuffer} contents to capture
	 * @param h
	 *            the height of the {@link #frameBuffer} contents to capture
	 */
	private Pixmap takeScreenShot(int x, int y, int w, int h) {
		Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
				pixels);
		return pixmap;
	}

	/**
	 * Initializes the {@link #frameBuffer}, {@link #showingTexRegion} and
	 * {@link #flusher} to the coordinates the the {@link EditorStage}, only if
	 * they are null. This method should only be called in {@link #layout()}. If
	 * the {@link EditorStage} size changed, the resources are translated via
	 * {@link #translateResources(Actor)}.
	 */
	private void reinitializeRenderingResources() {
		Stage stage = this.scaledView.getStage();
		int stageWidth = Math.round(stage.getWidth());
		int stageHeight = Math.round(stage.getHeight());

		if (this.frameBuffer != null) {
			// If the new size is different from the old size
			// we must recalculate our Matrix4 and recreate our rendering
			// resources.
			if (!MathUtils.isEqual(stageWidth, this.frameBuffer.getWidth(),
					1.0f)
					|| !MathUtils.isEqual(stageHeight,
							this.frameBuffer.getHeight(), 1.0f)) {
				Gdx.app.log(
						MESH_TAG,
						"new stage width: "
								+ stageWidth
								+ ", height: "
								+ stageHeight
								+ " ~> old ("
								+ this.frameBuffer.getWidth()
								+ ", "
								+ this.frameBuffer.getHeight()
								+ ") proceeding to recreate the rendering resources.");
				this.frameBuffer.dispose();
				this.frameBuffer = null;
				if (this.flusher.pixmap != null) {
					this.flusher.pixmap.dispose();
					this.flusher.pixmap = null;
				}
			}
		}
		if (this.frameBuffer == null) {
			this.stageViewport = stage.getViewport();
			Gdx.app.log(MESH_TAG,
					"new viewport ~> " + stageViewport.getViewportX() + ", "
							+ stageViewport.getViewportY() + ", "
							+ stageViewport.getViewportWidth() + ", "
							+ stageViewport.getViewportHeight());
			this.recalculateMatrix = true;
			this.scaleX = 1 / this.scaledView.getScaleX();
			this.scaleY = 1 / this.scaledView.getScaleY();

			this.frameBuffer = new FrameBuffer(Format.RGBA8888, stageWidth,
					stageHeight, false);

			if (this.showingTexRegion == null) {
				this.showingTexRegion = new TextureRegion();
			}
			final Texture colorTexture = this.frameBuffer
					.getColorBufferTexture();
			this.showingTexRegion.setTexture(colorTexture);
			translateResources(this.scaledView);

			float x = scaledView.getX(), y = scaledView.getY(), w = scaledView
					.getWidth() * scaledView.getScaleX(), h = scaledView
					.getHeight() * scaledView.getScaleY();

			scaledView.getParent().localToStageCoordinates(temp.set(x, y));
			clampMinX = temp.x;
			clampMinY = temp.y;

			scaledView.getParent().localToStageCoordinates(
					temp.set(x + w, y + h));
			clampMaxX = temp.x;
			clampMaxY = temp.y;

			Gdx.app.log(MESH_TAG, " clamp bounds ~> " + clampMinX + ", "
					+ clampMinY + ", " + clampMaxX + ", " + clampMaxY);

			Gdx.app.log(MESH_TAG, "Texture region: " + x + ", " + y + ", " + w
					+ ", " + h);

			this.showingTexRegion.setRegion(Math.round(clampMinX),
					Math.round(clampMinY), Math.round(clampMaxX - clampMinX),
					Math.round(clampMaxY - clampMinY));
			this.showingTexRegion.flip(false, true);

			if (this.flusher.pixmap == null) {
				this.flusher.pixmap = new Pixmap(Math.round(this.frameBuffer
						.getWidth()), Math.round(this.frameBuffer.getHeight()),
						Format.RGBA8888);
				this.flusher.x = 0;
				this.flusher.y = 0;
			}
		}
	}

	/**
	 * Converts the current {@link PixmapRegion}s to the new {@link Stage} size.
	 * A conversion from the {@link #scaledView} position must be done in order
	 * to calculate the new position. This translation simply positions the
	 * {@link PixmapRegion}s to the new {@link #scaledView} position. If a more
	 * complex translation is required (e.g. scaling) this method should be
	 * extended overridden.
	 * 
	 * @param parent
	 */
	private void translateResources(Actor parent) {
		scaledView.getParent().localToStageCoordinates(
				temp.set(parent.getX(), parent.getY()));
		int newx = Math.round(temp.x);
		int newy = Math.round(temp.y);
		int offsetX = newx - this.prevParentX;
		int offsetY = newy - this.prevParentY;
		if (this.currentModifiedPixmap != null) {
			this.currentModifiedPixmap.x += offsetX;
			this.currentModifiedPixmap.y += offsetY;

			if (this.currentModifiedPixmap.pixmap != null) {
				final Texture colorTexture = this.showingTexRegion.getTexture();
				colorTexture.draw(this.currentModifiedPixmap.pixmap,
						this.currentModifiedPixmap.x,
						this.currentModifiedPixmap.y);
			}
		}
		if (!this.redoPixmaps.isEmpty()) {
			for (PixmapRegion redoPixReg : this.redoPixmaps) {
				redoPixReg.x += offsetX;
				redoPixReg.y += offsetY;
			}
		}
		if (!this.undoPixmaps.isEmpty()) {
			for (PixmapRegion redoPixReg : this.undoPixmaps) {
				redoPixReg.x += offsetX;
				redoPixReg.y += offsetY;
			}
		}
		this.prevParentX = newx;
		this.prevParentY = newy;
	}

	/**
	 * Clamps {@link #minX}, {@link #minY}, {@link #maxX} and {@link #maxY} to
	 * the values defined by {@link #clampMinX}, {@link #clampMinY},
	 * {@link #clampMaxX} and {@link #clampMaxY}.
	 */
	private void clampTotalBounds() {
		if (this.minX < clampMinX) {
			this.minX = clampMinX;
		}

		if (this.maxX > clampMaxX) {
			this.maxX = clampMaxX;
		}

		if (this.minY < clampMinY) {
			this.minY = clampMinY;
		}

		if (this.maxY > clampMaxY) {
			this.maxY = clampMaxY;
		}
	}

	/**
	 * Clears the {@link #mesh}. Nothing will be rendered by the mesh after the
	 * call to this method unless new vertices are added to the {@link #mesh}
	 * via {@link Mesh#setVertices(float[], int, int)}.
	 */
	private void resetMesh() {
		this.vertexIndex = 0;
		this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
	}

	/**
	 * Creates the {@link #mesh} with the basic {@link VertexAttribute}s to
	 * define a position.
	 */
	private void createMesh() {
		this.mesh = new Mesh(true, MAX_VERTICES, 0, new VertexAttribute(
				Usage.Position, 2, "a_position"));
	}

	/**
	 * Creates the {@link #meshShader} which will requite a {@link Matrix4} for
	 * the vertex positioning and a {@link Color} for the fragments.
	 */
	private void createShader() {
		// this shader tells OpenGL where to put things
		final String vertexShader = "attribute vec4 a_position; \n"
				+ "uniform mat4 u_worldView;					\n"
				+ "void main()                  				\n"
				+ "{                            				\n"
				+ "   gl_Position =  u_worldView * a_position;	}";

		// this one tells it what goes in between the points (i.e
		// color/texture)
		final String fragmentShader = "#ifdef GL_ES     \n"
				+ "precision mediump float;    			\n"
				+ "#endif                      			\n"
				+ "uniform vec4 u_color;				\n"
				+ "void main()                 			\n"
				+ "{                           			\n"
				+ "  gl_FragColor = u_color;   			}";

		// make an actual shader from our strings
		ShaderProgram.pedantic = false;
		this.meshShader = new ShaderProgram(vertexShader, fragmentShader);

		// check there's no shader compile error
		if (!this.meshShader.isCompiled())
			throw new IllegalStateException(this.meshShader.getLog());
	}

	/**
	 * Renders everything necessary to edit and draw brush strokes.
	 * 
	 * @param batch
	 * @param parentAlpha
	 */
	void draw(Batch batch, float parentAlpha) {
		drawShowingTexture(batch);
		if (this.vertexIndex < MIN_VERTICES) {
			if (this.recalculateMatrix) {
				this.recalculateMatrix = false;
				this.combinedMatrix.idt().mul(
						scaledView.getStage().getCamera().combined);
			}
			return;
		}
		batch.end();
		drawMesh();
		batch.begin();
	}

	/**
	 * Draws the {@link #showingTexRegion}. Considering the
	 * {@link #showingTexRegion} is created in {@link EditorStage} coordinate
	 * system, the {@link Texture} is drawn scaled with a scale equal to 1 /
	 * {@link #scaledView}.getScaleXY().
	 * 
	 * @param batch
	 */
	private void drawShowingTexture(Batch batch) {
		batch.draw(this.showingTexRegion, 0, 0, 0, 0,
				this.showingTexRegion.getRegionWidth(),
				this.showingTexRegion.getRegionHeight(), this.scaleX,
				this.scaleY, 0);
	}

	/**
	 * Draws the {@link #mesh} with the {@link #meshShader}. The
	 * {@link #meshShader} receives a {@link Color} specified via
	 * {@link #setColor(Color)} and the {@link #combinedMatrix} from the
	 * {@link Camera#combined} (ProjectionMatrix * TransformMatrix).
	 */
	private void drawMesh() {
		this.meshShader.begin();

		this.meshShader.setUniformf("u_color", this.r, this.g, this.b, this.a);
		this.meshShader.setUniformMatrix("u_worldView", this.combinedMatrix);

		this.mesh.render(this.meshShader, this.primitiveType);

		this.meshShader.end();
	}

	@Override
	public void dispose() {
		if (this.flusher.pixmap != null) {
			this.flusher.pixmap.dispose();
			this.flusher.pixmap = null;
		}
		this.mesh.dispose();
		this.mesh = null;
		this.meshShader.dispose();
		this.meshShader = null;
		this.frameBuffer.dispose();
		this.frameBuffer = null;
	}

	/**
	 * This method transforms the input to the specified format of the
	 * {@link #mesh} in order to render a brush stroke.
	 * 
	 * @param x
	 * @param y
	 */
	void drawInput(float x, float y) {
		if (this.vertexIndex == MAX_VERTICES_2)
			return;

		this.unprojectedVertex.set(x, y);

		x = this.unprojectedVertex.x;
		y = this.unprojectedVertex.y;

		if (this.vertexIndex == 0) {
			this.lineVertices[this.vertexIndex++] = x;
			this.lineVertices[this.vertexIndex++] = y;

			this.lastX = x;
			this.lastY = y;

			clampTotalBounds(x, y, x, y);

		} else if (this.unprojectedVertex.dst2(this.lastX, this.lastY) > DASH_ACCURACY
				|| (MathUtils.atan2(x, y) - MathUtils.atan2(this.lastX,
						this.lastY)) > CURVE_ACCURACY) {

			this.unprojectedVertex.sub(this.lastX, this.lastY).nor()
					.set(-this.unprojectedVertex.y, this.unprojectedVertex.x)
					.scl(this.drawRadius);

			float maxNorX = x + this.unprojectedVertex.x;
			this.lineVertices[this.vertexIndex++] = maxNorX;

			float maxNorY = y + this.unprojectedVertex.y;
			this.lineVertices[this.vertexIndex++] = maxNorY;

			float minNorX, minNorY;
			if (this.vertexIndex < MAX_VERTICES_2) {
				minNorX = x - this.unprojectedVertex.x;
				this.lineVertices[this.vertexIndex++] = minNorX;

				minNorY = y - this.unprojectedVertex.y;
				this.lineVertices[this.vertexIndex++] = minNorY;
			} else {
				minNorX = Float.MAX_VALUE;
				minNorY = minNorX;
			}

			if (this.vertexIndex >= MIN_VERTICES) {
				primitiveType = GL20.GL_TRIANGLE_STRIP;

				clampTotalBounds(Math.min(maxNorX, minNorX),
						Math.min(maxNorY, minNorY), Math.max(maxNorX, minNorX),
						Math.max(maxNorY, minNorY));

				this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
			}

			this.lastX = x;
			this.lastY = y;
		}
	}

	/**
	 * Clamps {@link #minX}, {@link #minY}, {@link #maxX} and {@link #maxY} to
	 * the values from the parameters. This method supposes the attributes are
	 * given in the {@link Stage} coordinate system, so no conversion will be
	 * performed.
	 * 
	 * @param minx
	 * @param miny
	 * @param maxx
	 * @param maxy
	 */
	private void clampTotalBounds(float minx, float miny, float maxx, float maxy) {
		this.minX = Math.min(this.minX, minx);
		this.minY = Math.min(this.minY, miny);
		this.maxX = Math.max(this.maxX, maxx);
		this.maxY = Math.max(this.maxY, maxy);
	}

	/**
	 * This method decides if a dot should be rendered via
	 * {@link GL20#GL_TRIANGLE_FAN}, if the user didn't drag enough space to
	 * draw a stroke, or a normal {@link #drawInput(float, float)}.
	 * 
	 * @param x
	 * @param y
	 */
	void drawTouchUp(float x, float y) {
		if (this.vertexIndex < MIN_VERTICES) {
			this.vertexIndex = 0;
			final int startCount = 2;
			final int triangleAmount = startCount
					+ Math.round(MAX_DOT_TRIANGLES * this.drawRadius
							/ this.maxDrawRadius);
			primitiveType = GL20.GL_TRIANGLE_FAN;
			lineVertices[vertexIndex++] = x;
			lineVertices[vertexIndex++] = y;

			lineVertices[vertexIndex++] = x + (drawRadius);
			lineVertices[vertexIndex++] = y + (0);

			float circleStep = MathUtils.PI2 / triangleAmount;
			lineVertices[vertexIndex++] = x
					+ (drawRadius * MathUtils.cos(circleStep));
			lineVertices[vertexIndex++] = y
					+ (drawRadius * MathUtils.sin(circleStep));

			for (int i = startCount; i <= triangleAmount; ++i) {
				lineVertices[vertexIndex++] = x
						+ (drawRadius * MathUtils.cos(i * circleStep));
				lineVertices[vertexIndex++] = y
						+ (drawRadius * MathUtils.sin(i * circleStep));
			}

			this.minX = Math.min(this.minX, x - drawRadius);
			this.minY = Math.min(this.minY, y - drawRadius);
			this.maxX = Math.max(this.maxX, x + drawRadius);
			this.maxY = Math.max(this.maxY, y + drawRadius);
			this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
		} else {
			drawInput(x, y);
		}
	}

	/**
	 * Initializes {@link #minX}, {@link #minY} to {@link Float#MAX_VALUE} and
	 * {@link #maxX}, {@link #maxY} to {@link Float#MIN_VALUE},
	 */
	private void resetTotalBounds() {
		this.minX = this.minY = Float.MAX_VALUE;
		this.maxX = this.maxY = Float.MIN_VALUE;
	}

	/**
	 * Sets the {@link Color} of the brush.
	 */
	void setColor(Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}

	/**
	 * Sets the radius of the brush while erasing.
	 * 
	 * @param radius
	 */
	public void setEraseRadius(float radius) {
		this.eraseRadius = radius;
	}

	/**
	 * Sets the width of the brush.
	 */
	void setDrawRadius(float radius) {
		this.drawRadius = radius;
	}

	/**
	 * This value will be used to determine how many triangles will be processed
	 * when drawing a dot via {@link GL20#GL_TRIANGLE_FAN}.
	 */
	void setMaxDrawRadius(float maxRadius) {
		this.maxDrawRadius = maxRadius;
	}

	/**
	 * Updates {@link #minX}, {@link #minY}, {@link #maxX} and {@link #maxY} to
	 * the coordinates of the {@link PixmapRegion pixRegion}. Since
	 * {@link PixmapRegion pixRegion} uses {@link EditorStage} coordinate system
	 * which is the same as the one used by {@link #minX}, {@link #minY},
	 * {@link #maxX} and {@link #maxY} no conversion must be done before
	 * updating the new values.
	 */
	private void updateTotalBounds(PixmapRegion pixRegion) {
		final Pixmap pix = pixRegion.pixmap;

		if (pix != flusher.pixmap) {
			float x = pixRegion.x;
			float y = pixRegion.y;

			minX = x;
			minY = y;
			maxX = x + pix.getWidth();
			maxY = y + pix.getHeight();
		} else {
			resetTotalBounds();
		}
	}

	/**
	 * @return the command that draws lines
	 */
	Command getDrawCommand() {
		return drawLine;
	}

	public void eraseInput(float stageX, float stageY) {

	}

	public void eraseTouchUp(float stageX, float stageY) {

	}

	public Command getEraseCommand() {
		return null;
	}

	/**
	 * @return the undoPixmaps
	 */
	Stack<PixmapRegion> getUndoPixmaps() {
		return undoPixmaps;
	}

	/**
	 * @return the redoPixmaps
	 */
	Stack<PixmapRegion> getRedoPixmaps() {
		return redoPixmaps;
	}

	/**
	 * Uses {@link MeshHelper} attributes in order to perform correctly brush
	 * strokes.
	 */
	private class DrawLineCommand extends Command {

		private final ModelEvent dummyEvent = new ModelEvent() {
			@Override
			public Object getTarget() {
				return null;
			}
		};

		@Override
		public ModelEvent doCommand() {

			if (vertexIndex == 0 && !redoPixmaps.isEmpty()) {

				undoPixmaps.push(currentModifiedPixmap);
				currentModifiedPixmap = null;

				final PixmapRegion oldPix = redoPixmaps.pop();
				Pixmap.setBlending(Blending.None);
				showingTexRegion.getTexture().draw(oldPix.pixmap, oldPix.x,
						oldPix.y);
				currentModifiedPixmap = oldPix;
				updateTotalBounds(oldPix);
				Pixmap.setBlending(Blending.SourceOver);

			} else if (vertexIndex > 0) {

				clampTotalBounds();

				int pixX = Math.round(minX);
				int pixY = Math.round(minY);
				int pixWidth = Math.round(maxX - minX);
				int pixHeight = Math.round(maxY - minY);

				if (currentModifiedPixmap != null) {
					undoPixmaps.push(currentModifiedPixmap);
				} else {
					undoPixmaps.push(flusher);
				}

				frameBuffer.begin();
				drawMesh();
				currentModifiedPixmap = new PixmapRegion(takeScreenShot(pixX,
						pixY, pixWidth, pixHeight), pixX, pixY);
				frameBuffer.end(stageViewport.getViewportX(),
						stageViewport.getViewportY(),
						stageViewport.getViewportWidth(),
						stageViewport.getViewportHeight());

				resetMesh();
			}

			return this.dummyEvent;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public ModelEvent undoCommand() {
			if (undoPixmaps.isEmpty()) {
				return this.dummyEvent;
			}

			redoPixmaps.push(currentModifiedPixmap);
			currentModifiedPixmap = null;

			Pixmap.setBlending(Blending.None);
			flusher.pixmap.fill();

			final PixmapRegion oldPix = undoPixmaps.pop();
			if (oldPix.pixmap != flusher.pixmap) {
				flusher.pixmap.drawPixmap(oldPix.pixmap, oldPix.x, oldPix.y);
			}
			currentModifiedPixmap = oldPix;

			updateTotalBounds(oldPix);

			showingTexRegion.getTexture().draw(flusher.pixmap, flusher.x,
					flusher.y);
			Pixmap.setBlending(Blending.SourceOver);

			return this.dummyEvent;
		}

		@Override
		public boolean combine(Command other) {
			return false;
		}
	};

	/**
	 * Keeps a reference to a {@link Pixmap} and the screen position that should
	 * be drawn. The position is represented in the {@link EditorStage}
	 * coordinate system.
	 */
	private class PixmapRegion implements Disposable {
		private Pixmap pixmap;
		private int x, y;

		/**
		 * Keeps a reference to a {@link Pixmap} and the screen position that
		 * should be drawn. The position is represented in the
		 * {@link EditorStage} coordinate system.
		 */
		public PixmapRegion(Pixmap pixmap, int x, int y) {
			this.pixmap = pixmap;
			this.x = x;
			this.y = y;
		}

		@Override
		public void dispose() {
			if (this.pixmap != null && this.pixmap != flusher.pixmap) {
				this.pixmap.dispose();
				this.pixmap = null;
			}
		}
	}
}
