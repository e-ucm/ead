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
package es.eucm.ead.engine.tests.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.DefaultGameView;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockEntitiesLoader;
import es.eucm.ead.engine.processors.renderers.EmptyRendererProcessor;
import es.eucm.ead.engine.processors.renderers.ImageProcessor;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.*;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.Layer;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * This class tests that {@link EmptyRenderer} is able to "block" input events
 * 
 * Created by Javier Torrente on 5/06/14.
 */
public class EmptyRendererComponentTest {

	@Test
	public void test() {
		MockApplication.initStatics();
		MockEntitiesLoader mockEntitiesLoader = new MockEntitiesLoader();
		GameLoop gameLoop = mockEntitiesLoader.getGameLoop();
		mockEntitiesLoader.getComponentLoader().registerComponentProcessor(
				EmptyRenderer.class, new EmptyRendererProcessor(gameLoop));
		mockEntitiesLoader.getComponentLoader()
				.registerComponentProcessor(
						Image.class,
						new ImageProcessor(gameLoop, mockEntitiesLoader
								.getGameAssets()));

		DefaultGameView gameView = new DefaultGameView(gameLoop);

		// Add an entity with simple image and touch
		BufferedImage bufferedImage = new BufferedImage(500, 500,
				BufferedImage.OPAQUE);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setColor(Color.black);
		graphics2D.fillRect(0, 0, 500, 500);
		graphics2D.dispose();
		FileHandle tmpImageFile = FileHandle.tempFile("ead-test");
		try {
			ImageIO.write(bufferedImage, "jpg", tmpImageFile.file());
		} catch (IOException e) {
			Gdx.app.debug("Exception", "writing temp image", e);
			fail("An exception occurred writing temp image");
		}

		ModelEntity entity1 = new ModelEntity();
		es.eucm.ead.schema.renderers.Image image = new es.eucm.ead.schema.renderers.Image();
		image.setUri(tmpImageFile.path());
		entity1.getComponents().add(image);

		FileHandle parent = tmpImageFile.parent();
		mockEntitiesLoader.getGameAssets().setLoadingPath(parent.path());
		EngineEntity engineEntity1 = mockEntitiesLoader.toEngineEntity(entity1);
		mockEntitiesLoader.getGameAssets().finishLoading();
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, engineEntity1);

		// Now, add a simple entity with empty renderer that blocks out the
		// other entity
		EmptyRenderer emptyRenderer = new EmptyRenderer();
		Rectangle rectangle = new Rectangle();
		rectangle.setHeight(500);
		rectangle.setWidth(500);
		emptyRenderer.setShape(rectangle);
		ModelEntity modelEntity2 = new ModelEntity();
		modelEntity2.getComponents().add(emptyRenderer);

		EngineEntity engineEntity2 = mockEntitiesLoader
				.toEngineEntity(modelEntity2);
		Actor hit = gameView.getLayer(Layer.SCENE_CONTENT).getGroup()
				.hit(5, 5, true);
		assertSame(hit.getUserObject(), engineEntity1);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, engineEntity2);
		Actor hit2 = gameView.getLayer(Layer.SCENE_CONTENT).getGroup()
				.hit(5, 5, true);
		assertSame(hit2.getUserObject(), engineEntity2);
	}

}
