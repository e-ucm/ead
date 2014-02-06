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
package es.eucm.ead.engine.renderers.frameanimation;

import com.badlogic.gdx.graphics.g2d.Batch;
import es.eucm.ead.engine.components.SequenceEngineObject;
import es.eucm.ead.engine.renderers.RendererEngineObject;
import es.eucm.ead.schema.components.LinearSequence;
import es.eucm.ead.schema.renderers.frameanimation.FrameAnimation;
import es.eucm.ead.schema.renderers.frameanimation.Timed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier Torrente on 2/02/14.
 */
public class FrameAnimationEngineObject extends
		RendererEngineObject<FrameAnimation> {

	private List<TimedEngineObject> frames;
	private int currentFrame;
	private SequenceEngineObject function;

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
	public float getWidth() {
		return getCurrentFrame().getWidth();
	}

	@Override
	public void initialize(FrameAnimation schemaObject) {
		function = gameLoop.getAssets().getEngineObject(
				schemaObject.getSequence() != null ? schemaObject.getSequence()
						: new LinearSequence());
		frames = new ArrayList<TimedEngineObject>();

		for (Timed f : schemaObject.getFrames()) {
			frames.add((TimedEngineObject) gameLoop.getAssets()
					.getEngineObject(f));
		}
		setCurrentFrame(function.getFirst(frames.size()));
	}

	@Override
	public void act(float delta) {
		// Propagate to superclass
		super.act(delta);

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
				setCurrentFrame(function.getNextIndex(currentFrame,
						frames.size()));
			}
		}
	}

	private void setCurrentFrame(int newFrameIndex) {
		if (newFrameIndex >= 0 && newFrameIndex < frames.size()) {
			currentFrame = newFrameIndex;
		}
	}

	private TimedEngineObject getCurrentFrame() {
		return frames.get(currentFrame);
	}

	@Override
	public void dispose() {
		for (TimedEngineObject frame : frames) {
			frame.dispose();
		}
		super.dispose();
	}

}
