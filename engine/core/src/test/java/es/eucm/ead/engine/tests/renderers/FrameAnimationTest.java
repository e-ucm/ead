/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.engine.renderers.frameanimation.FrameAnimationEngineObject;
import es.eucm.ead.schema.components.RandomSequence;
import es.eucm.ead.schema.components.Sequence;
import es.eucm.ead.schema.effects.ChangeRenderer;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.renderers.Rectangle;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.frameanimation.Frame;
import es.eucm.ead.schema.renderers.frameanimation.FrameAnimation;
import es.eucm.ead.schema.components.LinearSequence;
import es.eucm.ead.schema.renderers.frameanimation.Timed;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * This class contains 5 different tests for FrameAnimations:
 * 
 * There are three simple ones that test 3 sequences of rectangles: linear with
 * loop, linear without loop, random
 * 
 * There is another one that tests a complex animation composed by a sequence of
 * other frame animations and simple frames with variable durations. This test
 * evaluates that the animation updates correctly when big deltas occur (i.e.
 * frames are just skipped as needed) and also that a frame animation works well
 * in loop when inside a frame that lasts less, equals or more than the total
 * duration of its subframes.
 * 
 * The final test checks that frame animations behave well when the
 * delegateRenderer of the frame is empty
 * 
 * Created by Javier Torrente on 2/02/14 to test
 * {@link es.eucm.ead.schema.renderers.frameanimation.FrameAnimation}.
 * */

public class FrameAnimationTest {
	private MockGame mockGame;

