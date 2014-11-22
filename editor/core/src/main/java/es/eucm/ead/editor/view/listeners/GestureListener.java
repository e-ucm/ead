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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class GestureListener extends InputListener {

	private float tapSquareSize;
	private long tapCountInterval;
	private float longPressSeconds;
	private long maxFlingDelay;

	private boolean inTapSquare;
	private int tapCount;
	private long lastTapTime;
	private float lastTapX, lastTapY;
	private int lastTapButton, lastTapPointer;
	boolean longPressFired;
	private boolean pinching;
	private boolean panning;

	private final VelocityTracker tracker = new VelocityTracker();
	private float tapSquareCenterX, tapSquareCenterY;
	private long gestureStartTime;
	Vector2 pointer1 = new Vector2();
	private final Vector2 pointer2 = new Vector2();
	private final Vector2 initialPointer1 = new Vector2();
	private final Vector2 initialPointer2 = new Vector2();

	private final Task longPressTask = new Task() {
		@Override
		public void run() {
			if (!longPressFired)
				longPressFired = longPress(pointer1.x, pointer1.y);
		}
	};

	/**
	 * Creates a new GestureDetector with default values: halfTapSquareSize=20,
	 * tapCountInterval=0.4f, longPressDuration=1.1f, maxFlingDelay=0.15f.
	 */
	public GestureListener() {
		this(20, 0.4f, 1.1f, 0.15f);
	}

	/**
	 * @param halfTapSquareSize
	 *            half width in pixels of the square around an initial touch
	 *            event, see {@link GestureListener#tap(float, float, int, int)}
	 *            .
	 * @param tapCountInterval
	 *            time in seconds that must pass for two touch down/up sequences
	 *            to be detected as consecutive taps.
	 * @param longPressDuration
	 *            time in seconds that must pass for the detector to fire a
	 *            {@link GestureListener#longPress(float, float)} event.
	 * @param maxFlingDelay
	 *            time in seconds the finger must have been dragged for a fling
	 *            event to be fired, see
	 *            {@link GestureListener#fling(float, float, int)}
	 **/
	public GestureListener(float halfTapSquareSize, float tapCountInterval,
			float longPressDuration, float maxFlingDelay) {
		this.tapSquareSize = halfTapSquareSize;
		this.tapCountInterval = (long) (tapCountInterval * 1000000000l);
		this.longPressSeconds = longPressDuration;
		this.maxFlingDelay = (long) (maxFlingDelay * 1000000000l);
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (pointer > 1)
			return false;

		if (pointer == 0) {
			pointer1.set(x, y);
			gestureStartTime = Gdx.input.getCurrentEventTime();
			tracker.start(x, y, gestureStartTime);
			if (Gdx.input.isTouched(1)) {
				// Start pinch.
				inTapSquare = false;
				pinching = true;
				initialPointer1.set(pointer1);
				initialPointer2.set(pointer2);
				longPressTask.cancel();
			} else {
				// Normal touch down.
				inTapSquare = true;
				pinching = false;
				longPressFired = false;
				tapSquareCenterX = x;
				tapSquareCenterY = y;
				if (!longPressTask.isScheduled())
					Timer.schedule(longPressTask, longPressSeconds);
			}
		} else {
			// Start pinch.
			pointer2.set(x, y);
			inTapSquare = false;
			pinching = true;
			initialPointer1.set(pointer1);
			initialPointer2.set(pointer2);
			longPressTask.cancel();
		}
		return true;
	}

	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		if (pointer > 1)
			return;
		if (longPressFired)
			return;

		if (pointer == 0)
			pointer1.set(x, y);
		else
			pointer2.set(x, y);

		// handle pinch zoom
		if (pinching) {
			pinch(initialPointer1, initialPointer2, pointer1, pointer2);
			zoom(initialPointer1.dst(initialPointer2), pointer1.dst(pointer2));
			return;
		}

		// update tracker
		tracker.update(x, y, Gdx.input.getCurrentEventTime());

		// check if we are still tapping.
		if (inTapSquare
				&& !isWithinTapSquare(x, y, tapSquareCenterX, tapSquareCenterY)) {
			longPressTask.cancel();
			inTapSquare = false;
		}

		// if we have left the tap square, we are panning
		if (!inTapSquare) {
			panning = true;
			pan(x, y, tracker.deltaX, tracker.deltaY);
		}
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		if (pointer > 1)
			return;

		// check if we are still tapping.
		if (inTapSquare
				&& !isWithinTapSquare(x, y, tapSquareCenterX, tapSquareCenterY))
			inTapSquare = false;

		boolean wasPanning = panning;
		panning = false;

		longPressTask.cancel();
		if (longPressFired)
			return;

		if (inTapSquare) {
			// handle taps
			if (lastTapButton != button || lastTapPointer != pointer
					|| TimeUtils.nanoTime() - lastTapTime > tapCountInterval
					|| !isWithinTapSquare(x, y, lastTapX, lastTapY))
				tapCount = 0;
			tapCount++;
			lastTapTime = TimeUtils.nanoTime();
			lastTapX = x;
			lastTapY = y;
			lastTapButton = button;
			lastTapPointer = pointer;
			gestureStartTime = 0;
			tap(x, y, tapCount, button);
			return;
		}

		if (pinching) {
			// handle pinch end
			pinching = false;
			panning = true;
			// we are in pan mode again, reset velocity tracker
			if (pointer == 0) {
				// first pointer has lifted off, set up panning to use the
				// second pointer...
				tracker.start(pointer2.x, pointer2.y,
						Gdx.input.getCurrentEventTime());
			} else {
				// second pointer has lifted off, set up panning to use the
				// first pointer...
				tracker.start(pointer1.x, pointer1.y,
						Gdx.input.getCurrentEventTime());
			}
			return;
		}

		// handle no longer panning
		if (wasPanning && !panning)
			panStop(x, y, pointer, button);

		// handle fling
		gestureStartTime = 0;
		// If touchUp is produced by cancelTouchFocus, fling is not triggered
		if (pointer == 0 && isOver(event.getTarget(), x, y)) {
			long time = Gdx.input.getCurrentEventTime();
			if (time - tracker.lastTime < maxFlingDelay) {
				tracker.update(x, y, time);
				fling(tracker.getVelocityX(), tracker.getVelocityY(), button);
			}
		}
	}

	public boolean isTouchCancelled(Actor actor, float x, float y) {
		return !isOver(actor, x, y);
	}

	public boolean isOver(Actor actor, float x, float y) {
		Actor hit = actor.hit(x, y, true);
		return !(hit == null || !hit.isDescendantOf(actor))
				|| inTapSquare(x, y);
	}

	public boolean inTapSquare(float x, float y) {
		return Math.abs(x - pointer1.x) < tapSquareSize
				&& Math.abs(y - pointer1.y) < tapSquareSize;
	}

	/**
	 * No further gesture events will be triggered for the current touch, if
	 * any.
	 */
	public void cancel() {
		longPressTask.cancel();
		longPressFired = true;
	}

	/**
	 * @return whether the user touched the screen long enough to trigger a long
	 *         press event.
	 */
	public boolean isLongPressed() {
		return isLongPressed(longPressSeconds);
	}

	/**
	 * @param duration
	 * @return whether the user touched the screen for as much or more than the
	 *         given duration.
	 */
	public boolean isLongPressed(float duration) {
		if (gestureStartTime == 0)
			return false;
		return TimeUtils.nanoTime() - gestureStartTime > (long) (duration * 1000000000l);
	}

	public boolean isPanning() {
		return panning;
	}

	public void reset() {
		gestureStartTime = 0;
		panning = false;
		inTapSquare = false;
	}

	private boolean isWithinTapSquare(float x, float y, float centerX,
			float centerY) {
		return Math.abs(x - centerX) < tapSquareSize
				&& Math.abs(y - centerY) < tapSquareSize;
	}

	/** The tap square will not longer be used for the current touch. */
	public void invalidateTapSquare() {
		inTapSquare = false;
	}

	public void setTapSquareSize(float halfTapSquareSize) {
		this.tapSquareSize = halfTapSquareSize;
	}

	/**
	 * @param tapCountInterval
	 *            time in seconds that must pass for two touch down/up sequences
	 *            to be detected as consecutive taps.
	 */
	public void setTapCountInterval(float tapCountInterval) {
		this.tapCountInterval = (long) (tapCountInterval * 1000000000l);
	}

	public void setLongPressSeconds(float longPressSeconds) {
		this.longPressSeconds = longPressSeconds;
	}

	public void setMaxFlingDelay(long maxFlingDelay) {
		this.maxFlingDelay = maxFlingDelay;
	}

	/**
	 * Called when a tap occurred. A tap happens if a touch went down on the
	 * screen and was lifted again without moving outside of the tap square. The
	 * tap square is a rectangular area around the initial touch position as
	 * specified on construction time.
	 * 
	 * @param count
	 *            the number of taps.
	 */
	public void tap(float x, float y, int count, int button) {

	}

	public boolean longPress(float x, float y) {
		return false;
	}

	/**
	 * Called when the user dragged a finger over the screen and lifted it.
	 * Reports the last known velocity of the finger in pixels per second.
	 * 
	 * @param velocityX
	 *            velocity on x in seconds
	 * @param velocityY
	 *            velocity on y in seconds
	 */
	public void fling(float velocityX, float velocityY, int button) {
	}

	/**
	 * Called when the user drags a finger over the screen.
	 * 
	 * @param deltaX
	 *            the difference in pixels to the last drag event on x.
	 * @param deltaY
	 *            the difference in pixels to the last drag event on y.
	 */
	public void pan(float x, float y, float deltaX, float deltaY) {
	}

	/** Called when no longer panning. */
	public void panStop(float x, float y, int pointer, int button) {
	}

	/**
	 * Called when the user performs a pinch zoom gesture. The original distance
	 * is the distance in pixels when the gesture started.
	 * 
	 * @param initialDistance
	 *            distance between fingers when the gesture started.
	 * @param distance
	 *            current distance between fingers.
	 */
	public void zoom(float initialDistance, float distance) {
	}

	/**
	 * Called when a user performs a pinch zoom gesture. Reports the initial
	 * positions of the two involved fingers and their current positions.
	 * 
	 * @param initialPointer1
	 * @param initialPointer2
	 * @param pointer1
	 * @param pointer2
	 */
	public void pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
	}

	static class VelocityTracker {
		int sampleSize = 10;
		float lastX, lastY;
		float deltaX, deltaY;
		long lastTime;
		int numSamples;
		float[] meanX = new float[sampleSize];
		float[] meanY = new float[sampleSize];
		long[] meanTime = new long[sampleSize];

		public void start(float x, float y, long timeStamp) {
			lastX = x;
			lastY = y;
			deltaX = 0;
			deltaY = 0;
			numSamples = 0;
			for (int i = 0; i < sampleSize; i++) {
				meanX[i] = 0;
				meanY[i] = 0;
				meanTime[i] = 0;
			}
			lastTime = timeStamp;
		}

		public void update(float x, float y, long timeStamp) {
			long currTime = timeStamp;
			deltaX = x - lastX;
			deltaY = y - lastY;
			lastX = x;
			lastY = y;
			long deltaTime = currTime - lastTime;
			lastTime = currTime;
			int index = numSamples % sampleSize;
			meanX[index] = deltaX;
			meanY[index] = deltaY;
			meanTime[index] = deltaTime;
			numSamples++;
		}

		public float getVelocityX() {
			float meanX = getAverage(this.meanX, numSamples);
			float meanTime = getAverage(this.meanTime, numSamples) / 1000000000.0f;
			if (meanTime == 0)
				return 0;
			return meanX / meanTime;
		}

		public float getVelocityY() {
			float meanY = getAverage(this.meanY, numSamples);
			float meanTime = getAverage(this.meanTime, numSamples) / 1000000000.0f;
			if (meanTime == 0)
				return 0;
			return meanY / meanTime;
		}

		private float getAverage(float[] values, int numSamples) {
			numSamples = Math.min(sampleSize, numSamples);
			float sum = 0;
			for (int i = 0; i < numSamples; i++) {
				sum += values[i];
			}
			return sum / numSamples;
		}

		private long getAverage(long[] values, int numSamples) {
			numSamples = Math.min(sampleSize, numSamples);
			long sum = 0;
			for (int i = 0; i < numSamples; i++) {
				sum += values[i];
			}
			if (numSamples == 0)
				return 0;
			return sum / numSamples;
		}
	}
}
