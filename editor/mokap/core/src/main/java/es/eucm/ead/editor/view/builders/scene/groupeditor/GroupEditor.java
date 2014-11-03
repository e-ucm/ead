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
package es.eucm.ead.editor.view.builders.scene.groupeditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEvent.Type;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class GroupEditor extends AbstractWidget {

	public final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),
			tmp3 = new Vector2(), tmp4 = new Vector2();

	private Drawable background;

	private Group rootGroup;

	private Group selectionLayer;

	private Array<Actor> selection;

	private boolean multipleSelection;

	public GroupEditor() {
		selection = new Array<Actor>();

		addListener(new GroupEditorListener());
		selectionLayer = new Group();
		addActor(selectionLayer);

		addListener(new GroupEditorListener());
		// Drags moving objects
		addListener(new DragListener() {
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				for (Actor actor : selectionLayer.getChildren()) {
					if (actor instanceof SelectionBox) {
						SelectionBox selectionBox = (SelectionBox) actor;
						if (selectionBox.isMoving()) {
							selectionBox.setPosition(selectionBox.getX()
									- getDeltaX(), selectionBox.getY()
									- getDeltaY());
						}
					}
				}
			}
		});
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the group being edited
	 */
	public void setRootGroup(Group rootGroup) {
		if (this.rootGroup != null) {
			this.rootGroup.remove();
		}
		this.rootGroup = rootGroup;
		if (rootGroup != null) {
			for (Actor actor : rootGroup.getChildren()) {
				adjustGroup(actor);
			}
			addActorAt(0, rootGroup);
		}
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	public void setSelection(Iterable<Actor> selection) {
		clearSelection();
		for (Actor actor : selection) {
			adjustGroup(actor);
			addToSelection(actor);
			SelectionBox selectionBox = Pools.obtain(SelectionBox.class);
			selectionBox.selected();
			selectionBox.setTarget(actor, background);
			selectionLayer.addActor(selectionBox);
			Pools.free(selectionBox);
		}
	}

	private void addToSelection(Actor actor) {
		selection.add(actor);
	}

	/**
	 * Clears the current selection
	 */
	public void clearSelection() {
		selection.clear();
		for (Actor selectionBox : selectionLayer.getChildren()) {
			Pools.free(selectionBox);
		}
		selectionLayer.clearChildren();
	}

	private Actor getDirectChild(Group parent, Actor child) {
		if (child == null || !child.isDescendantOf(parent)) {
			return null;
		}

		Actor firstChild = child;
		while (firstChild != null && firstChild.getParent() != parent) {
			firstChild = firstChild.getParent();
		}
		return firstChild;
	}

	public void refreshSelectionBox() {
		for (Actor actor : selectionLayer.getChildren()) {
			if (actor instanceof SelectionBox) {
				((SelectionBox) actor).readTargetBounds();
			}
		}
	}

	public class GroupEditorListener extends ActorGestureListener {

		private TouchDownTask task = new TouchDownTask();

		private Vector2 tmp = new Vector2();

		private boolean pinching = false;

		@Override
		public void touchDown(InputEvent event, float x, float y, int pointer,
				int button) {
			if (!event.isHandled() && pointer == 0) {
				Actor target = getDirectChild(selectionLayer, event.getTarget());
				if (target instanceof SelectionBox) {
					for (Actor actor : selectionLayer.getChildren()) {
						((SelectionBox) actor).moving();
					}
				} else {
					task.cancel();
					task.eventTarget = event.getTarget();
					if (selectionLayer.getChildren().size == 0) {
						task.run();
					} else {
						Timer.schedule(task, 0.2f);
					}
				}
			}
		}

		@Override
		public void pinch(InputEvent event, Vector2 initialPointer1,
				Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			task.cancel();
			float angle = tmp.set(pointer1.x - pointer2.x,
					pointer1.y - pointer2.y).angle();
			for (Actor selectionBox : selectionLayer.getChildren()) {
				if (selectionBox instanceof SelectionBox
						&& ((SelectionBox) selectionBox).isSelected()) {
					if (!pinching) {
						((SelectionBox) selectionBox)
								.setInitialPinchRotation(angle);
					} else {
						((SelectionBox) selectionBox).updateRotation(angle);
					}
				}
			}
			pinching = true;
		}

		@Override
		public boolean longPress(Actor actor, float x, float y) {
			return false;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			if (!event.isHandled() && pointer == 0) {
				if (pinching) {
					pinching = false;
					fireTransformed();
					return;
				}
				if (task.isScheduled()) {
					task.run();
					task.cancel();
				}
				Actor target = selectionLayer.hit(x, y, true);
				if (target instanceof SelectionBox) {
					if (((SelectionBox) target).isPressed()) {
						addToSelection(((SelectionBox) target).getTarget());
						fireSelection();
					} else if (((SelectionBox) target).isMoving()) {
						for (Actor actor : selectionLayer.getChildren()) {
							((SelectionBox) actor).selected();
						}
						fireTransformed();
					}
					((SelectionBox) target).selected();
				} else if (!multipleSelection) {
					clearSelection();
					fireSelection();
				}
			}
		}
	}

	public class TouchDownTask extends Task {

		private Actor eventTarget;

		@Override
		public void run() {
			Actor target = getDirectChild(rootGroup, eventTarget);
			if (target != null) {
				if (!multipleSelection) {
					clearSelection();
				}
				SelectionBox selectionBox = Pools.obtain(SelectionBox.class);
				selectionBox.setTarget(target, background);
				selectionLayer.addActor(selectionBox);
			}
		}
	}

	/**
	 * Fires some actors has been transformed
	 */
	private void fireTransformed() {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.transformed);
		groupEvent.setSelection(selection);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Notifies current selection has been updated
	 */
	private void fireSelection() {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.selected);
		groupEvent.setSelection(selection);
		fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Adjusts the position and size of the given group to its children
	 */
	private void adjustGroup(Actor root) {
		if (!(root instanceof Group)) {
			return;
		}

		Group group = (Group) root;
		if (group.getChildren().size == 0) {
			return;
		}

		for (Actor actor : group.getChildren()) {
			if (actor != this && actor instanceof Group) {
				adjustGroup(actor);
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
				if (actor != this) {
					actor.setPosition(actor.getX() - tmp1.x, actor.getY()
							- tmp1.y);
				}
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
	public void calculateBounds(Array<Actor> actors, Vector2 resultOrigin,
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
			// Ignore the modifier itself to calculate bounds
			if (actor != this) {
				tmp1.set(0, 0);
				tmp2.set(actor.getWidth(), 0);
				tmp3.set(0, actor.getHeight());
				tmp4.set(actor.getWidth(), actor.getHeight());
				actor.localToParentCoordinates(tmp1);
				actor.localToParentCoordinates(tmp2);
				actor.localToParentCoordinates(tmp3);
				actor.localToParentCoordinates(tmp4);

				minX = Math.min(
						minX,
						Math.min(tmp1.x,
								Math.min(tmp2.x, Math.min(tmp3.x, tmp4.x))));
				minY = Math.min(
						minY,
						Math.min(tmp1.y,
								Math.min(tmp2.y, Math.min(tmp3.y, tmp4.y))));
				maxX = Math.max(
						maxX,
						Math.max(tmp1.x,
								Math.max(tmp2.x, Math.max(tmp3.x, tmp4.x))));
				maxY = Math.max(
						maxY,
						Math.max(tmp1.y,
								Math.max(tmp2.y, Math.max(tmp3.y, tmp4.y))));
			}
		}
		resultOrigin.set(minX, minY);
		resultSize.set(maxX - minX, maxY - minY);
	}

	public static class GroupEvent extends Event {

		private Type type;

		private Array<Actor> selection = new Array<Actor>();

		private Group group;

		private Group parent;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Group getGroup() {
			return group;
		}

		public void setGroup(Group group) {
			this.group = group;
		}

		public Array<Actor> getSelection() {
			return selection;
		}

		public Group getParent() {
			return parent;
		}

		public void setParent(Group parent) {
			this.parent = parent;
		}

		public void setSelection(Array<Actor> selection) {
			this.selection.clear();
			this.selection.addAll(selection);
		}

		public void setSelection(Actor selection) {
			this.selection.clear();
			this.selection.add(selection);
		}

		@Override
		public void reset() {
			super.reset();
			this.selection.clear();
			this.group = null;
		}

		static public enum Type {
			selected, deleted, transformed, grouped, ungrouped, enteredEdition, exitedEdition, containerUpdated, rootChanged
		}
	}

	/**
	 * Base class to listen to {@link GroupEvent}s produced by
	 * {@link GroupEditor}.
	 */
	public static class GroupListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof GroupEvent) {
				GroupEvent groupEvent = (GroupEvent) event;
				switch (groupEvent.getType()) {
				case containerUpdated:
					containerUpdated(groupEvent, groupEvent.getParent());
					break;
				case rootChanged:
					rootChanged(groupEvent, groupEvent.getParent());
					break;
				case selected:
					selectionUpdated(groupEvent, groupEvent.getSelection());
					break;
				case deleted:
					deleted(groupEvent, groupEvent.getParent(),
							groupEvent.getSelection());
					break;
				case transformed:
					transformed(groupEvent, groupEvent.getParent(),
							groupEvent.getSelection());
					break;
				case grouped:
					grouped(groupEvent, groupEvent.getParent(),
							groupEvent.getGroup(), groupEvent.getSelection());
					break;
				case ungrouped:
					ungrouped(groupEvent, groupEvent.getParent(),
							groupEvent.getGroup(), groupEvent.getSelection());
					break;
				case enteredEdition:
					enteredGroupEdition(groupEvent, groupEvent.getGroup());
					break;
				case exitedEdition:
					exitedGroupEdition(groupEvent, groupEvent.getParent(),
							groupEvent.getGroup(), groupEvent.getSelection()
									.first());
				}
				return true;
			}
			return false;
		}

		/**
		 * The container of the group edited has changed (its panning offset or
		 * zoom has changed)
		 * 
		 * @param event
		 *            the event
		 * @param container
		 *            the container
		 */
		public void containerUpdated(GroupEvent event, Group container) {
		}

		/**
		 * The root of the group editor changed
		 * 
		 * @param groupEvent
		 *            the event
		 * @param root
		 *            the new root
		 */
		public void rootChanged(GroupEvent groupEvent, Group root) {
		}

		/**
		 * /** The selection has been updated
		 * 
		 * @param groupEvent
		 *            the event
		 * @param selection
		 *            the elements selected
		 */
		public void selectionUpdated(GroupEvent groupEvent,
				Array<Actor> selection) {
		}

		/**
		 * The selection has been deleted
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the deleted actors
		 * @param deleted
		 *            the actors deleted
		 */
		public void deleted(GroupEvent groupEvent, Group parent,
				Array<Actor> deleted) {
		}

		/**
		 * The selection has been transformed
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the transformed actors
		 * @param transformed
		 *            the actors transformed
		 */
		public void transformed(GroupEvent groupEvent, Group parent,
				Array<Actor> transformed) {
		}

		/**
		 * A new group has been created
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the new group
		 * @param newGroup
		 *            the new group recently created
		 * @param grouped
		 *            all the actors contained by the new group
		 */
		public void grouped(GroupEvent groupEvent, Group parent,
				Group newGroup, Array<Actor> grouped) {
		}

		/**
		 * A group was ungroup
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the ungrouped
		 * @param oldGroup
		 *            the group ungrouped
		 * @param ungrouped
		 *            the actors born from the ungrouping
		 */
		public void ungrouped(GroupEvent groupEvent, Group parent,
				Group oldGroup, Array<Actor> ungrouped) {
		}

		/**
		 * A group edition was started
		 * 
		 * @param groupEvent
		 *            the event
		 * @param group
		 *            the group edited
		 */
		public void enteredGroupEdition(GroupEvent groupEvent, Group group) {

		}

		/**
		 * Edition in the group was ended
		 * 
		 * @param groupEvent
		 *            the event
		 * @param parent
		 *            the parent of the edited group
		 * @param oldGroup
		 *            the group edited, before exiting the edition. It could be
		 *            the same as simplifiedGroup, meaning that after exiting
		 *            its edition, the group still have more than one child.
		 *            Check
		 *            {@link es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorDragListener#simplifyGroup(Group)}
		 *            for more details
		 * @param simplifiedGroup
		 *            the group simplified, after exiting the edition. It could
		 *            be the same as oldGroup.
		 */
		public void exitedGroupEdition(GroupEvent groupEvent, Group parent,
				Group oldGroup, Actor simplifiedGroup) {

		}
	}
}
