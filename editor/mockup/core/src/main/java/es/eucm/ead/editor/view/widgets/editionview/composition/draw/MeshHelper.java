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

import java.nio.ByteBuffer;

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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.view.listeners.ActionListener;

/**
 * Handles all the necessary data required to draw brush strokes, undo/redo and
 * delete them.
 */
public class MeshHelper implements Disposable {

	/**
	 * The maximum number of triangles. Increasing this value increases the
	 * length of the line avoiding the user to touchUp and start a new input
	 * process. But this will increase the memory usage and decrease the
	 * performance if the number of triangles is too high.
	 */
	private static final int MAX_TRIANGLES = 1500;
	/**
	 * Used to decide when to draw a dot or to start drawing the brush stroke.
	 */
	private static final int MIN_VERTICES = 0;
	/**
	 * Defines the quality of the dot. The current amount is calculated via
	 * MAX_DOT_TRIANGLES * currentRadius / maxRadius.
	 */
	private static final int MAX_DOT_TRIANGLES = 28;
	/**
	 * Auxiliary constants cached in order to avoid per-frame calculation.
	 */
	private static final int MAX_VERTICES = MAX_TRIANGLES * 2;

	/**
	 * The lower this value is the higher is the accuracy of the brush stroke
	 * but the length of the line will also decrease requiring the user to
	 * touchUp and start a new input process.
	 */
	private static final float DASH_ACCURACY = 50f;

	/**
	 * Used to convert from local to {@link Stage} coordinates and vice versa.
	 */
	private final Vector2 unprojectedVertex = new Vector2();
	/**
	 * Performs the undo/redo encapsulation while drawing.
	 */
	private final DrawLineCommand drawLine = new DrawLineCommand();
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
	 * Used to correctly perform the commands.
	 */
	private final Controller controller;

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
	private float drawRadius = 20f, maxDrawRadius = drawRadius * 2f;
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
	 * Last input position, used to calculate distance-based optimizations.
	 */
	private float lastX, lastY;
	/**
	 * If true, the {@link #combinedMatrix} will be recalculated next time is
	 * needed.
	 */
	private boolean recalculateMatrix;

	/**
	 * Used to know which {@link PixmapRegion} is the most recent. Useful to
	 * save and perform undo/redo commands.
	 */
	private PixmapRegion currModifiedPixmap;
	/**
	 * The {@link TextureRegion} that holds the {@link Texture} to whom the
	 * {@link #mesh} is being drawn, with the help of the {@link #frameBuffer}.
	 * This texture has {@link Stage} coordinates but is drawn with the local
	 * {@link Matrix4} (by the local {@link SpriteBatch}). To achieve this the
	 * {@link TextureRegion} must know what region of the {@link Texture} has to
	 * draw, this is done via
	 * {@link TextureRegion#setRegion(float, float, float, float)} and with what
	 * scale ({@link #scaleX}, {@link #scaleY}). Must keep in mind that
	 * {@link Pixmap}s are drawn with OpenGL ES coordinates (y-down) so
	 * {@link #showingTexRegion} is also flipped vertically via
	 * {@link TextureRegion#flip(boolean, boolean)}.
	 */
	private TextureRegion showingTexRegion;
	/**
	 * Used while erasing.
	 */
	private FrameBuffer fbo;
	private boolean erasing;
	private int vertexLimit;

	private boolean firstSemiCircle = false;
	private float lastAngle;

	private ShaderProgram meshShader;
	private Mesh mesh;

