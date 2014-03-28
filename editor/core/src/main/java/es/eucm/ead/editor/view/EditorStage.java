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
package es.eucm.ead.editor.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * EditorStages are similar to regular stages, but do a second rendering pass
 * with a shared ShapeRenderer that only affects ShapeDrawable actors.
 * 
 * Actors that wish to retain the same transforms applied to their Batch should
 * keep a copy of the transform matrices to be applied to the ShapeRenderer for
 * use in this second pass.
 */
public class EditorStage extends Stage {

	/** shared shapeRenderer */
	protected ShapeRenderer shapeRenderer;

	/** access to field of same name in Group */
	private Field batchTransform;
	/** access to field of same name in Group */
	private Field oldBatchTransform;

	public EditorStage(Viewport viewport) {
		super(viewport);
		shapeRenderer = new ShapeRenderer();

		try {
			batchTransform = ClassReflection.getDeclaredField(Group.class,
					"batchTransform");
			oldBatchTransform = ClassReflection.getDeclaredField(Group.class,
					"oldBatchTransform");
			batchTransform.setAccessible(true);
			oldBatchTransform.setAccessible(true);
		} catch (ReflectionException re) {
			Gdx.app.error("EditorStage", "Cannot access Group transforms", re);
		}
	}

	private void applyGroupTransform(Group g) {
		try {
			shapeRenderer.setTransformMatrix((Matrix4) batchTransform.get(g));
		} catch (ReflectionException re) {
			throw new IllegalArgumentException(
					"Cannot access group transforms", re);
		}
	}

	private void revertGroupTransform(Group g) {
		try {
			shapeRenderer
					.setTransformMatrix((Matrix4) oldBatchTransform.get(g));
		} catch (ReflectionException re) {
			throw new IllegalArgumentException(
					"Cannot access group transforms", re);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void draw() {
		super.draw();

		if (!getRoot().isVisible()) {
			return;
		}

		// and now, draw using the ShapeRenderer
		shapeRenderer.setProjectionMatrix(getViewport().getCamera().combined);
		shapeRenderer.getTransformMatrix().idt();
		drawShapes(shapeRenderer, getRoot());
	}

	private void drawShapes(ShapeRenderer shapeRenderer, Actor actor) {

		if (actor == null || !actor.isVisible()) {
			return;
		} else if (actor instanceof Group) {
			Group group = (Group) actor;
			applyGroupTransform(group);

			// attempt to draw self (for groups: before recursion)
			if (group instanceof ShapeDrawable) {
				((ShapeDrawable) group).drawShapes(shapeRenderer);
			}

			// recursion (using snapshot, to avoid concurrent modification)
			SnapshotArray<Actor> children = group.getChildren();
			Actor[] actors = children.begin();
			for (int i = 0, n = children.size; i < n; i++) {
				Actor child = actors[i];
				if (child.isVisible()) {
					drawShapes(shapeRenderer, child);
				}
			}
			children.end();

			revertGroupTransform(group);
		} else if (actor instanceof ShapeDrawable) {

			// attempt to draw self (for non-groups)
			((ShapeDrawable) actor).drawShapes(shapeRenderer);
		}
	}
}
