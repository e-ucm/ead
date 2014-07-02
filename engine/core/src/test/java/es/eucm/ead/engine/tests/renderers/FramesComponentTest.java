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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.components.renderers.frames.FramesComponent;
import es.eucm.ead.engine.components.renderers.frames.sequences.LinearSequence;
import es.eucm.ead.engine.components.renderers.frames.sequences.YoyoSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FramesComponentTest {

	@Test
	public void testYoyoSquence() {
		FramesComponent frames = new FramesComponent();
		for (int i = 0; i < 10; i++) {
			frames.addFrame(new MockRendererComponent(), 1);
		}
		frames.setSequence(new YoyoSequence());

		frames.act(0.5f);
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 10; i++) {
				assertEquals(i, frames.getCurrentFrameIndex());
				System.out.print(i + ", ");
				frames.act(1);
			}
			for (int i = 8; i > 0; i--) {
				assertEquals(i, frames.getCurrentFrameIndex());
				System.out.print(i + ", ");
				frames.act(1);
			}
		}
	}

	@Test
	public void testFrames() {
		FramesComponent frames = new FramesComponent();
		for (int i = 0; i < 10; i++) {
			frames.addFrame(new MockRendererComponent(), 1);
		}
		frames.setSequence(new LinearSequence());

		frames.act(0.5f);
		assertEquals(frames.getCurrentFrameIndex(), 0);
		frames.act(0.5f);
		assertEquals(frames.getCurrentFrameIndex(), 1);
		frames.act(2.0f);
		assertEquals(frames.getCurrentFrameIndex(), 3);
		frames.act(0.5f);
		assertEquals(frames.getCurrentFrameIndex(), 3);
		frames.act(1.5f);
		assertEquals(frames.getCurrentFrameIndex(), 5);
	}

	@Test
	public void testZeroFramesDuration() {
		FramesComponent frames = new FramesComponent();
		for (int i = 0; i < 10; i++) {
			frames.addFrame(new MockRendererComponent(), 0);
		}
		frames.setSequence(new LinearSequence());
		frames.act(10);
	}

	public class MockRendererComponent extends RendererComponent {

		@Override
		public void draw(Batch batch) {

		}

		@Override
		public float getWidth() {
			return 0;
		}

		@Override
		public float getHeight() {
			return 0;
		}

		@Override
		public Array<Polygon> getCollider() {
			return null;
		}
	}

}
