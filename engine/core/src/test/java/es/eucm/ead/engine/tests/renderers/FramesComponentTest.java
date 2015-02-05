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
import es.eucm.ead.engine.components.renderers.frames.sequences.LastFrameSequence;
import es.eucm.ead.engine.components.renderers.frames.sequences.LinearSequence;
import es.eucm.ead.engine.components.renderers.frames.sequences.YoyoSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FramesComponentTest {

	@Test
	public void testLastFrameSquence() {
		FramesComponent frames = new FramesComponent();
		for (int i = 0; i < 10; i++) {
			frames.addFrame(new MockRendererComponent(), 1);
		}
		frames.setSequence(new LastFrameSequence());

		frames.act(0.5f);
		for (int i = 0; i < 10; i++) {
			assertEquals(i, frames.getCurrentFrameIndex());
			System.out.print(i + ", ");
			frames.act(1);
		}
		for (int i = 0; i < 10; i++) {
			assertEquals(9, frames.getCurrentFrameIndex());
			System.out.print(i + ", ");
			frames.act(1);
		}
	}

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
	public void testNestedFrames() {
		FramesComponent level2A = new FramesComponent();
		level2A.setSequence(new LinearSequence());
		float leafDuration = 0.1F;
		int level2AFrames = 3;
		for (int i = 1; i <= level2AFrames; i++) {
			level2A.addFrame(new MockRendererComponent(), leafDuration);
		}

		FramesComponent level2B = new FramesComponent();
		level2B.setSequence(new LinearSequence());
		int level2BFrames = 7;
		for (int i = 1; i <= level2BFrames; i++) {
			level2B.addFrame(new MockRendererComponent(), leafDuration);
		}

		FramesComponent level2C = new FramesComponent();
		level2C.setSequence(new LinearSequence());
		int level2CFrames = 7;
		for (int i = 1; i <= level2CFrames; i++) {
			level2C.addFrame(new MockRendererComponent(), leafDuration);
		}

		FramesComponent level1 = new FramesComponent();
		level1.setSequence(new LinearSequence());
		level1.addFrame(level2A, leafDuration * level2AFrames);
		level1.addFrame(level2B, leafDuration * level2BFrames);
		level1.addFrame(level2C, leafDuration * level2CFrames);

		float actStep = leafDuration / 2.0F;

		nestedFramesStep(actStep, level1, 0, level2A, 0, level2B, 0, level2C, 0);
		for (int i = 1; i < level2AFrames * 2 - 1; i++) {
			nestedFramesStep(actStep, level1, 0, level2A, (i + 1) / 2, level2B,
					0, level2C, 0);
		}
		for (int i = 0; i < level2BFrames * 2; i++) {
			nestedFramesStep(actStep, level1, 1, level2A, 0, level2B, i / 2,
					level2C, 0);
		}
		for (int i = 0; i < level2CFrames * 2; i++) {
			nestedFramesStep(actStep, level1, 2, level2A, 0, level2B, 0,
					level2C, i / 2);
		}
	}

	private void nestedFramesStep(float delta, FramesComponent level1,
			int expectedLevel1Index, FramesComponent level2A,
			int expectedLevel2AIndex, FramesComponent level2B,
			int expectedLevel2BIndex, FramesComponent level2C,
			int expectedLevel2CIndex) {
		level1.act(delta);
		assertEquals("First level FramesComponent not updated correctly",
				expectedLevel1Index, level1.getCurrentFrameIndex());
		assertEquals("Second level (A) FramesComponent not updated correctly",
				expectedLevel2AIndex, level2A.getCurrentFrameIndex());
		assertEquals("Second level (B) FramesComponent not updated correctly",
				expectedLevel2BIndex, level2B.getCurrentFrameIndex());
		assertEquals("Second level (C) FramesComponent not updated correctly",
				expectedLevel2CIndex, level2C.getCurrentFrameIndex());
	}

	@Test
	public void testZeroFramesDuration() {
		FramesComponent frames = new FramesComponent();
		for (int i = 0; i < 10; i++) {
			frames.addFrame(new MockRendererComponent(), 0);
		}
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

		@Override
		public void reset() {

		}
	}

}