	/**
	 * Handles all the necessary data required to draw brush strokes, undo/redo
	 * and delete them. The {@link #scaledView} parameter will be used to
	 * perform local to stage coordinates conversion in order to recalculate the
	 * new required data/positions. This class assumes that
	 * {@link Pixmap#setBlending(Blending.None)} is activated in order to
	 * function correctly.
	 * 
	 * @param controller
	 *            Used to correctly perform the commands.
	 */
	public MeshHelper(Actor scaledView, Controller controller) {
		this.primitiveType = GL20.GL_TRIANGLE_STRIP;
		this.lineVertices = new float[MAX_VERTICES];
		this.controller = controller;
		this.scaledView = scaledView;
		resetTotalBounds();
		createShader();
		createMesh();

		ActionListener erasePixmaps = new ActionListener() {
			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (!enable) {
					if (actionClass == Undo.class) {
						release(drawLine.undoPixmaps);
					} else {
						release(drawLine.redoPixmaps);
					}
				}
			}
		};
		Actions actions = controller.getActions();
		actions.addActionListener(Undo.class, erasePixmaps);
		actions.addActionListener(Redo.class, erasePixmaps);
	}

	/**
	 * @return true if the {@link #currModifiedPixmap} has been modified and has
	 *         something to save.
	 */
	boolean hasSomethingToSave() {
		return this.currModifiedPixmap != null
				&& this.currModifiedPixmap.pixmap != null
				&& this.currModifiedPixmap.pixmap != this.flusher.pixmap
				&& minX != Float.MAX_VALUE;
	}

	/**
	 * Disposes the contents of the {@link Array} and clears the {@link Array}.
	 * 
	 * @param pixmaps
	 */
	private void release(Array<PixmapRegion> pixmaps) {
		if (pixmaps.size == 0)
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
	 * 
	 * @return
	 */
	PixmapRegion save(FileHandle file) {
		final Pixmap savedPixmap = this.currModifiedPixmap.pixmap;
		// We must convert the OpenGL ES coordinates of the pixels (y-down)
		// to an y-up coordinate system before saving.
		final int w = savedPixmap.getWidth();
		final int h = savedPixmap.getHeight();
		final ByteBuffer pixels = savedPixmap.getPixels();
		byte[] lines = new byte[w * h * 4];
		final int numBytesPerLine = w * 4, height_index = h - 1;
		for (int i = 0; i < h; ++i) {
			pixels.position((height_index - i) * numBytesPerLine);
			pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
		}
		pixels.clear();
		pixels.put(lines);

		PixmapIO.writePNG(file, savedPixmap);
		return currModifiedPixmap;
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
	 * {@link #flusher} to the coordinates the the {@link Stage}, only if they
	 * are null. This method should only be called in {@link #layout()}. If the
	 * {@link Stage} size changed, the resources are translated via
	 * {@link #translateResources(Actor)}.
	 */
	private void reinitializeRenderingResources() {
		if (showingTexRegion != null) {
			return;
		}

		this.recalculateMatrix = true;

		if (this.showingTexRegion == null) {
			this.showingTexRegion = new TextureRegion();
		}

		float w = scaledView.getWidth(), h = scaledView.getHeight();

		fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), false);
		showingTexRegion = new TextureRegion(fbo.getColorBufferTexture());

		clampMinX = 0f;
		clampMinY = 0f;

		clampMaxX = w;
		clampMaxY = h;

		scaledView.localToStageCoordinates(temp.set(0f, 0f));
		int stageX = MathUtils.round(temp.x), stageY = MathUtils.round(temp.y);
		showingTexRegion.setRegion(stageX, stageY, MathUtils.round(w),
				MathUtils.round(h));
		showingTexRegion.flip(false, true);
		if (this.flusher.pixmap == null) {
			this.flusher.pixmap = new Pixmap(MathUtils.round(w),
					MathUtils.round(h), Format.RGBA8888);
			flusher.x = stageX;
			flusher.y = stageY;
		}
	}

	/**
	 * Clears the {@link #mesh}. Nothing will be rendered by the mesh after the
	 * call to this method unless new vertices are added to the {@link #mesh}
	 * via {@link Mesh#setVertices(float[], int, int)}.
	 */
	private void resetMesh() {
		this.vertexIndex = 0;
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
		if (!erasing) {
			drawShowingTexture(batch);
			if (this.recalculateMatrix) {
				this.recalculateMatrix = false;
				this.combinedMatrix.set(batch.getProjectionMatrix()).mul(
						batch.getTransformMatrix());
			}
			if (this.vertexIndex > MIN_VERTICES) {
				batch.end();
				drawMesh();
				batch.begin();
			}
		} else {
			if (this.vertexIndex > MIN_VERTICES && minX != Float.MAX_VALUE) {
				batch.end();
				fbo.begin();
				drawMesh();
				fbo.end();
				batch.begin();
			}
			drawShowingTexture(batch);
		}
	}

	/**
	 * Draws the {@link #showingTexRegion}. Considering the
	 * {@link #showingTexRegion} is created in {@link Stage} coordinate system,
	 * the {@link Texture} is drawn scaled with a scale equal to 1 /
	 * {@link #scaledView}.getScaleXY().
	 * 
	 * @param batch
	 */
	private void drawShowingTexture(Batch batch) {
		batch.draw(this.showingTexRegion, 0, 0);
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
		if (fbo != null) {
			fbo.dispose();
			fbo = null;
		}
		this.mesh.dispose();
		this.mesh = null;
		this.meshShader.dispose();
		this.meshShader = null;
	}

	void touchDown(float x, float y) {
		vertexLimit = Integer.MAX_VALUE;
		drawHalfCircle(0, x, y);
		drawHalfCircle(180, x, y);

		this.lastX = x;
		this.lastY = y;
		this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
		clampTotalBounds(x - drawRadius, y - drawRadius, x + drawRadius, y
				+ drawRadius);
	}

	/**
	 * This method transforms the input to the specified format of the
	 * {@link #mesh} in order to render a brush stroke.
	 * 
	 * @param x
	 * @param y
	 */
	void touchDragged(float x, float y) {
		if (this.vertexIndex >= vertexLimit) {
			return;
		}

		if (this.unprojectedVertex.set(x, y).dst2(this.lastX, this.lastY) > DASH_ACCURACY) {

			if (!firstSemiCircle) {
				firstSemiCircle = true;
				this.vertexIndex = 0;

				this.unprojectedVertex.set(lastX, lastY).sub(x, y);
				float angle = unprojectedVertex.angle() - 90;
				drawHalfCircle(angle, lastX, lastY);
				prepareLine(angle, lastX, lastY);

				vertexIndex += ((1 + getHalfCircleTriangles()) * 4 + 2);
				lastAngle = angle + 180;

			}

			int circleTriangles = ((1 + getHalfCircleTriangles()) * 4 + 2);
			vertexIndex -= circleTriangles;

			vertexLimit = MAX_VERTICES - (circleTriangles + 4);

			this.unprojectedVertex.set(x, y).sub(this.lastX, this.lastY).nor();
			float currentAngle = unprojectedVertex.angle() - 90;

			if (Math.abs(currentAngle - lastAngle) > 45) {

				drawHalfCircle(lastAngle, lastX, lastY);
				prepareLine(lastAngle, lastX, lastY);
				x = lastX;
				y = lastY;
			}
			lastAngle = currentAngle;

			unprojectedVertex.set(-this.unprojectedVertex.y,
					this.unprojectedVertex.x).scl(this.drawRadius);

			float maxNorX = x + this.unprojectedVertex.x;
			this.lineVertices[this.vertexIndex++] = maxNorX;

			float maxNorY = y + this.unprojectedVertex.y;
			this.lineVertices[this.vertexIndex++] = maxNorY;

			float minNorX, minNorY;
			minNorX = x - this.unprojectedVertex.x;
			this.lineVertices[this.vertexIndex++] = minNorX;

			minNorY = y - this.unprojectedVertex.y;
			this.lineVertices[this.vertexIndex++] = minNorY;

			this.lineVertices[this.vertexIndex++] = x;
			this.lineVertices[this.vertexIndex++] = y;
			drawHalfCircle(currentAngle, x, y);

			clampTotalBounds(x - drawRadius, y - drawRadius, x + drawRadius, y
					+ drawRadius);

			this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);

			this.lastX = x;
			this.lastY = y;
		}
	}

	private void prepareLine(float angle, float x, float y) {

		float cosDeg = MathUtils.cosDeg(angle) * drawRadius;
		float maxNorX = x + cosDeg;
		this.lineVertices[this.vertexIndex++] = maxNorX;

		float sinDeg = MathUtils.sinDeg(angle) * drawRadius;
		float maxNorY = y + sinDeg;
		this.lineVertices[this.vertexIndex++] = maxNorY;

		float minNorX = x - cosDeg;
		this.lineVertices[this.vertexIndex++] = minNorX;

		float minNorY = y - sinDeg;
		this.lineVertices[this.vertexIndex++] = minNorY;
	}

	/**
	 * This method decides if a dot should be rendered via
	 * {@link GL20#GL_TRIANGLE_FAN}, if the user didn't drag enough space to
	 * draw a stroke, or a normal {@link #touchDragged(float, float)}.
	 * 
	 * @param x
	 * @param y
	 */
	void touchUp(float x, float y) {

		touchDragged(x, y);
		firstSemiCircle = false;
		if (minX != Float.MAX_VALUE) {
			this.controller.command(drawLine);
		} else {
			resetMesh();
		}
	}

	private void drawHalfCircle(float startingAngle, float x, float y) {
		final int triangleAmount = getHalfCircleTriangles();

		float startCount = triangleAmount * startingAngle / 180;

		float circleStep = MathUtils.PI / triangleAmount;

		for (int i = 0; i <= triangleAmount; ++i) {
			float deg = (i + startCount) * circleStep;
			lineVertices[vertexIndex++] = x + (drawRadius * MathUtils.cos(deg));
			lineVertices[vertexIndex++] = y + (drawRadius * MathUtils.sin(deg));
			lineVertices[vertexIndex++] = x;
			lineVertices[vertexIndex++] = y;
		}

	}

	private int getHalfCircleTriangles() {
		return MathUtils.round(MAX_DOT_TRIANGLES * this.drawRadius
				/ this.maxDrawRadius * .5f);
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
		if (!erasing) {
			this.minX = Math.min(this.minX, minx);
			this.minY = Math.min(this.minY, miny);
			this.maxX = Math.max(this.maxX, maxx);
			this.maxY = Math.max(this.maxY, maxy);
		}
	}

	/**
	 * Sets the {@link Color} of the brush.
	 */
	void setColor(Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
		if (a == 0f) {
			erasing = true;
		} else {
			erasing = false;
		}
	}

	/**
	 * Sets the width of the brush.
	 */
	void setRadius(float radius) {
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
	 * {@link PixmapRegion pixRegion} uses {@link Stage} coordinate system which
	 * is the same as the one used by {@link #minX}, {@link #minY},
	 * {@link #maxX} and {@link #maxY} no conversion must be done before
	 * updating the new values.
	 */
	private void updateTotalBounds(PixmapRegion pixRegion) {
		final Pixmap pix = pixRegion.pixmap;

		if (pix != flusher.pixmap) {
			float x = pixRegion.x;
			float y = pixRegion.y;

			scaledView.stageToLocalCoordinates(temp.set(x, y));
			minX = temp.x;
			minY = temp.y;
			maxX = minX + pix.getWidth();
			maxY = minY + pix.getHeight();
		} else {
			resetTotalBounds();
		}
	}

	/**
	 * Initializes the mesh with the pixels of the given group.
	 * 
	 * The result vectors must be in {@link #scaledView} coordinates.
	 * 
	 * @param toEdit
	 * @param resultOrigin
	 * @param resultSize
	 */
	public void show(Group toEdit, Vector2 resultOrigin, Vector2 resultSize) {
		int x = MathUtils.round(resultOrigin.x), y = MathUtils
				.round(resultOrigin.y), width = (int) resultSize.x, height = (int) resultSize.y;
		minX = x;
		minY = y;
		maxX = minX + width;
		maxY = minY + height;

		scaledView.localToStageCoordinates(temp.set(x, y));
		int stageX = MathUtils.round(temp.x), stageY = MathUtils.round(temp.y);

		Batch batch = controller.getPlatform().getBatch();
		batch.setProjectionMatrix(combinedMatrix);
		fbo.begin();
		batch.begin();
		toEdit.draw(batch, 1f);
		batch.end();
		currModifiedPixmap = new PixmapRegion(takeScreenShot(stageX, stageY,
				width, height), stageX, stageY);
		fbo.end();
	}

	/**
	 * Clears the contents of the current {@link #frameBuffer} by clearing it's
	 * {@link Texture} with the help of the {@link #flusher}.
	 */
	void clear() {
		if (this.showingTexRegion == null)
			return;

		this.flusher.pixmap.fill();
		this.showingTexRegion.getTexture().draw(this.flusher.pixmap,
				this.flusher.x, this.flusher.y);
	}

	/**
	 * Disposes all the {@link Array}s, {@link #currModifiedPixmap} and resets
	 * {@link #minX}, {@link #minY}, {@link #maxX} and {@link #maxY} via
	 * {@link #resetTotalBounds()}. Also disposed {@link #erasedPixmap} and
	 * {@link #currModifiedPixmap} if they weren't already disposed.
	 */
	void release() {
		resetTotalBounds();
		release(drawLine.redoPixmaps);
		release(drawLine.undoPixmaps);
		if (this.currModifiedPixmap != null) {
			this.currModifiedPixmap.dispose();
			this.currModifiedPixmap = null;
		}
	}

	/**
	 * Uses {@link MeshHelper} attributes in order to perform correctly brush
	 * strokes.
	 */
	private class DrawLineCommand extends Command {

		private final boolean debug = false;

		/**
		 * Used by the {@link #drawLine} to perform correctly.
		 */
		private final Array<PixmapRegion> undoPixmaps = new Array<PixmapRegion>(
				false, 15), redoPixmaps = new Array<PixmapRegion>(false, 15);

		private final ModelEvent dummyEvent = new ModelEvent() {
			@Override
			public Object getTarget() {
				return null;
			}
		};

		@Override
		public ModelEvent doCommand() {

			if (vertexIndex == 0 && redoPixmaps.size != 0) {

				undoPixmaps.add(currModifiedPixmap);
				currModifiedPixmap = null;

				final PixmapRegion oldPix = redoPixmaps.pop();
				debug(oldPix);
				showingTexRegion.getTexture().draw(oldPix.pixmap, oldPix.x,
						oldPix.y);
				currModifiedPixmap = oldPix;
				updateTotalBounds(oldPix);

			} else if (vertexIndex > 0) {

				clampTotalBounds();

				scaledView.localToStageCoordinates(temp.set(minX, minY));
				int pixX = MathUtils.round(temp.x);
				int pixY = MathUtils.round(temp.y);
				scaledView.localToStageCoordinates(temp.set(maxX, maxY));
				int pixWidth = MathUtils.round(temp.x - pixX);
				int pixHeight = MathUtils.round(temp.y - pixY);

				if (currModifiedPixmap != null) {
					undoPixmaps.add(currModifiedPixmap);
				} else {
					undoPixmaps.add(flusher);
				}

				takePixmap(pixX, pixY, pixWidth, pixHeight);
				resetMesh();
			}

			return this.dummyEvent;
		}

		private void takePixmap(int x, int y, int width, int height) {
			fbo.begin();
			drawMesh();
			currModifiedPixmap = new PixmapRegion(takeScreenShot(x, y, width,
					height), x, y);
			fbo.end();
		}

		private void debug(PixmapRegion pix) {
			if (debug) {
				pix.pixmap.setColor(Color.GREEN);
				pix.pixmap.drawRectangle(pix.x, pix.y, pix.pixmap.getWidth(),
						pix.pixmap.getHeight());
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public ModelEvent undoCommand() {
			if (undoPixmaps.size == 0) {
				return this.dummyEvent;
			}

			redoPixmaps.add(currModifiedPixmap);
			currModifiedPixmap = null;

			if (debug) {
				flusher.pixmap.setColor(Color.BLUE);
			}
			flusher.pixmap.fill();

			final PixmapRegion oldPix = undoPixmaps.pop();
			if (oldPix.pixmap != flusher.pixmap) {
				debug(oldPix);
				flusher.pixmap.drawPixmap(oldPix.pixmap, oldPix.x - flusher.x,
						oldPix.y - flusher.y);
			}
			currModifiedPixmap = oldPix;

			updateTotalBounds(oldPix);
			debug(flusher);

			showingTexRegion.getTexture().draw(flusher.pixmap, flusher.x,
					flusher.y);

			return this.dummyEvent;
		}

		@Override
		public boolean combine(Command other) {
			return false;
		}
	};

	/**
	 * Keeps a reference to a {@link Pixmap} and the screen position that should
	 * be drawn. The position is represented in the {@link Stage} coordinate
	 * system.
	 */
	class PixmapRegion implements Disposable {
		Pixmap pixmap;
		int x, y;

		/**
		 * Keeps a reference to a {@link Pixmap} and the screen position that
		 * should be drawn. The position is represented in the {@link Stage}
		 * coordinate system.
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