	private GameLoop gameLoop;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		gameLoop = mockGame.getGameLoop();
	}

	@Test
	public void testSimpleLinearNoLoopAnimation() {
		testSimpleLinearAnimation(false);
	}

	@Test
	public void testSimpleLinearLoopAnimation() {
		testSimpleLinearAnimation(true);
	}

	@Test
	public void testSimpleRandomAnimation() {
		int nSquares = 100;

		// Load the scene
		mockGame.act();

		// Build two identical sequences of 100 red squares of increasing size
		// and duration
		List<Timed> frames = new ArrayList<Timed>();
		for (int i = 0; i < nSquares; i++) {
			Frame frame = buildFrame(buildSquare(i + 10, Color.RED), 1);
			frames.add(frame);
		}
		Sequence sequence = buildNextFrame(NextFunction_Type.RANDOM, false);
		FrameAnimation frameAnimation1 = buildFrameAnimation(sequence, frames);
		FrameAnimation frameAnimation2 = buildFrameAnimation(sequence, frames);

		// Creates a scene element.
		SceneElement sceneElement = new SceneElement();
		sceneElement.setRenderer(frameAnimation1);
		// Adds sceneElement to the game and retrieves the reference to
		// SceneElementActor
		gameLoop.getSceneView().getCurrentScene().addActor(sceneElement);
		mockGame.act();

		SceneElementEngineObject sceneElementActor = ((SceneElementEngineObject) (gameLoop
				.getSceneElement(sceneElement)));
		FrameAnimationEngineObject frameAnimationRenderer = (FrameAnimationEngineObject) sceneElementActor
				.getRenderer();

		// Iterate through the 100 frames to record the sequence of frameSizes
		List<Float> sequence1 = new ArrayList<Float>();
		for (int i = 0; i < nSquares; i++) {
			sequence1.add(frameAnimationRenderer.getHeight());
			sceneElementActor.act(1.0F);
		}

		// Now add a change renderer effect to the scene element so the second
		// frame animation is set
		ChangeRenderer changeRenderer = new ChangeRenderer();
		changeRenderer.setSetInitialRenderer(false);
		changeRenderer.setNewRenderer(frameAnimation2);
		sceneElementActor.addEffect(changeRenderer);
		mockGame.act();

		// Iterate through the 100 frames again to compare if this second
		// sequence is different from the previous one
		boolean different = false;
		for (int i = 0; i < nSquares; i++) {
			if (!sequence1.get(i).equals(frameAnimationRenderer.getHeight())) {
				different = true;
				break;
			}
			sceneElementActor.act(1.0F);
		}

		assertTrue("Two sequences of random frames should be always different",
				different);
	}

	@Test
	public void testEmptyFrames() {
		int nSquares = 100;

		// Load the scene
		mockGame.act();

		// Build the animation: a sequence of 100 null frames
		List<Timed> frames = new ArrayList<Timed>();
		for (int i = 0; i < nSquares; i++) {
			frames.add(buildFrame(null, 1));
		}
		FrameAnimation frameAnimation = buildFrameAnimation(
				buildNextFrame(NextFunction_Type.LINEAR, false), frames);
		// Creates a scene element.
		SceneElement sceneElement = new SceneElement();
		sceneElement.setRenderer(frameAnimation);
		// Adds sceneElement to the game and retrieves the reference to
		// SceneElementActor
		gameLoop.getSceneView().getCurrentScene().addActor(sceneElement);
		mockGame.act();

		SceneElementEngineObject sceneElementActor = ((SceneElementEngineObject) (gameLoop
				.getSceneElement(sceneElement)));
		FrameAnimationEngineObject frameAnimationRenderer = (FrameAnimationEngineObject) sceneElementActor
				.getRenderer();

		// Check animation goes on with no problem
		for (int i = 0; i < nSquares; i++) {
			assertTrue(frameAnimationRenderer.getHeight() == 0
					&& frameAnimationRenderer.getWidth() == 0);
			sceneElementActor.act(1);
		}

	}

	@Test
	public void testComplexAnimationAndBigDeltas() {
		// Load the scene
		mockGame.act();
		int nFrames = 50;
		int nFramesPerSubanimation = 30;
		float durationSubframe = 2.0F;
		float durationFrame = durationSubframe * nFramesPerSubanimation;

		List<Timed> frames = new ArrayList<Timed>();
		for (int i = 0; i < nFrames; i++) {
			Frame frame = null;
			if (i % 2 == 0) {
				List<Timed> subFrames = new ArrayList<Timed>();
				for (int j = 0; j < nFramesPerSubanimation; j++) {
					subFrames.add(buildFrame(
							buildRectangle(i * nFramesPerSubanimation,
									2 * j + 1, Color.BLACK), durationSubframe));
				}
				Sequence nextSubFrame = buildNextFrame(
						NextFunction_Type.LINEAR, true);
				FrameAnimation subAnimation = buildFrameAnimation(nextSubFrame,
						subFrames);
				frame = buildFrame(subAnimation, durationFrame - (4 - i)
						* durationSubframe);
			} else {
				frame = buildFrame(
						buildRectangle(i * nFramesPerSubanimation, 20,
								Color.GREEN), durationFrame);
			}
			frames.add(frame);
		}
		Sequence sequence = buildNextFrame(NextFunction_Type.LINEAR, false);
		FrameAnimation frameAnimation = buildFrameAnimation(sequence, frames);

		// Creates a scene element.
		SceneElement sceneElement = new SceneElement();
		sceneElement.setRenderer(frameAnimation);
		// Adds sceneElement to the game and retrieves the reference to
		// SceneElementActor
		gameLoop.getSceneView().getCurrentScene().addActor(sceneElement);
		mockGame.act();

		SceneElementEngineObject sceneElementActor = ((SceneElementEngineObject) (gameLoop
				.getSceneElement(sceneElement)));
		FrameAnimationEngineObject frameAnimationRenderer = (FrameAnimationEngineObject) sceneElementActor
				.getRenderer();

		// Test the first 10 frames. The total sequence is:
		// [NS-4] [F] [NS-2] [F] [NS] [F] [NS+2] [F] [NS+4] [F]
		// where NS = number of subframes in a subframeanimation, and F is a
		// single frame
		float w, h;
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < nFramesPerSubanimation - 4 + j * 2; i++) {
				int nFrame = (i % nFramesPerSubanimation);

				h = frameAnimationRenderer.getHeight();
				w = frameAnimationRenderer.getWidth();
				// Height is odd in subanimations. Width should be 0 for being
				// the first subanimation
				assertTrue(h == 2 * nFrame + 1
						&& w == nFramesPerSubanimation * j * 2);
				sceneElementActor.act(durationSubframe);
			}

			// At this point the total duration of the subanimation has been
			// consumed, so the next frame should be a single one
			h = frameAnimationRenderer.getHeight();
			w = frameAnimationRenderer.getWidth();
			// The next frame should be a simple one. Those have even height.
			assertTrue(w == nFramesPerSubanimation * (2 * j + 1) && h == 20);
			sceneElementActor.act(durationFrame);
		}

		// Now, test what happens when a big delay occurs. Try to skip three
		// frames at once
		// delta = (durationFrame + 6*durationSubframe) + durationFrame +
		// (durationFrame + 8*durationSubframe)
		float bigDelta = 3 * durationFrame + 14 * durationSubframe;
		sceneElementActor.act(bigDelta);
		// The next one should be a simple frame (i==13)
		h = frameAnimationRenderer.getHeight();
		w = frameAnimationRenderer.getWidth();
		assertTrue(w == nFramesPerSubanimation * 13 && h == 20);

		// Another bigDelta that jumps into a subanimation
		// Delta=durationFrame + (durationFrame+10*durationSubframe)
		// +durationFrame + 3.5*durationSubframe
		bigDelta = 3 * durationFrame + 13.5F * durationSubframe;
		sceneElementActor.act(bigDelta);
		// This should be the 4th subframe in [NS+12] (i==16)
		h = frameAnimationRenderer.getHeight();
		w = frameAnimationRenderer.getWidth();
		assertTrue(w == nFramesPerSubanimation * 16 && h == 2 * 3 + 1);

		// THe final one: go to the very end of the frame animation and check
		// the last frame stays still
		bigDelta = nFrames * durationFrame * 1000;
		sceneElementActor.act(bigDelta);
		h = frameAnimationRenderer.getHeight();
		w = frameAnimationRenderer.getWidth();
		assertTrue(w == nFramesPerSubanimation * (nFrames - 1) && h == 20);
	}

	private void testSimpleLinearAnimation(boolean loop) {
		int nSquares = 100;

		// Load the scene
		mockGame.act();

		// Build the animation: a sequence of 100 red squares of increasing size
		// and duration
		List<Timed> frames = new ArrayList<Timed>();
		for (int i = 0; i < nSquares; i++) {
			frames.add(buildFrame(buildSquare(i + 10, Color.RED), i + 1));
		}
		FrameAnimation frameAnimation = buildFrameAnimation(
				buildNextFrame(NextFunction_Type.LINEAR, loop), frames);
		// Creates a scene element.
		SceneElement sceneElement = new SceneElement();
		sceneElement.setRenderer(frameAnimation);
		// Adds sceneElement to the game and retrieves the reference to
		// SceneElementActor
		gameLoop.getSceneView().getCurrentScene().addActor(sceneElement);
		mockGame.act();

		SceneElementEngineObject sceneElementActor = ((SceneElementEngineObject) (gameLoop
				.getSceneElement(sceneElement)));
		FrameAnimationEngineObject frameAnimationRenderer = (FrameAnimationEngineObject) sceneElementActor
				.getRenderer();

		// Check frames change with appropriate timing
		for (int i = 0; i < nSquares; i++) {
			assertTrue("The size of the square must be: " + (i + 10)
					+ "; It is :" + frameAnimationRenderer.getHeight(),
					frameAnimationRenderer.getHeight() == i + 10);
			sceneElementActor.act((i + 1) / 2.0F);
			assertTrue("The size of the square must still be: " + (i + 10),
					frameAnimationRenderer.getHeight() == i + 10);
			sceneElementActor.act((i + 1) / 2.0F);
		}

		for (int i = 0; i < nSquares; i++) {
			if (loop) {
				// Check count restarts
				assertTrue("The size of the square must be: " + (i + 10)
						+ "; It is :" + frameAnimationRenderer.getHeight(),
						frameAnimationRenderer.getHeight() == i + 10);
			} else {
				// Check last frame stays still - forever and ever
				assertTrue("The size of the last square must be" + nSquares + 9
						+ "; It is :" + frameAnimationRenderer.getHeight(),
						frameAnimationRenderer.getHeight() == nSquares + 9);
			}

			sceneElementActor.act(i + 1);
		}
	}

	/**
	 * Creates a square with the given size and Color using the Rectangle
	 * renderer
	 * 
	 * @param size
	 *            The size (bottom=0, left=0, top=size, right=size)
	 * @param color
	 *            The color
	 *            {@link es.eucm.ead.engine.tests.renderers.FrameAnimationTest.Color}
	 * @return The rectangle
	 */
	private Rectangle buildSquare(int size, Color color) {

		Rectangle square = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setBottom(0);
		bounds.setTop(size);
		bounds.setLeft(0);
		bounds.setRight(size);
		square.setBounds(bounds);
		square.setPaint(buildPaint(color));

		return square;
	}

	/**
	 * Builds a rectangle with the given width, height and color
	 */
	private Rectangle buildRectangle(int w, int h, Color color) {

		Rectangle rectangle = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setBottom(0);
		bounds.setTop(h);
		bounds.setLeft(0);
		bounds.setRight(w);
		rectangle.setBounds(bounds);
		rectangle.setPaint(buildPaint(color));

		return rectangle;
	}

	/**
	 * Builds a frame with the given params.
	 */
	private Frame buildFrame(Renderer delegate, float duration) {
		Frame frame = new Frame();
		frame.setDelegateRenderer(delegate);
		frame.setDuration(duration);
		return frame;
	}

	/**
	 * Builds an animation with the given timedFrames and just a null nextFrame
	 * function.
	 * 
	 * @param timedFrames
	 *            The frames to be added to the animation
	 * @return The frame animation
	 */
	private FrameAnimation buildFrameAnimation(Timed... timedFrames) {
		return buildFrameAnimation(null, timedFrames);
	}

	/**
	 * Builds a frame animation with the given frames and nextFrame function
	 */
	private FrameAnimation buildFrameAnimation(Sequence function,
			Timed... timedFrames) {

		List<Timed> frameList = new ArrayList<Timed>();
		for (Timed f : timedFrames) {
			frameList.add(f);
		}

		return buildFrameAnimation(function, frameList);
	}

	private FrameAnimation buildFrameAnimation(Sequence function,
			List<Timed> frameList) {
		FrameAnimation animation = new FrameAnimation();
		animation.setFrames(frameList);
		animation.setSequence(function);
		return animation;
	}

	/**
	 * Builds a function for frame sequencing of the given type.
	 * 
	 * @param type
	 *            The type of the function(random or linear)
	 * @param loop
	 *            If true, the animation will loop, if false it will end with
	 *            the last frame. Only used if type==LinearSequence
	 * @return
	 */
	private Sequence buildNextFrame(NextFunction_Type type, boolean loop) {
		Sequence function = null;
		if (type == NextFunction_Type.LINEAR) {
			function = new LinearSequence();
			((LinearSequence) function).setLoop(loop);
		} else {
			function = new RandomSequence();
		}
		return function;
	}

	/**
	 * Builds a basic paint for testing, using the given color as primary color
	 * for the content. The border is always set to black
	 */
	private String buildPaint(Color color) {
		switch (color) {
		case RED:
			return "FF0000;000000";
		case GREEN:
			return "00FF00;000000";
		case BLUE:
			return "0000FF;000000";
		case WHITE:
			return "FFFFFF;000000";
		case BLACK:
		default:
			return "000000;000000";
		}
	}

	private enum Color {
		RED, GREEN, BLUE, WHITE, BLACK
	}

	private enum NextFunction_Type {
		RANDOM, LINEAR
	}
}
