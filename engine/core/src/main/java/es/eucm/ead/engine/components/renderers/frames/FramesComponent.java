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
package es.eucm.ead.engine.components.renderers.frames;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.components.renderers.frames.sequences.Sequence;

/**
 * Created by Javier Torrente on 2/02/14.
 */
public class FramesComponent extends RendererComponent {

	private Array<Frame> frames;
	private int currentFrameIndex;
	private Frame currentFrame;
	private Sequence function;

	public FramesComponent() {
		frames = new Array<Frame>();
	}

	public int getCurrentFrameIndex() {
		return currentFrameIndex;
	}

	@Override
	public void draw(Batch batch) {
		if (currentFrame != null) {
			currentFrame.draw(batch);
		}
	}

	@Override
	public Array<Polygon> getCollider() {
		return currentFrame == null ? null : currentFrame.getRenderer()
				.getCollider();
	}

	@Override
	public boolean hit(float x, float y) {
		return currentFrame != null && currentFrame.hit(x, y);
	}

	@Override
	public float getWidth() {
		return currentFrame == null ? 0 : currentFrame.getWidth();
	}

	@Override
	public float getHeight() {
		return currentFrame == null ? 0 : currentFrame.getHeight();
	}

	public void setSequence(Sequence sequence) {
		function = sequence;
	}

	/**
	 * Adds a new frame with the given renderer. If {@code duration == 0}, the
	 * frames is ignored.
	 */
	public void addFrame(RendererComponent renderer, float duration) {
		if (duration > 0) {
			Frame frame = new Frame(renderer, duration);
			if (currentFrame == null) {
				currentFrame = frame;
			}
			frames.add(frame);
		}
	}

	public void act(float delta) {
		/*
		 * Iterate while "there is still delta to distribute": it calls
		 * act(delta) on the currentFrame, and retrieves the surplus delta, if
		 * any, since the currentFrame may not consume it all. This is
		 * especially relevant for the frameAnimation to work properly in case
		 * delta > duration of the current frame.
		 * 
		 * For example, lets suppose that the current frame has a duration of 2
		 * seconds. For any unknown reason, delta gets the unusually high value
		 * of 3 seconds. After invoking act(), the currentFrame has a surplus
		 * time of 1 second. In consequence, the current Frame should advance
		 * and also get invoked to its act() method
		 */
		while (delta > 0 && currentFrame != null) {
			currentFrame.act(delta);
			delta = currentFrame.surplusTime();
			if (delta >= 0) {
				currentFrame.reset();
				setCurrentFrameIndex(function.getNextIndex(currentFrameIndex,
						frames.size));
			}
		}
	}

	private void setCurrentFrameIndex(int newFrameIndex) {
		currentFrameIndex = newFrameIndex;
		currentFrame = frames.get(currentFrameIndex);
	}

	/**
	 * Created by Javier Torrente on 2/02/14.
	 */
	private static class Frame {

		private RendererComponent renderer;

		private float duration;

		private float elapsedTime;

		private Frame(RendererComponent renderer, float duration) {
			this.renderer = renderer;
			this.duration = duration;
		}

		public void draw(Batch batch) {
			// Just delegate to delegateRenderer
			if (renderer != null)
				renderer.draw(batch);
		}

		private RendererComponent getRenderer() {
			return renderer;
		}

		public float getHeight() {
			if (renderer != null)
				return renderer.getHeight();
			return 0;
		}

		public float getWidth() {
			if (renderer != null)
				return renderer.getWidth();
			return 0;
		}

		public boolean hit(float x, float y) {
			return renderer != null && renderer.hit(x, y);
		}

		public void act(float delta) {
			elapsedTime += delta;
		}

		/**
		 * Returns the difference between the elapsedTime and the duration of
		 * the frame. If this difference is greater or equals zero, this means
		 * the frame should not be rendererd by the frameRendererd animation,
		 * which should change to the next frame as defined by the Sequence
		 * function.
		 * 
		 * {@link #surplusTime()} should be invoked right after
		 * {@link #act(float)}, which is the method that updates elapsedTime.
		 * 
		 * @return A float value that represents the difference between the
		 *         elapsedTime of this frame (time the frame has been visible)
		 *         and the total duration of the frame, as defined in the
		 *         schemaObject.
		 */
		public float surplusTime() {
			return elapsedTime - duration;
		}

		/**
		 * Just sets elapsedTime to zero again. THis is to be invoked after
		 * isDone() returns true, so the next time the frame is to be rendered
		 * it is still alive
		 */
		public void reset() {
			elapsedTime = 0;
		}
	}

}
