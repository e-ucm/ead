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
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SpineAnimationComponent extends CollidableRendererComponent {

	private SkeletonRenderer skeletonRenderer = new SkeletonRenderer();

	private AnimationState state;

	private Skeleton skeleton;

	public void setSkeleton(SkeletonData skeletonData) {
		this.skeleton = new Skeleton(skeletonData);
		AnimationStateData stateData = new AnimationStateData(skeletonData);
		state = new AnimationState(stateData);
	}

	public void setState(String stateName) {
		if (stateName != null) {
			state.setAnimation(0, stateName, true);
		}
	}

	@Override
	public void act(float delta) {
		state.update(delta);
	}

	@Override
	public void draw(Batch batch) {
		state.apply(skeleton);
		skeleton.updateWorldTransform();
		/*
		 * batch has to be casted to PolygonSpriteBatch as SkeletonRenderer has
		 * two different draw methods: draw(Batch, Skeleton) and
		 * draw(PolygonSpriteBatch, Skeleton).
		 */
		skeletonRenderer.draw((PolygonSpriteBatch) batch, skeleton);
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}
}
