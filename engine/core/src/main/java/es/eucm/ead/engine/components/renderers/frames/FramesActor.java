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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.components.renderers.frames.sequences.LinearSequence;
import es.eucm.ead.engine.components.renderers.frames.sequences.Sequence;
import es.eucm.ead.engine.entities.actors.EntityGroup;

public class FramesActor extends EntityGroup {

	private static final Sequence DEFAULT_SEQUENCE = new LinearSequence();

	private float elapsedTime = 0;

	private FrameData currentFrame;

	private Array<FrameData> framesData = new Array<FrameData>();

	private Sequence function = DEFAULT_SEQUENCE;

	public void setSequence(Sequence function) {
		this.function = function;
	}

	/**
	 * Adds a frame with the given renderer and duration. If duration <= 0,
	 * frames is ignored.
	 */
	public void addFrame(RendererComponent renderer, float duration) {
		if (duration > 0) {
			framesData.add(new FrameData(renderer, duration, framesData.size));
			if (framesData.size == 1) {
				setCurrentFrame(0);
			}
		}
	}

	protected void setCurrentFrame(int index) {
		currentFrame = framesData.get(index);
		clearChildren();
		addActor(currentFrame.renderer.getEntityGroup());
	}

	public int getCurrentFrameIndex() {
		return currentFrame == null ? -1 : currentFrame.index;
	}

	@Override
	public void restart() {
		setCurrentFrame(function.getFirst(framesData.size));
		if (currentFrame != null) {
			currentFrame.renderer.restart();
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		elapsedTime += delta;
		float surplus;
		if (currentFrame != null) {
			do {
				surplus = elapsedTime - currentFrame.duration;
				if (surplus >= 0) {
					elapsedTime = surplus;
					setCurrentFrame(function.getNextIndex(currentFrame.index,
							framesData.size));
				}
			} while (surplus > 0 && currentFrame != null);
		}
	}

	private static class FrameData {
		int index;
		float duration;
		RendererComponent renderer;

		public FrameData(RendererComponent renderer, float duration, int index) {
			this.renderer = renderer;
			this.duration = duration;
			this.index = index;
		}
	}

}
