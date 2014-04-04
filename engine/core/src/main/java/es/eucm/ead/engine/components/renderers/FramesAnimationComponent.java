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
package es.eucm.ead.engine.components.renderers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.sequences.LinearSequenceEngineObject;
import es.eucm.ead.engine.sequences.SequenceEngineObject;

/**
 * Created by Javier Torrente on 2/02/14.
 */
public class FramesAnimationComponent extends RendererComponent {

	private Array<FrameEngineObject> frames;
	private int currentFrame;
	private SequenceEngineObject function;

	public FramesAnimationComponent() {
		frames = new Array<FrameEngineObject>();
		function = new LinearSequenceEngineObject();
	}

	@Override
	public void draw(Batch batch) {
		// Just delegate
		getCurrentFrame().draw(batch);
	}

	@Override
	public float getHeight() {
		return getCurrentFrame().getHeight();
	}

	@Override
	public boolean hit(float x, float y) {
		return getCurrentFrame().hit(x, y);
	}

	@Override
	public float getWidth() {
		return getCurrentFrame().getWidth();
	}

	public void setSequence(SequenceEngineObject sequence) {
		function = sequence;
	}

	public void addFrame(RendererComponent renderer, float duration) {
		frames.add(new FrameEngineObject(renderer, duration));
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
		while (delta > 0) {
			getCurrentFrame().act(delta);
			delta = getCurrentFrame().surplusTime();
			if (delta >= 0) {
				getCurrentFrame().reset();
				setCurrentFrame(function
						.getNextIndex(currentFrame, frames.size));
			}
		}
	}

	private void setCurrentFrame(int newFrameIndex) {
		if (newFrameIndex >= 0 && newFrameIndex < frames.size) {
			currentFrame = newFrameIndex;
		}
	}

	private FrameEngineObject getCurrentFrame() {
		return frames.get(currentFrame);
	}

	/**
	 * Created by Javier Torrente on 2/02/14.
	 */
	private static class FrameEngineObject {

		private RendererComponent renderer;

		private float duration;

		private float elapsedTime;

		private FrameEngineObject(RendererComponent renderer, float duration) {
			this.renderer = renderer;
			this.duration = duration;
		}

		public void draw(Batch batch) {
			// Just delegate to delegateRenderer
			if (renderer != null)
				renderer.draw(batch);
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
