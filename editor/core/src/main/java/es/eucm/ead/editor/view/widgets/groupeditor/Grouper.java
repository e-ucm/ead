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
package es.eucm.ead.editor.view.widgets.groupeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Virtual group that handles drawing borders of the current selection
 */
public class Grouper extends Group {

	public final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),
			tmp3 = new Vector2();

	private ShapeRenderer shapeRenderer;

	private Modifier modifier;

	private boolean modified;

	public Grouper(ShapeRenderer shapeRenderer, Modifier modifier) {
		this.shapeRenderer = shapeRenderer;
		this.modifier = modifier;
	}

	/**
	 * @return a new group with the current selection
	 */
	public Group createGroup() {
		// New group has the same transformation as this
		Group group = new Group();
		group.setBounds(getX(), getY(), getWidth(), getHeight());
		group.setOrigin(getOriginX(), getOriginY());
		group.setRotation(getRotation());
		group.setScale(getScaleX(), getScaleY());

		// Each children in the group must be contained by the new group
		for (Actor actor : getChildren()) {
			SelectionGhost ghost = (SelectionGhost) actor;
			Actor representedActor = ghost.getRepresentedActor();
			representedActor.setPosition(ghost.getX(), ghost.getY());
			representedActor.setRotation(ghost.getRotation());
			representedActor.setScale(ghost.getScaleX(), ghost.getScaleY());
			group.addActor(representedActor);
		}
		return group;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_DST_COLOR);
		Gdx.gl.glBlendEquation(GL20.GL_FUNC_SUBTRACT);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.begin(ShapeType.Line);
		super.drawChildren(batch, parentAlpha);
		shapeRenderer.end();
		Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		/*
		 * Grouper can NEVER be hit, so Grouper isn't returned for dragging,
		 * since dragging it has no sense
		 */
		Actor actor = super.hit(x, y, touchable);
		return actor == this ? null : actor;
	}

	/**
	 * Adds an actor to the group
	 */
	public void addToGroup(Actor actor) {
		SelectionGhost ghost = new SelectionGhost(shapeRenderer, actor);
		addActorAt(0, ghost);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		modified = true;
	}

	@Override
	public void setRotation(float degrees) {
		super.setRotation(degrees);
		modified = true;
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		super.setScale(scaleX, scaleY);
		modified = true;
	}

	@Override
	public void setScale(float scaleXY) {
		super.setScale(scaleXY);
		modified = true;
	}

	@Override
	public void setScaleX(float scaleX) {
		super.setScaleX(scaleX);
		modified = true;
	}

	@Override
	public void setScaleY(float scaleY) {
		super.setScaleY(scaleY);
		modified = true;
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		modified = true;
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		modified = true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (modified) {
			modifier.getHandles().readActorTransformation();
			modified = false;
		}
	}

	@Override
	public void clear() {
		super.clear();
		setBounds(0, 0, 0, 0);
		setRotation(0);
		setScale(1, 1);
		setOrigin(0, 0);
	}

	/**
	 * Wrapper for a selected actor. They are direct children of the grouper,
	 * and in charge of updating their represented actors with their current
	 * transformation, that varies when grouper transformation changes.
	 */
	public class SelectionGhost extends Group {

		private ShapeRenderer shapeRenderer;

		private Actor representedActor;

		private float startingRotation;

		public SelectionGhost(ShapeRenderer shapeRenderer,
				Actor representedActor) {
			this.shapeRenderer = shapeRenderer;
			this.representedActor = representedActor;
			setOrigin(representedActor.getOriginX(),
					representedActor.getOriginY());
			setBounds(representedActor.getX(), representedActor.getY(),
					representedActor.getWidth(), representedActor.getHeight());
			setRotation(representedActor.getRotation());
			setScale(representedActor.getScaleX(), representedActor.getScaleY());
			startingRotation = representedActor.getRotation();
		}

		public Actor getRepresentedActor() {
			return representedActor;
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			// If the grouper has been modified, transformation must be
			// transmitted to represented actors
			if (modified) {
				updateTransformation();
			}
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			shapeRenderer.rect(0, 0, getWidth(), getHeight());
		}

		/**
		 * Updates transformation of the represented actor to match the
		 * selection ghost transformation
		 */
		private void updateTransformation() {
			Actor parent = representedActor.getParent();
			this.localToAscendantCoordinates(parent, tmp1.set(0, 0));
			this.localToAscendantCoordinates(parent, tmp2.set(getWidth(), 0));
			this.localToAscendantCoordinates(parent, tmp3.set(0, getHeight()));

			modifier.applyTransformation(representedActor, tmp1, tmp2, tmp3);
			representedActor.setRotation(startingRotation
					+ getParent().getRotation());
		}
	}
}
