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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import es.eucm.ead.engine.entities.actors.EntityGroup;

public class SpineActor extends EntityGroup {

	private SkeletonRenderer skeletonRenderer = new SkeletonRenderer();

	private AnimationState state;

	private Skeleton skeleton;

	private SkeletonBounds bounds;

	private Actor boundsActor = new Actor();

	public SpineActor() {
		setVisible(false);
		addActor(boundsActor);
	}

	public void setSkeleton(SkeletonData skeletonData) {
		this.skeleton = new Skeleton(skeletonData);
		AnimationStateData stateData = new AnimationStateData(skeletonData);
		state = new AnimationState(stateData);
		setVisible(true);
		bounds = new SkeletonBounds();
	}

	public void setState(String stateName) {
		if (stateName != null) {
			state.setAnimation(0, stateName, true);
		}
	}

	@Override
	public void act(float delta) {
		if (state != null) {
			state.update(delta);
			bounds.update(skeleton, true);
			boundsActor.setBounds(bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		state.apply(skeleton);
		skeleton.updateWorldTransform();
		/*
		 * batch has to be casted to PolygonSpriteBatch as SkeletonRenderer has
		 * two different draw methods: draw(Batch, Skeleton) and
		 * draw(PolygonSpriteBatch, Skeleton).
		 */
		skeletonRenderer.draw((PolygonSpriteBatch) batch, skeleton);
		bounds.update(skeleton, true);
	}
}
