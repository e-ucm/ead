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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupEvent;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupEvent.Type;

/**
 * Handles selection and transformation operations
 */
public class Modifier extends Group {

	public final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),

	tmp3 = new Vector2(), tmp4 = new Vector2(), tmp5 = new Vector2();

	private final Matrix3 tmpMatrix = new Matrix3();

	private GroupEditor groupEditor;

	private Handles handles;

	private Grouper grouper;

	private Array<Actor> selection;

	private boolean refreshPending;

	public Modifier(ShapeRenderer shapeRenderer, GroupEditor groupEditor) {
		this.groupEditor = groupEditor;
		handles = new Handles(shapeRenderer, this);
		selection = new Array<Actor>();
		grouper = new Grouper(shapeRenderer, this);
		addActor(handles);
		addActor(grouper);
	}

	public Handles getHandles() {
		return handles;
	}

	public Array<Actor> getSelection() {
		return selection;
	}

	/**
	 * Clears the selection and sets the selection to th given actor
	 */
	public void setSelection(Actor actor) {
		deselectAll();
		addToSelection(actor);
		fireSelection();
	}

	/**
	 * Deselects the current selection
	 */
	public void deselectAll() {
		selection.clear();
		remove();
		fireSelection();
	}

	/**
	 * Adds the given actor to the selection. If the actor is already in the
	 * selection, it is removed instead.
	 */
	public void addToSelection(Actor actor) {
		if (selection.contains(actor, true)) {
			selection.removeValue(actor, true);
		} else {
			selection.add(actor);
		}

		if (selection.size == 0) {
			deselectAll();
		} else if (selection.size == 1) {
			grouper.setVisible(false);
			Actor selected = selection.first();
			selected.getParent().addActor(this);
			handles.setInfluencedActor(selected);
		} else {
			grouper.setVisible(true);
			grouper.clear();
			for (Actor a : selection) {
				grouper.addToGroup(a);
			}
			adjustGroup(grouper);
			handles.setInfluencedActor(grouper);
			toFront();
		}
		fireSelection();
	}

	/**
	 * Re-reads selection and refresh handles and the grouper. This method must
	 * be call whenever the selection is modified externally.
	 */
	public void refresh() {
		refreshPending = true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (refreshPending) {
			refreshImpl();
			refreshPending = false;
		}
	}

	private void refreshImpl() {
		handles.readActorTransformation();
		grouper.clear();
		for (Actor a : selection) {
			// The actor has been removed externally
			if (a.getParent() == null) {
				selection.removeValue(a, true);
			}
		}

		if (selection.size > 0) {
			for (Actor a : selection) {
				grouper.addToGroup(a);
			}
			adjustGroup(grouper);
		} else {
			deselectAll();
		}
	}

	/**
	 * Deletes and removes all the actors in the selection
	 */
	public void deleteSelection() {
		if (selection.size > 0) {
			/*
			 * Due to the adding process, all actors selected have the same
			 * parent
			 */
			Group parent = selection.first().getParent();
			for (Actor a : selection) {
				a.remove();
			}
			fireDeleted(parent);
			deselectAll();
		}
	}

	/**
	 * Creates a group with the current selection and adds it to the passed
	 * parent
	 * 
	 * @param parent
	 *            the group parent
	 * @param newGroup
	 *            an empty group to be the root of the new group
	 */
	public void createGroup(Group parent, Group newGroup) {
		if (selection.size > 1) {
			Group group = grouper.createGroup(newGroup);
			if (group != null) {
				parent.addActor(group);
				fireGroup(parent, group);
				setSelection(group);
			}
		}
	}

	/**
	 * Iterates for all the actors in the current selection and split them if
	 * they are groups. The total of actors ungrouped are added to the current
	 * selection.
	 */
	public void ungroup() {
		// Store actors to be ungrouped
		Array<Actor> tobeUngrouped = new Array<Actor>();
		tobeUngrouped.addAll(selection);

		deselectAll();

		for (Actor group : tobeUngrouped) {
			if (group instanceof Group) {
				Group parent = group.getParent();
				Array<Actor> ungroup = ungroup((Group) group);
				for (Actor actor : ungroup) {
					parent.addActor(actor);
					addToSelection(actor);
				}
				fireUngroup(parent, (Group) group, ungroup);
				group.remove();
			} else {
				addToSelection(group);
			}
		}
	}

	/**
	 * Ungroups the given group in a list of actors. The resulting actors
	 * accumulates the transformation of the group to keep its absolute
	 * coordinates the same. The group is removed.
	 */
	public Array<Actor> ungroup(Group group) {
		Group parent = group.getParent();
		Array<Actor> actors = new Array<Actor>();
		for (Actor actor : group.getChildren()) {
			Vector2 o = tmp1.set(0, 0);
			Vector2 t = tmp2.set(actor.getWidth(), 0);
			Vector2 n = tmp3.set(0, actor.getHeight());
			actor.localToAscendantCoordinates(parent, o);
			actor.localToAscendantCoordinates(parent, t);
			actor.localToAscendantCoordinates(parent, n);
			actor.setRotation(actor.getRotation() + group.getRotation());
			applyTransformation(actor, o, t, n);
			actors.add(actor);
		}
		return actors;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor actor = super.hit(x, y, touchable);
		return actor == this ? null : actor;
	}

	/**
	 * Updates the handle scales to keep up with the current transformations
	 */
	public void updateHandlesScale() {
		handles.updateHandlesScale();
	}

	/**
	 * Updates the aspect ratio, reading the current influenced actor
	 */
	public void updateAspectRatio() {
		handles.updateAspectRatio();
	}

	/**
	 * Sets if the transformation must keep the aspect ratio.
	 */
	public void setKeepAspectRatio(boolean keepAspectRatio) {
		handles.setKeepAspectRatio(keepAspectRatio);
	}

	/**
	 * Applies the current transformation represented by the handles to given
	 * actor
	 */
	public void applyTransformation(Actor influencedActor, Vector2 origin,
			Vector2 tangent, Vector2 normal) {
		/*
		 * We are going to calculate the affine transformation for the actor to
		 * fit the bounds represented by the handles. The affine transformation
		 * is defined as follows:
		 */
		// |a b tx|
		// |c d ty|=|Translation Matrix| x |Scale Matrix| x |Rotation
		// Matrix|
		// |0 0 1 |
		/*
		 * More info about affine transformations:
		 * https://people.gnome.org/~mathieu
		 * /libart/libart-affine-transformation-matrices.html, To obtain the
		 * matrix, we want to resolve the following equation system:
		 */
		// | a b tx| |0| |o.x|
		// | c d ty|*|0|=|o.y|
		// | 0 0 1 | |1| | 1 |
		//
		// | a b tx| |w| |t.x|
		// | c d ty|*|0|=|t.y|
		// | 0 0 1 | |1| | 1 |
		//
		// | a b tx| |0| |n.x|
		// | c d ty|*|h|=|n.y|
		// | 0 0 1 | |1| | 1 |
		/*
		 * where o is handles[0] (origin), t is handles[2] (tangent) and n is
		 * handles[6] (normal), w is actor.getWidth() and h is
		 * actor.getHeight().
		 * 
		 * This matrix defines that the 3 points defining actor bounds are
		 * transformed to the 3 points defining modifier bounds. E.g., we want
		 * that actor origin (0,0) is transformed to (handles[0].x,
		 * handles[0].y), and that is expressed in the first equation.
		 * 
		 * Resolving these equations is obtained:
		 */
		// a = (t.x - o.y) / w
		// b = (t.y - o.y) / w
		// c = (n.x - o.x) / h
		// d = (n.y - o.y) / h
		/*
		 * Values for translation, scale and rotation contained by the matrix
		 * can be obtained directly making operations over a, b, c and d:
		 */
		// tx = o.x
		// ty = o.y
		// sx = sqrt(a^2+b^2)
		// sy = sqrt(c^2+d^2)
		// rotation = atan(c/d)
		// or
		// rotation = atan(-b/a)
		/*
		 * Rotation can give two different values (this happens when there is
		 * more than one way of obtaining the same transformation). To avoid
		 * that, we ignore the rotation to obtain the final values.
		 */

		Vector2 o = tmp1.set(origin.x, origin.y);
		Vector2 t = tmp2.set(tangent.x, tangent.y);
		Vector2 n = tmp3.set(normal.x, normal.y);

		Vector2 vt = tmp4.set(t).sub(o);
		Vector2 vn = tmp5.set(n).sub(o);

		// Ignore rotation
		float rotation = influencedActor.getRotation();
		vt.rotate(-rotation);
		vn.rotate(-rotation);

		t.set(vt).add(o);
		n.set(vn).add(o);

		float a = (t.x - o.x) / influencedActor.getWidth();
		float c = (t.y - o.y) / influencedActor.getWidth();
		float b = (n.x - o.x) / influencedActor.getHeight();
		float d = (n.y - o.y) / influencedActor.getHeight();

		// Math.sqrt gives a positive value, but it also have a negatives.
		// The
		// signum is calculated computing the current rotation
		float signumX = vt.angle() > 90.0f && vt.angle() < 270.0f ? -1.0f
				: 1.0f;
		float signumY = vn.angle() > 180.0f ? -1.0f : 1.0f;

		float scaleX = (float) Math.sqrt(a * a + b * b) * signumX;
		float scaleY = (float) Math.sqrt(c * c + d * d) * signumY;

		influencedActor.setScale(scaleX, scaleY);

		/*
		 * To obtain the correct translation value we need to subtract the
		 * amount of translation due to the origin.
		 */
		tmpMatrix.setToTranslation(influencedActor.getOriginX(),
				influencedActor.getOriginY());
		tmpMatrix.rotate(influencedActor.getRotation());
		tmpMatrix.scale(influencedActor.getScaleX(),
				influencedActor.getScaleY());
		tmpMatrix.translate(-influencedActor.getOriginX(),
				-influencedActor.getOriginY());

		/*
		 * Now, the matrix has how much translation is due to the origin
		 * involved in the rotation and scaling operations
		 */
		float x = o.x - tmpMatrix.getValues()[Matrix3.M02];
		float y = o.y - tmpMatrix.getValues()[Matrix3.M12];
		influencedActor.setPosition(x, y);
	}

	/**
	 * Adjusts the position and size of the given group to its children
	 */
	public void adjustGroup(Group group) {
		if (group.getChildren().size == 0) {
			return;
		}

		for (Actor actor : group.getChildren()) {
			if (actor instanceof Group) {
				adjustGroup((Group) actor);
			}
		}

		calculateBounds(group.getChildren(), tmp1, tmp2);

		if (tmp1.x != 0 || tmp1.y != 0 || tmp2.x != group.getWidth()
				|| tmp2.y != group.getHeight()) {
			/*
			 * minX and minY are the new origin (new 0, 0), so everything inside
			 * the group must be translated that much.
			 */
			for (Actor actor : group.getChildren()) {
				actor.setPosition(actor.getX() - tmp1.x, actor.getY() - tmp1.y);
			}

			/*
			 * Now, we calculate the current origin (0, 0) and the new origin
			 * (minX, minY), and group is translated by that difference.
			 */
			group.localToParentCoordinates(tmp3.set(0, 0));
			group.localToParentCoordinates(tmp4.set(tmp1.x, tmp1.y));
			tmp4.sub(tmp3);
			group.setBounds(group.getX() + tmp4.x, group.getY() + tmp4.y,
					tmp2.x, tmp2.y);
		}
	}

	/**
	 * Calculate the bounds of the given actors as a group
	 * 
	 * @param actors
	 *            the actors
	 * @param resultOrigin
	 *            result origin of the bounds
	 * @param resultSize
	 *            result size of the bounds
	 */
	private void calculateBounds(Array<Actor> actors, Vector2 resultOrigin,
			Vector2 resultSize) {
		resultOrigin.set(0, 0);
		resultSize.set(0, 0);
		if (actors.size == 0) {
			return;
		}

		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		for (Actor actor : actors) {
			tmp1.set(0, 0);
			tmp2.set(actor.getWidth(), 0);
			tmp3.set(0, actor.getHeight());
			tmp4.set(actor.getWidth(), actor.getHeight());
			actor.localToParentCoordinates(tmp1);
			actor.localToParentCoordinates(tmp2);
			actor.localToParentCoordinates(tmp3);
			actor.localToParentCoordinates(tmp4);

			minX = Math.min(minX, Math.min(tmp1.x,
					Math.min(tmp2.x, Math.min(tmp3.x, tmp4.x))));
			minY = Math.min(minY, Math.min(tmp1.y,
					Math.min(tmp2.y, Math.min(tmp3.y, tmp4.y))));
			maxX = Math.max(maxX, Math.max(tmp1.x,
					Math.max(tmp2.x, Math.max(tmp3.x, tmp4.x))));
			maxY = Math.max(maxY, Math.max(tmp1.y,
					Math.max(tmp2.y, Math.max(tmp3.y, tmp4.y))));
		}
		resultOrigin.set(minX, minY);
		resultSize.set(maxX - minX, maxY - minY);
	}

	/**
	 * Notifies current selection has been updated
	 */
	private void fireSelection() {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.selected);
		groupEvent.setSelection(selection);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Notifies the current selection has been deleted
	 */
	private void fireDeleted(Group parent) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.deleted);
		groupEvent.setParent(parent);
		groupEvent.setSelection(selection);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Notifies the current selection has been grouped
	 */
	private void fireGroup(Group parent, Group newGroup) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.grouped);
		groupEvent.setParent(parent);
		groupEvent.setGroup(newGroup);
		groupEvent.setSelection(newGroup.getChildren());
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Fires an ungroup notification
	 * 
	 * @param parent
	 *            the parent for the actors
	 * @param oldGroup
	 *            the old group grouping the actors
	 * @param actors
	 *            the actors ungrouped
	 */
	private void fireUngroup(Group parent, Group oldGroup, Array<Actor> actors) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.ungrouped);
		groupEvent.setParent(parent);
		groupEvent.setGroup(oldGroup);
		groupEvent.setSelection(actors);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}
}
