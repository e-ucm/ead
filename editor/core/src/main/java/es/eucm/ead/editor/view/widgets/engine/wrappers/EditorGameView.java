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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.ShapeDrawable;
import es.eucm.ead.engine.GameAssets;
import es.eucm.ead.engine.GameView;

/**
 * Does a second rendering pass with a shared ShapeRenderer that only affects
 * itself and ShapeDrawable children.
 * 
 * Actors that wish to retain the same transforms applied to their Batch should
 * keep a copy of the transform matrices to be applied to the ShapeRenderer for
 * use in this second pass.
 */
public class EditorGameView extends GameView implements ShapeDrawable {
	/** shared shapeRenderer */
	private static ShapeRenderer shapeRenderer;

	/** access to field of same name in Group */
	private Field batchTransform;
	/** access to field of same name in Group */
	private Field oldBatchTransform;

	private float cameraWidth;

	private float cameraHeight;

	private Model model;

	private static final Color STAGE_BORDER_COLOR = Color.WHITE;

	public EditorGameView(Model model, GameAssets gameAssets) {
		super(gameAssets);
		this.model = model;
		this.model.addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				addProjectListener();
				modelLoaded();
			}
		});

		if (shapeRenderer == null) {
			shapeRenderer = new ShapeRenderer(2000);
		}
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

	protected void addProjectListener() {
		model.addFieldListener(model.getGame(), new FieldListener() {
			@Override
			public void modelChanged(FieldEvent event) {
				modelLoaded();
			}

			@Override
			public boolean listenToField(FieldNames fieldName) {
				return FieldNames.EDIT_SCENE == fieldName;
			}
		});
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
		// and now, draw using the ShapeRenderer
		batch.end();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		drawShapes(shapeRenderer, this);
		batch.begin();
	}

	@Override
	public void clear() {
		super.clear();
		if (shapeRenderer != null) {
			shapeRenderer.dispose();
			shapeRenderer = null;
		}
	}

	private void drawShapes(ShapeRenderer shapeRenderer, Actor actor) {

		if (actor instanceof Group) {
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

	protected void modelLoaded() {
		setCameraSize(model.getGame().getWidth(), model.getGame().getHeight());
		invalidateHierarchy();
	}

	public void setCameraSize(float width, float height) {
		this.cameraWidth = width;
		this.cameraHeight = height;
	}

	@Override
	public float getPrefWidth() {
		return cameraWidth;
	}

	@Override
	public float getPrefHeight() {
		return cameraHeight;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor a = super.hit(x, y, touchable);
		if (a == null) {
			if (x > 0 && x < getWidth() && y > 0 && y < getHeight()) {
				return this;
			}
		}
		return a;
	}

	@Override
	public void drawShapes(ShapeRenderer sr) {
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(STAGE_BORDER_COLOR);
		sr.rect(0, 0, getWidth(), getHeight());
		sr.end();
	}
}
