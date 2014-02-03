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
import es.eucm.ead.engine.renderers.AbstractRenderer;
import es.eucm.ead.schema.renderers.frameanimation.FrameAnimation;
import es.eucm.ead.schema.renderers.frameanimation.Linear;
import es.eucm.ead.schema.renderers.frameanimation.Timed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier Torrente on 2/02/14.
 */
public class FrameAnimationRenderer extends TimedRenderer<FrameAnimation> {

	private List<TimedRenderer> frames;
	private int currentFrame;
	private NextFrameFunction function;

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
		function = gameLoop.getFactory().getEngineObject(
				schemaObject.getNextframe() != null ? schemaObject
						.getNextframe() : new Linear());
		frames = new ArrayList<TimedRenderer>();
		for (Timed f : schemaObject.getFrames()) {
			frames.add((TimedRenderer) gameLoop.getFactory().getEngineObject(f));
		}
		currentFrame = function.getInitialFrameIndex(frames.size());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		getCurrentFrame().act(delta);
		if (getCurrentFrame().isDone()) {
			getCurrentFrame().reset();
			currentFrame = function.getNextFrameIndex(currentFrame,
					frames.size());
		}
	}

	private TimedRenderer getCurrentFrame() {
		if (currentFrame >= 0 && currentFrame < frames.size()) {
			return frames.get(currentFrame);
		}
		return null;
	}

	@Override
	public void dispose() {
		for (TimedRenderer frame : frames) {
			frame.dispose();
		}
		super.dispose();
	}

}
