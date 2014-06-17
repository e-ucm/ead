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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionListener;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;

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
	private static final int MIN_VERTICES = 8;
	/**
	 * Defines the quality of the dot. The current amount is calculated via
	 * MAX_DOT_TRIANGLES * currentRadius / maxRadius.
	 */
	private static final int MAX_DOT_TRIANGLES = 14;
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
	private static final float DASH_ACCURACY = 50f;

	/**
	 * The maximum width or height of the thumbnail.
	 */
	private static final float MAX_THUMBNAIL_SIZE = 100F;

	/**
	 * Establishes the maximum amount of inputs that will be cached before
	 * updating the eased pixels to the GPU while in
	 * {@link #eraseTouchDragged(float, float)} .
	 */
	private static final int MAX_CACHED_ERASING_INPUTS = 2;

	/**
	 * Used to convert from local to {@link Stage} coordinates and vice versa.
	 */
	private final Vector2 unprojectedVertex = new Vector2();
	/**
	 * Performs the undo/redo encapsulation while drawing.
	 */
	private final DrawLineCommand drawLine = new DrawLineCommand();
	/**
	 * Performs the undo/redo encapsulation while erasing.
	 */
	private final EraseLineCommand eraseLine = new EraseLineCommand();
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
	private int cachedErasingInput, cachedErasingInputs;
	/**
	 * Used to perform the coordinate translation when the window is resized.
	 */
	private int prevParentX, prevParentY;
	private int eraseRadius;
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
	 * Defines the scale with which the {@link #showingTexRegion} should be
	 * drawn.
	 */
	private float scaleX, scaleY;
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
	 * If true, the resources will be recalculated after a resize event;
	 */
	private boolean resizable = false;

	/**
	 * Used to know the previous view port of the default frame buffer.
	 */
	private Viewport stageViewport;

	/**
	 * Used to know which {@link PixmapRegion} is the most recent. Useful to
	 * save and perform undo/redo commands.
	 */
	private PixmapRegion currModifiedPixmap;
	/**
	 * Used to perform undo/redo commands while erasing.
	 */
	private Pixmap erasedPixmap;
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
	private ShaderProgram meshShader;
	private FrameBuffer frameBuffer;
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
		this.lineVertices = new float[MAX_VERTICES];
		this.controller = controller;
		this.scaledView = scaledView;
		resetTotalBounds();
		createShader();
		createMesh();

		Actions actions = controller.getActions();
		actions.addActionListener(Undo.class, new ActionListener() {
			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (!enable) {
					release(drawLine.undoPixmaps);
					release(eraseLine.undoPixmaps);
				}
			}
		});
		actions.addActionListener(Redo.class, new ActionListener() {
			@Override
			public void enableChanged(Class actionClass, boolean enable) {
				if (!enable) {
					release(drawLine.redoPixmaps);
					release(eraseLine.redoPixmaps);
				}
			}
		});
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
		release(eraseLine.redoPixmaps);
		release(eraseLine.undoPixmaps);
		if (this.erasedPixmap != null) {
			this.erasedPixmap.dispose();
			this.erasedPixmap = null;
		}
		if (this.currModifiedPixmap != null) {
			this.currModifiedPixmap.dispose();
			this.currModifiedPixmap = null;
		}
	}

	/**
	 * @return true if the {@link #currModifiedPixmap} has been modified and has
	 *         something to save.
	 */
	boolean hasSomethingToSave() {
		return this.currModifiedPixmap != null
				&& this.currModifiedPixmap.pixmap != null
				&& this.currModifiedPixmap.pixmap != this.flusher.pixmap;
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
	 */
	void save(FileHandle file, FileHandle thumbFile) {
		final Pixmap savedPixmap = this.currModifiedPixmap.pixmap;
		scaledView.stageToLocalCoordinates(temp.set(currModifiedPixmap.x,
				currModifiedPixmap.y));
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

		float thumbW, thumbH;
		if (w > h) {
			if (w > MAX_THUMBNAIL_SIZE) {
				float div = ((float) w) / MAX_THUMBNAIL_SIZE;

				thumbW = MAX_THUMBNAIL_SIZE;
				thumbH = ((float) h) / div;
			} else {
				thumbW = w;
				thumbH = h;
			}
		} else {
			if (h > MAX_THUMBNAIL_SIZE) {
				float div = ((float) h) / MAX_THUMBNAIL_SIZE;

				thumbH = MAX_THUMBNAIL_SIZE;
				thumbW = ((float) w) / div;
			} else {
				thumbW = w;
				thumbH = h;
			}
		}

		// Thumbnail...
		Pixmap thumbnail = new Pixmap(Math.round(thumbW), Math.round(thumbH),
				Format.RGBA8888);

		thumbnail.drawPixmap(savedPixmap, 0, 0, savedPixmap.getWidth(),
				savedPixmap.getHeight(), 0, 0, thumbnail.getWidth(),
				thumbnail.getHeight());

		PixmapIO.writePNG(thumbFile, thumbnail);
		thumbnail.dispose();
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
		Stage stage = this.scaledView.getStage();
		int stageWidth = MathUtils.round(stage.getWidth());
		int stageHeight = MathUtils.round(stage.getHeight());

		if (this.resizable && this.frameBuffer != null) {
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

			scaledView.localToStageCoordinates(temp.set(x, y));
			clampMinX = temp.x;
			clampMinY = temp.y;

			scaledView.localToStageCoordinates(temp.set(x + w, y + h));
			clampMaxX = temp.x;
			clampMaxY = temp.y;

			Gdx.app.log(MESH_TAG, "clamp bounds ~> " + clampMinX + ", "
					+ clampMinY + ", " + clampMaxX + ", " + clampMaxY);

			Gdx.app.log(MESH_TAG, "texture region ~> " + x + ", " + y + ", "
					+ w + ", " + h);

			this.showingTexRegion.setRegion(MathUtils.round(clampMinX),
					MathUtils.round(clampMinY),
					MathUtils.round(clampMaxX - clampMinX),
					MathUtils.round(clampMaxY - clampMinY));
			this.showingTexRegion.flip(false, true);

			if (this.flusher.pixmap == null) {
				this.flusher.pixmap = new Pixmap(
						MathUtils.round(this.frameBuffer.getWidth()),
						MathUtils.round(this.frameBuffer.getHeight()),
						Format.RGBA8888);
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
		scaledView.localToStageCoordinates(temp.set(parent.getX(),
				parent.getY()));
		int newx = MathUtils.round(temp.x);
		int newy = MathUtils.round(temp.y);
		int offsetX = newx - this.prevParentX;
		int offsetY = newy - this.prevParentY;
		if (this.currModifiedPixmap != null) {
			this.currModifiedPixmap.x += offsetX;
			this.currModifiedPixmap.y += offsetY;

			if (this.currModifiedPixmap.pixmap != null) {
				final Texture colorTexture = this.showingTexRegion.getTexture();
				colorTexture.draw(this.currModifiedPixmap.pixmap,
						this.currModifiedPixmap.x, this.currModifiedPixmap.y);
			}
		}
		translate(eraseLine.redoPixmaps, offsetX, offsetY);
		translate(eraseLine.undoPixmaps, offsetX, offsetY);
		translate(drawLine.redoPixmaps, offsetX, offsetY);
		translate(drawLine.undoPixmaps, offsetX, offsetY);
		this.flusher.x = 0;
		this.flusher.y = 0;
		minX += offsetX;
		maxX += offsetX;
		minY += offsetY;
		maxY += offsetY;
		this.prevParentX = newx;
		this.prevParentY = newy;
	}

	/**
	 * Converts the {@link PixmapRegion}s from the {@link Array} to the new
	 * {@link Stage} size. The new position is determined by offsetX and
	 * offsetY.
	 */
	private void translate(Array<PixmapRegion> stack, int offsetX, int offsetY) {
		if (stack.size != 0) {
			for (PixmapRegion redoPixReg : stack) {
				if (currModifiedPixmap != redoPixReg) {
					redoPixReg.x += offsetX;
					redoPixReg.y += offsetY;
				}
			}
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
	 * {@link #showingTexRegion} is created in {@link Stage} coordinate system,
	 * the {@link Texture} is drawn scaled with a scale equal to 1 /
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

	void drawTouchDown(float x, float y) {
		this.primitiveType = GL20.GL_TRIANGLE_STRIP;
		this.lineVertices[this.vertexIndex++] = x;
		this.lineVertices[this.vertexIndex++] = y;

		this.lastX = Float.MAX_VALUE;
		this.lastY = this.lastX;

		clampTotalBounds(x, y, x, y);
	}

	/**
	 * This method transforms the input to the specified format of the
	 * {@link #mesh} in order to render a brush stroke.
	 * 
	 * @param x
	 * @param y
	 */
	void drawTouchDragged(float x, float y) {
		if (this.vertexIndex == MAX_VERTICES_2)
			return;

		this.unprojectedVertex.set(x, y);

		x = this.unprojectedVertex.x;
		y = this.unprojectedVertex.y;

		if (this.unprojectedVertex.dst2(this.lastX, this.lastY) > DASH_ACCURACY) {

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
	 * This method decides if a dot should be rendered via
	 * {@link GL20#GL_TRIANGLE_FAN}, if the user didn't drag enough space to
	 * draw a stroke, or a normal {@link #drawTouchDragged(float, float)}.
	 * 
	 * @param x
	 * @param y
	 */
	void drawTouchUp(float x, float y) {
		if (this.vertexIndex < MIN_VERTICES) {
			this.vertexIndex = 0;
			final int startCount = 2;
			final int triangleAmount = startCount
					+ MathUtils.round(MAX_DOT_TRIANGLES * this.drawRadius
							/ this.maxDrawRadius) + 1;
			primitiveType = GL20.GL_TRIANGLE_FAN;
			lineVertices[vertexIndex++] = x;
			lineVertices[vertexIndex++] = y;

			float rightMostX = x + drawRadius;
			lineVertices[vertexIndex++] = rightMostX;
			lineVertices[vertexIndex++] = y;

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

			clampTotalBounds(x - drawRadius, y - drawRadius, rightMostX, y
					+ drawRadius);

			this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
		} else if (x != this.lastX && y != this.lastY
				&& this.vertexIndex < MAX_VERTICES_2) {
			drawTouchDown(x, y);
			this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
		}
		this.controller.command(drawLine);
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
		this.minX = Math.min(this.minX, minx);
		this.minY = Math.min(this.minY, miny);
		this.maxX = Math.max(this.maxX, maxx);
		this.maxY = Math.max(this.maxY, maxy);
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
	void setEraseRadius(float radius) {
		this.eraseRadius = MathUtils.round(radius);
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

	float getScaleX() {
		return scaleX;
	}

	float getScaleY() {
		return scaleY;
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

			minX = x;
			minY = y;
			maxX = x + pix.getWidth();
			maxY = y + pix.getHeight();
		} else {
			resetTotalBounds();
		}
	}

	void eraseTouchDown(float stageX, float stageY) {
		if (currModifiedPixmap == null)
			return;
		erasedPixmap = new Pixmap(currModifiedPixmap.pixmap.getWidth(),
				currModifiedPixmap.pixmap.getHeight(), Format.RGBA8888);
		erasedPixmap.drawPixmap(currModifiedPixmap.pixmap, 0, 0);

		currModifiedPixmap.pixmap.fillCircle(MathUtils.round(stageX)
				- currModifiedPixmap.x, MathUtils.round(stageY)
				- currModifiedPixmap.y, eraseRadius);
		showingTexRegion.getTexture().draw(currModifiedPixmap.pixmap,
				currModifiedPixmap.x, currModifiedPixmap.y);
		this.cachedErasingInput = 0;

		int fps = Gdx.graphics.getFramesPerSecond() - 25;
		this.cachedErasingInputs = fps < 0 ? 0 : MathUtils
				.round(MAX_CACHED_ERASING_INPUTS * fps / 35f);
	}

	void eraseTouchDragged(float stageX, float stageY) {
		if (currModifiedPixmap == null)
			return;
		currModifiedPixmap.pixmap.fillCircle(MathUtils.round(stageX)
				- currModifiedPixmap.x, MathUtils.round(stageY)
				- currModifiedPixmap.y, eraseRadius);
		if (this.cachedErasingInput >= this.cachedErasingInputs) {
			this.cachedErasingInput = 0;
			showingTexRegion.getTexture().draw(currModifiedPixmap.pixmap,
					currModifiedPixmap.x, currModifiedPixmap.y);
		} else {
			++cachedErasingInput;
		}
	}

	/**
	 * @return The position of the saved image in {@link #scaledView} local
	 *         coordinates.
	 */
	Vector2 getPosition() {
		return temp;
	}

	/**
	 * @param resizable
	 *            if true the resources are recreated after a resize event.
	 *            Default is false.
	 */
	void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	void eraseTouchUp(float stageX, float stageY) {
		if (currModifiedPixmap == null)
			return;
		currModifiedPixmap.pixmap.fillCircle(MathUtils.round(stageX)
				- currModifiedPixmap.x, MathUtils.round(stageY)
				- currModifiedPixmap.y, eraseRadius);
		showingTexRegion.getTexture().draw(currModifiedPixmap.pixmap,
				currModifiedPixmap.x, currModifiedPixmap.y);
		controller.command(eraseLine);
	}

	/**
	 * Uses {@link MeshHelper} attributes in order to perform correctly brush
	 * strokes.
	 */
	private class DrawLineCommand extends Command {

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
				showingTexRegion.getTexture().draw(oldPix.pixmap, oldPix.x,
						oldPix.y);
				currModifiedPixmap = oldPix;
				updateTotalBounds(oldPix);

			} else if (vertexIndex > 0) {

				clampTotalBounds();

				int pixX = MathUtils.round(minX);
				int pixY = MathUtils.round(minY);
				int pixWidth = MathUtils.round(maxX - minX);
				int pixHeight = MathUtils.round(maxY - minY);

				if (currModifiedPixmap != null) {
					undoPixmaps.add(currModifiedPixmap);
				} else {
					undoPixmaps.add(flusher);
				}

				frameBuffer.begin();
				drawMesh();
				currModifiedPixmap = new PixmapRegion(takeScreenShot(pixX,
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
			if (undoPixmaps.size == 0) {
				return this.dummyEvent;
			}

			redoPixmaps.add(currModifiedPixmap);
			currModifiedPixmap = null;

			flusher.pixmap.fill();

			final PixmapRegion oldPix = undoPixmaps.pop();
			if (oldPix.pixmap != flusher.pixmap) {
				flusher.pixmap.drawPixmap(oldPix.pixmap, oldPix.x, oldPix.y);
			}
			currModifiedPixmap = oldPix;

			updateTotalBounds(oldPix);

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
	 * Uses {@link MeshHelper} attributes in order to erase correctly brush
	 * strokes.
	 */
	private class EraseLineCommand extends Command {

		/**
		 * Used by the {@link #eraseLine} to perform correctly.
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

			if (erasedPixmap != null) {

				undoPixmaps.add(new PixmapRegion(erasedPixmap,
						currModifiedPixmap.x, currModifiedPixmap.y));
				erasedPixmap = null;

			} else if (redoPixmaps.size != 0) {

				undoPixmaps.add(currModifiedPixmap);
				currModifiedPixmap = null;

				PixmapRegion oldPix = redoPixmaps.pop();
				showingTexRegion.getTexture().draw(oldPix.pixmap, oldPix.x,
						oldPix.y);

				currModifiedPixmap = oldPix;

			}

			return this.dummyEvent;
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

			PixmapRegion oldPix = undoPixmaps.pop();
			showingTexRegion.getTexture().draw(oldPix.pixmap, oldPix.x,
					oldPix.y);

			currModifiedPixmap = oldPix;

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
	private class PixmapRegion implements Disposable {
		private Pixmap pixmap;
		private int x, y;

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
