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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
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

	public PaintingWidget(Controller controller) {
		this.controller = controller;
		this.mesh = new MeshHelper();
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					mesh.input(x, y);
				}
				return true;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (pointer == 0) {
					mesh.input(x, y);
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					mesh.input(x, y);
					mesh.restartDrawing();
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
		final EditorGameAssets gameAssets = this.controller.getEditorGameAssets();
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

		SceneElement savedElement = this.controller.getTemplates()
				.createSceneElement(File.separator + "images" + File.separator + name + i + ".png");
		Transformation transform = savedElement.getTransformation();
		transform.setScaleX(1 / getParent().getScaleX());
		transform.setScaleY(-1 / getParent().getScaleY());
		transform.setX(transform.getOriginX()
				* (transform.getScaleX() - 1));
		transform.setY(transform.getOriginY());
		this.controller.action(AddSceneElement.class, savedElement);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			this.mesh.flush();
		}
	}

	@Override
	public void dispose() {
		this.mesh.dispose();
	}

	private class MeshHelper implements Disposable {
		private Mesh mesh;
		private ShaderProgram meshShader;
		private static final int MAX_LINES = 500;
		private final Color transparentColor = new Color(0f, 0f, 0f, 0f);
		private final float[] lineVertices;
		private int vertexIndex = 0;
		private final Vector2 unprojectedVertex = new Vector2();
		private final Vector2 minxy = new Vector2();
		private final Vector2 maxxy = new Vector2();
		private float lastX, lastY;
		private float r = 1f, g = 1f, b = 0f, a = 1f;
		private float radius = 20;
		private float minX, minY, maxX, maxY, prevMinX, prevMinY, prevMaxX,
				prevMaxY;
		private TextureRegion showingTexRegion;
		private FrameBuffer frameBuffer;
		private Matrix4 combinedMatrix;

		public MeshHelper() {
			this.lineVertices = new float[MAX_LINES * 2 * 2];
			createShader();
			createMesh();
			initBounds();
		}

		private void flush() {
			if (this.showingTexRegion == null)
				return;
			final Pixmap flusher = new Pixmap(
					Math.round(frameBuffer.getWidth()), Math.round(frameBuffer
							.getHeight()), Format.RGBA8888);

			Pixmap.setBlending(Blending.None);
			flusher.setColor(transparentColor);
			flusher.fill();
			this.showingTexRegion.getTexture().draw(flusher, 0, 0);
			flusher.dispose();
			Pixmap.setBlending(Blending.SourceOver);
			initBounds();
		}

		private void layout() {
			initResources();
		}

		private void restartDrawing() {
			this.frameBuffer.begin();
			drawMesh();
			this.frameBuffer.end();
			reset();
		}

		/**
		 * Saves the minimum amount of pixels that encapsulates the drawn image.
		 */
		private void save(FileHandle file) {
			updateBounds();

			localToStageCoordinates(this.minxy.set(minX, minY));
			localToStageCoordinates(this.maxxy.set(maxX, maxY));

			int minWidth = Math.round(maxxy.x - minxy.x);
			int minHeight = Math.round(maxxy.y - minxy.y);

			Gdx.app.log(PAINTING_TAG, "minX: " + minX + ", minY: " + minY
					+ ", minWidth: " + minWidth + ", minHeight: " + minHeight);

			initResources();

			this.frameBuffer.begin();

			drawMesh();

			Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
			final Pixmap pixmap = new Pixmap(minWidth, minHeight,
					Format.RGBA8888);
			ByteBuffer pixels = pixmap.getPixels();
			Gdx.gl.glReadPixels(Math.round(minxy.x), Math.round(minxy.y),
					minWidth, minHeight, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
					pixels);

			this.frameBuffer.end();

			PixmapIO.writePNG(file, pixmap);
			pixmap.dispose();
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
				this.showingTexRegion.setTexture(this.frameBuffer
						.getColorBufferTexture());
				Actor parent = getParent();
				float x = parent.getX(), y = parent.getY(), w = parent
						.getWidth() * parent.getScaleX(), h = parent
						.getHeight() * parent.getScaleY();

				Gdx.app.log(PAINTING_TAG, "Texture Regions: " + x + ", " + y
						+ ", " + w + ", " + h);

				this.showingTexRegion.setRegion(Math.round(x), Math.round(y),
						Math.round(w), Math.round(h));
				this.showingTexRegion.flip(false, true);
			}
		}

		private void updateBounds() {
			float width = getWidth(), height = getHeight();
			float x = 0, y = 0;
			if (prevMinX > minX) {
				minX -= radius;
				if (minX < x) {
					minX = x;
				}
				prevMinX = minX;
			}

			if (prevMaxX < maxX) {
				maxX += radius;
				if (maxX > width) {
					maxX = width;
				}
				prevMaxX = maxX;
			}

			if (prevMinY > minY) {
				minY -= radius;
				if (minY < y) {
					minY = y;
				}
				prevMinY = minY;
			}

			if (prevMaxY < maxX) {
				maxY += radius;
				if (maxY > height) {
					maxY = height;
				}
				prevMaxY = maxY;
			}
		}

		private void reset() {
			this.vertexIndex = 0;
			this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);
		}

		private void createMesh() {
			this.mesh = new Mesh(true, MAX_LINES * 2, 0, new VertexAttribute(
					Usage.Position, 2, "a_position"), new VertexAttribute(
					Usage.Color, 4, "u_color"));
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
			Actor parent = getParent();
			batch.draw(this.showingTexRegion, 0, 0, 0, 0,
					this.showingTexRegion.getRegionWidth(),
					this.showingTexRegion.getRegionHeight(),
					1 / parent.getScaleX(), 1 / parent.getScaleY(), 0);
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

		private void drawMesh() {
			this.meshShader.begin();

			this.meshShader.setUniformf("u_color", this.r, this.g, this.b,
					this.a);
			this.meshShader
					.setUniformMatrix("u_worldView", this.combinedMatrix);
			this.mesh.render(this.meshShader, GL20.GL_TRIANGLE_STRIP);

			this.meshShader.end();
		}

		public void dispose() {
			this.mesh.dispose();
			this.frameBuffer.dispose();
			this.frameBuffer = null;
			this.meshShader.dispose();
		}

		private void input(float x, float y) {
			if (this.vertexIndex == this.lineVertices.length)
				return;
			this.unprojectedVertex.set(x, y);
			x = this.unprojectedVertex.x;
			y = this.unprojectedVertex.y;

			if (this.vertexIndex == 0
					|| unprojectedVertex.dst(this.lastX, this.lastY) > 8) {

				this.unprojectedVertex.set(x, y).sub(this.lastX, this.lastY)
						.nor();
				this.unprojectedVertex.set(-this.unprojectedVertex.y,
						this.unprojectedVertex.x);
				this.unprojectedVertex.scl(this.radius);

				float maxNorX = x + this.unprojectedVertex.x;
				this.lineVertices[this.vertexIndex++] = maxNorX;

				float maxNorY = y + this.unprojectedVertex.y;
				this.lineVertices[this.vertexIndex++] = maxNorY;

				float minNorX = x - unprojectedVertex.x;
				this.lineVertices[this.vertexIndex++] = minNorX;

				float minNorY = y - unprojectedVertex.y;
				this.lineVertices[this.vertexIndex++] = minNorY;

				this.minX = Math.min(this.minX, x);
				this.maxX = Math.max(this.maxX, x);
				this.minY = Math.min(this.minY, y);
				this.maxY = Math.max(this.maxY, y);

				this.mesh.setVertices(this.lineVertices, 0, this.vertexIndex);

				this.lastX = x;
				this.lastY = y;
			}
		}

		private void initBounds() {
			this.prevMinX = this.prevMinY = this.minX = this.minY = Float.MAX_VALUE;
			this.prevMaxX = this.prevMaxY = this.maxX = this.maxY = Float.MIN_VALUE;
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
	}

	public void setRadius(float radius) {
		this.mesh.setRadius(radius);
	}

	public void setMeshColor(Color color) {
		this.mesh.setColor(color);
	}
}
