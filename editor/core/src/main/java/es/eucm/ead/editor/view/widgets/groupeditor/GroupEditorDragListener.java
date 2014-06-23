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
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupEvent;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupEvent.Type;
import es.eucm.ead.editor.view.widgets.groupeditor.Grouper.SelectionGhost;
import es.eucm.ead.editor.view.widgets.groupeditor.Handles.Handle;
import es.eucm.ead.editor.view.widgets.groupeditor.Handles.RotationHandle;

/**
 * Handles all input interaction over a group editor
 */
public class GroupEditorDragListener extends DragListener {

	public static final float SCALE_FACTOR = 0.8f;

	public final Vector2 tmp1 = new Vector2();

	public static final float ALPHA_FACTOR = 0.5f;

	private GroupEditor groupEditor;

	private Modifier modifier;

	private Group container;

	private Group rootGroup;

	private Group editedGroup;

	private Actor dragging;

	private float offsetX;

	private float offsetY;

	private boolean panningMode = false;

	private boolean selecting = false;

	private boolean panning;

	private ClickListener clickListener;

	public GroupEditorDragListener(GroupEditor groupEditor,
			ShapeRenderer shapeRenderer, GroupEditorConfiguration config) {
		this.groupEditor = groupEditor;
		this.container = new Group();
		groupEditor.addActor(container);
		setTapSquareSize(0);
		setButton(-1);
		this.modifier = new Modifier(shapeRenderer, groupEditor, config);
		clickListener = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (getTapCount() == 2) {
					Group group = getEditedGroupChild(event.getTarget());
					enterGroupEdition(group);
				}
			}
		};
	}

	@Override
	public boolean handle(Event e) {
		clickListener.handle(e);
		return super.handle(e);
	}

	/**
	 * Sets the root edition group. This group acts as main container. Receives
	 * panning and zoom operations. The group and all its children are adjusted
	 * to fit the boundaries set by their children.
	 */
	public void setRootGroup(Group group) {
		editedGroup = rootGroup = group;
		container.clearChildren();
		container.addActor(group);
		container.setSize(group.getWidth(), group.getHeight());

		for (Actor actor : group.getChildren()) {
			if (actor instanceof Group) {
				adjustGroup((Group) actor);
			}
		}
	}

	public Modifier getModifier() {
		return modifier;
	}

	/**
	 * Iterates over the actor parents until one of them is a direct child of
	 * the current edited group
	 * 
	 * @return a direct child of the edited group. Returns the passed actor if
	 *         the given actor is the group editor or a handle
	 */
	public Group getEditedGroupChild(Actor actor) {
		if (actor == groupEditor || actor instanceof Handle) {
			return (Group) actor;
		}

		if (!actor.isDescendantOf(editedGroup)) {
			return null;
		}

		Group root = (actor instanceof Group) ? (Group) actor : actor
				.getParent();
		while (root != null && root.getParent() != editedGroup) {
			root = root.getParent();
		}
		return root;
	}

	/**
	 * Changes the current edited group to the given one.
	 * 
	 * @param group
	 *            must be a direct child of the current edited group. If not,
	 *            nothing happens
	 */
	private void enterGroupEdition(Group group) {
		if (group != null && group.getChildren().size > 1
				&& editedGroup.getChildren().contains(group, true)) {
			editedGroup = group;
			for (Actor actor : editedGroup.getParent().getChildren()) {
				// Make non-edited actors transparent
				if (actor != editedGroup) {
					Color c = actor.getColor();
					actor.setColor(c.r, c.g, c.b, c.a * ALPHA_FACTOR);
				}
			}
			modifier.deselectAll(true);
			fireEnteredGroupEdition(editedGroup);
		}
	}

	/**
	 * Edited group is set to the parent of the current edited group
	 */
	private void endGroupEdition() {
		// Only can end a group edition of the edited group is not the root
		if (editedGroup != rootGroup) {
			modifier.deselectAll(false);
			Group nextEditedGroup = editedGroup.getParent();
			Group oldGroup = editedGroup;
			/*
			 * The current edited group has changed. It is necessary to simplify
			 * it in case it has less than 2 children, which will make it no
			 * longer a group.
			 */
			Actor simplifiedGroup = simplifyGroup(oldGroup);

			/*
			 * Removed the edited group. Whatever is left of it is in
			 * simplifiedGroup.
			 */
			oldGroup.remove();

			if (simplifiedGroup != null) {
				nextEditedGroup.addActor(simplifiedGroup);
				modifier.setSelection(simplifiedGroup);
			}
			editedGroup = nextEditedGroup;

			// Restore transparency values
			for (Actor actor : editedGroup.getChildren()) {
				if (actor != editedGroup) {
					Color c = actor.getColor();
					actor.setColor(c.r, c.g, c.b, c.a / ALPHA_FACTOR);
				}
			}

			fireEndedGroupEdition(nextEditedGroup, oldGroup, simplifiedGroup);
		}
	}

	public Group getContainer() {
		return container;
	}

	/**
	 * Sets if panning mode is activated. In panning mode, whatever drag
	 * interaction the user does over the widget, will move the viewport
	 */
	public void setPanningMode(boolean panningMode) {
		this.panningMode = panningMode;
	}

	private boolean isPanning() {
		return panningMode || Gdx.input.isKeyPressed(Keys.SPACE)
				|| Gdx.input.isButtonPressed(Buttons.MIDDLE);
	}

	private boolean isMultipleSelection() {
		return Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)
				|| Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
	}

	/**
	 * @return if the group has 1 children, returns that child with the parent
	 *         transformation applied. If the group has no children,
	 *         {@code null} is returned. Otherwise, the passed group is
	 *         returned.
	 */
	private Actor simplifyGroup(Group group) {
		Actor result;
		if (group.getChildren().size == 0) {
			result = null;
		} else if (group.getChildren().size == 1) {
			result = modifier.ungroup(group).first();
		} else {
			result = group;
		}
		return result;
	}

	/**
	 * Adds to the selection the actors inside the current selection rectangle
	 */
	private void calculateSelection() {
		Array<Actor> selected = getActorsInside(groupEditor.getSelection());
		selected.removeValue(modifier, true);
		if (isMultipleSelection()) {
			Array<Actor> currentSelection = modifier.getSelection();
			for (Actor a : currentSelection) {
				if (!selected.contains(a, true)) {
					selected.add(a);
				}
			}
		}
		modifier.setSelection(selected, true);
	}

	/**
	 * @return the directly children of the current edited group that are inside
	 *         the given selection rectangle
	 */
	private Array<Actor> getActorsInside(Rectangle selection) {
		if (selection.width < 0) {
			selection.x += selection.width;
			selection.width = Math.abs(selection.width);
		}

		if (selection.height < 0) {
			selection.y += selection.height;
			selection.height = Math.abs(selection.height);
		}

		Vector2 o = new Vector2();
		Vector2 t = new Vector2();
		Vector2 n = new Vector2();
		Vector2 d = new Vector2();
		Array<Actor> actors = new Array<Actor>();
		for (Actor a : editedGroup.getChildren()) {
			o.set(0, 0);
			t.set(a.getWidth(), 0);
			n.set(0, a.getHeight());
			d.set(a.getWidth(), a.getHeight());
			a.localToAscendantCoordinates(groupEditor, o);
			a.localToAscendantCoordinates(groupEditor, t);
			a.localToAscendantCoordinates(groupEditor, n);
			a.localToAscendantCoordinates(groupEditor, d);
			if (selection.contains(o) && selection.contains(t)
					&& selection.contains(n) && selection.contains(d)) {
				actors.add(a);
			}
		}
		return actors;
	}

	/**
	 * Fits the scene in the current container size.
	 */
	public void fit() {
		container.setPosition(0, 0);
		float scaleX = container.getParent().getWidth() / container.getWidth();
		float scaleY = container.getParent().getHeight()
				/ container.getHeight();
		float scale = Math.min(scaleX, scaleY);
		float offsetX = (container.getParent().getWidth() - container
				.getWidth() * scale) / 2.0f;
		float offsetY = (container.getParent().getHeight() - container
				.getHeight() * scale) / 2.0f;
		container.setPosition(offsetX, offsetY);
		container.setScale(scale);
		fireTransformed(container);
	}

	/**
	 * Scales the root group
	 */
	public void scale(float scale) {
		container.setScale(container.getScaleX() * scale);
		modifier.updateHandlesScale();
	}

	public void adjustGroup(Group group) {
		modifier.adjustGroup(group);
	}

	/**
	 * Re-reads selection and refresh the selection. Must be call whenever the
	 * selection is modified externally.
	 */
	public void refresh() {
		modifier.refresh();
	}

	@Override
	public void dragStart(InputEvent event, float x, float y, int pointer) {
		if (pointer != 0) {
			/*
			 * On desktop the multitouch isn't supported so pointer will always
			 * be 0. This check prevents that while we're touching with two
			 * fingers (pointer 0 and pointer 1) the group isn't moved (with the
			 * first finger) nor we're selecting with the second finger.
			 */
			dragStop(event, x, y, pointer);
			return;
		}
		panning = isPanning();
		if (panning) {
			dragging = container;
		} else if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Actor target = event.getTarget();
			if (target instanceof Handle) {
				dragging = target;
				if (dragging instanceof RotationHandle) {
					((RotationHandle) dragging).startRotation();
				}
			} else if (target instanceof SelectionGhost) {
				/*
				 * If the dragged element is a selection ghost, its parent (the
				 * Grouper), must be dragged then
				 */
				dragging = target.getParent();
			} else if (target != editedGroup
					&& target.isDescendantOf(editedGroup)) {
				dragging = getEditedGroupChild(target);
				modifier.deselectAll(false);
			} else {
				selecting = true;
				groupEditor.setSelectionStart(x, y);
				dragging = null;
			}

			/*
			 * Calculate offset between the mouse click and the origin of the
			 * dragged element.
			 */
			if (dragging != null) {
				tmp1.set(x, y);
				groupEditor.localToDescendantCoordinates(editedGroup, tmp1);
				offsetX = tmp1.x - dragging.getX();
				offsetY = tmp1.y - dragging.getY();
			}
		}
	}

	@Override
	public void drag(InputEvent event, float x, float y, int pointer) {
		if (pointer != 0) {
			return;
		}
		if (panning) {
			container.setPosition(container.getX() - getDeltaX(),
					container.getY() - getDeltaY());
		} else if (dragging != null) {
			/*
			 * x and y are coordinates in the GroupEditor space. However, all
			 * transformations are made relatively to the edited group, so we
			 * need to convert them to its coordinates system.
			 */
			tmp1.set(x, y);
			groupEditor.localToDescendantCoordinates(editedGroup, tmp1);
			dragging.setPosition(tmp1.x - offsetX, tmp1.y - offsetY);
		} else if (selecting) {
			groupEditor.setSelectionEnd(x, y);
		}
	}

	@Override
	public void dragStop(InputEvent event, float x, float y, int pointer) {
		if (pointer != 0 && pointer != 1) {
			/*
			 * Because the second finger (pointer 1) must also execute a
			 * dragStop event as invoked on touchDown, so we assure that we're
			 * not selecting with the second finger while also scaling/rotating.
			 */
			return;
		}
		if (dragging != null && dragging != container
				&& modifier.getSelection().size == 0) {
			/* After dragging an element, make it selected again. */
			modifier.setSelection(dragging);
		}

		if (!(dragging instanceof Handle) && !(dragging instanceof Grouper)) {
			if (editedGroup != rootGroup) {
				modifier.adjustGroup(editedGroup);
			}
			refresh();
		}

		if (dragging != null) {
			fireTransformed(modifier.getSelection());
		}

		dragging = null;
		modifier.updateAspectRatio();
		panning = false;

		if (selecting) {
			selecting = false;
			calculateSelection();
		}
		groupEditor.endSelection();
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
			int button) {
		if (event.getButton() == Buttons.LEFT) {
			/* Touch up process the updates of the current selection */

			Actor target = event.getTarget();
			Group touched = getEditedGroupChild(target);
			if (touched == null || touched == groupEditor) {
				/*
				 * If nothing or group editor is touched, deselect all if not
				 * panning or selecting
				 */
				if (!panningMode && !selecting) {
					modifier.deselectAll(true);
				}
			} else if (target != editedGroup && !(target instanceof Handle)) {
				/*
				 * If it is an normal actor, update the selection
				 */
				Actor selected;
				if (target instanceof SelectionGhost) {
					/*
					 * If a selection ghost is touched, the actor really
					 * selected is the one that it represents
					 */
					selected = ((SelectionGhost) target).getRepresentedActor();
				} else {
					selected = touched;
				}

				if (!isDragging()) {
					if (isMultipleSelection()) {
						modifier.addToSelection(selected, true);
					} else {
						modifier.setSelection(selected);
					}
				}
			}
		}
		super.touchUp(event, x, y, pointer, button);
	}

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		switch (keycode) {
		case Keys.SPACE:
			panningMode = true;
			return true;
		case Keys.NUM_1:
			fit();
			return true;
		case Keys.MINUS:
			scale(SCALE_FACTOR);
			return true;
		case Keys.PLUS:
			scale(1.f / SCALE_FACTOR);
			return true;
		case Keys.ESCAPE:
			endGroupEdition();
			return true;
		case Keys.SHIFT_LEFT:
		case Keys.SHIFT_RIGHT:
			modifier.setKeepAspectRatio(true);
			return true;
		case Keys.DEL:
		case Keys.FORWARD_DEL:
			modifier.deleteSelection();
			return true;
		case Keys.G:
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)
					|| Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
				modifier.createGroup(editedGroup, groupEditor.newGroup());
			} else if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)
					|| Gdx.input.isKeyPressed(Keys.ALT_RIGHT)) {
				modifier.ungroup();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(InputEvent event, int keycode) {
		switch (keycode) {
		case Keys.SPACE:
			panningMode = false;
			return true;
		case Keys.SHIFT_LEFT:
		case Keys.SHIFT_RIGHT:
			modifier.setKeepAspectRatio(false);
			return true;
		}
		return false;
	}

	/**
	 * Fires some actors has been transformed
	 */
	private void fireTransformed(Array<Actor> transformed) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.transformed);
		groupEvent.setSelection(transformed);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Fires some actors has been transformed
	 */
	public void fireTransformed(Actor transformed) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.transformed);
		groupEvent.setParent(editedGroup);
		groupEvent.setSelection(transformed);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	private void fireEnteredGroupEdition(Group group) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.enteredEdition);
		groupEvent.setGroup(group);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	private void fireEndedGroupEdition(Group parent, Group oldGroup,
			Actor resultingGroup) {
		GroupEvent groupEvent = Pools.obtain(GroupEvent.class);
		groupEvent.setType(Type.exitedEdition);
		groupEvent.setParent(parent);
		groupEvent.setGroup(oldGroup);
		groupEvent.setSelection(resultingGroup);
		groupEditor.fire(groupEvent);
		Pools.free(groupEvent);
	}

	/**
	 * Modify the selection from outside the widget
	 */
	public void deselectAll() {
		modifier.deselectAll(false);
	}

	/**
	 * Modify the selection from outside the widget
	 */
	public void setSelection(Array<Actor> actor) {
		modifier.setSelection(actor, false);
	}
}
