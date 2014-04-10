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
import java.nio.ByteBuffer;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Actions;
import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action.ActionListener;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Transformation;

/**
 * A widget that draws lines renders them to a texture and manages the necessary
 * {@link Pixmap pixmaps} to perform undo/redo actions, erase and save it as a
 * {@link SceneElement}
 */
public class PaintingWidget extends Widget implements Disposable {

	private static final String PAINTING_TAG = "Painting";

	private final Controller controller;
	private final MeshHelper mesh;
	private String savePath;

	public PaintingWidget(Controller controller) {
		this.controller = controller;
		this.mesh = new MeshHelper();
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					PaintingWidget.this.mesh.input(x, y);
				}
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (pointer == 0) {
					PaintingWidget.this.mesh.input(x, y);
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					PaintingWidget.this.mesh.touchUp(x, y);
					PaintingWidget.this.controller
							.command(PaintingWidget.this.mesh.drawLine);
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

	public void save() {
		String savingPath = this.controller.getLoadingPath() + File.separator
				+ "images" + File.separator;
		final EditorGameAssets gameAssets = this.controller
				.getEditorGameAssets();
		final FileHandle savingDir = gameAssets.absolute(savingPath);
		if (!savingDir.exists()) {
			savingDir.mkdirs();
		}
		final String name = this.controller.getEditorGameAssets().getI18N()
				.m("element");
		savingPath += name;
		FileHandle savingImage = null;
		int i = 0;
		do {
			savingImage = gameAssets.absolute(savingPath + (++i) + ".png");
		} while (savingImage.exists());

		this.mesh.save(savingImage);

		this.savePath = File.separator + "images" + File.separator + name + i
				+ ".png";
	}

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

	public void release() {
		Commands commands = this.controller.getCommands();
		commands.getUndoHistory().clear();
		commands.getRedoHistory().clear();
		this.mesh.initTotalBounds();
		this.mesh.release();
	}

	public void flush() {
		this.mesh.flush();
	}

	public void setRadius(float radius) {
		this.mesh.setRadius(radius);
	}

	@Override
	public void setColor(Color color) {
		this.mesh.setColor(color);
	}

	@Override
	public void dispose() {
		this.mesh.dispose();
	}

	private class MeshHelper implements Disposable {

		private static final int MAX_LINES = 250;
		private static final int MAX_VERTICES = MAX_LINES * 2 * 2;
		private static final int MAX_VERTICES_2 = MAX_VERTICES - 2;

		private static final float DASH_ACCURACY = 250;
		private static final float CURVE_ACCURACY = MathUtils.PI / 10;

		private final Stack<PixmapRegion> undoPixmaps = new Stack<PixmapRegion>();
		private final Stack<PixmapRegion> redoPixmaps = new Stack<PixmapRegion>();
		private final Vector2 unprojectedVertex = new Vector2();
		private final Vector2 minxy = new Vector2();
		private final Vector2 maxxy = new Vector2();

		private float minX, minY, maxX, maxY;

		private float r = 1f, g = 1f, b = 0f, a = 1f;

		private final float[] lineVertices;
		private ShaderProgram meshShader;
		private int vertexIndex = 0, renderType;
		private Mesh mesh;

		private float lastX, lastY;
		private float radius = 20f;

		private TextureRegion showingTexRegion;
		private PixmapRegion currentModifiedPixmap;
		private FrameBuffer frameBuffer;
		private Matrix4 combinedMatrix;
		private Pixmap flusher;

		public MeshHelper() {
			this.lineVertices = new float[MAX_VERTICES];
			createShader();
			createMesh();
			initTotalBounds();
			Actions actions = PaintingWidget.this.controller.getActions();
			actions.addActionListener(Undo.class, new ActionListener() {
				@Override
				public void enableChanged(Class actionClass, boolean enable) {
					if (!enable) {
						release(MeshHelper.this.undoPixmaps);
					}
				}
			});
			actions.addActionListener(Redo.class, new ActionListener() {
				@Override
				public void enableChanged(Class actionClass, boolean enable) {
					if (!enable) {
						release(MeshHelper.this.redoPixmaps);
					}
				}
			});
		}

		private void flush() {
			if (this.showingTexRegion == null)
				return;

			Pixmap.setBlending(Blending.None);
			this.flusher.fill();
			this.showingTexRegion.getTexture().draw(this.flusher, 0, 0);
			Pixmap.setBlending(Blending.SourceOver);
		}

		public void release() {
			release(this.redoPixmaps);
			release(this.undoPixmaps);
			if (this.currentModifiedPixmap != null) {
				this.currentModifiedPixmap.dispose();
				this.currentModifiedPixmap = null;
			}
		}

		private void release(Stack<PixmapRegion> pixmaps) {
			if (pixmaps.isEmpty())
				return;
			for (PixmapRegion pixmap : pixmaps) {
				pixmap.dispose();
				pixmap = null;
			}
			pixmaps.clear();
		}

		private void layout() {
			initResources();
		}

		/**
		 * Saves the minimum amount of pixels that encapsulates the drawn image.
		 */
		private void save(FileHandle file) {
			PixmapIO.writePNG(file, this.currentModifiedPixmap.pixmap);
		}

		private Pixmap takeScreenShot(int x, int y, int w, int h) {
			Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
			final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
			ByteBuffer pixels = pixmap.getPixels();
			Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA,
					GL20.GL_UNSIGNED_BYTE, pixels);
			return pixmap;
		}

		private void initResources() {
			if (this.frameBuffer == null) {
				int stageWidth = Math.round(getStage().getWidth());
				int stageHeight = Math.round(getStage().getHeight());

				Gdx.app.log(PAINTING_TAG, "stageWidth: " + stageWidth
						+ ", stageHeight: " + stageHeight);

				this.frameBuffer = new FrameBuffer(Format.RGBA8888, stageWidth,
						stageHeight, false);

				if (this.showingTexRegion == null) {
					this.showingTexRegion = new TextureRegion();
				}
				final Texture colorTexture = this.frameBuffer
						.getColorBufferTexture();

				this.showingTexRegion.setTexture(colorTexture);
				Actor parent = getParent();
				float x = parent.getX(), y = parent.getY(), w = parent
						.getWidth() * parent.getScaleX(), h = parent
						.getHeight() * parent.getScaleY();

				Gdx.app.log(PAINTING_TAG, "Texture Regions: " + x + ", " + y
						+ ", " + w + ", " + h);

				this.showingTexRegion.setRegion(Math.round(x), Math.round(y),
						Math.round(w), Math.round(h));
				this.showingTexRegion.flip(false, true);

				if (flusher == null) {
					flusher = new Pixmap(
							Math.round(this.frameBuffer.getWidth()),
							Math.round(this.frameBuffer.getHeight()),
							Format.RGBA8888);

				}
			}
		}

		private void clampBounds() {
			float width = getWidth(), height = getHeight();
			float x = 0, y = 0;
			if (this.minX < x) {
				this.minX = x;
			}

			if (this.maxX > width) {
				this.maxX = width;
			}

			if (this.minY < y) {
				this.minY = y;
			}

			if (this.maxY > height) {
				this.maxY = height;
			}
		}

		private void reset() {
			this.vertexIndex = 0;
			this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
		}

		private void createMesh() {
			this.mesh = new Mesh(true, MAX_VERTICES, 0, new VertexAttribute(
					Usage.Position, 2, "a_position"));
		}

		private void createShader() {
			// this shader tells OpenGL where to put things
			final String vertexShader = "attribute vec4 a_position; \n"
					+ "uniform vec4 u_color;						\n"
					+ "uniform mat4 u_worldView;					\n"
					+ "varying vec4 v_color;						\n"
					+ "void main()                  				\n"
					+ "{                            				\n"
					+ "   v_color = u_color;						\n"
					+ "   gl_Position =  u_worldView * a_position;	}";

			// this one tells it what goes in between the points (i.e
			// color/texture)
			final String fragmentShader = "#ifdef GL_ES     \n"
					+ "precision mediump float;    			\n"
					+ "#endif                      			\n"
					+ "varying vec4 v_color;				\n"
					+ "void main()                 			\n"
					+ "{                           			\n"
					+ "  gl_FragColor = v_color;   			}";

			// make an actual shader from our strings
			ShaderProgram.pedantic = false;
			this.meshShader = new ShaderProgram(vertexShader, fragmentShader);

			// check there's no shader compile error
			if (this.meshShader.isCompiled() == false)
				throw new IllegalStateException(this.meshShader.getLog());
		}

		private void draw(Batch batch, float parentAlpha) {
			drawShowingTexture(batch);
			if (this.vertexIndex == 0)
				return;
			batch.end();
			if (this.combinedMatrix == null) {
				this.combinedMatrix = new Matrix4(batch.getProjectionMatrix())
						.mul(batch.getTransformMatrix());
			}
			drawMesh();
			batch.begin();
		}

		private void drawShowingTexture(Batch batch) {
			Actor parent = getParent();
			batch.draw(this.showingTexRegion, 0, 0, 0, 0,
					this.showingTexRegion.getRegionWidth(),
					this.showingTexRegion.getRegionHeight(),
					1 / parent.getScaleX(), 1 / parent.getScaleY(), 0);
		}

		private void drawMesh() {
			this.meshShader.begin();

			this.meshShader.setUniformf("u_color", this.r, this.g, this.b,
					this.a);
			this.meshShader
					.setUniformMatrix("u_worldView", this.combinedMatrix);

			this.mesh.render(this.meshShader, renderType);

			this.meshShader.end();
		}

		@Override
		public void dispose() {
			if (this.flusher != null) {
				this.flusher.dispose();
				this.flusher = null;
			}
			this.mesh.dispose();
			this.mesh = null;
			this.meshShader.dispose();
			this.meshShader = null;
			this.frameBuffer.dispose();
			this.frameBuffer = null;
		}

		private void input(float x, float y) {
			if (this.vertexIndex == MAX_VERTICES_2)
				return;

			float oldX = this.unprojectedVertex.x;
			float oldY = this.unprojectedVertex.y;
			this.unprojectedVertex.set(x, y);

			x = this.unprojectedVertex.x;
			y = this.unprojectedVertex.y;

			if (this.vertexIndex == 0) {
				this.lineVertices[this.vertexIndex++] = x;
				this.lineVertices[this.vertexIndex++] = y;

				this.lastX = x;
				this.lastY = y;
			} else if (this.unprojectedVertex.dst2(this.lastX, this.lastY) > DASH_ACCURACY
					|| (MathUtils.atan2(unprojectedVertex.x,
							unprojectedVertex.y) - MathUtils.atan2(oldX, oldY)) > CURVE_ACCURACY) {

				this.unprojectedVertex.set(x, y).sub(this.lastX, this.lastY)
						.nor();
				this.unprojectedVertex.set(-this.unprojectedVertex.y,
						this.unprojectedVertex.x);
				this.unprojectedVertex.scl(this.radius);

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

				if (this.vertexIndex > 5) {
					renderType = GL20.GL_TRIANGLE_STRIP;
					this.minX = Math.min(this.minX, Math.min(maxNorX, minNorX));
					this.minY = Math.min(this.minY, Math.min(maxNorY, minNorY));
					this.maxX = Math.max(this.maxX, Math.max(maxNorX, minNorX));
					this.maxY = Math.max(this.maxY, Math.max(maxNorY, minNorY));

					this.mesh.setVertices(this.lineVertices, 0,
							this.vertexIndex);
				}

				this.lastX = x;
				this.lastY = y;
			}
		}

		private void touchUp(float x, float y) {
			if (this.vertexIndex < 6) {
				this.vertexIndex = 0;
				final int triangleAmount = 9;
				renderType = GL20.GL_TRIANGLE_FAN;
				lineVertices[vertexIndex++] = x;
				lineVertices[vertexIndex++] = y;

				lineVertices[vertexIndex++] = x + (radius);
				lineVertices[vertexIndex++] = y + (0);

				float circleStep = MathUtils.PI2 / (triangleAmount);
				lineVertices[vertexIndex++] = x
						+ (radius * MathUtils.cos(circleStep));
				lineVertices[vertexIndex++] = y
						+ (radius * MathUtils.sin(circleStep));

				for (int i = 2; i <= triangleAmount; i++) {
					lineVertices[vertexIndex++] = x
							+ (radius * MathUtils.cos(i * circleStep));
					lineVertices[vertexIndex++] = y
							+ (radius * MathUtils.sin(i * circleStep));
				}

				this.minX = Math.min(this.minX, x - radius);
				this.minY = Math.min(this.minY, y - radius);
				this.maxX = Math.max(this.maxX, x + radius);
				this.maxY = Math.max(this.maxY, y + radius);
				this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
			}
		}

		private void initTotalBounds() {
			this.minX = this.minY = Float.MAX_VALUE;
			this.maxX = this.maxY = Float.MIN_VALUE;
		}

		private void setColor(Color color) {
			this.r = color.r;
			this.g = color.g;
			this.b = color.b;
			this.a = color.a;
		}

		private void setRadius(float radius) {
			this.radius = radius;
		}

		private final Command drawLine = new Command() {

			private final ModelEvent dummyEvent = new ModelEvent() {
				@Override
				public Object getTarget() {
					return null;
				}
			};

			@Override
			public ModelEvent doCommand() {

				if (MeshHelper.this.vertexIndex == 0
						&& !MeshHelper.this.redoPixmaps.isEmpty()) {

					MeshHelper.this.undoPixmaps
							.push(MeshHelper.this.currentModifiedPixmap);
					MeshHelper.this.currentModifiedPixmap = null;

					final PixmapRegion oldPix = MeshHelper.this.redoPixmaps
							.pop();
					Pixmap.setBlending(Blending.None);
					MeshHelper.this.showingTexRegion.getTexture().draw(
							oldPix.pixmap, oldPix.x, oldPix.y);
					MeshHelper.this.currentModifiedPixmap = oldPix;
					Pixmap.setBlending(Blending.SourceOver);

				} else if (vertexIndex > 0) {
					clampBounds();

					localToStageCoordinates(minxy.set(minX, minY));
					localToStageCoordinates(maxxy.set(maxX, maxY));

					int pixX = Math.round(minxy.x);
					int pixY = Math.round(minxy.y);
					int pixWidth = Math.round(maxxy.x - minxy.x);
					int pixHeight = Math.round(maxxy.y - minxy.y);

					if (MeshHelper.this.currentModifiedPixmap != null) {
						MeshHelper.this.undoPixmaps
								.push(MeshHelper.this.currentModifiedPixmap);
					} else {
						MeshHelper.this.undoPixmaps.push(new PixmapRegion(
								flusher, 0, 0));
					}
					MeshHelper.this.frameBuffer.begin();
					drawMesh();
					MeshHelper.this.currentModifiedPixmap = new PixmapRegion(
							takeScreenShot(pixX, pixY, pixWidth, pixHeight),
							pixX, pixY);

					MeshHelper.this.frameBuffer.end();

					PaintingWidget.this.mesh.reset();
				}

				return this.dummyEvent;
			}

			@Override
			public boolean canUndo() {
				return true;
			}

			@Override
			public ModelEvent undoCommand() {
				if (MeshHelper.this.undoPixmaps.isEmpty()) {
					return this.dummyEvent;
				}

				MeshHelper.this.redoPixmaps
						.push(MeshHelper.this.currentModifiedPixmap);
				MeshHelper.this.currentModifiedPixmap = null;

				Pixmap.setBlending(Blending.None);
				flusher.fill();

				final PixmapRegion oldPix = MeshHelper.this.undoPixmaps.pop();
				if (oldPix.pixmap != flusher) {
					flusher.drawPixmap(oldPix.pixmap, oldPix.x, oldPix.y);
				}
				MeshHelper.this.currentModifiedPixmap = oldPix;

				MeshHelper.this.showingTexRegion.getTexture().draw(flusher, 0,
						0);
				Pixmap.setBlending(Blending.SourceOver);

				return this.dummyEvent;
			}

			@Override
			public boolean combine(Command other) {
				return false;
			}
		};

		private class PixmapRegion implements Disposable {
			private Pixmap pixmap;
			private int x, y;

			public PixmapRegion(Pixmap pixmap, int x, int y) {
				this.pixmap = pixmap;
				this.x = x;
				this.y = y;
			}

			@Override
			public void dispose() {
				if (this.pixmap != MeshHelper.this.flusher) {
					this.pixmap.dispose();
					this.pixmap = null;
				}
			}
		}
	}
}